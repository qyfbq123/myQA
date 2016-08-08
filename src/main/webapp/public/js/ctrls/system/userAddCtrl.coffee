define ['can/control', 'can', 'Auth', 'base', 'reqwest', 'bootbox', 'localStorage', 'select2cn'], (Ctrl, can, Auth, base, reqwest, bootbox, localStorage)->

  return Ctrl.extend
    init: (el, data)->
      if !can.base
        new base('', data)

      addPageData = new can.Map
        pageType: '新增'

      this.element.html can.view('../public/view/home/system/userAdd.html', addPageData)

      isNew = window.location.hash.endsWith 'userAdd'
      if !isNew
        addPageData.attr( 'pageType',  '修改' )
        tmpUser = localStorage.get 'tmpUserInfo'
        if tmpUser
          for k, v of tmpUser
            addPageData.attr k, v if !_.isObject v

      ###*
       * 16-6-20 用户上级主管下拉框
      ###
      reqwest("#{Auth.apiHost}user/all?_=#{Date.now()}").then (data)->
          data = _.map data, (d)->
            id: d.id, text: d.username || d.loginid
          data.unshift id: 'unselected', text: '请选择'
          if leader = tmpUser?.leader
            _.each data, (l)->
              if l.id == leader.id
                l.selected = true
          $('#leader').select2
            language: 'zh-CN'
            theme: 'bootstrap'
            data: data

      reqwest("#{Auth.apiHost}dict/cities?_=#{Date.now()}").then((data)->
          if city = tmpUser?.city
            _.each data, (c)->
              if c.id == city.id
                c.selected = true
          $('#city').select2 {
            language: 'zh-CN'
            theme: "bootstrap"
            data: data
          }
          $('#city').val(null).change() if tmpUser && !tmpUser?.city
      )

      reqwest("#{Auth.apiHost}user/role/all?_=#{Date.now()}").then((data)->

          roleList = _.map data, (role)->
            id: role.id, text: role.name

          if roles = tmpUser?.roleList
            _.each roleList, (role0)->
              _.each roles, (role1)->
                if role0.id == role1.id
                  role0.selected = true

          $('#roles').select2 {
            language: 'zh-CN'
            theme: "bootstrap"
            data: roleList
            multiple: true
            tokenSeparators: [',', ' ']
          }
      )

      $('#operates button').unbind('click').bind 'click', ->
        switch $(this).data 'action'
          when 'save'

            user = {}
            for k, v of addPageData.attr()
              user[k] = v if k != 'pageType'  and !_.isObject v

            user.id = Number user.id
            ###*
             * 16-6-20 用户上级主管
            ###
            user.leader = id: (if $('#leader').val() == 'unselected' then null else $('#leader').val())
            user.city = id:$('#city').val()

            if $('#roles').val()
              user.roleList = _.map $('#roles').val(), (role)->
                id: role

            reqwest({
              url: "#{Auth.apiHost}user/save"
              method: "post"
              contentType: 'application/json'
              type: 'html'
              data: JSON.stringify user
            }).then(->
              localStorage.remove 'tmpUserInfo'
              bootbox.alert "用户#{if isNew then '新增' else '修改'}成功！", ->
                window.location.hash = "home/system/userView"
            ).fail (err)->
              bootbox.alert "用户#{if isNew then '新增' else '修改'}失败！#{err.responseText}"
          when 'refresh'
            if tmpUser
              for k, v of tmpUser
                addPageData.attr k, v if !_.isObject v

              if tmpUser.city
                $('#city').val(tmpUser.city.id).trigger 'change'

              if tmpUser.roleList
                roles = _.map tmpUser.roleList, (role)-> role.id
                $('#roles').val(roles).trigger 'change'

            else
              for k, v of addPageData.attr()
                continue if k == 'pageType'
                addPageData.attr k, 0 if typeof v == 'number'
                addPageData.attr k, '' if typeof v == 'string'
              $('#city').val(1).trigger 'change'
              $('#roles').val(null).trigger 'change'
          when 'cancel'
            window.location.hash = "home/system/userView"
