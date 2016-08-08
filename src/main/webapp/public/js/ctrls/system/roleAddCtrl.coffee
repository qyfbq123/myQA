define ['can/control', 'can', 'Auth', 'base', 'reqwest', 'bootbox', 'localStorage', 'select2cn'], (Ctrl, can, Auth, base, reqwest, bootbox, localStorage)->

  return Ctrl.extend
    init: (el, data)->
      if !can.base
        new base('', data)

      roleInfo = new can.Map
        pageType: '新增'

      this.element.html can.view('../public/view/home/system/roleAdd.html', roleInfo)

      isNew = window.location.hash.endsWith 'roleAdd'
      if !isNew
        roleInfo.attr( 'pageType',  '修改' )
        tmpRole = localStorage.get 'tmpRoleInfo'
        if tmpRole
          for k, v of tmpRole
            roleInfo.attr k, v if !_.isObject v

      genMenuCheck = (menu)->
        $input = $('<input type="checkbox" name="menuList"/>').val(menu.id).prop('checked', !!menu.checked)
        $div = $('<div/>').addClass('checkbox').append $('<label/>').text(menu.name).prepend $input
        $('#menuList').append $div

      reqwest( "#{Auth.apiHost}user/allSysMenus?_=#{Date.now()}").then (menus)->
        _.each menus, (menu)->
          if tmpRole?.menuList
            _.each tmpRole.menuList, (menu0)->
              menu.checked = true if menu.id == menu0.id
          genMenuCheck menu

      $('#operates button').unbind('click').bind 'click', ->
        switch $(this).data 'action'
          when 'save'

            role = $('form').serializeObject()
            role.id = Number role.id

            if role.menuList
              if _.isArray role.menuList
                role.menuList = _.map role.menuList, (m)-> id: Number m
              else
                role.menuList = [ id: Number role.menuList]

            reqwest({
              url: "#{Auth.apiHost}user/role/save"
              method: "post"
              contentType: 'application/json'
              type: 'html'
              data: JSON.stringify role
            }).then(->
              localStorage.remove 'tmpRoleInfo'
              bootbox.alert "角色#{if isNew then '新增' else '修改'}成功！", ->
                window.location.hash = "home/system/roleView"
            ).fail (err)->
              bootbox.alert "角色#{if isNew then '新增' else '修改'}失败！#{err.responseText}"
          when 'refresh'
            if tmpRole
              for k, v of tmpRole
                roleInfo.attr k, v if !_.isObject v

              $('form input:checked').prop 'checked', false
              if tmpRole.menuList
                _.each tmpRole.menuList, (menu)->
                  $("form input[type='checkbox'][value='#{menu.id}']").prop 'checked', true

            else
              for k, v of roleInfo.attr()
                continue if k == 'pageType'
                roleInfo.attr k, 0 if typeof v == 'number'
                roleInfo.attr k, '' if typeof v == 'string'

                $('form input:checked').prop 'checked', false
          when 'cancel'
            window.location.hash = "home/system/roleView"
