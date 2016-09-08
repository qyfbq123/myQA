define ['can/control', 'can', 'Auth', 'base', 'reqwest', 'bootbox', 'localStorage', 'select2cn', 'datepickercn', 'datatables.net', 'datatables.net-bs', 'es6shim', 'jqueryFileupload'], (Ctrl, can, Auth, base, reqwest, bootbox, localStorage)->

  return Ctrl.extend
    init: (el, data)->
      questionInfo = new can.Map
        category: data.category
        title: if data.id then "详情" else "系统管理 / 新增"

      this.element.html can.view('../public/view/home/question/questionAdd.html', questionInfo)

      dialogLogin = (done)->
        bootbox.dialog
          title: "登录",
          message: '<div class="row">  ' +
              '<div class="col-md-12"> ' +
              '<form class="form-horizontal"> ' +
              '<div class="form-group"> ' +
              '<label class="col-md-4 control-label" for="dialogLoginID">LoginID</label> ' +
              '<div class="col-md-4"> ' +
              '<input id="dialogLoginID" name="dialogLoginID" type="text" placeholder="Your loginID" class="form-control input-md"> ' +
              '</div> ' +
              '</div> ' +
              '<div class="form-group"> ' +
              '<label class="col-md-4 control-label" for="dialogPassword">Password</label> ' +
              '<div class="col-md-4"> ' +
              '<input id="dialogPassword" name="dialogPassword" type="password" placeholder="Your password" class="form-control input-md"> ' +
              '</div> ' +
              '</div> ' +
              '</form> </div>  </div>',
          buttons:
              success: 
                  label: "登录",
                  className: "btn-success",
                  callback: ->
                      loginID = $('#dialogLoginID').val()
                      password = $('#dialogPassword').val()
                      Auth.login (loginID: loginID, password: password), (e)->
                        return bootbox.alert e.responseText if e
                        done?(e)

      if Auth.logined()
        if !can.base
          new base('', data)
      else
        $('#subNavbar').removeClass 'hide'
        $('#filePicker').closest('span').remove()
        $('#submitBtn').closest('.form-group').remove()
        $('#loginBtn').click ->
          dialogLogin ->
            window.location.reload()

      $('.input-group.date input').datepicker {
        language: 'zh-CN'
        startDate: "0d"
        todayBtn: true
        autoclose: true
      }

      reqwest( "#{Auth.apiHost}dict/projects?_=#{Date.now()}" ).then (data)->
        data = _.map data, (d)->
          id: d.text, text: d.text
        data.unshift id: 'unselected', text: '请选择'
        $('#project').select2 {
          language: 'zh-CN'
          theme: "bootstrap"
          data: data
        }

      reqwest( "#{Auth.apiHost}dict/types/ms?_=#{Date.now()}" ).then (data)->
        data = _.map data, (d)->
          id: d.text, text: d.text
        $('#type').select2 {
          language: 'zh-CN'
          theme: "bootstrap"
          data: data
        }

      reqwest( "#{Auth.apiHost}user/group/all?_=#{Date.now()}").then (data)->
        groups = _.map data, (group)->
          id: group.id, text: group.name
        groups.unshift id: 'unselected', text: '请选择'
        $('#group').select2 {
          language: 'zh-CN'
          theme: "bootstrap"
          data: groups
        }

      reqwest( "#{Auth.apiHost}user/role/handlers?_=#{Date.now()}" ).then (data)->
        ###*
         * 16-6-22 用户按地域区分下
        ###
        data = _.groupBy data, (user)-> user.city && user.city.name
        data = _.map data, (users, city)->
          children = _.map users, (user)->
            id: user.id, text: user.username || user.loginid

          { text: (if city is 'null' then '其他' else city), children: children }
        $('#handler').select2 {
          language: 'zh-CN'
          theme: "bootstrap"
          data: data
        }

      reqwest( "#{Auth.apiHost}dict/cities?_=#{Date.now()}" ).then (data)->
        $('#city').select2 {
          language: 'zh-CN'
          theme: "bootstrap"
          data: data
        }

      reqwest( "#{Auth.apiHost}dict/severity?_=#{Date.now()}" ).then (data)->
        data = _.map data, (d)->
          id: d.text, text: d.text
        $('#severity').select2 {
          language: 'zh-CN'
          theme: "bootstrap"
          data: data
        }

      # $('#filePicker').uploadify({
      #   'width': 200
      #   'fileSizeLimit': '5096k'
      #   'buttonText': "插入图片"
      #   'fileTypeDesc' : '图片',
      #   'swf': './lib/uploadify/uploadify.swf',
      #   'uploader': "#{Auth.apiHost}question/photo/upload",
      #   'onUploadSuccess': (file, data, response)->
      #     $("<img/>").attr('src', "#{Auth.apiHost}question/photo?name=#{data}").appendTo '#description'
      #   'onUploadError': (file, errorCode, errorMsg, errorString)->
      #     bootbox.alert '图片上传失败'
      # });

      if data.id
        $('#submitBtn, #resetBtn').addClass 'hide'
        reqwest( "#{Auth.apiHost}/question/#{data.id}" ).then( (data)->
          questionInfo.attr 'title', "编号：#{data.number}"
          $('#project, #type, #severity').each (i, e)->
            return if !$(this).attr 'name'
            $(this).val(data[$(this).attr 'name'] || 'unselected').change()
          $('#group, #city, #handler').each (i, e)->
            return if !$(this).attr 'name'
            if $(this).attr('name') is 'group'
              $(this).val(data[$(this).attr 'name']?.id || 'unselected').change()
            else $(this).val(data[$(this).attr 'name']?.id).change()

          $('form input').each (i, e)->
            return if !$(this).attr 'name'
            d = data[$(this).attr 'name']
            if $(this).parent().hasClass 'date'
              $(this).datepicker 'setDate', new Date d
            else
              $(this).val d
          $('form textarea').each (i, e)->
            return if !$(this).attr 'name'
            $(this).val(data[$(this).attr 'name'])

          if data.attachmentList?.length > 0
            table.clear()
            table.rows.add(data.attachmentList)
            table.draw()
            $('#attachmentRow').removeClass 'hide'
          else
            table.clear()
            $('#attachmentRow').addClass 'hide'

          if !data.closed and data.handleStatus < 2
            $('#saveBtn').removeClass 'hide'
          else
            $('#filePicker').closest('span').addClass 'hide'

          $('#rootCause,#correctiveAction').closest('.form-group').removeClass 'hide'
          if data.handler?.loginid  != Auth.user().loginID
            $('<p class="form-control-static"/>').text(data.rootCause || '').insertAfter $('#rootCause')
            $('<p class="form-control-static"/>').text(data.correctiveAction || '').insertAfter $('#correctiveAction')
            $('#rootCause,#correctiveAction').remove()
        ).fail ->
          bootbox.alert '获取问题详细信息失败！'

      $('#resetBtn').unbind('click').bind 'click', (e)->
        for k, v of questionInfo.attr()
          if k != 'category' and k != 'title'
            questionInfo.attr k, ''

        $('#startdate').datepicker 'setDate', null
        $('#promisedate').datepicker 'setDate', null

        table.clear().draw()
        $('#attachmentRow').addClass 'hide'

      $('#submitBtn').unbind('click').bind 'click', (e)->
        question = {
          project: if $('#project').val() is 'unselected' then null else $('#project').val()
          type: $('#type').val()
          emailto: $('#emailTo').val()
          group:
            if $('#group').val() is 'unselected' then null else (id: $('#group').val())
          city:
            id: $('#city').val()
          handler:
            id: $('#handler').val()
          startdate: $('#startdate').datepicker 'getDate'
          promisedate: $('#promisedate').datepicker 'getDate'
          severity: $('#severity').val()
          description: $('#description').val()
        }
        for k, v of questionInfo.attr()
          question[k] = v if k != 'title'

        attachmentList = table.rows().data()
        if attachmentList.length > 0
          question.attachmentList = _.map  attachmentList, (a)->
            return id: a.id

        reqwest( {
          url: "#{Auth.apiHost}question/create"
          method: 'post'
          data: JSON.stringify question
          contentType: "application/json"
          type: 'html'
        }).then(->
          bootbox.alert '新增成功！', ->
            $('#resetBtn').trigger 'click'
        ).fail (err)->
          bootbox.alert "新增失败！#{err.responseText}"

      $('#saveBtn').click ->
        question = {
          id: $('#id').val()
          category: $('#category').val()
          project: if $('#project').val() is 'unselected' then null else $('#project').val()
          type: $('#type').val()
          emailto: $('#emailTo').val()
          group:
            if $('#group').val() is 'unselected' then null else (id: $('#group').val())
          city:
            id: $('#city').val()
          handler:
            id: $('#handler').val()
          startdate: $('#startdate').datepicker 'getDate'
          promisedate: $('#promisedate').datepicker 'getDate'
          severity: $('#severity').val()
          description: $('#description').val()
          rootCause: $('#rootCause').val()
          correctiveAction: $('#correctiveAction').val()
        }

        attachmentList = table.rows().data()
        if attachmentList.length > 0
          question.attachmentList = _.map  attachmentList, (a)->
            return id: a.id

        reqwest( {
          url: "#{Auth.apiHost}question/update"
          method: 'post'
          data: JSON.stringify question
          contentType: "application/json"
          type: 'html'
        }).then(->
          bootbox.alert '保存成功！', ->
            history.go -1
        ).fail (err)->
          bootbox.alert "保存失败！#{err.responseText}"


      $('#filePicker').fileupload(
        url: "#{Auth.apiHost}question/attachment/upload"
        dataType: 'json'
        done: (e, data)->
          table.row.add(data.result).draw false
          $('#attachmentRow').removeClass 'hide'
        fail: ()->
          bootbox.alert '附件上传失败'
      ).prop('disabled', !$.support.fileInput).parent().addClass($.support.fileInput ? undefined : 'disabled')

      table = $('#attachmentList').DataTable {
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
            "<a href='#{Auth.apiHost}question/attachment/#{row.id}/download'>#{data}</a>"
        ,
          data: 'uploaded'
          render: (data)->
            if data then new Date(data).toLocaleString() else new Date().toLocaleString()
        ,
          data: 'size'
        ,
          data: 'uploader'
          render: (data)->
            data?.username || data?.email || ''
        ,
          data: 'uploader'
          render: (data)->
            if Auth.user()?.loginID is data.loginid
              $('<button/>').addClass('btn btn-sm btn-danger').text('删除')[0].outerHTML
            else
              '无'
        ]
      }

      $('#attachmentList tbody').on 'click', 'button.btn-danger', ->
        table.row($(this).parents('tr')).remove().draw()
        $('#attachmentRow').addClass('hide') if table.rows().data().length <= 0
