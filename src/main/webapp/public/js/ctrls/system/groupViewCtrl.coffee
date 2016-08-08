define ['can/control', 'can', 'Auth', 'base', 'reqwest', 'localStorage', 'datatables.net', 'datatables.net-bs', 'datatables.net-responsive', 'datatables.net-responsive-bs', 'bootbox'], (Ctrl, can, Auth, base, reqwest, localStorage)->
  return Ctrl.extend
    init: (el, data)->
      if !can.base
        new base('', data)

      this.element.html can.view('../public/view/home/system/groupView.html')

      table = $('#groupTable').DataTable {
          paging: false
          ajax: 
            url: Auth.apiHost + 'user/group/all'
            dataSrc: (data)->
              data
          columns: [
            data: 'id'
            visible: false
          ,
            data: 'name'
          ,
            data: 'userList'
            render:  (data)->
              users = _.map data, (user)->
                "#{user.username || '匿名'}(#{user.loginid})"
              users.splice(0,8).join(', ') + if users.length then '...' else ''
          ]
        }

      $('#groupTable tbody').on 'click', 'tr', ->
        if $(this).hasClass('info')
          $(this).removeClass('info')
          $('#operates button:gt(0)').attr('disabled', 'disabled')
        else
          table.$('tr.info').removeClass('info')
          $(this).addClass('info')
          row = table.row(this).data()

          $('#operates button[disabled]').removeAttr('disabled')

      $('#operates button').unbind('click').bind 'click', ->
        selRow = table.row('.info').data()
        switch $(this).data('action')
          when 'create'
            window.location.hash = 'home/system/groupAdd'
          when 'update'
            localStorage.set 'tmpGroupInfo', selRow
            window.location.hash = "home/system/groupAdd/#{selRow.id}"
          when 'delete'
            reqwest(
              url: "#{Auth.apiHost}user/group/del/#{selRow.id}"
              method: "delete"
              type: "html"
            ).then(->
              table.row('.info').remove().draw()
            ).fail (err)->
              bootbox.alert '组删除失败！'
