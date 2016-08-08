define ['can/control', 'can/view/mustache', 'base', 'Auth', '_', 'flot.excanvas', 'flot', 'flot.pie', 'flot.resize', 'flot.time', 'flot.tooltip'], (Control, can, base, Auth)->
  pageData = new can.Map()

  return Control.extend
    init: (el, data)->
      this.element.html can.view('../public/view/home/dashboard/dashboard.html', pageData)

