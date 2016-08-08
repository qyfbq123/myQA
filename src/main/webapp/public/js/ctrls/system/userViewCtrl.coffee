define ['can/control', 'can', 'Auth', 'base', 'reqwest', 'localStorage', 'bootbox', 'datatables.net', 'datatables.net-bs', 'datatables.net-responsive', 'datatables.net-responsive-bs'], (Ctrl, can, Auth, base, reqwest, localStorage, bootbox)->
  return Ctrl.extend
    init: (el, data)->
      if !can.base
        new base('', data)

      this.element.html can.view('../public/view/home/system/userView.html')

      table = $('#userTable').DataTable {
          paging: false
          ajax: 
            url: Auth.apiHost + 'user/all'
            dataSrc: (data)->
              data
          columns: [
            data: 'id'
            visible: false
          ,
            data: 'loginid'
          ,
            data: 'password'
          ,
            data: 'username'
          ,
            data: 'email'
          ,
            data: 'leader'
            render: (data)->
              if data
                data.username || data.loginid
              else
                ''
          , 
            data: 'roleList'
            render:  '[, ].name'
          ,
            data: 'city'
            render: (data)->
              data?.name || ''
          ,
            data: 'locked'
            render: (data)->
              if data
                '禁用'
              else
                '启用'
          ]
        }

      $('#userTable tbody').on 'click', 'tr', ->
        if $(this).hasClass('info')
          $(this).removeClass('info')
          $('#operates button:gt(0)').attr('disabled', 'disabled')
        else
          table.$('tr.info').removeClass('info')
          $(this).addClass('info')
          row = table.row(this).data()
          $('#operates button[data-action="toggle"] font').text if row.locked then '启用' else '禁用'

          $('#operates button[disabled]').removeAttr('disabled')

      $('#operates button').unbind('click').bind 'click', ->
        selRow = table.row('.info').data()
        switch $(this).data('action')
          when 'create'
            window.location.hash = 'home/system/userAdd'
          when 'update'
            localStorage.set 'tmpUserInfo', selRow
            window.location.hash = "home/system/userAdd/#{selRow.id}"
          when 'delete'
            reqwest(
              url: "#{Auth.apiHost}user/del/#{selRow.id}"
              method: "delete"
              type: "html"
            ).then(->
              table.row('.info').remove().draw()
              $('#operates button:gt(0)').attr('disabled', 'disabled')
            ).fail (err)->
              bootbox.alert '用户删除失败！'
            
          when 'toggle'
            reqwest( {
              url: "#{Auth.apiHost}user/toggle/#{selRow.id}"
              method: "put"
              type: "html"
            }).then(->
              selRow.locked = !selRow.locked
              $('#operates button[data-action="toggle"] font').text if selRow.locked then '启用' else '禁用'
              table.row(table.$('tr.info')).data(selRow)
            ).fail (err)->
              bootbox.alert '用户启用／禁用失败！'
