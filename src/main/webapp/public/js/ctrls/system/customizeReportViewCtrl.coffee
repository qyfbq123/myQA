define ['can/control', 'can', 'Auth', 'base', 'reqwest', 'localStorage', 'bootbox', 'datatables.net', 'datatables.net-bs', 'datatables.net-responsive', 'datatables.net-responsive-bs'], (Ctrl, can, Auth, base, reqwest, localStorage, bootbox)->
  return Ctrl.extend
    init: (el, data)->
      if !can.base
        new base('', data)

      this.element.html can.view('../public/view/home/system/customizeReportView.html')

      table = $('#reportTable').DataTable {
          paging: false
          ajax: 
            url: "#{Auth.apiHost}customize/report/all?_=#{Date.now()}"
            dataSrc: (data)->
              data
          columns: [
            data: 'id'
            visible: false
          ,
            data: 'name'
          ,
            data: 'type'
          ,
            data: 'value'
          ,
            data: 'created'
            render: (data)->
              if data then new Date(data).toLocaleString() else ''
          ,
            data: 'modified'
            render: (data)->
              if data then new Date(data).toLocaleString() else ''
          , 
            data: 'param1'
          ,
            data: 'type1'
          , 
            data: 'param2'
          ,
            data: 'type2'
          , 
            data: 'param3'
          ,
            data: 'type3'
          , 
            data: 'param4'
          ,
            data: 'type4'
          , 
            data: 'param5'
          ,
            data: 'type5'
          , 
            data: 'param6'
          ,
            data: 'type6'
          ]
        }

      $('#reportTable tbody').on 'click', 'tr', ->
        if $(this).hasClass('info')
          $(this).removeClass('info')
          $('#operates button:gt(0)').attr('disabled', 'disabled')
        else
          table.$('tr.info').removeClass('info')
          $(this).addClass('info')
          $('#operates button[disabled]').removeAttr('disabled')

      $('#operates button').unbind('click').bind 'click', ->
        selRow = table.row('.info').data()
        switch $(this).data('action')
          when 'create'
            window.location.hash = 'home/system/customizeReportAdd'
          when 'update'
            localStorage.set 'tmpCustomizeReport', selRow
            window.location.hash = "home/system/customizeReportAdd/#{selRow.id}"
          when 'delete'
            reqwest(
              url: "#{Auth.apiHost}customize/report/#{selRow.id}"
              method: "delete"
              type: "html"
            ).then(->
              table.row('.info').remove().draw()
              $('#operates button:gt(0)').attr('disabled', 'disabled')
            ).fail (err)->
              bootbox.alert '自定义报表删除失败！'
