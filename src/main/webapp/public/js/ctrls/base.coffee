
define ['can', 'can/control', 'homeCtrl', 'Auth', 'localStorage', '_'], (can, Control, homeCtrl, Auth, localStorage)->
  return Control.extend
    init: (el, data)->
      menus = localStorage.get('menus')
      # currentMenu = _.find(menus, (item)-> item.url == data.id)

      can.home ?= new homeCtrl('body', {})
      can.home.showFirstMenu()
