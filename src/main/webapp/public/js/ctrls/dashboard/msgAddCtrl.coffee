define ['can/control', 'can', 'base', 'Auth', 'reqwest', 'bootbox', '_', 'select2cn', 'datepickercn', 'validate'], (Control, can, base, Auth, reqwest, bootbox)->
  return Control.extend
    init: (el, data)->
      msgData = new can.Map()

      this.element.html can.view('../public/view/home/dashboard/bulletinNew.html', msgData)

      $('#function').select2 {
        language: 'zh-CN'
        theme: "bootstrap"
        placeholder: 'Select a Function'
      }

      $('#elc').select2 {
        language: 'zh-CN'
        theme: "bootstrap"
        placeholder: 'Select an ELC'
      }

      $('.input-group.date input').datepicker {
        language: 'zh-CN'
        todayBtn: true
        autoclose: true
      }

      reqwest( "#{Auth.apiHost}dict/projects?_=#{Date.now()}" ).then (data)->
        data = _.map data, (d)->
          id: d.text, text: d.text
        $('#projectName').select2 {
          language: 'zh-CN'
          theme: "bootstrap"
          data: data
          placeholder: 'Select a Project'
        }

      reqwest( "#{Auth.apiHost}dict/bulletinTypes?_=#{Date.now()}" ).then (data)->
        data = _.map data, (d)->
          id: d.text, text: d.text
        $('#type').select2 {
          language: 'zh-CN'
          theme: "bootstrap"
          data: data
          placeholder: 'Select a Type'
        }

      ###*
       * 获取pm组事件参与人
      ###
      # reqwest( "#{Auth.apiHost}user/group/pm?_=#{Date.now()}" ).then (data)->
      #   data = _.groupBy data, (u)->return u.city && u.city.name

      #   generateCityTeammates = (cityName, users)->
      #     $("#teammates").append $('<div class="col-sm-2 text-right"/>').html "<input type='checkbox'/>#{cityName}:"
      #     $div = $('<div class="col-sm-10"/>')
      #     _.each users, (user)->
      #       $label = $('<label/>').text user.username
      #       $input = $('<input type="checkbox" name="teammates"/>').val(user.id).prependTo $label
      #       $div.append $label
      #     $('#teammates').append $div

      #   reqwest("#{Auth.apiHost}dict/cities?_=#{Date.now()}").then (cities)->
      #     _.each cities, (city)->
      #       if data[city.text]
      #         generateCityTeammates city.text, data[city.text]
      #         delete data[city.text]

      #     for k,v of data
      #       k = if k is 'null' then '其他' else k
      #       generateCityTeammates k, v

      # $('#teammates').on 'change', '.col-sm-2 :checkbox', ->
      #   $(':checkbox', $(this).parent().next('.col-sm-10')).prop 'checked', $(this).is(':checked')

      $('#submitBtn').unbind('click').bind 'click', ->
        return if !$('form').valid()
        msgObj = $('form').serializeObject()

        msgObj.date = $('#date').datepicker 'getDate'

        if msgObj.teammates and msgObj.teammates.length > 0
          msgObj.teammates = [msgObj.teammates] if _.isString msgObj.teammates
          msgObj.receivers = _.map msgObj.teammates, (e, i)->
            return {id: e}

        delete msgObj.teammates

        reqwest( {
          url: "#{Auth.apiHost}msg/create"
          method: 'post'
          data: JSON.stringify msgObj
          contentType: "application/json"
          type: 'html'
        }).then(->
          bootbox.alert '发布成功！', ->
            location.hash = '#!home/msgDashboard'
        ).fail (err)->
          bootbox.alert "发布失败！#{err.responseText}"
