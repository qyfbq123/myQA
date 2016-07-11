// Generated by CoffeeScript 1.10.0
define(['can/control', 'can', 'Auth', 'base', 'reqwest', 'bootbox', 'localStorage', 'select2cn', 'datepickercn', 'datatables.net', 'datatables.net-bs', 'es6shim', 'jqueryFileupload'], function(Ctrl, can, Auth, base, reqwest, bootbox, localStorage) {
  return Ctrl.extend({
    init: function(el, data) {
      var dialogLogin, questionInfo, ref, table;
      questionInfo = new can.Map({
        category: data.category,
        title: data.id ? "详情" : data.category + "管理 / 新增"
      });
      this.element.html(can.view('../public/view/home/question/questionAdd.html', questionInfo));
      dialogLogin = function(done) {
        return bootbox.dialog({
          title: "登录",
          message: '<div class="row">  ' + '<div class="col-md-12"> ' + '<form class="form-horizontal"> ' + '<div class="form-group"> ' + '<label class="col-md-4 control-label" for="dialogLoginID">LoginID</label> ' + '<div class="col-md-4"> ' + '<input id="dialogLoginID" name="dialogLoginID" type="text" placeholder="Your loginID" class="form-control input-md"> ' + '</div> ' + '</div> ' + '<div class="form-group"> ' + '<label class="col-md-4 control-label" for="dialogPassword">Password</label> ' + '<div class="col-md-4"> ' + '<input id="dialogPassword" name="dialogPassword" type="password" placeholder="Your password" class="form-control input-md"> ' + '</div> ' + '</div> ' + '</form> </div>  </div>',
          buttons: {
            success: {
              label: "登录",
              className: "btn-success",
              callback: function() {
                var loginID, password;
                loginID = $('#dialogLoginID').val();
                password = $('#dialogPassword').val();
                return Auth.login({
                  loginID: loginID,
                  password: password
                }, function(e) {
                  if (e) {
                    return bootbox.alert(e.responseText);
                  }
                  return typeof done === "function" ? done(e) : void 0;
                });
              }
            }
          }
        });
      };
      if (Auth.logined()) {
        if (!can.base) {
          new base('', data);
        }
      } else {
        $('#subNavbar').removeClass('hide');
        $('#filePicker').closest('span').remove();
        $('#submitBtn').closest('.form-group').remove();
        $('#loginBtn').click(function() {
          return dialogLogin(function() {
            return window.location.reload();
          });
        });
      }
      $('.input-group.date input').datepicker({
        language: 'zh-CN',
        startDate: "0d",
        todayBtn: true,
        autoclose: true
      });
      reqwest(Auth.apiHost + "dict/projects?_=" + (Date.now())).then(function(data) {
        data = _.map(data, function(d) {
          return {
            id: d.text,
            text: d.text
          };
        });
        return $('#project').select2({
          language: 'zh-CN',
          theme: "bootstrap",
          data: data
        });
      });
      reqwest(Auth.apiHost + "dict/types/ms?_=" + (Date.now())).then(function(data) {
        data = _.map(data, function(d) {
          return {
            id: d.text,
            text: d.text
          };
        });
        return $('#type').select2({
          language: 'zh-CN',
          theme: "bootstrap",
          data: data
        });
      });
      reqwest(Auth.apiHost + "user/group/all?_=" + (Date.now())).then(function(data) {
        var groups;
        groups = _.map(data, function(group) {
          return {
            id: group.id,
            text: group.name
          };
        });
        return $('#group').select2({
          language: 'zh-CN',
          theme: "bootstrap",
          data: groups
        });
      });
      reqwest(Auth.apiHost + "user/role/handlers?_=" + (Date.now())).then(function(data) {

        /**
         * 16-6-22 用户按地域区分下
         */
        data = _.groupBy(data, function(user) {
          return user.city && user.city.name;
        });
        data = _.map(data, function(users, city) {
          var children;
          children = _.map(users, function(user) {
            return {
              id: user.id,
              text: user.username || user.loginid
            };
          });
          return {
            text: (city === 'null' ? '其他' : city),
            children: children
          };
        });
        return $('#handler').select2({
          language: 'zh-CN',
          theme: "bootstrap",
          data: data
        });
      });
      reqwest(Auth.apiHost + "dict/cities?_=" + (Date.now())).then(function(data) {
        return $('#city').select2({
          language: 'zh-CN',
          theme: "bootstrap",
          data: data
        });
      });
      if (data.id) {
        $('#submitBtn, #resetBtn').addClass('hide');
        reqwest(Auth.apiHost + "/question/" + data.id).then(function(data) {
          var ref;
          questionInfo.attr('title', "编号：" + data.number);
          $('#project, #type').each(function(i, e) {
            if (!$(this).attr('name')) {
              return;
            }
            return $(this).val(data[$(this).attr('name')]).change();
          });
          $('#group, #city, #handler').each(function(i, e) {
            var ref;
            if (!$(this).attr('name')) {
              return;
            }
            return $(this).val((ref = data[$(this).attr('name')]) != null ? ref.id : void 0).change();
          });
          $('form input').each(function(i, e) {
            var d;
            if (!$(this).attr('name')) {
              return;
            }
            d = data[$(this).attr('name')];
            if ($(this).parent().hasClass('date')) {
              return $(this).datepicker('setDate', new Date(d));
            } else {
              return $(this).val(d);
            }
          });
          $('form textarea').each(function(i, e) {
            if (!$(this).attr('name')) {
              return;
            }
            return $(this).val(data[$(this).attr('name')]);
          });
          if (((ref = data.attachmentList) != null ? ref.length : void 0) > 0) {
            table.clear();
            table.rows.add(data.attachmentList);
            table.draw();
            $('#attachmentRow').removeClass('hide');
          } else {
            table.clear();
            $('#attachmentRow').addClass('hide');
          }
          if (!data.closed && !data.handleStatus) {
            return $('#saveBtn').removeClass('hide');
          } else {
            return $('#filePicker').closest('span').addClass('hide');
          }
        }).fail(function() {
          return bootbox.alert('获取问题详细信息失败！');
        });
      }
      $('#resetBtn').unbind('click').bind('click', function(e) {
        var k, ref, v;
        ref = questionInfo.attr();
        for (k in ref) {
          v = ref[k];
          if (k !== 'category' && k !== 'title') {
            questionInfo.attr(k, '');
          }
        }
        $('#startdate').datepicker('setDate', null);
        $('#promisedate').datepicker('setDate', null);
        table.clear().draw();
        return $('#attachmentRow').addClass('hide');
      });
      $('#submitBtn').unbind('click').bind('click', function(e) {
        var attachmentList, k, question, ref, v;
        question = {
          project: $('#project').val(),
          type: $('#type').val(),
          group: {
            id: $('#group').val()
          },
          city: {
            id: $('#city').val()
          },
          handler: {
            id: $('#handler').val()
          },
          startdate: $('#startdate').datepicker('getDate'),
          promisedate: $('#promisedate').datepicker('getDate'),
          description: $('#description').val()
        };
        ref = questionInfo.attr();
        for (k in ref) {
          v = ref[k];
          if (k !== 'title') {
            question[k] = v;
          }
        }
        attachmentList = table.rows().data();
        if (attachmentList.length > 0) {
          question.attachmentList = _.map(attachmentList, function(a) {
            return {
              id: a.id
            };
          });
        }
        return reqwest({
          url: Auth.apiHost + "question/create",
          method: 'post',
          data: JSON.stringify(question),
          contentType: "application/json",
          type: 'html'
        }).then(function() {
          return bootbox.alert('新增成功！', function() {
            return $('#resetBtn').trigger('click');
          });
        }).fail(function(err) {
          return bootbox.alert("新增失败！" + err.responseText);
        });
      });
      $('#saveBtn').click(function() {
        var attachmentList, question;
        question = {
          id: $('#id').val(),
          category: $('#category').val(),
          project: $('#project').val(),
          type: $('#type').val(),
          group: {
            id: $('#group').val()
          },
          city: {
            id: $('#city').val()
          },
          handler: {
            id: $('#handler').val()
          },
          startdate: $('#startdate').datepicker('getDate'),
          promisedate: $('#promisedate').datepicker('getDate'),
          description: $('#description').val()
        };
        attachmentList = table.rows().data();
        if (attachmentList.length > 0) {
          question.attachmentList = _.map(attachmentList, function(a) {
            return {
              id: a.id
            };
          });
        }
        return reqwest({
          url: Auth.apiHost + "question/update",
          method: 'post',
          data: JSON.stringify(question),
          contentType: "application/json",
          type: 'html'
        }).then(function() {
          return bootbox.alert('保存成功！', function() {
            return history.go(-1);
          });
        }).fail(function(err) {
          return bootbox.alert("保存失败！" + err.responseText);
        });
      });
      $('#filePicker').fileupload({
        url: Auth.apiHost + "question/attachment/upload",
        dataType: 'json',
        done: function(e, data) {
          table.row.add(data.result).draw(false);
          return $('#attachmentRow').removeClass('hide');
        },
        fail: function() {
          return bootbox.alert('附件上传失败');
        }
      }).prop('disabled', !$.support.fileInput).parent().addClass((ref = $.support.fileInput) != null ? ref : {
        undefined: 'disabled'
      });
      table = $('#attachmentList').DataTable({
        paging: false,
        ordering: false,
        searching: false,
        info: false,
        columns: [
          {
            data: 'id',
            visible: false
          }, {
            data: 'filename',
            render: function(data, d, row) {
              return "<a href='" + Auth.apiHost + "question/attachment/" + row.id + "/download'>" + data + "</a>";
            }
          }, {
            data: 'uploaded',
            render: function(data) {
              if (data) {
                return new Date(data).toLocaleString();
              } else {
                return new Date().toLocaleString();
              }
            }
          }, {
            data: 'size'
          }, {
            data: 'uploader',
            render: function(data) {
              return (data != null ? data.username : void 0) || (data != null ? data.email : void 0) || '';
            }
          }, {
            data: 'uploader',
            render: function(data) {
              var ref1;
              if (((ref1 = Auth.user()) != null ? ref1.loginID : void 0) === data.loginid) {
                return $('<button/>').addClass('btn btn-sm btn-danger').text('删除')[0].outerHTML;
              } else {
                return '无';
              }
            }
          }
        ]
      });
      return $('#attachmentList tbody').on('click', 'button.btn-danger', function() {
        table.row($(this).parents('tr')).remove().draw();
        if (table.rows().data().length <= 0) {
          return $('#attachmentRow').addClass('hide');
        }
      });
    }
  });
});
