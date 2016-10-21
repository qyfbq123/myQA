define ['can/control', 'can', 'Auth', 'base', 'reqwest', 'bootbox', 'localStorage', 'select2cn', 'datepickercn', 'datatables.net', 'datatables.net-bs'], (Ctrl, can, Auth, base, reqwest, bootbox, localStorage)->

  return Ctrl.extend
    init: (el, data)->
      if !can.base
        new base('', data)

      questionInfo = new can.Map
        closed: false
        handlerId: if data.type == 'all' then 0 else -1

      this.element.html can.view('../public/view/home/question/questionHandle.html', questionInfo)

      handleQuestion = (question, btn)->
        if data.type == 'all'
          reqwest({
            url: Auth.apiHost + "question/toggleTop/" + question.id,
            method: "put",
            type: 'html'
          }).then(->
            if question.toTop == 0
              $(btn).text('撤销').closest('.panel').removeClass('panel-info').addClass 'panel-danger'
              $(btn).closest('.row').insertAfter '#tempDialog'
              question.toTop = 1
            else
              $(btn).text('置顶').closest('.panel').removeClass('panel-danger').addClass 'panel-info'
              $(btn).closest('.row').appendTo '#page-wrapper'
          ).fail ->
            bootbox.alert '置顶失败'
          return
        bootbox.dialog {
            title: '案例处理'
            message: $('#tempDialog').html()
            buttons:
              success:
                label: '确认'
                className: 'btn-success'
                callback: ->
                  obj = $('.bootbox-body form').serializeObject()
                  if obj.handlePromisedate
                    obj.handlePromisedate = $('.bootbox-body .input-group.date input').datepicker 'getDate'
                  
                  for k, v of obj
                    delete obj[k] if !v
                  reqwest( {
                    url: "#{Auth.apiHost}question/handle/#{question.id}"
                    method: "put"
                    data: obj
                    type: 'html'
                  }).then(->
                    bootbox.alert '处理成功'
                    if obj.handleStatus == '2'
                      $(btn).closest('.row').hide()
                  ).fail(->
                    bootbox.alert '处理失败！'
                  )
          }
        $('.bootbox-body select[name="handleStatus"]').unbind('change').bind('change', ->
            switch Number $(this).val()
              when 0
                $('.handlePromisedate,.handleResult', $(this).closest('form')).addClass 'hide'
              when 1
                $('.handleResult textarea', $(this).closest('form')).val ''
                $('.handlePromisedate', $(this).closest('form')).removeClass 'hide'
                $('.handleResult', $(this).closest('form')).addClass 'hide'
              when 2
                $('.handlePromisedate input', $(this).closest('form')).datepicker 'setDate', null
                $('.handlePromisedate', $(this).closest('form')).addClass 'hide'
                $('.handleResult', $(this).closest('form')).removeClass 'hide'
          ).select2({
            language: 'zh-CN'
            theme: "bootstrap"
            data: [
              id: 0, text: '未进行'
            ,
              id: 1, text: '进行中'
            ,
              id: 2, text: '完成'
            ]
          })

        $('.bootbox-body .input-group.date input').datepicker {
          language: 'zh-CN'
          startDate: "0d"
          todayBtn: true
          autoclose: true
        }


      generateRow = (question)->
        cls = if question.closed then 'panel-warning' else 'panel-info'
        cls = if question.toTop then 'panel-danger' else 'panel-info'
        $row = $ '<div class="row question"><div class="col-lg-10 col-lg-offset-1"><div class="panel ' + cls + '"><div></div></div>'
        $header = $ '<div class="panel-heading clearfix"><div>'
        $header.append $('<div class="panel-title pull-left"/>').text "编号：#{question.number}"
        if !question.closed
          $btn = $('<button class="btn btn-default btn-sm" type="button"><span class="glyphicon glyphicon-close" aria-hidden="true">处理</span></button').bind 'click', (e)->
            handleQuestion question, this
          if data.type == 'all'
            if question.toTop == 0
              $btn.text '置顶'
            else
              $btn.text '撤销'
          $header.append $('<div class="btn-group pull-right"></div>').append $btn

        $('.panel', $row).append $header

        $body = $ '<div class="panel-body"></div>'
        $body.append $('<p/>').text "项目：#{question.project}"
        $body.append $('<p/>').text "问题类型：#{question.type}"
        $body.append $('<p/>').text "城市：#{question.city?.name}"
        if !question.closed
          $body.append $('<p/>').text "项目发起时间：#{if question.startdate then new Date(question.startdate).toLocaleDateString()  else ''}"
          $body.append $('<p/>').text "要求结束时间：#{if question.promisedate then new Date(question.promisedate).toLocaleDateString() else ''}"
        $body.append $('<p/>').text "问题描述：#{question.description}"
        if question.closed
          $body.append $('<p/>').text "解决回馈：#{question.feedback}"
        $body.append $('<p/>').text "发起人：#{question.creator?.username || question.creator?.loginid || ''}"
        $body.append $('<p/>').text "提交时间：#{if question.created then new Date(question.created).toLocaleString() else ''}"
        $body.append $('<p/>').text "处理状态：#{if question.handleStatus then '进行中' else '未进行'}"
        if question.handlePromisedate
          $body.append $('<p/>').text "处理预计完成时间：#{if question.handlePromisedate then new Date(question.handlePromisedate).toLocaleDateString() else ''}"
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

        $('#page-wrapper').append $row

      reqwest( "#{Auth.apiHost}dict/projects?_=#{Date.now()}" ).then (data)->
        data = _.map data, (d)->
          id: d.text, text: d.text
        data.unshift id: 'unselected', text: '不限'
        $('#project').select2 {
          language: 'zh-CN'
          theme: "bootstrap"
          data: data
        }

      reqwest( "#{Auth.apiHost}dict/types/ms?_=#{Date.now()}" ).then (data)->
        data = _.map data, (d)->
          id: d.text, text: d.text
        data.unshift id: 'unselected', text: '不限'
        $('#type').select2 {
          language: 'zh-CN'
          theme: "bootstrap"
          data: data
        }

      reqwest( "#{Auth.apiHost}user/all?_=#{Date.now()}" ).then (data)->
        data = _.map data, (d)->
          id: d.id, text: d.username || d.loginid
        data.unshift id: 'unselected', text: '不限'
        $('#creatorId').select2 {
          language: 'zh-CN'
          theme: "bootstrap"
          data: data
        }

      $('#category').select2 {
          language: 'zh-CN'
          theme: "bootstrap"
          data: [
            id: 'unselected', text: '不限'
          ,
            id: 'WMS', text: 'WMS'
          ,
            id: 'TMS', text: 'TMS'
          ]
        }

      $('#handleStatus').select2 {
          language: 'zh-CN'
          theme: "bootstrap"
          data: [
            id: 'unselected', text: '不限'
          ,
            id: 0, text: '未进行'
          ,
            id: 1, text: '进行中'
          ]
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
          _.each data, (question)->
            generateRow question

      $('#number').val(data.number) if data.number
      $('#searchForm button').trigger 'click'
