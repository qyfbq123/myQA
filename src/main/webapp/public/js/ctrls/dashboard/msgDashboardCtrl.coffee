define ['can/control', 'can', 'base', 'Auth', 'reqwest', '_'], (Control, can, base, Auth, reqwest)->
  pageData = new can.Map()

  return Control.extend
    init: (el, data)->
      this.element.html can.view('../public/view/home/dashboard/msgDashboard.html', pageData)

      queryMsgPage = (callback)->
        start = $('#page-wrapper .msgRow').length
        reqwest( "#{Auth.apiHost}msg/page?_=#{Date.now()}&start=#{start}" ).then (data)->
          _.each data, (e, i)->
            e.created = new Date(e.created).toLocaleString() if e.created
            e.date = new Date(e.date).toLocaleDateString() if e.date
            msg = can.view '../public/view/home/dashboard/msg.stache', e
            $('#page-wrapper').append msg

          callback(data) if callback

      queryMsgPage (data)->
        $('#noMsgShow').removeClass 'hide' if data.length is 0
        if data.length != 20
            $('#more').addClass 'hide'
          else
            $('#page-wrapper').append $('#more')


      $('#more').unbind('click').bind 'click', ->
        queryMsgPage (data)->
          if data.length != 20
            $('#more').addClass 'hide'
          else
            $('#page-wrapper').append $('#more')
