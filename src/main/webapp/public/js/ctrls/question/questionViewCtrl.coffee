define ['can/control', 'can', 'Auth', 'base', 'reqwest', 'bootbox', 'localStorage', 'select2cn', 'datatables.net', 'datatables.net-bs', 'es6shim'], (Ctrl, can, Auth, base, reqwest, bootbox, localStorage)->

  return Ctrl.extend
    init: (el, data)->
      if !can.base
        new base('', data)

      questionInfo = new can.Map
        category: data.category
        closed: data.closed
        pageinfo: "#{if data.category == 'PM' then '事件' else data.category}#{if data.closed then '历史解决查询' else '关闭'}"
        handleStatus: 2

      this.element.html can.view('../public/view/home/question/questionView.html', questionInfo)

      closeQuestion = (question, btn)->
        bootbox.prompt
          title:"解决回馈："
          inputType: 'textarea'
          callback:(feedback)->
            if feedback != null
              reqwest( {
                url: "#{Auth.apiHost}question/close/#{question.id}"
                data:
                  feedback: feedback
                method: "put"
                type: 'html'
              } ).then( -> bootbox.alert '关闭成功。'; $(btn).closest('.row').hide()).fail ->bootbox.alert '关闭失败！'

      genTextArea = (pre, content)->
        $('<div/>').append($('<div class="pull-left"/>').text pre).append($('<div class="pull-left"/>').html content.replace /\n/g, '<br/>').append $('<div class="clearfix"/>')
      
      generateRow = (question)->
        cls = if question.closed then 'panel-warning' else 'panel-info'
        cls = if question.toTop then 'panel-danger' else cls || 'panel-info'
        $row = $ '<div class="row question"><div class="col-lg-10 col-lg-offset-1"><div class="panel ' + cls + '"><div></div></div>'
        $header = $ '<div class="panel-heading clearfix"><div>'
        $header.append $('<div class="panel-title pull-left"/>').text "编号：#{question.number}"
        if !question.closed
          if questionInfo.category != 'PM'
            if question.creator?.leader?.loginid == Auth.user().loginID
              $btn = $('<button class="btn btn-default btn-sm" type="button"><span class="glyphicon glyphicon-close" aria-hidden="true">关闭</span></button>').bind 'click', (e)->
                closeQuestion question, this
              $header.append $('<div class="btn-group pull-right"></div>').append $btn
          else
            $a = $("<a class='btn btn-default btn-sm' href='#!home/question/#{questionInfo.category}/#{question.id}'><span aria-hidden='true'>详情</span></button>")
            $header.append $('<div class="btn-group pull-right"></div>').append $a

        $('.panel', $row).append $header

        $body = $ '<div class="panel-body"></div>'
        $body.append $('<p/>').text "项目：#{question.project}"
        $body.append $('<p/>').text "问题类型：#{question.type}"
        if questionInfo.category != 'PM'
          $body.append $('<p/>').text "城市：#{question.city?.name}"
          if !question.closed
            $body.append $('<p/>').text "项目发起时间：#{if question.startdate then new Date(question.startdate).toLocaleDateString()  else ''}"
            $body.append $('<p/>').text "要求结束时间：#{if question.promisedate then new Date(question.promisedate).toLocaleDateString() else ''}"
          $body.append $('<p/>').text "问题描述：#{question.description}"
          # $body.append $('<p/>').text "解决建议：#{question.suggestion}"
          if question.closed
            $body.append $('<p/>').text "解决回馈：#{question.feedback}"
        $body.append $('<p/>').text "发起人：#{question.creator?.username || question.creator?.loginid || ''}"
        if questionInfo.category != 'PM'
          $body.append $('<p/>').text "处理人：#{question.handler?.username || question.handler?.loginid || ''}"
          $body.append $('<p/>').text "处理结果：#{question.handleResult}"
        $body.append $('<p/>').text "提交时间：#{if question.created then new Date(question.created).toLocaleString() else ''}"
        if question.closed
          $body.append $('<p/>').text "关闭时间：#{if question.modified then new Date(question.modified).toLocaleString() else ''}"

        if questionInfo.category == 'PM'
          $body.append $('<p/>').text "所属供应商：#{question.supplier || ''}"
          $body.append $('<p/>').text "问题发现时间：#{if question.issueDate then new Date(question.issueDate).toLocaleString() else ''}"
          $body.append $('<p/>').text "是否由客户反馈：#{if question.isCFeedback then 'Y' else 'N'}"
          $body.append $('<p/>').text "补救方案期限：#{if question.containmentPlanDate then new Date(question.containmentPlanDate).toLocaleString() else ''}"
          $body.append $('<p/>').text "改善方案期限：#{if question.actionPlanDate then new Date(question.actionPlanDate).toLocaleString() else ''}"
          $body.append $('<p/>').text "问题严重性：#{question.severity || ''}"
          $body.append $('<p/>').text "始发地和涉及库房信息：#{question.beginStorehouse || ''}"
          $body.append $('<p/>').text "SPC名：#{question.spc || ''}"
          $body.append $('<p/>').text "订单号：#{question.orderNo || ''}"
          $body.append $('<p/>').text "运单号：#{question.hawb || ''}"
          $body.append $('<p/>').append genTextArea "备案信息：", "#{question.partInformation || ''}"
          $body.append $('<p/>').text "原定派送／取件时间：#{if question.scheduledTime then new Date(question.scheduledTime).toLocaleString() else ''}"
          $body.append $('<p/>').text "实际派送／取件时间：#{if question.actualTime then new Date(question.actualTime).toLocaleString() else ''}"
          $body.append $('<p/>').append genTextArea "问题描述：", "#{question.problemStatement || ''}"
          ###*
           * 16-6-20 隐藏情况反馈
          ###
          # $body.append $('<p/>').text "情况反馈：#{question.issueDescription || ''}"
          $body.append $('<p/>').append genTextArea "补救方案描述：", "#{question.recoveryDescription || ''}"
          $body.append $('<p/>').append genTextArea "问题根本原因：", "#{question.rootCause || ''}"
          $body.append $('<p/>').append genTextArea "改善方案：", "#{question.correctiveAction || ''}"
          $body.append $('<p/>').append genTextArea "主管建议：", "#{question.suggest || ''}"
        if question.attachmentList?.length > 0
          $table = $('#tempAttachmentList').clone().removeAttr('id').removeClass 'hide'
          $table.DataTable {
            paging: false
            ordering: false
            searching : false
            info: false
            data: question.attachmentList
            columns: [
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
            ]
          }
          $body.append $('<p/>').text "附件："
          $body.append $('<p/>').append $table
        $('.panel', $row).append $body

        # $('#page-wrapper').append $row
        $row.insertBefore '#more'

      reqwest( "#{Auth.apiHost}dict/projects?_=#{Date.now()}" ).then (data)->
        data = _.map data, (d)->
          id: d.text, text: d.text
        data.unshift id: 'unselected', text: '不限'
        $('#project').select2 {
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

      $('#beginStorehouse').closest('.form-group').addClass('hide') if questionInfo.category != 'PM'

      # reqwest( "#{Auth.apiHost}dict/types?_=#{Date.now()}" ).then (data)->
      #   data = _.map data, (d)->
      #     id: d.text, text: d.text
      #   data.unshift id: 'unselected', text: '不限'
      #   $('#type').select2 {
      #     language: 'zh-CN'
      #     theme: "bootstrap"
      #     data: data
      #   }

      reqwest( "#{Auth.apiHost}user/all?_=#{Date.now()}" ).then (data)->
        data = _.map data, (d)->
          id: d.id, text: d.username || d.loginid
        data.unshift id: 'unselected', text: '不限'
        $('#creatorId').select2 {
          language: 'zh-CN'
          theme: "bootstrap"
          data: data
        }

      $('#searchForm button').unbind('click').bind 'click', (e)->
        $('#page-wrapper>.row.question').remove()
        search = $('#searchForm').serializeObject()
        for k, v of search
          delete search[k] if !v || v == 'unselected'
        reqwest( {
          url: "#{Auth.apiHost}question/page?_=#{Date.now()}"
          data: search
        }).then (data)->
          if(data.length == 20)
            $('#more').removeClass 'hide'
          else
            $('#more').addClass 'hide'
          _.each data, (question)->
            generateRow question

      $('#number').val(data.number) if data.number
      $('#searchForm button').trigger 'click'

      $('#more button').click ->
        search = $('#searchForm').serializeObject()
        for k, v of search
          delete search[k] if !v || v == 'unselected'
        reqwest( {
          url: "#{Auth.apiHost}question/page?_=#{Date.now()}&start=#{$('.row.question').size()}"
          data: search
        }).then (data)->
          $('#more').addClass 'hide' if data.length < 20
          _.each data, (question)->
            generateRow question
