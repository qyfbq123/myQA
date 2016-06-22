// Generated by CoffeeScript 1.10.0
define(['can/control', 'can', 'Auth', 'base', 'reqwest', 'localStorage', 'bootbox', 'datatables.net', 'datatables.net-bs', 'datatables.net-responsive', 'datatables.net-responsive-bs'], function(Ctrl, can, Auth, base, reqwest, localStorage, bootbox) {
  return Ctrl.extend({
    init: function(el, data) {
      var table;
      if (!can.base) {
        new base('', data);
      }
      this.element.html(can.view('../public/view/home/system/userView.html'));
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
            visible: false
          }, {
            data: 'loginid'
          }, {
            data: 'password'
          }, {
            data: 'username'
          }, {
            data: 'email'
          }, {
            data: 'leader',
            render: function(data) {
              if (data) {
                return data.username || data.loginid;
              } else {
                return '';
              }
            }
          }, {
            data: 'roleList',
            render: '[, ].name'
          }, {
            data: 'city',
            render: function(data) {
              return (data != null ? data.name : void 0) || '';
            }
          }, {
            data: 'locked',
            render: function(data) {
              if (data) {
                return '禁用';
              } else {
                return '启用';
              }
            }
          }
        ]
      });
      $('#userTable tbody').on('click', 'tr', function() {
        var row;
        if ($(this).hasClass('info')) {
          $(this).removeClass('info');
          return $('#operates button:gt(0)').attr('disabled', 'disabled');
        } else {
          table.$('tr.info').removeClass('info');
          $(this).addClass('info');
          row = table.row(this).data();
          $('#operates button[data-action="toggle"] font').text(row.locked ? '启用' : '禁用');
          return $('#operates button[disabled]').removeAttr('disabled');
        }
      });
      return $('#operates button').unbind('click').bind('click', function() {
        var selRow;
        selRow = table.row('.info').data();
        switch ($(this).data('action')) {
          case 'create':
            return window.location.hash = 'home/system/userAdd';
          case 'update':
            localStorage.set('tmpUserInfo', selRow);
            return window.location.hash = "home/system/userAdd/" + selRow.id;
          case 'delete':
            return reqwest({
              url: Auth.apiHost + "user/del/" + selRow.id,
              method: "delete",
              type: "html"
            }).then(function() {
              table.row('.info').remove().draw();
              return $('#operates button:gt(0)').attr('disabled', 'disabled');
            }).fail(function(err) {
              return bootbox.alert('用户删除失败！');
            });
          case 'toggle':
            return reqwest({
              url: Auth.apiHost + "user/toggle/" + selRow.id,
              method: "put",
              type: "html"
            }).then(function() {
              selRow.locked = !selRow.locked;
              $('#operates button[data-action="toggle"] font').text(selRow.locked ? '启用' : '禁用');
              return table.row(table.$('tr.info')).data(selRow);
            }).fail(function(err) {
              return bootbox.alert('用户启用／禁用失败！');
            });
        }
      });
    }
  });
});
