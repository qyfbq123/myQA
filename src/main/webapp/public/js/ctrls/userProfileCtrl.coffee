define ['can/control', 'can', 'Auth', 'base', 'reqwest', 'bootbox'], (Ctrl, can, Auth, base, reqwest, bootbox)->

  return Ctrl.extend
    init: (el, data)->
      if !can.base
        new base('', data)

      this.element.html can.view('../public/view/home/userProfile.html')

      user = Auth.user()

      $('#loginid').text user.loginID
      $('#username').text user.username

      $('#submitBtn').click ->
        newpwd = $('#newpwd').val()
        confirmNewpwd = $('#confirmNewpwd').val()

        if !newpwd
          return bootbox.alert '新密码不能为空!'
        else if newpwd != confirmNewpwd
          return bootbox.alert '前后密码不一致!'
        else
          reqwest({
            url: "#{Auth.apiHost}user/pwd/update"
            method: "post"
            type: 'html'
            data: (newpwd: newpwd)
          }).then(->
            bootbox.alert '密码修改成功！', ->
              $('#newpwd, #confirmNewpwd').val ''
          ).fail ->
            bootbox.alert '密码修改失败！'