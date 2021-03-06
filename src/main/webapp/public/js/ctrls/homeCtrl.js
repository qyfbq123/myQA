// Generated by CoffeeScript 1.10.0
define(['can/control', 'can/view/mustache', 'Auth', 'localStorage', 'reqwest', 'bootbox', '_', 'bootstrap', 'metisMenu'], function(Control, can, Auth, localStorage, reqwest, bootbox) {
  var homePageData, menus;
  menus = {};
  homePageData = new can.Map({
    username: '',
    childMenuName: '工作区域'
  });
  return Control.extend({
    init: function() {
      var failed, success, updateMenus;
      this.element.html(can.view('../public/view/home/home.html', homePageData));
      $('body').removeClass('DHL-backgroud');

      /**
       * 弹出框弹出登录
       */
      $('#loginBtn').click(function() {
        return bootbox.dialog({
          title: "登录",
          message: '<div class="row">  ' + '<div class="col-md-12"> ' + '<form class="form-horizontal"> ' + '<div class="form-group"> ' + '<label class="col-md-4 control-label" for="dialogLoginID">LoginID</label> ' + '<div class="col-md-4"> ' + '<input id="dialogLoginID" name="dialogLoginID" type="text" placeholder="Your loginID" class="form-control input-md"> ' + '</div> ' + '</div> ' + '<div class="form-group"> ' + '<label class="col-md-4 control-label" for="dialogPassword">Password</label> ' + '<div class="col-md-4"> ' + '<input id="dialogPassword" name="dialogPassword" type="password" placeholder="Your password" class="form-control input-md"> ' + '</div> ' + '</div> ' + '</form> </div>  </div>',
          buttons: {
            success: {
              label: "登录",
              className: "btn-success",
              callback: function() {
                var loginID, password;
                loginID = $('#dialogLoginID').val();
                password = $('#dialogPassword').val();
                return Auth.login({
                  loginID: loginID,
                  password: password
                }, function(e) {
                  if (e) {
                    return bootbox.alert(err.responseText);
                  }
                  return window.location.reload();
                });
              }
            }
          }
        });
      });
      success = function(data) {
        if (parseInt(data.status) !== 0) {
          return;
        }
        return genMenu(data.data);
      };
      failed = function(error) {
        return console.error(error);
      };
      updateMenus = function(menus) {
        var fa;
        _.each(menus, function(menu) {
          var $menu;
          $menu = $("#side-menu a[href$='" + menu.url + "']");
          if ($menu.closest('ul').is("#side-menu")) {
            return $menu.closest('li').show();
          } else {
            $menu.closest('ul').closest('li').show();
            return $menu.closest('li').show();
          }
        });
        fa = $("#side-menu a:visible");
        if (window.location.hash === '#!home' && fa.size() > 0) {
          fa[0].click();
          if (fa[0].href.endsWith('#')) {
            setTimeout((function() {
              return $("#side-menu a:visible[href!='#']")[0].click();
            }), 100);
          }
        }
        return reqwest({
          url: Auth.apiHost + "customize/report/allByUser?_=" + (Date.now()),
          method: "get"
        }).then(function(data) {
          return _.each(data, function(e) {
            return $("<li><a href='#!home/report/customizeReport/" + e.id + "'>" + e.name + "</a></li>").appendTo($('#customizeReport'));
          });
        }).fail(function(err) {
          return bootbox.alert("获取自定义报表失败！" + err.responseText);
        });
      };
      reqwest({
        url: Auth.apiHost + ("user/menu?_=" + (Date.now()))
      }).then(updateMenus).fail(function(err) {
        delete can.home;
        return Auth.logout();
      });
      $('#side-menu').metisMenu();
      $("#side-menu>li").hide();
      $("#side-menu ul.nav-second-level>li").hide();
      return $(window).bind("load resize", function() {
        var height, topOffset, width;
        topOffset = 50;
        width = this.window.innerWidth > 0 ? this.window.innerWidth : this.screen.width;
        if (width < 768) {
          $('div.navbar-collapse').addClass('collapse');
          topOffset = 100;
        } else {
          $('div.navbar-collapse').removeClass('collapse');
        }
        height = (this.window.innerHeight > 0 ? this.window.innerHeight : this.screen.height) - 1;
        height = height - topOffset;
        if (height < 1) {
          height = 1;
        }
        if (height > topOffset) {
          return $("#page-wrapper").css("min-height", height + "px");
        }
      });
    },
    showFirstMenu: function() {
      var fa;
      fa = $("#side-menu a:visible");
      if (window.location.hash === '#!home' && fa.size() > 0) {
        return setTimeout((function() {
          return $("#side-menu a:visible[href!='#']")[0].click();
        }), 100);
      }
    }
  });
});
