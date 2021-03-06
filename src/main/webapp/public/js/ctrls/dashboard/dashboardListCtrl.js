// Generated by CoffeeScript 1.10.0
define(['can/control', 'can/view/mustache', 'base', 'Auth', 'reqwest', 'bootbox', '_', 'datatables.net', 'datatables.net-bs', 'datatables.net-responsive', 'datatables.net-responsive-bs', 'select2cn'], function(Control, can, base, Auth, reqwest, bootbox) {
  var pageData;
  pageData = new can.Map();
  return Control.extend({
    init: function(el, pdata) {
      var table;
      pageData.attr('userIsOnSite', Auth.userIsOnSite());
      pageData.attr('userIsContract', Auth.userIsContract());
      this.element.html(can.view("../public/view/home/dashboard/dashboardList.html", pageData));

      /**
       * 首页添加专门的事件列表标签页
       */
      table = $('#issueList').DataTable({
        paging: true,
        bFilter: false,
        processing: true,
        serverSide: true,
        ordering: false,
        "order": [],
        ajax: {
          url: Auth.apiHost + "question/page2?_=" + (Date.now()),
          data: function(d) {
            var k, search, v;
            search = $('#searchForm').serializeObject();
            for (k in search) {
              v = search[k];
              if (!v || v === 'unselected') {
                delete search[k];
              }
            }
            _.extend(d, search);
            return d;
          }
        },
        columns: [
          {
            data: 'number'
          }, {
            data: 'project'
          }, {
            data: 'severity'
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
            data: 'handlePromisedate',
            render: function(data, d, row) {
              if (row.handleStatus === 2) {
                return '已完成';
              }
              if (row.handleStatus <= 2 && data) {
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
              $btn = $("<a href='#!home/question/" + row.category + "/" + row.id + "' class='btn btn-danger btn-xs' type='button'/>").text('详情');
              return $btn[0].outerHTML;
            }
          }, {
            data: 'toTop',
            responsivePriority: 2,
            render: function(data, d, row) {
              var $btn, ref, ref1, ref2;
              if (row.closed) {
                $btn = $("<a href='#!home/question/" + (row.category.toLowerCase()) + "View/" + row.number + "' class='btn btn-danger btn-xs' type='button'/>").text('历史');
              } else if (row.handleStatus < 2) {
                $btn = $("<a href='#!home/question/handle/" + row.number + "' class='btn btn-danger btn-xs' type='button'/>").text('处理');
                if (((ref = row.handler) != null ? ref.loginid : void 0) !== Auth.user().loginID) {
                  $btn.addClass('disabled');
                }
              } else {
                $btn = $("<a href='#!home/question/" + (row.category.toLowerCase()) + "Close/" + row.number + "' class='btn btn-danger btn-xs' type='button'/>").text('关闭');
                if (((ref1 = row.creator) != null ? (ref2 = ref1.leader) != null ? ref2.loginid : void 0 : void 0) !== Auth.user().loginID) {
                  $btn.addClass('disabled');
                }
              }
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
      $('#issueList tbody').on('click', 'button', function(e) {
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

      /**
       * 下拉框填充
       */
      reqwest(Auth.apiHost + "dict/projects?_=" + (Date.now())).then(function(data) {
        data = _.map(data, function(d) {
          return {
            id: d.text,
            text: d.text
          };
        });
        data.unshift({
          id: 'unselected',
          text: '不限'
        });
        return $('#project').select2({
          language: 'zh-CN',
          theme: "bootstrap",
          data: data
        });
      });
      reqwest(Auth.apiHost + "user/all?_=" + (Date.now())).then(function(data) {
        data = _.map(data, function(d) {
          return {
            id: d.id,
            text: d.username || d.loginid
          };
        });
        data.unshift({
          id: 'unselected',
          text: '不限'
        });
        return $('#creatorId').select2({
          language: 'zh-CN',
          theme: "bootstrap",
          data: data
        });
      });

      /**
       * 16-6-20 PM单独列出处理
       */
      $('#category').select2({
        language: 'zh-CN',
        theme: "bootstrap",
        width: '80px',
        data: [
          {
            id: 'unselected',
            text: '不限'
          }, {
            id: 'WMS',
            text: 'WMS'
          }, {
            id: 'TMS',
            text: 'TMS'
          }
        ]
      });
      $('#closed').select2({
        language: 'zh-CN',
        theme: "bootstrap"
      });
      return $('#searchForm button').unbind('click').bind('click', function(e) {
        return table.ajax.reload();
      });
    }
  });
});
