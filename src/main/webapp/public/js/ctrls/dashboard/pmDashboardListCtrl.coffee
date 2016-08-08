###*
 * 16-6-20 添加事件列表
###
define ['can/control', 'can/view/mustache', 'base', 'Auth', 'reqwest', '_', 'datatables.net', 'datatables.net-bs', 'datatables.net-responsive', 'datatables.net-responsive-bs', 'select2cn'], (Control, can, base, Auth, reqwest)->
  pageData = new can.Map()

  return Control.extend
    init: (el, pdata)->
      this.element.html can.view("../public/view/home/dashboard/pmDashboardList.html", pageData)

      ###*
       * 首页添加专门的事件列表标签页
      ###

      table = $('#issueList').DataTable {
        paging: false
        ordering: false
        ajax: 
          url: "#{Auth.apiHost}question/pmList?_=#{Date.now()}"
          dataSrc: (data)->
            data
        columns: [
          data: 'number'
        ,
          data: 'project'
        ,
          data: 'beginStorehouse'
        ,
          data: 'creator'
          render: (data)->
            data?.username || ''
        ,
          data: 'created'
          render: (data)->
            if data then new Date(data).toLocaleString() else ''
        ,
          data: 'issueDate'
          render: (data, d, row)->
            if data then new Date(data).toLocaleDateString() else ''
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
          render: (data, d, row)->
            $btn = $("<a href='#!home/question/pmClose/#{row.number}' class='btn btn-danger btn-xs' type='button'/>").text '详情'
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

