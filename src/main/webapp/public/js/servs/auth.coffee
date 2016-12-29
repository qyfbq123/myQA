
define ['localStorage', 'loading', 'reqwest', 'bootbox', 'lscache'], (localStorage, loading, reqwest, bootbox, lscache)->
  # apiHost = 'http://192.168.1.4:8080/';
  apiHost = '/myQA/';
  # apiHost = '/'
  isLogining = false

  menus = {}

  return {
    apiHost: apiHost
    login: (userinfo, done)->
      return if isLogining
      isLogining = true
      $('html').loading({message:'正在登录...'});

      reqwest({
        url: apiHost + 'main/login',
        method: 'post',
        data: JSON.stringify(userinfo),
        type: 'json',
        contentType: 'application/json',
        crossOrigin: true
      }).then( (data)->
        isLogining = false
        $('html').loading('stop');
        localStorage.set('logined', true);
        userinfo = _.extend userinfo, data
        localStorage.set('user', userinfo);

        ###*
         * 16-6-20 优化回调函数
        ###
        if done then done() else window.location.hash = '#!home'
      ).fail( (err)->
        isLogining = false
        $('html').loading('stop');
        ###*
         * 16-6-20 优化回调函数
        ###
        if done
          done(err)
        else
          window.location.hash = '!login'
          switch err.status
            when 404 then bootbox.alert '该用户名不存在'
            when 403 then bootbox.alert '该用户已被禁用'
            when 400 then bootbox.alert '用户名或密码错误'
            else bootbox.alert '登录失败'
          # if err.responseText
          #   bootbox.alert err.responseText
          # jAlert('http error:' + status + ' ' + data.statusText, '登录失败');
      )

    logout: ()->
      localStorage.remove('logined');
      localStorage.remove('user');

      localStorage.remove('menu');
      document.cookie = ''
      window.location.hash = '#!login'
      reqwest({
        url: apiHost + "main/logout?_=#{Date.now()}"
        type: 'json'
      })
    logined: ()->
      if userinfo = localStorage.get 'user'
        localStorage.set 'user', userinfo
        localStorage.set 'logined', true
        
      return Boolean(localStorage.get('logined'));
    user: ()->
      return localStorage.get('user');
    userIsOnSite: ()->
      user = localStorage.get('user')
      if user and user.roleList
        return user.roleList.map((e)-> e.name).indexOf('On-Site') != -1
      else
        false
    userIsContract: ()->
      user = localStorage.get('user')
      if user and user.groupList
        return user.groupList.map((e)-> e.name).indexOf('合同') != -1
      else
        false
  }
