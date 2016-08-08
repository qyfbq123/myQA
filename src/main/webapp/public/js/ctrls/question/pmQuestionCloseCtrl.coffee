###*
 * 16-6-20 添加这个文件处理事件关闭
###
define ['can/control', 'can', 'Auth', 'base', 'reqwest', 'bootbox', 'localStorage', 'select2cn', 'datepickercn', 'uploader', 'datatables.net', 'datatables.net-bs', 'es6shim', 'jqueryFileupload'], (Ctrl, can, Auth, base, reqwest, bootbox, localStorage)->
  return Ctrl.extend
    init: (el, data)->

      pageInfo = new can.Map
        pageinfo: if location.hash.indexOf('home') != -1 then '事件关闭' else '事件详情'
      this.element.html can.view('../public/view/home/question/pmQuestionClose.html', pageInfo)

      if !Auth.logined()
        $('#submitBtn').addClass 'hide'
        $('#subNavbar').removeClass 'hide'
        $('#fileSpan').addClass 'hide'

        $('#loginBtn').click ->
          dialogLogin ->
            window.location.reload()
      else if !can.base
        new base('', data)


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

          if cache = $('#teammates').data 'cache'
            $('#teammates input').val cache
            $('#teammates').removeData 'cache'

          $('#teammates').data 'ended', true

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

      fetchQuestion = (fetchUrl)->
        $('#question').unbind 'change'
        $('#question .row .chat-panel').remove()
        reqwest( fetchUrl ).then( (data)->
          if($('#question').css('visibility') is 'hidden')
            $('#question').css 'visibility', 'inherit'
          $('#question').addClass 'hide'
          return if !data.id
          $('#number').val data.number
          data.isCFeedback = Number data.isCFeedback
          if data.teammates
            data.teammates = JSON.parse(data.teammates)
            if !($('#teammates').data 'ended')
              $('#teammates').data 'cache', data.teammates
          $('#question form select').each (i, e)->
            return if !$(this).attr 'name'
            $(this).val(data[$(this).attr 'name'] || 'unselected').change()

          $('#question form input').each (i, e)->
            return if !$(this).attr 'name'
            d = data[$(this).attr 'name']
            if $(this).parent().hasClass 'date'
              $(this).datepicker('setDate', new Date d) if d
            else
              $(this).val d
          $('#question form textarea').each (i, e)->
            $(this).val('')
            return if !$(this).attr 'name'
            if records = data[$(this).attr 'name']
              records = records.replace /\n$/, ''
              $chat = $('#sampleChat').clone().removeAttr 'id'
              _.each records.split('\n'), (record, index)->
                if index % 2 == 0
                  if index % 4 < 2
                    $li = $('#sampleOdd', $chat).clone().removeAttr 'id'
                  else
                    $li = $('#sampleEven', $chat).clone().removeAttr 'id'
                  $('ul.chat', $chat).append $li

                if index % 2 == 0
                  $('li:last strong', $chat).text record
                else
                  $('li:last p', $chat).html record
              $('#sampleOdd, #sampleEven', $chat).remove()
              $chat.removeClass('hide').insertBefore $(this)

          # if data.attachmentPath
          #   $('#attachment').attr('href', "#{Auth.apiHost}question/#{data.id}/attachment/download").removeClass 'hide'
          # else
          #   $('#attachment').closest('.row').addClass 'hide'

          if data.attachmentList?.length > 0
            table.clear()
            table.rows.add(data.attachmentList)
            table.draw()
            $('#attachmentList').removeClass 'hide'
          else
            table.clear()
            $('#attachmentList').addClass 'hide'
            $('#attachmentRow').addClass('hide') if data.closed
          $('#question').removeClass 'hide'

          if !Auth.logined()
            $('#question').bind 'change', ->
              if Auth.logined()
                $('#question').unbind 'change'
                $('#submitBtn').removeClass 'hide'
                if data.creator.leader && data.creator.leader.loginid == Auth.user().loginID
                  $('#suggest').removeClass 'hide'
                  $('#closeBtn').removeClass 'hide'
                return
              dialogLogin ->
                # $('#submitBtn').removeClass 'hide'
                # if data.creator.leader && data.creator.leader.loginid == Auth.user().loginID
                #   $('#suggest').removeClass 'hide'
                #   $('#closeBtn').removeClass 'hide'
                window.location.reload()
          else
            if data.creator.leader && data.creator.leader.loginid == Auth.user()?.loginID
              $('#suggest').removeClass 'hide'
              $('#closeBtn').removeClass 'hide'

          if data.closed
            $('#fileSpan').addClass 'hide'
            $('#submitBtn, #closeBtn').addClass 'hide'
          else if Auth.logined()
            $('#fileSpan').removeClass 'hide'
        ).fail ->
          $('#question').addClass 'hide'
          bootbox.alert '获取问题详细信息失败！'

      if data.id
        fetchQuestion "#{Auth.apiHost}question/#{data.id}"
      if data.number
        fetchQuestion "#{Auth.apiHost}question/byNumber/#{data.number}"

      $('#searchForm button').unbind('click').bind 'click', (e)->
        return $('#question').addClass 'hide' if !$('#number').val()
        fetchQuestion "#{Auth.apiHost}question/byNumber/#{$('#number').val()}"


      $('#submitBtn').unbind('click').bind 'click', (e)->
        if !Auth.logined()
          dialogLogin ->
            window.location.reload()
          return

        question = $('#question form').serializeObject()

        $('select').each (i, e)->
            return if !$(this).attr 'name'
            delete question[$(this).attr 'name'] if $(this).val() is 'unselected'

        $('.input-group.date input').each (e, i)->
          question[$(this).attr 'name'] = $(this).datepicker 'getDate'

        $('#question form textarea').each ->
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
          url: "#{Auth.apiHost}question/update"
          method: 'post'
          data: JSON.stringify question
          contentType: "application/json"
          type: 'html'
        }).then(->
          bootbox.alert '保存成功！', ->
            fetchQuestion "#{Auth.apiHost}question/#{question.id}"
        ).fail (err)->
          bootbox.alert "保存失败！#{err.responseText}"

      $('#closeBtn').unbind('click').bind 'click', (e)->
        if !Auth.logined()
          dialogLogin ->
            window.location.reload()
          return
        bootbox.prompt
          title:"解决回馈："
          inputType: 'textarea'
          callback:(feedback)->
            if feedback != null
              reqwest( {
                url: "#{Auth.apiHost}question/close/#{$('#question form input[name="id"]').val()}"
                data:
                  feedback: feedback
                method: "put"
                type: 'html'
              } ).then( -> bootbox.alert '关闭成功。'; history.go(-1); ).fail ->bootbox.alert '关闭失败！'

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

      $('#filePicker').fileupload(
        url: "#{Auth.apiHost}question/attachment/upload"
        dataType: 'json'
        done: (e, data)->
          table.row.add(data.result).draw false
          $('#attachmentList').removeClass 'hide'
        fail: ()->
          bootbox.alert '附件上传失败'
      ).prop('disabled', !$.support.fileInput).parent().addClass($.support.fileInput ? undefined : 'disabled')