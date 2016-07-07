// Generated by CoffeeScript 1.10.0
define(['localStorage', 'loading', 'reqwest', 'bootbox', 'lscache'], function(localStorage, loading, reqwest, bootbox, lscache) {
  var apiHost, isLogining, menus;
  apiHost = '/myQA/';
  isLogining = false;
  menus = {};
  return {
    apiHost: apiHost,
    login: function(userinfo, done) {
      if (isLogining) {
        return;
      }
      isLogining = true;
      $('html').loading({
        message: '正在登录...'
      });
      return reqwest({
        url: apiHost + 'main/login',
        method: 'post',
        data: JSON.stringify(userinfo),
        type: 'html',
        contentType: 'application/json',
        crossOrigin: true
      }).then(function(data) {
        isLogining = false;
        $('html').loading('stop');
        localStorage.set('logined', true);
        userinfo.username = data;
        localStorage.set('user', userinfo);

        /**
         * 16-6-20 优化回调函数
         */
        if (done) {
          return done();
        } else {
          return window.location.hash = '#!home';
        }
      }).fail(function(err) {
        isLogining = false;
        $('html').loading('stop');

        /**
         * 16-6-20 优化回调函数
         */
        if (done) {
          return done(err);
        } else {
          window.location.hash = '!login';
          if (err.responseText) {
            return bootbox.alert(err.responseText);
          }
        }
      });
    },
    logout: function() {
      localStorage.remove('logined');
      localStorage.remove('user');
      localStorage.remove('menu');
      document.cookie = '';
      window.location.hash = '#!login';
      return reqwest({
        url: apiHost + ("main/logout?_=" + (Date.now())),
        type: 'json'
      });
    },
    logined: function() {
      var userinfo;
      if (userinfo = localStorage.get('user')) {
        localStorage.set('user', userinfo);
        localStorage.set('logined', true);
      }
      return Boolean(localStorage.get('logined'));
    },
    user: function() {
      return localStorage.get('user');
    }
  };
});
