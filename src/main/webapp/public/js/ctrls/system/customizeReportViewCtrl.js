// Generated by CoffeeScript 1.10.0
define(['can/control', 'can', 'Auth', 'base', 'reqwest', 'localStorage', 'bootbox', 'datatables.net', 'datatables.net-bs', 'datatables.net-responsive', 'datatables.net-responsive-bs'], function(Ctrl, can, Auth, base, reqwest, localStorage, bootbox) {
  return Ctrl.extend({
    init: function(el, data) {
      var table;
      if (!can.base) {
        new base('', data);
      }
      this.element.html(can.view('../public/view/home/system/customizeReportView.html'));
      table = $('#reportTable').DataTable({
        paging: false,
        ajax: {
          url: Auth.apiHost + "customize/report/all?_=" + (Date.now()),
          dataSrc: function(data) {
            return data;
          }
        },
        columns: [
          {
            data: 'id',
            visible: false
          }, {
            data: 'name'
          }, {
            data: 'type'
          }, {
            data: 'value'
          }, {
            data: 'groupList',
            render: function(data) {
              var groups;
              groups = _.map(data, function(group) {
                return "" + group.name;
              });
              return groups.splice(0, 3).join(', ') + (groups.length ? '...' : '');
            }
          }, {
            data: 'created',
            render: function(data) {
              if (data) {
                return new Date(data).toLocaleString();
              } else {
                return '';
              }
            }
          }, {
            data: 'modified',
            render: function(data) {
              if (data) {
                return new Date(data).toLocaleString();
              } else {
                return '';
              }
            }
          }, {
            data: 'param1'
          }, {
            data: 'type1'
          }, {
            data: 'param2'
          }, {
            data: 'type2'
          }, {
            data: 'param3'
          }, {
            data: 'type3'
          }, {
            data: 'param4'
          }, {
            data: 'type4'
          }, {
            data: 'param5'
          }, {
            data: 'type5'
          }, {
            data: 'param6'
          }, {
            data: 'type6'
          }
        ]
      });
      $('#reportTable tbody').on('click', 'tr', function() {
        if ($(this).hasClass('info')) {
          $(this).removeClass('info');
          return $('#operates button:gt(0)').attr('disabled', 'disabled');
        } else {
          table.$('tr.info').removeClass('info');
          $(this).addClass('info');
          return $('#operates button[disabled]').removeAttr('disabled');
        }
      });
      return $('#operates button').unbind('click').bind('click', function() {
        var selRow;
        selRow = table.row('.info').data();
        switch ($(this).data('action')) {
          case 'create':
            return window.location.hash = 'home/system/customizeReportAdd';
          case 'update':
            localStorage.set('tmpCustomizeReport', selRow);
            return window.location.hash = "home/system/customizeReportAdd/" + selRow.id;
          case 'delete':
            return reqwest({
              url: Auth.apiHost + "customize/report/" + selRow.id,
              method: "delete",
              type: "html"
            }).then(function() {
              table.row('.info').remove().draw();
              return $('#operates button:gt(0)').attr('disabled', 'disabled');
            }).fail(function(err) {
              return bootbox.alert('自定义报表删除失败！');
            });
        }
      });
    }
  });
});
