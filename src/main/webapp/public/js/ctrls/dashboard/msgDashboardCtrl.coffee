define ['can/control', 'can', 'base', 'Auth', 'reqwest', '_', 'datatables.net', 'datatables.net-bs', 'datatables.net-responsive', 'datatables.net-responsive-bs', 'datepickercn'], (Control, can, base, Auth, reqwest)->
  pageData = new can.Map()

  return Control.extend
    init: (el, data)->
      this.element.html can.view('../public/view/home/dashboard/msgDashboard.html', pageData)

      $('.input-group.date input').datepicker {
          language: 'zh-CN'
          todayBtn: true
          autoclose: true
        }

      table = $('#msgList').DataTable {
        paging: false
        ordering: false
        ajax: 
          url: "#{Auth.apiHost}msg/page?_=#{Date.now()}"
          dataSrc: (data)->
            data
          data: (data)->
            data.startDate = $('#start').datepicker('getDate') || undefined
            data.endDate = $('#end').datepicker('getDate') || undefined
            data
        columns: [
          data: 'projectName'
        ,
          data: 'creator'
          render: (data)->
            data?.username || ''
        ,
          data: 'elc'
        ,
          data: 'type'
        ,
          data: 'workContent'
        ,
          data: 'solution'
        ,
          data: 'remark'
        ,
          data: 'date'
          render: (data, d, row)->
            if data then new Date(data).toLocaleDateString() else ''
        ]
      }

      $('#searchForm button').unbind('click').bind 'click', (e)->
        table.ajax.reload()

      # queryMsgPage = (callback)->
      #   start = $('#page-wrapper .msgRow').length
      #   reqwest( "#{Auth.apiHost}msg/page?_=#{Date.now()}&start=#{start}" ).then (data)->
      #     _.each data, (e, i)->
      #       e.created = new Date(e.created).toLocaleString() if e.created
      #       e.date = new Date(e.date).toLocaleDateString() if e.date
      #       msg = can.view '../public/view/home/dashboard/msg.stache', e
      #       $('#page-wrapper').append msg

      #     callback(data) if callback

      # queryMsgPage (data)->
      #   $('#noMsgShow').removeClass 'hide' if data.length is 0
      #   if data.length != 20
      #       $('#more').addClass 'hide'
      #     else
      #       $('#page-wrapper').append $('#more')


      # $('#more').unbind('click').bind 'click', ->
      #   queryMsgPage (data)->
      #     if data.length != 20
      #       $('#more').addClass 'hide'
      #     else
      #       $('#page-wrapper').append $('#more')
