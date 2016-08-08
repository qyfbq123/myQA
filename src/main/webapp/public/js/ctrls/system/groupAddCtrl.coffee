define ['can/control', 'can', 'Auth', 'base', 'reqwest', 'bootbox', 'localStorage', 'select2cn', 'datatables.net', 'datatables.net-bs', 'datatables.net-responsive', 'datatables.net-responsive-bs'], (Ctrl, can, Auth, base, reqwest, bootbox, localStorage)->

  return Ctrl.extend
    init: (el, data)->
      if !can.base
        new base('', data)

      groupInfo = new can.Map
        pageType: '新增'

      this.element.html can.view('../public/view/home/system/groupAdd.html', groupInfo)

      isNew = window.location.hash.endsWith 'groupAdd'
      if !isNew
        groupInfo.attr( 'pageType',  '修改' )
        tmpGroup = localStorage.get 'tmpGroupInfo'
        if tmpGroup
          for k, v of tmpGroup
            groupInfo.attr k, v if !_.isObject v

      $('#operates button').unbind('click').bind 'click', ->
        switch $(this).data 'action'
          when 'save'

            group = {}
            for k, v of groupInfo.attr()
              group[k] = v if k!='pageType' and !_.isObject v 
            group.id = Number group.id

            if _.isArray rows_selected
              group.userList = _.map rows_selected, (id)-> id: id

            reqwest({
              url: "#{Auth.apiHost}user/group/save"
              method: "post"
              contentType: 'application/json'
              type: 'html'
              data: JSON.stringify group
            }).then(->
              localStorage.remove 'tmpGroupInfo'
              bootbox.alert "组#{if isNew then '新增' else '修改'}成功！", ->
                window.location.hash = "home/system/groupView"
            ).fail (err)->
              bootbox.alert "组#{if isNew then '新增' else '修改'}失败！#{err.responseText}"
          when 'refresh'
            if tmpGroup
              for k, v of tmpGroup
                groupInfo.attr k, v if !_.isObject v and k != 'pageType'

              $('form input:checked').prop 'checked', false
              if tmpGroup?.userList
                table.rows().every (rowIdx)->
                  if (_.where tmpGroup.userList, (id: this.data().id)).length > 0
                    $(this.node()).find('input[type="checkbox"]:not(:checked)').trigger('click')
                  else
                    $(this.node()).find('input[type="checkbox"]:checked').trigger('click')
            else
              for k, v of groupInfo.attr()
                continue if k == 'pageType'
                groupInfo.attr k, 0 if typeof v == 'number'
                groupInfo.attr k, '' if typeof v == 'string'

                $('form input:checked').prop 'checked', false
          when 'cancel'
            window.location.hash = "home/system/groupView"

      updateDataTableSelectAllCtrl = (table)->
        $table             = table.table().node()
        $chkbox_all        = $('tbody input[type="checkbox"]', $table)
        $chkbox_checked    = $('tbody input[type="checkbox"]:checked', $table)
        chkbox_select_all  = $('thead input[name="select_all"]', $table).get(0)

        if $chkbox_checked.length == 0
          chkbox_select_all.checked = false
          if 'indeterminate' in chkbox_select_all
             chkbox_select_all.indeterminate = false

        else if $chkbox_checked.length == $chkbox_all.length
          chkbox_select_all.checked = true;
          if 'indeterminate' in chkbox_select_all
             chkbox_select_all.indeterminate = false
        else 
          chkbox_select_all.checked = true;
          if 'indeterminate' in chkbox_select_all
             chkbox_select_all.indeterminate = true

      rows_selected = []
      table = $('#userTable').DataTable {
        paging: false
        ajax: 
          url: Auth.apiHost + 'user/all'
          dataSrc: (data)->
            data
        columns: [
          data: 'id'
          searchable: false
          orderable: false
          width: '1%'
          className: 'dt-body-center'
          render: (data)->
            if tmpGroup?.userList
              if _.where(tmpGroup.userList, (id: data)).length > 0
                rows_selected.push data if _.indexOf(rows_selected, data) == -1
                return '<input type="checkbox" checked="checked">'
                       
            '<input type="checkbox">'
        ,
          data: 'loginid'
        ,
          data: 'username'
        ,
          ###*
           * 16-6-22 显示用户的地域
          ###
          data: 'city'
          render: (data)->
            data?.name || ''
        ]
        rowCallback: (row, data, dataIndex)->
          rowId = data.id
          if $.inArray(rowId, rows_selected) != -1
            $(row).find('input[type="checkbox"]').prop('checked', true)
            $(row).addClass('selected')
      }

      $('#userTable tbody').on 'click', 'input[type="checkbox"]', (e)->
        $row = $(this).closest('tr')
        data = table.row($row).data()
        rowId = data.id
        index = $.inArray(rowId, rows_selected)
        if this.checked and index == -1
          rows_selected.push(rowId)
        else if !this.checked && index != -1
          rows_selected.splice(index, 1)

        if this.checked
          $row.addClass('selected')
        else
          $row.removeClass('selected')

        updateDataTableSelectAllCtrl(table)
        e.stopPropagation()

      $('#userTable').on 'click', 'tbody td, thead th:first-child', (e)->
        $(this).parent().find('input[type="checkbox"]').trigger('click')

      $('thead input[name="select_all"]', table.table().container()).on 'click', (e)->
        if this.checked
          $('#userTable tbody input[type="checkbox"]:not(:checked)').trigger('click')
        else
          $('#userTable tbody input[type="checkbox"]:checked').trigger('click')

        e.stopPropagation()

      table.on 'draw', ->
        updateDataTableSelectAllCtrl table
