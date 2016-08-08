define ['can/control', 'can', 'Auth', 'base', 'reqwest', 'localStorage', 'bootbox', 'datatables.net', 'datatables.net-bs', 'datatables.net-responsive', 'datatables.net-responsive-bs'], (Ctrl, can, Auth, base, reqwest, localStorage, bootbox)->
  return Ctrl.extend
    init: (el, data)->
      if !can.base
        new base('', data)

      this.element.html can.view('../public/view/home/system/roleView.html')

      table = $('#roleTable').DataTable {
          paging: false
          ajax: 
            url: Auth.apiHost + 'user/role/all'
            dataSrc: (data)->
              data
          columns: [
            data: 'id'
            visible: false
          ,
            data: 'name'
          ,
            data: 'menuList'
            render:  (data)->
              menus = ''
              for k, v of data
                menus += "#{v.name}, "
                if k >= 10
                  menus = menus.substring(0, menus.length-2) + '...'
                  break
              menus
          ]
        }

      $('#roleTable tbody').on 'click', 'tr', ->
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
            window.location.hash = 'home/system/roleAdd'
          when 'update'
            localStorage.set 'tmpRoleInfo', selRow
            window.location.hash = "home/system/roleAdd/#{selRow.id}"
          when 'delete'
            reqwest(
              url: "#{Auth.apiHost}user/role/del/#{selRow.id}"
              method: "delete"
              type: "html"
            ).then(->
              table.row('.info').remove().draw()
            ).fail (err)->
              bootbox.alert '角色删除失败！'
