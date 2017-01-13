define ['can/control', 'can', 'Auth', 'base', 'reqwest', 'bootbox', 'localStorage', 'select2cn', 'datepickercn', 'datatables.net', 'datatables.net-bs', 'es6shim', 'jqueryFileupload'], (Ctrl, can, Auth, base, reqwest, bootbox, localStorage)->

  return Ctrl.extend
    init: (el, data)->
      if !can.base
        new base('', data)

      contractInfo = new can.Map()
      
      this.element.html can.view('../public/view/home/administration/contractAdd.html', contractInfo)
      isNew = if data.id then false else true

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
          bootbox.alert '合同上传失败'
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
          render: (data, d, row)->
            "<a href='#{Auth.apiHost}doc/file/#{row.id}/#{encodeURIComponent data}'>#{data}</a>"
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
        contract =  contractInfo.attr()

        if isNew
          fileList = table.rows().data()
          if fileList.length > 0
            contract.file = {id : fileList[0].id}
          else
            bootbox.alert '必须上传合同！'
            return

        $('select').each (i, e)->
          return if !$(this).attr 'name'
          delete contract[$(this).attr 'name'] if !$(this).val()

        $('.input-group.date input').each (e, i)->
          contract[$(this).attr 'name'] = $(this).datepicker 'getDate'

        if isNew
          reqwest( {
            url: "#{Auth.apiHost}doc/createContract"
            method: 'post'
            data: JSON.stringify contract
            contentType: "application/json"
            type: 'html'
          }).then(->
            bootbox.alert '保存成功！', ->
              for k, v of contractInfo.attr()
                contractInfo.attr k, ''

              $('.input-group.date input').datepicker 'setDate', null

              table.clear().draw()
              $('#fileRow').addClass 'hide'
              $('#filePicker').closest('span').removeClass 'hide'
          ).fail (err)->
            bootbox.alert "保存失败！#{err.responseText}"
        else
          reqwest( {
            url: "#{Auth.apiHost}doc/updateContract"
            method: 'put'
            data: JSON.stringify contract
            contentType: "application/json"
            type: 'html'
          }).then(->
            bootbox.alert '保存成功！', ->
              history.go -1
          ).fail (err)->
            bootbox.alert "保存失败！#{err.responseText}"


      if data.id
        reqwest("#{Auth.apiHost}doc/contract/#{data.id}?_=#{Date.now()}").then (data)->
          contractInfo.attr data
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
