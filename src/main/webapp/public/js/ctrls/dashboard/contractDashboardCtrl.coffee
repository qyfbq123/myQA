define ['can/control', 'can/view/mustache', 'base', 'Auth', 'reqwest', 'bootbox', 'pdfjs-dist/web/pdf_viewer', 'videojs', '_', 'datatables.net', 'datatables.net-bs', 'datatables.net-responsive', 'datatables.net-responsive-bs', 'select2cn', 'datepickercn'], (Control, can, base, Auth, reqwest, bootbox, pdfDist, videojs)->
  pageData = new can.Map()

  return Control.extend
    init: (el, pdata)->

      PDFJS = pdfDist.PDFJS

      pageData.attr 'userIsOnSite', Auth.userIsOnSite()
      pageData.attr 'userIsContract', Auth.userIsContract()
      this.element.html can.view("../public/view/home/dashboard/contractDashboard.html", pageData)

      $('select').select2 {
          language: 'zh-CN'
          theme: "bootstrap"
          placeholder: '请选择'
      }

      $('.input-group.date input').datepicker {
        language: 'zh-CN'
        todayBtn: true
        autoclose: true
      }

      table = $('#contractList').DataTable {
        paging: true
        bFilter:  false
        processing: true
        serverSide: true
        ordering: false
        "order": []
        ajax: 
          url: "#{Auth.apiHost}doc/contract/page?_=#{Date.now()}"
          data: (d)->
            search = $('#searchForm').serializeObject()
            for k, v of search
              delete search[k] if !v
            _.extend d, search
            d.signDateBegin = $('#signDateBegin').datepicker('getDate') || undefined
            d.signDateEnd = $('#signDateEnd').datepicker('getDate') || undefined
            d
        columns: [
          data: 'vendorName'
        ,
          data: 'vendorCode'
        ,
          data: 'involvedELC'
        ,
          data: 'vendorType'
        ,
          data: 'purchaseKeeper'
        ,
          data: 'contractName'
        ,
          data: 'contractText'
        ,
          data: 'signDate'
          render: (data)->
            if data then new Date(data).toLocaleDateString() else ''
        ,
          data: 'currentStatus'
        ,
          data: 'id'
          responsivePriority: 2
          render: (data, d, row)->
            $btn = $("<a href='#!home/administration/contract/#{data}' class='btn btn-danger btn-xs' type='button'/>").text '详情'
            $btn[0].outerHTML
        ,
          data: 'id'
          responsivePriority: 2
          render: (data, d, row)->
            $btn = $("<button class='btn btn-danger btn-xs' type='button'/>").text '删除'
            $btn[0].outerHTML
        ,
          data: 'file'
          responsivePriority: 2
          render: (data,d,row)->
            if data
              if data.filename.endsWith '.pdf'
                $href = $("<a href='#' data-src='#{Auth.apiHost}doc/file/#{data.id}/#{encodeURIComponent data.filename}' data-toggle='modal' data-target='#largeModal'/>").text "#{data.filename}"
                
              else if /\.avi|\.rmvb|\.rm|\.asf|\.divx|\.mpg|\.mpeg|\.wmv|\.mp4|\.mkv/.test data.filename
                $href = $("<a href='#' data-src='#{Auth.apiHost}doc/video/#{data.id}/#{encodeURIComponent data.filename}' data-downloadurl='#{Auth.apiHost}doc/file/#{data.id}/#{encodeURIComponent data.filename}' data-toggle='modal' data-target='#normalModal'/>").text "#{data.filename}"
              else
                $href = $("<a href='#{Auth.apiHost}doc/file/#{data.id}/#{encodeURIComponent data.filename}'/>").text "#{data.filename}"
              $href[0].outerHTML
        ]
      }

      $('#contractList tbody').on 'click', 'button', (e)->
        $row = $(this).closest('tr')
        doc = table.row($row).data()
        reqwest({
            url: "#{Auth.apiHost}doc/contract/#{doc.id}",
            method: "delete",
            type: 'html'
          }).then(->
            table.ajax.reload()
          ).fail ->
            bootbox.alert '删除失败！'

      $('#searchForm button').unbind('click').bind 'click', (e)->
        table.ajax.reload()

      container = document.getElementById('viewerContainer')
      pdfViewer = new PDFJS.PDFViewer container: container
      container.addEventListener 'pagesinit', ->
        pdfViewer.currentScaleValue = 'page-width'

      videoPlayer = videojs $('video', $('#normalModal'))[0], {}

      $('.modal').on 'show.bs.modal', (event)->
        $ahref = $ event.relatedTarget
        url = $ahref.data 'src'

        if url isnt $('.download-btn', modal).attr 'href'

          modal = $ $ahref.data 'target' 
          $('.modal-title', modal).text $ahref.text()
          $('.download-btn', modal).attr 'href', $ahref.data('downloadurl') || url

          if $ahref.text().endsWith '.pdf'
            PDFJS.getDocument(url).then (pdfDocument)->
              pdfViewer.setDocument(pdfDocument)
          else
            if url isnt videoPlayer.currentSrc()
              videoPlayer.src url

      $('#normalModal').on 'shown.bs.modal', (event)->
        modalBodyWidth = $('#normalModal .modal-body').width()
        videoPlayer.dimensions(modalBodyWidth, modalBodyWidth/640 * 360);
        videoPlayer.play()

      $('#normalModal').on 'hide.bs.modal', (event)->
        videoPlayer.pause()
