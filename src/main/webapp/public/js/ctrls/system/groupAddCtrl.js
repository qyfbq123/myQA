// Generated by CoffeeScript 1.10.0
var indexOf = [].indexOf || function(item) { for (var i = 0, l = this.length; i < l; i++) { if (i in this && this[i] === item) return i; } return -1; };

define(['can/control', 'can', 'Auth', 'base', 'reqwest', 'bootbox', 'localStorage', 'select2cn', 'datatables.net', 'datatables.net-bs', 'datatables.net-responsive', 'datatables.net-responsive-bs'], function(Ctrl, can, Auth, base, reqwest, bootbox, localStorage) {
  return Ctrl.extend({
    init: function(el, data) {
      var groupInfo, isNew, k, rows_selected, table, tmpGroup, updateDataTableSelectAllCtrl, v;
      if (!can.base) {
        new base('', data);
      }
      groupInfo = new can.Map({
        pageType: '新增'
      });
      this.element.html(can.view('../public/view/home/system/groupAdd.html', groupInfo));
      isNew = window.location.hash.endsWith('groupAdd');
      if (!isNew) {
        groupInfo.attr('pageType', '修改');
        tmpGroup = localStorage.get('tmpGroupInfo');
        if (tmpGroup) {
          for (k in tmpGroup) {
            v = tmpGroup[k];
            if (!_.isObject(v)) {
              groupInfo.attr(k, v);
            }
          }
        }
      }
      $('#operates button').unbind('click').bind('click', function() {
        var group, ref, ref1, results;
        switch ($(this).data('action')) {
          case 'save':
            group = {};
            ref = groupInfo.attr();
            for (k in ref) {
              v = ref[k];
              if (k !== 'pageType' && !_.isObject(v)) {
                group[k] = v;
              }
            }
            group.id = Number(group.id);
            if (_.isArray(rows_selected)) {
              group.userList = _.map(rows_selected, function(id) {
                return {
                  id: id
                };
              });
            }
            return reqwest({
              url: Auth.apiHost + "user/group/save",
              method: "post",
              contentType: 'application/json',
              type: 'html',
              data: JSON.stringify(group)
            }).then(function() {
              localStorage.remove('tmpGroupInfo');
              return bootbox.alert("组" + (isNew ? '新增' : '修改') + "成功！", function() {
                return window.location.hash = "home/system/groupView";
              });
            }).fail(function(err) {
              return bootbox.alert("组" + (isNew ? '新增' : '修改') + "失败！" + err.responseText);
            });
          case 'refresh':
            if (tmpGroup) {
              for (k in tmpGroup) {
                v = tmpGroup[k];
                if (!_.isObject(v && k !== 'pageType')) {
                  groupInfo.attr(k, v);
                }
              }
              $('form input:checked').prop('checked', false);
              if (tmpGroup != null ? tmpGroup.userList : void 0) {
                return table.rows().every(function(rowIdx) {
                  if ((_.where(tmpGroup.userList, {
                    id: this.data().id
                  })).length > 0) {
                    return $(this.node()).find('input[type="checkbox"]:not(:checked)').trigger('click');
                  } else {
                    return $(this.node()).find('input[type="checkbox"]:checked').trigger('click');
                  }
                });
              }
            } else {
              ref1 = groupInfo.attr();
              results = [];
              for (k in ref1) {
                v = ref1[k];
                if (k === 'pageType') {
                  continue;
                }
                if (typeof v === 'number') {
                  groupInfo.attr(k, 0);
                }
                if (typeof v === 'string') {
                  groupInfo.attr(k, '');
                }
                results.push($('form input:checked').prop('checked', false));
              }
              return results;
            }
            break;
          case 'cancel':
            return window.location.hash = "home/system/groupView";
        }
      });
      updateDataTableSelectAllCtrl = function(table) {
        var $chkbox_all, $chkbox_checked, $table, chkbox_select_all;
        $table = table.table().node();
        $chkbox_all = $('tbody input[type="checkbox"]', $table);
        $chkbox_checked = $('tbody input[type="checkbox"]:checked', $table);
        chkbox_select_all = $('thead input[name="select_all"]', $table).get(0);
        if ($chkbox_checked.length === 0) {
          chkbox_select_all.checked = false;
          if (indexOf.call(chkbox_select_all, 'indeterminate') >= 0) {
            return chkbox_select_all.indeterminate = false;
          }
        } else if ($chkbox_checked.length === $chkbox_all.length) {
          chkbox_select_all.checked = true;
          if (indexOf.call(chkbox_select_all, 'indeterminate') >= 0) {
            return chkbox_select_all.indeterminate = false;
          }
        } else {
          chkbox_select_all.checked = true;
          if (indexOf.call(chkbox_select_all, 'indeterminate') >= 0) {
            return chkbox_select_all.indeterminate = true;
          }
        }
      };
      rows_selected = [];
      table = $('#userTable').DataTable({
        paging: false,
        ajax: {
          url: Auth.apiHost + 'user/all',
          dataSrc: function(data) {
            return data;
          }
        },
        columns: [
          {
            data: 'id',
            searchable: false,
            orderable: false,
            width: '1%',
            className: 'dt-body-center',
            render: function(data) {
              if (tmpGroup != null ? tmpGroup.userList : void 0) {
                if (_.where(tmpGroup.userList, {
                  id: data
                }).length > 0) {
                  if (_.indexOf(rows_selected, data) === -1) {
                    rows_selected.push(data);
                  }
                  return '<input type="checkbox" checked="checked">';
                }
              }
              return '<input type="checkbox">';
            }
          }, {
            data: 'loginid'
          }, {
            data: 'username'
          }, {

            /**
             * 16-6-22 显示用户的地域
             */
            data: 'city',
            render: function(data) {
              return (data != null ? data.name : void 0) || '';
            }
          }
        ],
        rowCallback: function(row, data, dataIndex) {
          var rowId;
          rowId = data.id;
          if ($.inArray(rowId, rows_selected) !== -1) {
            $(row).find('input[type="checkbox"]').prop('checked', true);
            return $(row).addClass('selected');
          }
        }
      });
      $('#userTable tbody').on('click', 'input[type="checkbox"]', function(e) {
        var $row, index, rowId;
        $row = $(this).closest('tr');
        data = table.row($row).data();
        rowId = data.id;
        index = $.inArray(rowId, rows_selected);
        if (this.checked && index === -1) {
          rows_selected.push(rowId);
        } else if (!this.checked && index !== -1) {
          rows_selected.splice(index, 1);
        }
        if (this.checked) {
          $row.addClass('selected');
        } else {
          $row.removeClass('selected');
        }
        updateDataTableSelectAllCtrl(table);
        return e.stopPropagation();
      });
      $('#userTable').on('click', 'tbody td, thead th:first-child', function(e) {
        return $(this).parent().find('input[type="checkbox"]').trigger('click');
      });
      $('thead input[name="select_all"]', table.table().container()).on('click', function(e) {
        if (this.checked) {
          $('#userTable tbody input[type="checkbox"]:not(:checked)').trigger('click');
        } else {
          $('#userTable tbody input[type="checkbox"]:checked').trigger('click');
        }
        return e.stopPropagation();
      });
      return table.on('draw', function() {
        return updateDataTableSelectAllCtrl(table);
      });
    }
  });
});
