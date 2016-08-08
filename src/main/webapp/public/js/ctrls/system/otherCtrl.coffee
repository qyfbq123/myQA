define ['can/control', 'can', 'Auth', 'base', 'reqwest', 'bootbox', 'select2cn'], (Ctrl, can, Auth, base, reqwest, bootbox)->
  return Ctrl.extend
    init: (el, data)->
      if !can.base
        new base('', data)

      this.element.html can.view('../public/view/home/system/other.html')

      $('#section').select2 {
        language: 'zh-CN'
        width: '80px'
        theme: "bootstrap"
      }

      $('#section').change ->
        $('#downloadBtn').attr 'href', "#{Auth.apiHost}question/report/#{$(this).val()}/download?_=#{Date.now()}"

      $('#section').trigger 'change'

      $('#pushBtn').click ->
        reqwest(url: "#{Auth.apiHost}question/report/#{$('#section').val()}/push?_=#{Date.now()}", type: 'html').then( (data)->
          bootbox.alert '推送成功！'
        ).fail ->
          bootbox.alert '推送失败！'

      $('#richPushBtn').click ->
        _year = $('#year').val()
        _month = $('#month').val()
        _date = $('#date').val()
        data =
          year: _year
        data.month = _month if _month isnt '0'
        data.date = _date if _date isnt '0'
        reqwest(url: "#{Auth.apiHost}question/report/push?_=#{Date.now()}", data: data, type: 'html').then( (data)->
          bootbox.alert '推送成功！'
        ).fail ->
          bootbox.alert '推送失败！'


      years = [2000..2020]
      $('#year').select2 {
        language: 'zh-CN'
        width: '80px'
        theme: 'bootstrap'
        data: years.map (y)->
          {id:y, text: y}
      }

      months = [1..12].map (m)->
        {id: m, text: m}
      months.unshift { id: 0, text: '请选择', selected: true}
      $('#month').select2 {
        language: 'zh-CN'
        width: '80px'
        theme: 'bootstrap'
        data: months
      }
      $('#year,#month').change ->
        if (_m = $('#month').val()) > 0
          $('#date').prop('disabled', false)

          switch Number(_m)
            when 1,3,5,7,8,10,12
              _days = [1..31]
            when 4,6,9,11
              _days = [1..30]
            when 2
              _days = [1..28]
              _year = $('#year').val()
              _days.push(29) if (_year%4==0 && _year%100!=0) || _year%400==0

          _days = _days.map (d)->
                {id: d, text: d}
          _days.unshift {id:0, text: '请选择', selected: true}
          $('#date').select2().empty()
          $('#date').select2 {
            language: 'zh-CN'
            width: '80px'
            theme: 'bootstrap'
            data: _days
          }

        else
          $('#date').val(0).change()
          $('#date').prop('disabled', true)

      days = [{id:0, text: '请选择', selected: true}]
      $('#date').select2 {
        language: 'zh-CN'
        width: '80px'
        theme: 'bootstrap'
        data: days
      }
      
      $('#year, #month, #date').change ->
        _href = "#{Auth.apiHost}question/report/download?_=#{Date.now()}"
        _year = $('#year').val()
        _month = $('#month').val()
        _date = $('#date').val()
        _href += "&year=#{_year}#{if _month isnt '0' then '&month=' + _month else ''}#{if _date isnt '0' then '&date=' + _date else ''}"
        $('#richDownloadBtn').attr 'href', _href

      $('#year').val((new Date()).getFullYear()).change()
      $('#date').prop('disabled', true)