define ['can/control', 'can', 'Auth', 'base', 'reqwest', 'bootbox', 'localStorage', 'select2cn', 'datepickercn', 'uploader', 'datatables.net', 'datatables.net-bs', 'es6shim', 'jqueryFileupload'], (Ctrl, can, Auth, base, reqwest, bootbox, localStorage)->
  return Ctrl.extend
    init: (el, data)->
      if !can.base
        new base('', data)

      ###*
       * data.id存在即为显示详情，其它为新增
      ###
      questionInfo = new can.Map
        title: if data.id then "详情" else "事件管理 / 新增"

      this.element.html can.view('../public/view/home/question/pmQuestionInfo.html', questionInfo)

      ###*
       * 获取pm组事件参与人
      ###
      reqwest( "#{Auth.apiHost}user/group/pm?_=#{Date.now()}" ).then (data)->
        ###*
         * 16-6-22 用户按地域区分显示
        ###
        data = _.groupBy data, (u)->return u.city && u.city.name

        generateCityTeammates = (cityName, users)->
          $("#teammates").append $('<div class="col-sm-2"/>').text "#{cityName}:"
          $div = $('<div class="col-sm-10"/>')
          _.each users, (user)->
            $label = $('<label/>').text user.username
            $input = $('<input type="checkbox" name="teammates"/>').val(user.id).prependTo $label
            $div.append $label
          $('#teammates').append $div

        reqwest("#{Auth.apiHost}dict/cities?_=#{Date.now()}").then (cities)->
          _.each cities, (city)->
            if data[city.text]
              generateCityTeammates city.text, data[city.text]
              delete data[city.text]

          for k,v of data
            k = if k is 'null' then '其他' else k
            generateCityTeammates k, v

      ###*
       * 日期组件
      ###
      $('.input-group.date input').datepicker {
        language: 'zh-CN'
        todayBtn: true
        autoclose: true
      }

      ###*
       * 下拉框填充
      ###
      $('#isCFeedback').select2 {
        language: 'zh-CN'
        theme: "bootstrap"
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

      reqwest( "#{Auth.apiHost}dict/types/pm?_=#{Date.now()}" ).then (data)->
        data = _.map data, (d)->
          id: d.text, text: d.text
        data.unshift id: 'unselected', text: '请选择'
        $('#type').select2 {
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

      reqwest( "#{Auth.apiHost}dict/beginStorehouses?_=#{Date.now()}" ).then (data)->
        data = _.map data, (d)->
          id: d.text, text: d.text
        data.unshift id: 'unselected', text: '请选择'
        $('#beginStorehouse').select2 {
          language: 'zh-CN'
          theme: "bootstrap"
          data: data
        }

      ###*
       * 16-6-20 所属供应商 改为下拉框
      ###
      reqwest( "#{Auth.apiHost}dict/suppliers?_=#{Date.now()}" ).then (data)->
        data = _.map data, (d)->
          id: d.text, text: d.text
        data.unshift id: 'unselected', text: '请选择'
        $('#supplier').select2 {
          language: 'zh-CN'
          theme: "bootstrap"
          data: data
        }

      ###*
       * 显示问题详情
      ###
      if data.id
        if location.hash.startsWith '#!home'
          $('#submitBtn').addClass 'hide'
          $('#closeBtn').removeClass('hide').unbind('click').bind 'click', (e)->
            bootbox.prompt
              title:"解决回馈："
              inputType: 'textarea'
              callback:(feedback)->
                if feedback != null
                  reqwest( {
                    url: "#{Auth.apiHost}question/close/#{data.id}"
                    data:
                      feedback: feedback
                    method: "put"
                    type: 'html'
                  } ).then( -> bootbox.alert '关闭成功。'; history.go(-1); ).fail ->bootbox.alert '关闭失败！'
        else
          $('#submitBtn').closest('.form-group').addClass 'hide'
        reqwest( "#{Auth.apiHost}question/#{data.id}" ).then( (data)->
          questionInfo.attr 'title', "编号：#{data.number}"
          data.isCFeedback = Number data.isCFeedback
          data.teammates = JSON.parse(data.teammates) if data.teammates
          $('select').each (i, e)->
            return if !$(this).attr 'name'
            $(this).val(data[$(this).attr 'name'] || 'unselected').change()

          $('form input').each (i, e)->
            return if !$(this).attr 'name'
            d = data[$(this).attr 'name']
            if $(this).parent().hasClass 'date'
              $(this).datepicker('setDate', new Date d) if d
            else
              $(this).val d
          $('form textarea').each (i, e)->
            return if !$(this).attr 'name'
            $(this).val(data[$(this).attr 'name'])

          if data.attachmentPath
            $('#filePicker').parent().addClass 'hide'
            $('#attachment').attr('href', "#{Auth.apiHost}question/#{data.id}/attachment/download").removeClass 'hide'
          else
            $('#filePicker').closest('.row').addClass 'hide'
        ).fail ->
          bootbox.alert '获取问题详细信息失败！'

      # $('#filePicker').uploadify({
      #   'width': 200
      #   'fileSizeLimit': '50960k'
      #   'buttonText': "浏览..."
      #   'fileTypeDesc' : '文件',
      #   'swf': './lib/uploadify/uploadify.swf',
      #   'uploader': "#{Auth.apiHost}question/photo/upload",
      #   'onUploadSuccess': (file, data, response)->
      #     $('#attachmentPath').val data
      #   'onUploadError': (file, errorCode, errorMsg, errorString)->
      #     bootbox.alert '附件上传失败'
      # });
      
      $('#filePicker').fileupload(
        url: "#{Auth.apiHost}question/attachment/upload"
        dataType: 'json'
        done: (e, data)->
          table.row.add(data.result).draw false
          $('#attachmentRow').removeClass 'hide'
          # $('#attachmentPath').val data.result.files[0].name
          # $('#filePicker').parent().nextAll('label').remove()
          # $('<label/>').text(data.result.files[0].name).insertAfter $('#filePicker').parent()
        fail: ()->
          bootbox.alert '附件上传失败'
      ).prop('disabled', !$.support.fileInput).parent().addClass($.support.fileInput ? undefined : 'disabled')

      ###*
       * 重置页面表单
      ###
      $('#resetBtn').unbind('click').bind 'click', (e)->
        for k, v of questionInfo.attr()
          questionInfo.attr k, '' if k != 'title'

        $('#teammates :checked').prop 'checked', false

        $('.input-group.date input').datepicker 'setDate', null

        table.clear().draw()
        $('#attachmentRow').addClass 'hide'

      ###*
       * 16-7-4 附件修改
      ###

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
          data: 'question_id'
          render: (data, d, row)->
            $("<button data-id='#{row.id}'/>").addClass('btn btn-sm btn-danger').text('删除')[0].outerHTML
        ]
      }

      $('#attachmentList tbody').on 'click', 'button.btn-danger', ->
        table.row($(this).parents('tr')).remove().draw()
        $('#attachmentRow').addClass('hide') if table.rows().data().length <= 0
        reqwest( {
          url: "#{Auth.apiHost}question/attachment/#{$(this).data 'id'}"
          method: 'delete'
          type: 'html'
        }).then ->

      ###*
       * 保存问题
      ###
      $('#submitBtn').unbind('click').bind 'click', (e)->

        question = $('form').serializeObject()

        $('select').each (i, e)->
            return if !$(this).attr 'name'
            delete question[$(this).attr 'name'] if $(this).val() is 'unselected'

        $('.input-group.date input').each (e, i)->
          question[$(this).attr 'name'] = $(this).datepicker 'getDate'

        ###*
         * 16-6-20 textarea转纪录
        ###
        $('form textarea').each ->
          if question[$(this).attr 'name']
            question[$(this).attr 'name'] = "#{new Date().toLocaleString()} 来自 #{Auth.user().username}\n#{question[$(this).attr 'name'].replace(/\n/g, '<br/>')}\n"

        question.isCFeedback = !!parseInt question.isCFeedback
        question.teammates = [ question.teammates ] if question.teammates and !_.isArray question.teammates
        question.teammates = JSON.stringify(question.teammates) if question.teammates

        ###*
         * 16-7-4 问题附件
        ###
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
            $('#filePicker').parent().nextAll('label').remove()
        ).fail (err)->
          bootbox.alert "新增失败！#{err.responseText}"