// Generated by CoffeeScript 1.10.0
define(['can/control', 'can/view/mustache', 'base', 'Auth', '_', 'flot.excanvas', 'flot', 'flot.pie', 'flot.resize', 'flot.time', 'flot.tooltip'], function(Control, can, base, Auth) {
  var pageData;
  pageData = new can.Map();
  return Control.extend({
    init: function(el, data) {
      pageData.attr('userIsOnSite', Auth.userIsOnSite());
      pageData.attr('userIsContract', Auth.userIsContract());
      return this.element.html(can.view('../public/view/home/dashboard/dashboard.html', pageData));
    }
  });
});
