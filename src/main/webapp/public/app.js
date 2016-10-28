// Generated by CoffeeScript 1.10.0

/**
 * 使用到的所有组件设定
 */
require.config({
  baseUrl: './lib',
  paths: {

    /**
     * 用到的第三方组件
     */
    _: 'underscore/underscore-min',
    $: 'jquery/dist/jquery.min',
    can: 'CanJS/amd/can',
    loading: 'jquery-loading/dist/jquery.loading.min',
    metisMenu: 'metisMenu/dist/metisMenu.min',
    validate: 'jquery-validation/dist/jquery.validate.min',
    bootstrap: 'bootstrap/dist/js/bootstrap.min',
    'datatables.net': 'datatables.net/js/jquery.dataTables.min',
    'datatables.net-bs': 'datatables.net-bs/js/dataTables.bootstrap.min',
    'datatables.net-responsive': 'datatables.net-responsive/js/dataTables.responsive.min',
    'datatables.net-responsive-bs': 'datatables.net-responsive-bs/js/responsive.bootstrap',
    lscache: 'lscache/lscache.min',
    reqwest: 'reqwest/reqwest.min',
    bootbox: 'bootbox.js/bootbox',
    select2: 'select2/dist/js/select2.min',
    select2cn: 'select2/dist/js/i18n/zh-CN',
    datepicker: 'bootstrap-datepicker/dist/js/bootstrap-datepicker.min',
    datepickercn: 'bootstrap-datepicker/dist/locales/bootstrap-datepicker.zh-CN.min',
    uploader: 'uploadify/jquery.uploadify.min',
    es6shim: 'es6-shim/es6-shim.min',

    /**
     * jquery file upload
     */
    "jquery.ui.widget": 'blueimp-file-upload/js/vendor/jquery.ui.widget',
    jqueryIframeTransport: 'blueimp-file-upload/js/jquery.iframe-transport',
    jqueryFileupload: 'blueimp-file-upload/js/jquery.fileupload',

    /**
     * 工具类
     */
    jqueryEx: '../public/js/servs/jQueryExtend',
    localStorage: '../public/js/servs/local-storage',
    Auth: '../public/js/servs/auth',

    /**
     * 具体网页功能类
     */
    base: '../public/js/ctrls/base',
    homeCtrl: '../public/js/ctrls/homeCtrl',
    loginCtrl: '../public/js/ctrls/loginCtrl',
    userProfileCtrl: '../public/js/ctrls/userProfileCtrl',
    dashboardCtrl: '../public/js/ctrls/dashboard/dashboardCtrl',
    msgDashboardCtrl: '../public/js/ctrls/dashboard/msgDashboardCtrl',
    msgAddCtrl: '../public/js/ctrls/dashboard/msgAddCtrl',
    dashboardListCtrl: '../public/js/ctrls/dashboard/dashboardListCtrl',

    /**
     * 16-6-20 新增事件列表
     */
    pmDashboardListCtrl: '../public/js/ctrls/dashboard/pmDashboardListCtrl',
    pmQuestionInfoCtrl: '../public/js/ctrls/question/pmQuestionInfoCtrl',

    /**
     * 16-6-20 新增事件关闭组件
     */
    pmQuestionCloseCtrl: '../public/js/ctrls/question/pmQuestionCloseCtrl',
    questionAddCtrl: '../public/js/ctrls/question/questionAddCtrl',
    questionViewCtrl: '../public/js/ctrls/question/questionViewCtrl',
    questionHandleCtrl: '../public/js/ctrls/question/questionHandleCtrl',
    userViewCtrl: '../public/js/ctrls/system/userViewCtrl',
    userAddCtrl: '../public/js/ctrls/system/userAddCtrl',
    roleViewCtrl: '../public/js/ctrls/system/roleViewCtrl',
    roleAddCtrl: '../public/js/ctrls/system/roleAddCtrl',
    groupViewCtrl: '../public/js/ctrls/system/groupViewCtrl',
    groupAddCtrl: '../public/js/ctrls/system/groupAddCtrl',
    dictSettingCtrl: '../public/js/ctrls/system/dictSettingCtrl',
    batchImportCtrl: '../public/js/ctrls/system/batchImportCtrl',
    otherCtrl: '../public/js/ctrls/system/otherCtrl',

    /**
     * 图表控件单独列出
     */
    'flot.excanvas': 'Flot/excanvas',
    flot: 'Flot/jquery.flot',
    'flot.pie': 'Flot/jquery.flot.pie',
    'flot.resize': 'Flot/jquery.flot.resize',
    'flot.time': 'Flot/jquery.flot.time',
    'flot.tooltip': 'flot.tooltip/js/jquery.flot.tooltip.min'
  },
  shim: {
    can: ['$', 'jqueryEx'],
    loading: ['$'],
    jqueryEx: ['$'],
    validate: ['$'],
    bootstrap: ['$'],
    'datatables.net': ['$'],
    'datatables.net-bs': ['bootstrap', 'datatables.net'],
    'datatables.net-responsive': ['datatables.net'],
    'datatables.net-responsive-bs': ['datatables.net-bs', 'datatables.net-responsive'],
    bootbox: ['bootstrap'],
    select2cn: ['select2'],
    datepickercn: ['datepicker'],
    uploader: ['$'],
    flot: ['flot.excanvas'],
    'flot.pie': ['flot'],
    'flot.resize': ['flot'],
    'flot.time': ['flot'],
    'flot.tooltip': ['flot'],
    jqueryFileupload: ['$', 'jquery.ui.widget', 'jqueryIframeTransport']
  }
});

