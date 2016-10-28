###*
 * 使用到的所有组件设定
###
require.config
  baseUrl: './lib'
  paths:
    ###*
     * 用到的第三方组件
    ###
    _: 'underscore/underscore-min'
    $: 'jquery/dist/jquery.min'
    can: 'CanJS/amd/can'
    loading: 'jquery-loading/dist/jquery.loading.min'
    metisMenu: 'metisMenu/dist/metisMenu.min'
    validate: 'jquery-validation/dist/jquery.validate.min'
    bootstrap: 'bootstrap/dist/js/bootstrap.min'
    'datatables.net': 'datatables.net/js/jquery.dataTables.min'
    'datatables.net-bs': 'datatables.net-bs/js/dataTables.bootstrap.min'
    'datatables.net-responsive': 'datatables.net-responsive/js/dataTables.responsive.min'
    'datatables.net-responsive-bs': 'datatables.net-responsive-bs/js/responsive.bootstrap'
    lscache: 'lscache/lscache.min'
    reqwest: 'reqwest/reqwest.min'
    bootbox: 'bootbox.js/bootbox'
    select2: 'select2/dist/js/select2.min'
    select2cn: 'select2/dist/js/i18n/zh-CN'
    datepicker: 'bootstrap-datepicker/dist/js/bootstrap-datepicker.min'
    datepickercn: 'bootstrap-datepicker/dist/locales/bootstrap-datepicker.zh-CN.min'
    uploader: 'uploadify/jquery.uploadify.min'
    es6shim: 'es6-shim/es6-shim.min'

    ###*
     * jquery file upload
    ###
    "jquery.ui.widget": 'blueimp-file-upload/js/vendor/jquery.ui.widget'
    jqueryIframeTransport: 'blueimp-file-upload/js/jquery.iframe-transport'
    jqueryFileupload: 'blueimp-file-upload/js/jquery.fileupload'

    ###*
     * 工具类
    ###
    jqueryEx: '../public/js/servs/jQueryExtend'
    localStorage: '../public/js/servs/local-storage'
    Auth: '../public/js/servs/auth'

    ###*
     * 具体网页功能类
    ###
    base: '../public/js/ctrls/base'
    homeCtrl: '../public/js/ctrls/homeCtrl'
    loginCtrl: '../public/js/ctrls/loginCtrl'
    userProfileCtrl: '../public/js/ctrls/userProfileCtrl'

    dashboardCtrl: '../public/js/ctrls/dashboard/dashboardCtrl'
    msgDashboardCtrl: '../public/js/ctrls/dashboard/msgDashboardCtrl'
    msgAddCtrl: '../public/js/ctrls/dashboard/msgAddCtrl'

    dashboardListCtrl: '../public/js/ctrls/dashboard/dashboardListCtrl'
    ###*
     * 16-6-20 新增事件列表
    ###
    pmDashboardListCtrl: '../public/js/ctrls/dashboard/pmDashboardListCtrl'

    pmQuestionInfoCtrl: '../public/js/ctrls/question/pmQuestionInfoCtrl'
    ###*
     * 16-6-20 新增事件关闭组件
    ###
    pmQuestionCloseCtrl: '../public/js/ctrls/question/pmQuestionCloseCtrl'
    questionAddCtrl: '../public/js/ctrls/question/questionAddCtrl'
    questionViewCtrl: '../public/js/ctrls/question/questionViewCtrl'

    questionHandleCtrl: '../public/js/ctrls/question/questionHandleCtrl'

    userViewCtrl: '../public/js/ctrls/system/userViewCtrl'
    userAddCtrl: '../public/js/ctrls/system/userAddCtrl'
    roleViewCtrl: '../public/js/ctrls/system/roleViewCtrl'
    roleAddCtrl: '../public/js/ctrls/system/roleAddCtrl'
    groupViewCtrl: '../public/js/ctrls/system/groupViewCtrl'
    groupAddCtrl: '../public/js/ctrls/system/groupAddCtrl'
    dictSettingCtrl: '../public/js/ctrls/system/dictSettingCtrl'
    batchImportCtrl: '../public/js/ctrls/system/batchImportCtrl'
    otherCtrl: '../public/js/ctrls/system/otherCtrl'

    ###*
     * 图表控件单独列出
    ###
    'flot.excanvas': 'Flot/excanvas'
    flot: 'Flot/jquery.flot'
    'flot.pie': 'Flot/jquery.flot.pie'
    'flot.resize': 'Flot/jquery.flot.resize'
    'flot.time': 'Flot/jquery.flot.time'
    'flot.tooltip': 'flot.tooltip/js/jquery.flot.tooltip.min'


  shim:
    can: ['$', 'jqueryEx']
    loading: ['$']
    jqueryEx: ['$']
    validate: ['$']
    bootstrap: ['$']
    'datatables.net': ['$']
    'datatables.net-bs': ['bootstrap', 'datatables.net']
    'datatables.net-responsive': ['datatables.net']
    'datatables.net-responsive-bs': ['datatables.net-bs', 'datatables.net-responsive']
    bootbox: ['bootstrap']
    select2cn: ['select2']
    datepickercn: ['datepicker']
    uploader: ['$']

    flot: ['flot.excanvas']
    'flot.pie': ['flot']
    'flot.resize': ['flot']
    'flot.time': ['flot']
    'flot.tooltip': ['flot']

    jqueryFileupload: ['$', 'jquery.ui.widget', 'jqueryIframeTransport']


