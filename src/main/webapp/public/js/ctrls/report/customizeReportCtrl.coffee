define ['can/control', 'can', 'Auth', 'base', 'reqwest', 'localStorage', 'bootbox', 'datatables.net', 'datatables.net-bs', 'datatables.net-responsive', 'datatables.net-responsive-bs', 'datepickercn', 'validate', '_'], (Ctrl, can, Auth, base, reqwest, localStorage, bootbox)->
  return Ctrl.extend
    init: (el, data)->
      if !can.base
        new base('', data)

      pageData = new can.Map()

      this.element.html can.view('../public/view/home/report/customizeReport.html', pageData)

      colsManager =
        instance: null
        saveCols: (columns)->
          if colsManager.instance
            window.clearTimeout colsManager.instance
          colsManager.instance = window.setTimeout (->
            if columns ?= $('#columns').data('columns')
              columns = _.map columns, (e)->
                title: e.title
                data: e.data
                visible: e.visible
              ucrColumns =
                id: $('#columns').data 'id'
                crId: data.id
                columns: JSON.stringify columns
              $.post "#{Auth.apiHost}/customize/report/columns/save", ucrColumns, (data)->$('#columns').data 'id', data.id
          ), 2000

      showColumns = ()->
        columns = $('#columns').data 'columns'
        _.each columns, (e)->
          $label = $('<label/>').text e.title
          $input = $('<input type="checkbox" name="column"/>').prop('checked', e.visible).val(e.title).prependTo $label
          $('#column').append $label

      reqwest({
        url: "#{Auth.apiHost}customize/report/#{data.id}?_=#{Date.now()}"
        method: 'get'
      }).then( (data)-> 
        pageData.attr data
        _.each [1..6], (e)->
          if data["param#{e}"]
            switch data["type#{e}"]
              when 'date'
                $param = $('#tempDate').clone()
              when 'number'
                $param = $('#tempNumber').clone()
              else
                $param = $('#temp').clone()

            $('label', $param).attr('for', "param#{e}").text data["param#{e}"]
            $('input', $param).attr('id', "param#{e}").attr('name', "param#{e}").attr 'required', 'required'
            $param.removeClass('hide').appendTo $('#params')

        $('.input-group.date input').datepicker {
          language: 'zh-CN'
          todayBtn: true
          autoclose: true
        }

        $('#params>.hide').remove()
      ).fail (err)->
        bootbox.alert "获取自定义报表失败！该自定义报表可能已被移除，请刷新后再试！#{err.responseText}"

      reqwest({
        url: "#{Auth.apiHost}customize/report/#{data.id}/columns?_=#{Date.now()}"
        method: 'get'
      }).then( (data)->
        if data?.id
          $('#columns').data 'id', data.id
        if data?.columns
          $('#columns').data 'columns', JSON.parse data.columns
          showColumns()
      ).fail (err)->
        bootbox.alert "获取自定义列失败！#{err.responseText}"

      $.fn.dataTable.ext.errMode = 'none';

      $('#columns').on 'change', (e)->
        $col = $(e.target)
        columns = $('#columns').data('columns')
        _.each columns, (col, i)->
          if col.data == $col.val()
            visible = $col.is ':checked'
            $reportTable.column(i).visible visible
            $reportTable.columns.adjust().draw false
            col.visible = visible
            $('#columns').data 'columns', columns
            colsManager.saveCols()

      $reportTable = null
      $('#btnGroup').on 'click', '.btn', (e)->
        return if !$('form').valid()

        report = $.extend {}, pageData.attr(), $('form').serializeObject()
        delete report['created']
        delete report['modified']
        delete report['groupList']
        $('.input-group.date input').each (i, e)->
          report[$(this).attr 'name'] = String Number $(this).datepicker('getDate')

        switch $(e.target).attr 'id'
          when 'setBtn'
            $('#columns').toggleClass 'hide'
          when 'selectBtn'
            
            reqwest({
              url: "#{Auth.apiHost}customize/report/run"
              data: report
              method: "get"
            }).then( (data)->
              if data && data[0]
                if !$reportTable

                  if !(columns = $('#columns').data 'columns')
                    columns = _.map data[0], (v, k)->
                      return (title: k, data: k, visible: true)
                    $('#columns').data 'columns', columns
                    showColumns()
                    colsManager.saveCols()
                  
                  $reportTable = $('#reportTable').DataTable
                    data: data
                    columns: columns
                else
                  $reportTable.clear()
                  $reportTable.rows.add(data)
                  $reportTable.draw()
              else if $reportTable
                $reportTable.clear()
                $reportTable.draw()
            ).fail (err)->
              bootbox.alert "获取自定义运行失败！#{err.responseText}"
          when 'downloadBtn'
            _href = "#{Auth.apiHost}customize/report/download?_=#{Date.now()}&id=#{report['id']}&name=#{report.name}&"
            _href += (_.map [1..6], (e)-> "param#{e}=#{report['param' + e] }&type#{e}=#{report['type' + e]}").join '&'
            $(e.target).attr 'href', _href
          when 'mailBtn'

            reqwest(url: "#{Auth.apiHost}customize/report/push?_=#{Date.now()}", data: report, type: 'html').then( (data)->
              bootbox.alert '推送成功！'
            ).fail ->
              bootbox.alert '推送失败！'
