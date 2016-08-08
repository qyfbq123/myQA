define ['can/control', 'can', 'Auth', 'base', 'reqwest', 'localStorage', 'bootbox', 'datatables.net', 'datatables.net-bs'], (Ctrl, can, Auth, base, reqwest, localStorage, bootbox)->
  return Ctrl.extend
    init: (el, data)->
      if !can.base
        new base('', data)

      this.element.html can.view('../public/view/home/system/dictSetting.html')

      reqwest( "#{Auth.apiHost}dict/attributes?_=#{Date.now()}" ).then (data)->
        _.each data, (dict)->
          $button = $ "<a href='#' class='list-group-item' data-dictid='#{dict.dictId}'>#{dict.dictValue}</a>"
          # $button = $ "<button type='button' class='list-group-item' data-dictId='#{dict.dictId}'>#{dict.dictValue}</button>"
          $("#dictList").append $button

      $('#dictList').on 'click', 'a.list-group-item', (e)->
        $('#dictList a.active').removeClass 'active'
        $(this).addClass 'active'
        $('#paramAdd').removeClass 'hide'
        $("#params li").remove()

        reqwest( "#{Auth.apiHost}dict/attributes/#{$(this).data 'dictid'}?_=#{Date.now()}" ).then (data)->
          _.each data, (dict)->
            $li = $ "<li class='list-group-item list-group-item-info' data-dictvalue='#{dict.dictValue}'>#{dict.dictValue}<button type='button' class='close'><span aria-hidden='true' class='pull-right'>&times;</span></button></li>"

            $("#params").append $li
        e.preventDefault()
        e.stopPropagation()

      $('#params').on 'click', 'li.list-group-item>button', (e)->
        $(this).closest('li').remove()

      $('#addBtn').unbind('click').click (e)->
        if value = $('#addValue').val()
          $li = $ "<li class='list-group-item list-group-item-info' data-dictvalue='#{value}'>#{value}<button type='button' class='close'><span aria-hidden='true' class='pull-right'>&times;</span></button></li>"
          $("#params").append $li
          $('#addValue').val ''

      $('#saveBtn').unbind('click').click (e)->
        params =[]
        $('#params li').each (index, li)->
          params.push
            dictId: index+1
            dictValue: $(this).data 'dictvalue'

        reqwest( {
          url: "#{Auth.apiHost}dict/attributes/#{$('#dictList a.active').data 'dictid'}"
          method: "put"
          contentType: 'application/json'
          data: JSON.stringify params
          type: 'html'
        }).then( ->
          bootbox.alert '保存成功'
        ).fail ->
          bootbox.alert '保存失败！'