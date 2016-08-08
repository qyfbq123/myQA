
define ['can/control', 'can/view/mustache', 'Auth', 'localStorage', 'reqwest', 'bootbox', '_', 'bootstrap', 'metisMenu'], (Control, can, Auth, localStorage, reqwest, bootbox)->
  menus = {}
  homePageData = new can.Map
    username: ''
    childMenuName: '工作区域'

  return Control.extend
    init: ()->
      this.element.html can.view('../public/view/home/home.html', homePageData)

      $('body').removeClass 'DHL-backgroud'

      ###*
       * 弹出框弹出登录
      ###
      $('#loginBtn').click ->
        bootbox.dialog
          title: "登录",
          message: '<div class="row">  ' +
              '<div class="col-md-12"> ' +
              '<form class="form-horizontal"> ' +
              '<div class="form-group"> ' +
              '<label class="col-md-4 control-label" for="dialogLoginID">LoginID</label> ' +
              '<div class="col-md-4"> ' +
              '<input id="dialogLoginID" name="dialogLoginID" type="text" placeholder="Your loginID" class="form-control input-md"> ' +
              '</div> ' +
              '</div> ' +
              '<div class="form-group"> ' +
              '<label class="col-md-4 control-label" for="dialogPassword">Password</label> ' +
              '<div class="col-md-4"> ' +
              '<input id="dialogPassword" name="dialogPassword" type="password" placeholder="Your password" class="form-control input-md"> ' +
              '</div> ' +
              '</div> ' +
              '</form> </div>  </div>',
          buttons:
              success: 
                  label: "登录",
                  className: "btn-success",
                  callback: ->
                      loginID = $('#dialogLoginID').val()
                      password = $('#dialogPassword').val()
                      Auth.login (loginID: loginID, password: password), (e)->
                        return bootbox.alert err.responseText if e
                        window.location.reload()
      
      success = (data)->
        if parseInt(data.status) != 0
          # jAlert('获取菜单失败 ' + data.message, '错误', -> Auth.logout());
          return;
        genMenu(data.data);
      failed = (error)->
        console.error error
      # $.getJSON Auth.apiHost + 'user/menu', {}, success, failed

      updateMenus = (menus)->
        _.each menus, (menu)->
          $menu = $("#side-menu a[href$='#{menu.url}']")
          if $menu.closest('ul').is("#side-menu")
            $menu.closest('li').show()
          else
            $menu.closest('ul').closest('li').show()
            $menu.closest('li').show()

        fa = $("#side-menu a:visible")
        if window.location.hash == '#!home' and fa.size() > 0
          fa[0].click()
          if fa[0].href.endsWith '#'
            setTimeout (->$("#side-menu a:visible[href!='#']")[0].click()), 100

      reqwest({
        url: Auth.apiHost + "user/menu?_=#{Date.now()}"
      }).then( updateMenus).fail (err)->
        # bootbox.alert '登录超时，请重新登录!' + err.responseText, ->
        delete can.home
        Auth.logout()

      $('#side-menu').metisMenu()
      $("#side-menu>li").hide();
      $("#side-menu ul.nav-second-level>li").hide();

      $(window).bind "load resize", ()->
        topOffset = 50
        width = if (this.window.innerWidth > 0) then this.window.innerWidth else this.screen.width
        if width < 768
            $('div.navbar-collapse').addClass('collapse')
            topOffset = 100
        else
            $('div.navbar-collapse').removeClass('collapse')

        height = (if (this.window.innerHeight > 0) then this.window.innerHeight else this.screen.height) - 1
        height = height - topOffset
        if height < 1
          height = 1
        if height > topOffset
          $("#page-wrapper").css "min-height", (height) + "px"

    showFirstMenu: ->
      fa = $("#side-menu a:visible")
      if window.location.hash == '#!home' and fa.size() > 0
        setTimeout (->$("#side-menu a:visible[href!='#']")[0].click()), 100
