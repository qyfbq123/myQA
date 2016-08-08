
define ['can/control', 'can/view/mustache', 'Auth', 'lscache', 'validate'], (Control, can, Auth, lscache)->
  if Auth.logined()
    window.location.hash = '!home'

  userInfo = new can.Map
    loginID: 'admin'
    password: 'admin'

  return Control.extend
    init: ()->

      this.element.html can.view('../public/view/login.html', userInfo)

      $('body').addClass 'DHL-backgroud'

      # $('input.login').keyup (e)->
      #   Auth.login userInfo.attr() if e.keyCode == 13

      $('.login_button').unbind('click').bind 'click', ()->
        return if !$('form').valid()
        Auth.login userInfo.attr()
        userInfo.attr 'password', ''

      $('.reset_botton').unbind('click').bind 'click', ()->
        userInfo.attr 'loginID', ''
        userInfo.attr 'password', ''
