// Generated by CoffeeScript 1.10.0
define(['can/control', 'can', 'Auth', 'base', 'reqwest', 'localStorage', 'datatables.net', 'datatables.net-bs', 'datatables.net-responsive', 'datatables.net-responsive-bs', 'bootbox'], function(Ctrl, can, Auth, base, reqwest, localStorage) {
  return Ctrl.extend({
    init: function(el, data) {
      var table;
      if (!can.base) {
        new base('', data);
      }
      this.element.html(can.view('../public/view/home/system/groupView.html'));
      table = $('#groupTable').DataTable({
        paging: false,
        ajax: {
          url: Auth.apiHost + 'user/group/all',
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
            data: 'userList',
            render: function(data) {
              var users;
              users = _.map(data, function(user) {
                return (user.username || '匿名') + "(" + user.loginid + ")";
              });
              return users.splice(0, 8).join(', ') + (users.length ? '...' : '');
            }
          }
        ]
      });
      $('#groupTable tbody').on('click', 'tr', function() {
        var row;
        if ($(this).hasClass('info')) {
          $(this).removeClass('info');
          return $('#operates button:gt(0)').attr('disabled', 'disabled');
        } else {
          table.$('tr.info').removeClass('info');
          $(this).addClass('info');
          row = table.row(this).data();
          return $('#operates button[disabled]').removeAttr('disabled');
        }
      });
      return $('#operates button').unbind('click').bind('click', function() {
        var selRow;
        selRow = table.row('.info').data();
        switch ($(this).data('action')) {
          case 'create':
            return window.location.hash = 'home/system/groupAdd';
          case 'update':
            localStorage.set('tmpGroupInfo', selRow);
            return window.location.hash = "home/system/groupAdd/" + selRow.id;
          case 'delete':
            return reqwest({
              url: Auth.apiHost + "user/group/del/" + selRow.id,
              method: "delete",
              type: "html"
            }).then(function() {
              return table.row('.info').remove().draw();
            }).fail(function(err) {
              return bootbox.alert('组删除失败！');
            });
        }
      });
    }
  });
});
