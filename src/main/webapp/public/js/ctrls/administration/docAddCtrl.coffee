define ['can/control', 'can', 'Auth', 'base', 'reqwest', 'bootbox', 'localStorage', 'pdfjs-dist/build/pdf', 'videojs', 'select2cn', 'datepickercn', 'datatables.net', 'datatables.net-bs', 'es6shim', 'jqueryFileupload'], (Ctrl, can, Auth, base, reqwest, bootbox, localStorage, pdfDist, videojs)->

  return Ctrl.extend
    init: (el, data)->
      if !can.base
        new base('', data)
      
      PDFJS = pdfDist.PDFJS
      docInfo = new can.Map()
      
      this.element.html can.view('../public/view/home/administration/docAdd.html', docInfo)
      isNew = if data.id then false else true

      reqwest("#{Auth.apiHost}dict/docTypes?_=#{Date.now()}").then( (data)->
        types = _.map data, (e)->
          id: e.text, text: e.text

        _val = $('#docType').val()
        $('#docType').select2 {
          language: 'zh-CN'
          theme: "bootstrap"
          placeholder: '请选择'
          data: types
        }
        $('#docType').val(_val).change if _val
      ).fail (err)->
        bootbox.alert '获取文档类型失败！'

      $('#docProperty').select2 {
          language: 'zh-CN'
          theme: "bootstrap"
          placeholder: '请选择'
        }

      $('.input-group.date input').datepicker {
        language: 'zh-CN'
        todayBtn: true
        autoclose: true
      }

      $('#filePicker').fileupload(
        url: "#{Auth.apiHost}doc/file/upload"
        dataType: 'json'
        send: ->
          $('#filePicker').prop 'disabled', true
        done: (e, data)->
          table.row.add(data.result).draw false
          $('#fileRow').removeClass 'hide'
          $('#filePicker').closest('span').addClass 'hide'
        fail: ()->
          bootbox.alert '文档上传失败'
        always: ->
          $('#filePicker').prop 'disabled', false
      ).prop('disabled', !$.support.fileInput).parent().addClass($.support.fileInput ? undefined : 'disabled')

      table = $('#fileList').DataTable {
        paging: false
        ordering: false
        searching : false
        info: false
        columns: [
          data: 'id'
          visible: false
        ,
          data: 'filename'
          render: (filename, d, data)->
            if data
              if data.filename.endsWith '.pdf'
                $href = $("<a href='#' data-src='#{Auth.apiHost}doc/file/#{data.id}/#{encodeURIComponent data.filename}' data-toggle='modal' data-target='#largeModal'/>").text "#{data.filename}"
                
              else if /\.avi|\.rmvb|\.rm|\.asf|\.divx|\.mpg|\.mpeg|\.wmv|\.mp4|\.mkv/.test data.filename
                $href = $("<a href='#' data-src='#{Auth.apiHost}doc/video/#{data.id}/#{encodeURIComponent data.filename}' data-downloadurl='#{Auth.apiHost}doc/file/#{data.id}/#{encodeURIComponent data.filename}' data-toggle='modal' data-target='#normalModal'/>").text "#{data.filename}"
              else
                $href = $("<a href='#{Auth.apiHost}doc/file/#{data.id}/#{encodeURIComponent data.filename}'/>").text "#{data.filename}"
              $href[0].outerHTML
        ,
          data: 'size'
        ,
          data: 'uploader'
          render: (data, d, row)->
            $("<button data-id='#{row.id}'/>").addClass('btn btn-sm btn-danger').data('id', row.id).text('删除')[0].outerHTML
        ]
      }

      $('#fileList tbody').on 'click', 'button.btn-danger', ->
        table.row($(this).parents('tr')).remove().draw()
        if table.rows().data().length <= 0
          $('#fileRow').addClass('hide')
          $('#filePicker').closest('span').removeClass 'hide'
        reqwest( {
          url: "#{Auth.apiHost}doc/file/#{$(this).data 'id'}"
          method: 'delete'
          type: 'html'
        }).then ->

      $('#saveBtn').click ->
        doc =  docInfo.attr()
        doc.category = 'doc'

        if isNew
          fileList = table.rows().data()
          if fileList.length > 0
            doc.file = {id : fileList[0].id}
          else
            bootbox.alert '必须上传文档！'
            return

        $('select').each (i, e)->
          return if !$(this).attr 'name'
          delete doc[$(this).attr 'name'] if !$(this).val()

        $('.input-group.date input').each (e, i)->
          doc[$(this).attr 'name'] = $(this).datepicker 'getDate'

        if isNew
          reqwest( {
            url: "#{Auth.apiHost}doc/createOther"
            method: 'post'
            data: JSON.stringify doc
            contentType: "application/json"
            type: 'html'
          }).then(->
            bootbox.alert '保存成功！', ->
              for k, v of docInfo.attr()
                docInfo.attr k, ''

              $('.input-group.date input').datepicker 'setDate', null

              table.clear().draw()
              $('#fileRow').addClass 'hide'
              $('#filePicker').closest('span').removeClass 'hide'
          ).fail (err)->
            bootbox.alert "保存失败！#{err.responseText}"
        else
          reqwest( {
            url: "#{Auth.apiHost}doc/updateOther"
            method: 'put'
            data: JSON.stringify doc
            contentType: "application/json"
            type: 'html'
          }).then(->
            bootbox.alert '保存成功！', ->
              history.go -1
          ).fail (err)->
            bootbox.alert "保存失败！#{err.responseText}"

      if data.id
        reqwest("#{Auth.apiHost}doc/other/#{data.id}?_=#{Date.now()}").then (data)->
          docInfo.attr data
          $('select').each ->
            return if !$(this).attr 'name'
            $(this).val(data[$(this).attr 'name'] || '').change()
          $('.input-group.date input').each ->
            return if !$(this).attr 'name'
            d = data[$(this).attr 'name']
            $(this).datepicker('setDate', new Date d) if d


          $('#filePicker').closest('span').addClass 'hide'
          if data.file
            table.row.add(data.file).draw false
            table.column(3).visible( false )
            $('#fileRow').removeClass('hide')

      DEFAULT_SCALE = 1.5
      renderPdf = (pdf, svgLib, modalBody)->
        modalFooter = $(modalBody).next '.modal-footer'
        $('.incomplete-warn', modalFooter).addClass 'hide'
        promise = $.Deferred().resolve()
        sumPages = pdf.numPages
        if sumPages> 20
          sumPages = 20
          $('.incomplete-warn', modalFooter).removeClass 'hide'
          $('.pages-number', modalFooter).text pdf.numPages
        
        _.each [1..sumPages], (e)->
          promise = promise.then ((pageNum)->
            return pdf.getPage(pageNum).then (page)->
              viewport = page.getViewport DEFAULT_SCALE
              container = document.createElement('div')
              container.id = 'pageContainer' + pageNum
              container.className = 'pageContainer'
              container.style.width = viewport.width + 'px'
              container.style.height = viewport.height + 'px'
              modalBody.appendChild(container)
              return page.getOperatorList().then (opList)->
                svgGfx = new svgLib.SVGGraphics  page.commonObjs, page.objs
                return svgGfx.getSVG(opList, viewport).then (svg)->
                  container.appendChild(svg)
          ).bind null, e

      videoPlayer = videojs $('video', $('#normalModal'))[0], {}

      $('.modal').on 'show.bs.modal', (event)->
        $ahref = $ event.relatedTarget
        url = $ahref.data 'src'

        if url isnt $('.download-btn', modal).attr 'href'
          $('#largeModal .pageContainer').remove()

          modal = $ $ahref.data 'target' 
          $('.modal-title', modal).text $ahref.text()
          $('.download-btn', modal).attr 'href', $ahref.data('downloadurl') || url

          if $ahref.text().endsWith '.pdf'
            PDFJS.getDocument(url).then (doc)->
              renderPdf doc, PDFJS, $('.modal-body', modal)[0]
          else
            if url isnt videoPlayer.currentSrc()
              videoPlayer.src url

      $('#normalModal').on 'shown.bs.modal', (event)->
        modalBodyWidth = $('#normalModal .modal-body').width()
        videoPlayer.dimensions(modalBodyWidth, modalBodyWidth/640 * 360);
        videoPlayer.play()

      $('#normalModal').on 'hide.bs.modal', (event)->
        videoPlayer.pause()