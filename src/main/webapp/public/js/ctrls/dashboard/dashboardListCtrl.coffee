define ['can/control', 'can/view/mustache', 'base', 'Auth', 'reqwest', '_', 'datatables.net', 'datatables.net-bs', 'datatables.net-responsive', 'datatables.net-responsive-bs', 'select2cn'], (Control, can, base, Auth, reqwest)->
  pageData = new can.Map()

  return Control.extend
    init: (el, pdata)->
      this.element.html can.view("../public/view/home/dashboard/dashboardList.html", pageData)

      ###*
       * 首页添加专门的事件列表标签页
      ###

      table = $('#issueList').DataTable {
        paging: true
        bFilter:  false
        processing: true
        serverSide: true
        ordering: false
        "order": []
        ajax: 
          url: "#{Auth.apiHost}question/page2?_=#{Date.now()}"
          data: (d)->
            search = $('#searchForm').serializeObject()
            for k, v of search
              delete search[k] if !v || v == 'unselected'
            _.extend d, search
            d
        columns: [
          data: 'number'
        ,
          data: 'project'
        ,
          data: 'severity'
        ,
          data: 'creator'
          render: (data)->
            data?.username || ''
        ,
          data: 'created'
          render: (data)->
            if data then new Date(data).toLocaleString() else ''
        ,
          data: 'handlePromisedate'
          render: (data, d, row)->
            if row.handleStatus == 2
              return '已完成'
            if row.handleStatus <= 2 and data then new Date(data).toLocaleDateString() else ''
        ,
          data: 'type'
        ,
          data: 'closed'
          render: (data)->
            if data then 'CLOSED' else 'OPEN'
        ,
          data: 'toTop'
          responsivePriority: 2
          render: (data)->
            $btn = $("<button class='btn btn-primary btn-xs' type='button'/>").text if data then '取消置顶' else '置顶'
            $btn[0].outerHTML
        ,
          data: 'toTop'
          responsivePriority: 2
          render: (data,d,row)->
            $btn = $("<a href='#!home/question/#{row.category}/#{row.id}' class='btn btn-danger btn-xs' type='button'/>").text '详情'
            $btn[0].outerHTML
        ,
          data: 'toTop'
          responsivePriority: 2
          render: (data, d, row)->
            if row.closed
              $btn = $("<a href='#!home/question/#{row.category.toLowerCase()}View/#{row.number}' class='btn btn-danger btn-xs' type='button'/>").text '历史'
            else if row.handleStatus < 2
              $btn = $("<a href='#!home/question/handle/#{row.number}' class='btn btn-danger btn-xs' type='button'/>").text '处理'
              if row.handler?.loginid != Auth.user().loginID
                $btn.addClass 'disabled'
            else
              $btn = $("<a href='#!home/question/#{row.category.toLowerCase()}Close/#{row.number}' class='btn btn-danger btn-xs' type='button'/>").text '关闭'
              if row.creator?.leader?.loginid != Auth.user().loginID
                $btn.addClass 'disabled'
            $btn[0].outerHTML
        ]
        rowCallback: (row, data, dataIndex)->
          $(row).addClass('danger') if data.toTop
      }

      ###*
       * 置顶功能
      ###
      $('#issueList tbody').on 'click', 'button', (e)->
        $row = $(this).closest('tr')
        question = table.row($row).data()
        reqwest({
            url: Auth.apiHost + "question/toggleTop/" + question.id,
            method: "put",
            type: 'html'
          }).then(->
            table.ajax.reload()
          ).fail ->
            bootbox.alert '置顶失败'

      ###*
       * 下拉框填充
      ###
      reqwest( "#{Auth.apiHost}dict/projects?_=#{Date.now()}" ).then (data)->
        data = _.map data, (d)->
          id: d.text, text: d.text
        data.unshift id: 'unselected', text: '不限'
        $('#project').select2 {
          language: 'zh-CN'
          theme: "bootstrap"
          data: data
        }

      # reqwest( "#{Auth.apiHost}dict/types/ms?_=#{Date.now()}" ).then (data)->
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

      ###*
       * 16-6-20 PM单独列出处理
      ###
      $('#category').select2 {
          language: 'zh-CN'
          theme: "bootstrap"
          width: '80px'
          data: [
            id: 'unselected', text: '不限'
          ,
            id: 'WMS', text: 'WMS'
          ,
            id: 'TMS', text: 'TMS'
          ]
        }

      $('#closed').select2 {
          language: 'zh-CN'
          theme: "bootstrap"
        }

      $('#searchForm button').unbind('click').bind 'click', (e)->
        table.ajax.reload()