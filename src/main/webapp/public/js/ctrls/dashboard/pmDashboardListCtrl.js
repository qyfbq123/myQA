// Generated by CoffeeScript 1.10.0
define(['can/control', 'can/view/mustache', 'base', 'Auth', 'reqwest', '_', 'datatables.net', 'datatables.net-bs', 'datatables.net-responsive', 'datatables.net-responsive-bs', 'select2cn'], function(Control, can, base, Auth, reqwest) {
  var pageData;
  pageData = new can.Map();
  return Control.extend({
    init: function(el, pdata) {
      var table;
      this.element.html(can.view("../public/view/home/dashboard/pmDashboardList.html", pageData));

      /**
       * 首页添加专门的事件列表标签页
       */
      table = $('#issueList').DataTable({
        paging: false,
        processing: true,
        ajax: {
          url: Auth.apiHost + "question/pmList?_=" + (Date.now()),
          dataSrc: function(data) {
            return data;
          }
        },
        columns: [
          {
            data: 'number'
          }, {
            data: 'project'
          }, {
            data: 'beginStorehouse'
          }, {
            data: 'creator',
            render: function(data) {
              return (data != null ? data.username : void 0) || '';
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
            data: 'issueDate',
            render: function(data, d, row) {
              if (data) {
                return new Date(data).toLocaleDateString();
              } else {
                return '';
              }
            }
          }, {
            data: 'type'
          }, {
            data: 'closed',
            render: function(data) {
              if (data) {
                return 'CLOSED';
              } else {
                return 'OPEN';
              }
            }
          }, {
            data: 'toTop',
            responsivePriority: 2,
            render: function(data) {
              var $btn;
              $btn = $("<button class='btn btn-primary btn-xs' type='button'/>").text(data ? '取消置顶' : '置顶');
              return $btn[0].outerHTML;
            }
          }, {
            data: 'toTop',
            responsivePriority: 2,
            render: function(data, d, row) {
              var $btn;
              $btn = $("<a href='#!home/question/pmClose/" + row.number + "' class='btn btn-danger btn-xs' type='button'/>").text('详情');
              return $btn[0].outerHTML;
            }
          }
        ],
        rowCallback: function(row, data, dataIndex) {
          if (data.toTop) {
            return $(row).addClass('danger');
          }
        }
      });

      /**
       * 置顶功能
       */
      return $('#issueList tbody').on('click', 'button', function(e) {
        var $row, question;
        $row = $(this).closest('tr');
        question = table.row($row).data();
        return reqwest({
          url: Auth.apiHost + "question/toggleTop/" + question.id,
          method: "put",
          type: 'html'
        }).then(function() {
          return table.ajax.reload();
        }).fail(function() {
          return bootbox.alert('置顶失败');
        });
      });
    }
  });
});