require ['can', 'Auth', 'localStorage'], (can, Auth, localStorage)->
  $.ajaxSetup cache:false

  ###*
   * 路由跳转时判定登录状态
  ###
  validRoute = (route, p)->
    if !Auth.logined() && route != 'login' && route.indexOf('home') != -1
      st = setTimeout (->delete can.home; Auth.logout(); clearTimeout st), 100
      return false
    else if !route
      window.location.hash = '!home/dashboard'

  ###*
   * canjs 路由配置
  ###
  Router = can.Control({
    '{can.route} change': (ev, attr, how, newVal, oldVal)->
      validRoute ev.route, 'change'

    ###*
     * 登录路由
    ###
    'login route': (data)->
      require ['loginCtrl'], (loginCtrl)->
        new loginCtrl('body', {})
    ###*
     * 登出路由
    ###
    'logout route': (data)->
      Auth.logout()
      delete can.home
    ###*
     * 默认路由为登录
    ###
    'route': ()->
      window.location.hash = '!login'
    'home route': (data)->
      require ['base'], (base)->
        new base('', {id:'dashboard'})

    'home/userProfile route': (data)->
      require ['userProfileCtrl', 'base'], (userProfileCtrl, base)->
        new base('', data) if !can.base
        new userProfileCtrl('#page-wrapper')

    ###
     * 以下为具体路由设定，没什么特别好说的，请对照网站查看
    ###
    'home/dashboard route': (data)->
      require ['dashboardCtrl', 'base'], (dashboardCtrl, base)->
        new base('', data) if !can.base
        new dashboardCtrl('#page-wrapper', {id:'dashboard'})
    'home/msgDashboard route': (data)->
      return location.hash = '#!home/pmDashboardList' if !Auth.userIsOnSite()
      require ['msgDashboardCtrl', 'base'], (msgDashboardCtrl, base)->
        new base('', data) if !can.base
        new msgDashboardCtrl('#page-wrapper', {id:'msgDashboard'})
    'home/system/bulletin route': (data)->
      require ['msgAddCtrl', 'base'], (msgAddCtrl, base)->
        new base('', data) if !can.base
        new msgAddCtrl('#page-wrapper', {id:'msgAdd'})

    'home/dashboardList route': (data)->
      require ['dashboardListCtrl', 'base'], (dashboardListCtrl, base)->
        new base('', data) if !can.base
        new dashboardListCtrl('#page-wrapper', {id:'dashboardList'})

    ###*
     * 16-6-20 事件列表
    ###
    'home/pmDashboardList route': (data)->
      require ['pmDashboardListCtrl', 'base'], (pmDashboardListCtrl, base)->
        new base('', data) if !can.base
        new pmDashboardListCtrl('#page-wrapper', {id:'pmDashboardList'})

    'home/question/pmAdd route': (data)->
      require ['pmQuestionInfoCtrl', 'base'], (pmQuestionInfoCtrl, base)->
        new base('', data) if !can.base
        new pmQuestionInfoCtrl('#page-wrapper')
    'home/question/wmsAdd route': (data)->
      require ['questionAddCtrl', 'base'], (questionAddCtrl, base)->
        new base('', data) if !can.base
        new questionAddCtrl('#page-wrapper', {category:'WMS'})

    'home/question/allHandle route': (data)->
      require ['questionHandleCtrl', 'base'], (questionHandleCtrl, base)->
        new base('', data) if !can.base
        new questionHandleCtrl('#page-wrapper', {id: 'questionHandle', type: 'all'})

    'home/question/handle route': (data)->
      require ['questionHandleCtrl', 'base'], (questionHandleCtrl, base)->
        new base('', data) if !can.base
        new questionHandleCtrl('#page-wrapper', {id: 'questionHandle'})
    'home/question/handle/:number route': (data)->
      require ['questionHandleCtrl', 'base'], (questionHandleCtrl, base)->
        new base('', data) if !can.base
        new questionHandleCtrl('#page-wrapper', {id: 'questionHandle', number: data.number})

    'home/question/pmView route': (data)->
      require ['questionViewCtrl', 'base'], (questionViewCtrl, base)->
        new base('', data) if !can.base
        new questionViewCtrl('#page-wrapper', {category:'PM', closed: true})
    'home/question/wmsView route': (data)->
      require ['questionViewCtrl', 'base'], (questionViewCtrl, base)->
        new base('', data) if !can.base
        new questionViewCtrl('#page-wrapper', {category:'WMS', closed: true})
    'home/question/wmsView/:number route': (data)->
      require ['questionViewCtrl', 'base'], (questionViewCtrl, base)->
        new base('', data) if !can.base
        new questionViewCtrl('#page-wrapper', {category:'WMS', closed: true, number: data.number})

    ###*
     * 16-6-20 事件关闭单独处理
     * @param  {[type]} data [description]
     * @return {[type]}      [description]
    ###
    'home/question/pmClose route': (data)->
      require ['pmQuestionCloseCtrl', 'base'], (pmQuestionCloseCtrl, base)->
        new base('', data) if !can.base
        new pmQuestionCloseCtrl('#page-wrapper', {category:'PM', closed: false})
    'home/question/pmClose/:number route': (data)->
      require ['pmQuestionCloseCtrl', 'base'], (pmQuestionCloseCtrl, base)->
        new base('', data) if !can.base
        new pmQuestionCloseCtrl('#page-wrapper', {number: data.number})

    'home/question/wmsClose route': (data)->
      require ['questionViewCtrl', 'base'], (questionViewCtrl, base)->
        new base('', data) if !can.base
        new questionViewCtrl('#page-wrapper', {category:'WMS', closed: false})
    'home/question/wmsClose/:number route': (data)->
      require ['questionViewCtrl', 'base'], (questionViewCtrl, base)->
        new base('', data) if !can.base
        new questionViewCtrl('#page-wrapper', {category:'WMS', closed: false, number: data.number})

    'home/system/userView route': (data)->
      require ['userViewCtrl', 'base'], (userViewCtrl, base)->
        new base('', data) if !can.base
        new userViewCtrl('#page-wrapper', {id:'userView'})
    'home/system/userAdd route': (data)->
      require ['userAddCtrl', 'base'], (userAddCtrl, base)->
        new base('', data) if !can.base
        new userAddCtrl('#page-wrapper', {id:'userView'})
    'home/system/userAdd/:id route': (data)->
      require ['userAddCtrl', 'base'], (userAddCtrl, base)->
        new base('', data) if !can.base
        new userAddCtrl('#page-wrapper', {id:'userView'})

    'home/system/roleView route': (data)->
      require ['roleViewCtrl', 'base'], (roleViewCtrl, base)->
        new base('', data) if !can.base
        new roleViewCtrl('#page-wrapper', {id:'roleView'})
    'home/system/roleAdd route': (data)->
      require ['roleAddCtrl', 'base'], (roleAddCtrl, base)->
        new base('', data) if !can.base
        new roleAddCtrl('#page-wrapper', {id:'roleAdd'})
    'home/system/roleAdd/:id route': (data)->
      require ['roleAddCtrl', 'base'], (roleAddCtrl, base)->
        new base('', data) if !can.base
        new roleAddCtrl('#page-wrapper', {id:'roleAdd'})

    'home/system/groupView route': (data)->
      require ['groupViewCtrl', 'base'], (groupViewCtrl, base)->
        new base('', data) if !can.base
        new groupViewCtrl('#page-wrapper', {id:'groupView'})
    'home/system/groupAdd route': (data)->
      require ['groupAddCtrl', 'base'], (groupAddCtrl, base)->
        new base('', data) if !can.base
        new groupAddCtrl('#page-wrapper', {id:'groupAdd'})
    'home/system/groupAdd/:id route': (data)->
      require ['groupAddCtrl', 'base'], (groupAddCtrl, base)->
        new base('', data) if !can.base
        new groupAddCtrl('#page-wrapper', {id:'groupAdd'})

    'home/system/dictSetting route': (data)->
      require ['dictSettingCtrl', 'base'], (dictSettingCtrl, base)->
        new base('', data) if !can.base
        new dictSettingCtrl('#page-wrapper', {id:'dictSetting'})

    'home/system/batchImport route': (data)->
      require ['batchImportCtrl', 'base'], (batchImportCtrl, base)->
        new base('', data) if !can.base
        new batchImportCtrl('#page-wrapper', {id:'batchImport'})

    'home/system/other route': (data)->
      require ['otherCtrl', 'base'], (otherCtrl, base)->
        new base('', data) if !can.base
        new otherCtrl('#page-wrapper', {id:'batchImport'})

    'home/:id route': (data)->
      require ['base'], (base)->
        new base('', data)

    'question/:category/:id route': (data)->
      if data.category == 'PM'
        ###*
         * 16-6-20 事件处理单独列出
        ###
        require ['pmQuestionCloseCtrl', 'base'], (pmQuestionCloseCtrl, base)->
          if Auth.logined()
            new base('', data) if !can.base
            window.location.hash = "home/question/#{data.category}/#{data.id}"
          else new pmQuestionCloseCtrl('body', {id: data.id})
      else
        require ['questionAddCtrl', 'base'], (questionAddCtrl, base)->
          if Auth.logined()
            new base('', data) if !can.base
            window.location.hash = "home/question/#{data.category}/#{data.id}"
          else new questionAddCtrl('body', {id: data.id, category: data.category})
    'home/question/:category/:id route': (data)->
      if data.category == 'PM'
        ###*
         * 16-6-20 事件处理单独列出
        ###
        require ['pmQuestionCloseCtrl', 'base'], (pmQuestionCloseCtrl, base)->
          new base('', data) if !can.base
          new pmQuestionCloseCtrl('#page-wrapper', {id: data.id})
      else
        require ['questionAddCtrl', 'base'], (questionAddCtrl, base)->
          new base('', data) if !can.base
          new questionAddCtrl('#page-wrapper', {id: data.id, category: data.category})

    'home/question/tmsAdd route': (data)->
      require ['questionAddCtrl', 'base'], (questionAddCtrl, base)->
        new base('', data) if !can.base
        new questionAddCtrl('#page-wrapper', {category:'TMS'})
    'home/question/tmsView route': (data)->
      require ['questionViewCtrl', 'base'], (questionViewCtrl, base)->
        new base('', data) if !can.base
        new questionViewCtrl('#page-wrapper', {category:'TMS', closed: true})
    'home/question/tmsView/:number route': (data)->
      require ['questionViewCtrl', 'base'], (questionViewCtrl, base)->
        new base('', data) if !can.base
        new questionViewCtrl('#page-wrapper', {category:'TMS', closed: true, number: data.number})
    'home/question/tmsClose route': (data)->
      require ['questionViewCtrl', 'base'], (questionViewCtrl, base)->
        new base('', data) if !can.base
        new questionViewCtrl('#page-wrapper', {category:'TMS', closed: false})
    'home/question/tmsClose/:number route': (data)->
      require ['questionViewCtrl', 'base'], (questionViewCtrl, base)->
        new base('', data) if !can.base
        new questionViewCtrl('#page-wrapper', {category:'TMS', closed: false, number: data.number})
  })

  new Router(window)

  can.route.ready()

String.prototype.endsWith = (flag = '')->
  return @indexOf(flag) + flag.length == @length