require(['can', 'Auth', 'localStorage'], function(can, Auth, localStorage) {
  var Router, validRoute;
  $.ajaxSetup({
    cache: false

    /**
     * 路由跳转时判定登录状态
     */
  });
  validRoute = function(route, p) {
    var st;
    if (!Auth.logined() && route !== 'login' && route.indexOf('home') !== -1) {
      st = setTimeout((function() {
        delete can.home;
        Auth.logout();
        return clearTimeout(st);
      }), 100);
      return false;
    } else if (!route) {
      return window.location.hash = '!home/dashboard';
    }
  };

  /**
   * canjs 路由配置
   */
  Router = can.Control({
    '{can.route} change': function(ev, attr, how, newVal, oldVal) {
      return validRoute(ev.route, 'change');
    },

    /**
     * 登录路由
     */
    'login route': function(data) {
      return require(['loginCtrl'], function(loginCtrl) {
        return new loginCtrl('body', {});
      });
    },

    /**
     * 登出路由
     */
    'logout route': function(data) {
      Auth.logout();
      return delete can.home;
    },

    /**
     * 默认路由为登录
     */
    'route': function() {
      return window.location.hash = '!login';
    },
    'home route': function(data) {
      return require(['base'], function(base) {
        return new base('', {
          id: 'dashboard'
        });
      });
    },
    'home/userProfile route': function(data) {
      return require(['userProfileCtrl', 'base'], function(userProfileCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new userProfileCtrl('#page-wrapper');
      });
    },

    /*
     * 以下为具体路由设定，没什么特别好说的，请对照网站查看
     */
    'home/dashboard route': function(data) {
      return require(['dashboardCtrl', 'base'], function(dashboardCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new dashboardCtrl('#page-wrapper', {
          id: 'dashboard'
        });
      });
    },
    'home/msgDashboard route': function(data) {
      if (!Auth.userIsOnSite()) {
        return location.hash = '#!home/pmDashboardList';
      }
      return require(['msgDashboardCtrl', 'base'], function(msgDashboardCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new msgDashboardCtrl('#page-wrapper', {
          id: 'msgDashboard'
        });
      });
    },
    'home/system/bulletin route': function(data) {
      return require(['msgAddCtrl', 'base'], function(msgAddCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new msgAddCtrl('#page-wrapper', {
          id: 'msgAdd'
        });
      });
    },
    'home/dashboardList route': function(data) {
      return require(['dashboardListCtrl', 'base'], function(dashboardListCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new dashboardListCtrl('#page-wrapper', {
          id: 'dashboardList'
        });
      });
    },

    /**
     * 16-6-20 事件列表
     */
    'home/pmDashboardList route': function(data) {
      return require(['pmDashboardListCtrl', 'base'], function(pmDashboardListCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new pmDashboardListCtrl('#page-wrapper', {
          id: 'pmDashboardList'
        });
      });
    },
    'home/question/pmAdd route': function(data) {
      return require(['pmQuestionInfoCtrl', 'base'], function(pmQuestionInfoCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new pmQuestionInfoCtrl('#page-wrapper');
      });
    },
    'home/question/wmsAdd route': function(data) {
      return require(['questionAddCtrl', 'base'], function(questionAddCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new questionAddCtrl('#page-wrapper', {
          category: 'WMS'
        });
      });
    },
    'home/question/allHandle route': function(data) {
      return require(['questionHandleCtrl', 'base'], function(questionHandleCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new questionHandleCtrl('#page-wrapper', {
          id: 'questionHandle',
          type: 'all'
        });
      });
    },
    'home/question/handle route': function(data) {
      return require(['questionHandleCtrl', 'base'], function(questionHandleCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new questionHandleCtrl('#page-wrapper', {
          id: 'questionHandle'
        });
      });
    },
    'home/question/handle/:number route': function(data) {
      return require(['questionHandleCtrl', 'base'], function(questionHandleCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new questionHandleCtrl('#page-wrapper', {
          id: 'questionHandle',
          number: data.number
        });
      });
    },
    'home/question/pmView route': function(data) {
      return require(['questionViewCtrl', 'base'], function(questionViewCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new questionViewCtrl('#page-wrapper', {
          category: 'PM',
          closed: true
        });
      });
    },
    'home/question/wmsView route': function(data) {
      return require(['questionViewCtrl', 'base'], function(questionViewCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new questionViewCtrl('#page-wrapper', {
          category: 'WMS',
          closed: true
        });
      });
    },
    'home/question/wmsView/:number route': function(data) {
      return require(['questionViewCtrl', 'base'], function(questionViewCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new questionViewCtrl('#page-wrapper', {
          category: 'WMS',
          closed: true,
          number: data.number
        });
      });
    },

    /**
     * 16-6-20 事件关闭单独处理
     * @param  {[type]} data [description]
     * @return {[type]}      [description]
     */
    'home/question/pmClose route': function(data) {
      return require(['pmQuestionCloseCtrl', 'base'], function(pmQuestionCloseCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new pmQuestionCloseCtrl('#page-wrapper', {
          category: 'PM',
          closed: false
        });
      });
    },
    'home/question/pmClose/:number route': function(data) {
      return require(['pmQuestionCloseCtrl', 'base'], function(pmQuestionCloseCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new pmQuestionCloseCtrl('#page-wrapper', {
          number: data.number
        });
      });
    },
    'home/question/wmsClose route': function(data) {
      return require(['questionViewCtrl', 'base'], function(questionViewCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new questionViewCtrl('#page-wrapper', {
          category: 'WMS',
          closed: false
        });
      });
    },
    'home/question/wmsClose/:number route': function(data) {
      return require(['questionViewCtrl', 'base'], function(questionViewCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new questionViewCtrl('#page-wrapper', {
          category: 'WMS',
          closed: false,
          number: data.number
        });
      });
    },
    'home/system/userView route': function(data) {
      return require(['userViewCtrl', 'base'], function(userViewCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new userViewCtrl('#page-wrapper', {
          id: 'userView'
        });
      });
    },
    'home/system/userAdd route': function(data) {
      return require(['userAddCtrl', 'base'], function(userAddCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new userAddCtrl('#page-wrapper', {
          id: 'userView'
        });
      });
    },
    'home/system/userAdd/:id route': function(data) {
      return require(['userAddCtrl', 'base'], function(userAddCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new userAddCtrl('#page-wrapper', {
          id: 'userView'
        });
      });
    },
    'home/system/roleView route': function(data) {
      return require(['roleViewCtrl', 'base'], function(roleViewCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new roleViewCtrl('#page-wrapper', {
          id: 'roleView'
        });
      });
    },
    'home/system/roleAdd route': function(data) {
      return require(['roleAddCtrl', 'base'], function(roleAddCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new roleAddCtrl('#page-wrapper', {
          id: 'roleAdd'
        });
      });
    },
    'home/system/roleAdd/:id route': function(data) {
      return require(['roleAddCtrl', 'base'], function(roleAddCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new roleAddCtrl('#page-wrapper', {
          id: 'roleAdd'
        });
      });
    },
    'home/system/groupView route': function(data) {
      return require(['groupViewCtrl', 'base'], function(groupViewCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new groupViewCtrl('#page-wrapper', {
          id: 'groupView'
        });
      });
    },
    'home/system/groupAdd route': function(data) {
      return require(['groupAddCtrl', 'base'], function(groupAddCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new groupAddCtrl('#page-wrapper', {
          id: 'groupAdd'
        });
      });
    },
    'home/system/groupAdd/:id route': function(data) {
      return require(['groupAddCtrl', 'base'], function(groupAddCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new groupAddCtrl('#page-wrapper', {
          id: 'groupAdd'
        });
      });
    },
    'home/system/dictSetting route': function(data) {
      return require(['dictSettingCtrl', 'base'], function(dictSettingCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new dictSettingCtrl('#page-wrapper', {
          id: 'dictSetting'
        });
      });
    },
    'home/system/batchImport route': function(data) {
      return require(['batchImportCtrl', 'base'], function(batchImportCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new batchImportCtrl('#page-wrapper', {
          id: 'batchImport'
        });
      });
    },
    'home/system/other route': function(data) {
      return require(['otherCtrl', 'base'], function(otherCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new otherCtrl('#page-wrapper', {
          id: 'batchImport'
        });
      });
    },
    'home/:id route': function(data) {
      return require(['base'], function(base) {
        return new base('', data);
      });
    },
    'question/:category/:id route': function(data) {
      if (data.category === 'PM') {

        /**
         * 16-6-20 事件处理单独列出
         */
        return require(['pmQuestionCloseCtrl', 'base'], function(pmQuestionCloseCtrl, base) {
          if (Auth.logined()) {
            if (!can.base) {
              new base('', data);
            }
            return window.location.hash = "home/question/" + data.category + "/" + data.id;
          } else {
            return new pmQuestionCloseCtrl('body', {
              id: data.id
            });
          }
        });
      } else {
        return require(['questionAddCtrl', 'base'], function(questionAddCtrl, base) {
          if (Auth.logined()) {
            if (!can.base) {
              new base('', data);
            }
            return window.location.hash = "home/question/" + data.category + "/" + data.id;
          } else {
            return new questionAddCtrl('body', {
              id: data.id,
              category: data.category
            });
          }
        });
      }
    },
    'home/question/:category/:id route': function(data) {
      if (data.category === 'PM') {

        /**
         * 16-6-20 事件处理单独列出
         */
        return require(['pmQuestionCloseCtrl', 'base'], function(pmQuestionCloseCtrl, base) {
          if (!can.base) {
            new base('', data);
          }
          return new pmQuestionCloseCtrl('#page-wrapper', {
            id: data.id
          });
        });
      } else {
        return require(['questionAddCtrl', 'base'], function(questionAddCtrl, base) {
          if (!can.base) {
            new base('', data);
          }
          return new questionAddCtrl('#page-wrapper', {
            id: data.id,
            category: data.category
          });
        });
      }
    },
    'home/question/tmsAdd route': function(data) {
      return require(['questionAddCtrl', 'base'], function(questionAddCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new questionAddCtrl('#page-wrapper', {
          category: 'TMS'
        });
      });
    },
    'home/question/tmsView route': function(data) {
      return require(['questionViewCtrl', 'base'], function(questionViewCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new questionViewCtrl('#page-wrapper', {
          category: 'TMS',
          closed: true
        });
      });
    },
    'home/question/tmsView/:number route': function(data) {
      return require(['questionViewCtrl', 'base'], function(questionViewCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new questionViewCtrl('#page-wrapper', {
          category: 'TMS',
          closed: true,
          number: data.number
        });
      });
    },
    'home/question/tmsClose route': function(data) {
      return require(['questionViewCtrl', 'base'], function(questionViewCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new questionViewCtrl('#page-wrapper', {
          category: 'TMS',
          closed: false
        });
      });
    },
    'home/question/tmsClose/:number route': function(data) {
      return require(['questionViewCtrl', 'base'], function(questionViewCtrl, base) {
        if (!can.base) {
          new base('', data);
        }
        return new questionViewCtrl('#page-wrapper', {
          category: 'TMS',
          closed: false,
          number: data.number
        });
      });
    }
  });
  new Router(window);
  return can.route.ready();
});

String.prototype.endsWith = function(flag) {
  if (flag == null) {
    flag = '';
  }
  return this.indexOf(flag) + flag.length === this.length;
};
