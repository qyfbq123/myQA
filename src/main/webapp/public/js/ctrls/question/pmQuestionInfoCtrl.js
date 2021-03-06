// Generated by CoffeeScript 1.10.0
define(['can/control', 'can', 'Auth', 'base', 'reqwest', 'bootbox', 'localStorage', 'select2cn', 'datepickercn', 'uploader', 'datatables.net', 'datatables.net-bs', 'es6shim', 'jqueryFileupload'], function(Ctrl, can, Auth, base, reqwest, bootbox, localStorage) {
  return Ctrl.extend({
    init: function(el, data) {
      var questionInfo, ref, table;
      if (!can.base) {
        new base('', data);
      }

      /**
       * data.id存在即为显示详情，其它为新增
       */
      questionInfo = new can.Map({
        title: data.id ? "详情" : "事件管理 / 新增"
      });
      this.element.html(can.view('../public/view/home/question/pmQuestionInfo.html', questionInfo));

      /**
       * 获取pm组事件参与人
       */
      reqwest(Auth.apiHost + "user/group/pm?_=" + (Date.now())).then(function(data) {

        /**
         * 16-6-22 用户按地域区分显示
         */
        var generateCityTeammates;
        data = _.groupBy(data, function(u) {
          return u.city && u.city.name;
        });
        generateCityTeammates = function(cityName, users) {
          var $div;
          $("#teammates").append($('<div class="col-sm-2"/>').text(cityName + ":"));
          $div = $('<div class="col-sm-10"/>');
          _.each(users, function(user) {
            var $input, $label;
            $label = $('<label/>').text(user.username);
            $input = $('<input type="checkbox" name="teammates"/>').val(user.id).prependTo($label);
            return $div.append($label);
          });
          return $('#teammates').append($div);
        };
        return reqwest(Auth.apiHost + "dict/cities?_=" + (Date.now())).then(function(cities) {
          var k, results, v;
          _.each(cities, function(city) {
            if (data[city.text]) {
              generateCityTeammates(city.text, data[city.text]);
              return delete data[city.text];
            }
          });
          results = [];
          for (k in data) {
            v = data[k];
            k = k === 'null' ? '其他' : k;
            results.push(generateCityTeammates(k, v));
          }
          return results;
        });
      });

      /**
       * 日期组件
       */
      $('.input-group.date input').datepicker({
        language: 'zh-CN',
        todayBtn: true,
        autoclose: true
      });

      /**
       * 下拉框填充
       */
      $('#isCFeedback').select2({
        language: 'zh-CN',
        theme: "bootstrap"
      });
      reqwest(Auth.apiHost + "dict/projects?_=" + (Date.now())).then(function(data) {
        data = _.map(data, function(d) {
          return {
            id: d.text,
            text: d.text
          };
        });
        data.unshift({
          id: 'unselected',
          text: '请选择'
        });
        return $('#project').select2({
          language: 'zh-CN',
          theme: "bootstrap",
          data: data
        });
      });
      reqwest(Auth.apiHost + "dict/types/pm?_=" + (Date.now())).then(function(data) {
        data = _.map(data, function(d) {
          return {
            id: d.text,
            text: d.text
          };
        });
        data.unshift({
          id: 'unselected',
          text: '请选择'
        });
        return $('#type').select2({
          language: 'zh-CN',
          theme: "bootstrap",
          data: data
        });
      });
      reqwest(Auth.apiHost + "dict/severity?_=" + (Date.now())).then(function(data) {
        data = _.map(data, function(d) {
          return {
            id: d.text,
            text: d.text
          };
        });
        return $('#severity').select2({
          language: 'zh-CN',
          theme: "bootstrap",
          data: data
        });
      });
      reqwest(Auth.apiHost + "dict/beginStorehouses?_=" + (Date.now())).then(function(data) {
        data = _.map(data, function(d) {
          return {
            id: d.text,
            text: d.text
          };
        });
        data.unshift({
          id: 'unselected',
          text: '请选择'
        });
        return $('#beginStorehouse').select2({
          language: 'zh-CN',
          theme: "bootstrap",
          data: data
        });
      });

      /**
       * 16-6-20 所属供应商 改为下拉框
       */
      reqwest(Auth.apiHost + "dict/suppliers?_=" + (Date.now())).then(function(data) {
        data = _.map(data, function(d) {
          return {
            id: d.text,
            text: d.text
          };
        });
        data.unshift({
          id: 'unselected',
          text: '请选择'
        });
        return $('#supplier').select2({
          language: 'zh-CN',
          theme: "bootstrap",
          data: data
        });
      });

      /**
       * 显示问题详情
       */
      if (data.id) {
        if (location.hash.startsWith('#!home')) {
          $('#submitBtn').addClass('hide');
          $('#closeBtn').removeClass('hide').unbind('click').bind('click', function(e) {
            return bootbox.prompt({
              title: "解决回馈：",
              inputType: 'textarea',
              callback: function(feedback) {
                if (feedback !== null) {
                  return reqwest({
                    url: Auth.apiHost + "question/close/" + data.id,
                    data: {
                      feedback: feedback
                    },
                    method: "put",
                    type: 'html'
                  }).then(function() {
                    bootbox.alert('关闭成功。');
                    return history.go(-1);
                  }).fail(function() {
                    return bootbox.alert('关闭失败！');
                  });
                }
              }
            });
          });
        } else {
          $('#submitBtn').closest('.form-group').addClass('hide');
        }
        reqwest(Auth.apiHost + "question/" + data.id).then(function(data) {
          questionInfo.attr('title', "编号：" + data.number);
          data.isCFeedback = Number(data.isCFeedback);
          if (data.teammates) {
            data.teammates = JSON.parse(data.teammates);
          }
          $('select').each(function(i, e) {
            if (!$(this).attr('name')) {
              return;
            }
            return $(this).val(data[$(this).attr('name')] || 'unselected').change();
          });
          $('form input').each(function(i, e) {
            var d;
            if (!$(this).attr('name')) {
              return;
            }
            d = data[$(this).attr('name')];
            if ($(this).parent().hasClass('date')) {
              if (d) {
                return $(this).datepicker('setDate', new Date(d));
              }
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
          if (data.attachmentPath) {
            $('#filePicker').parent().addClass('hide');
            return $('#attachment').attr('href', Auth.apiHost + "question/" + data.id + "/attachment/download").removeClass('hide');
          } else {
            return $('#filePicker').closest('.row').addClass('hide');
          }
        }).fail(function() {
          return bootbox.alert('获取问题详细信息失败！');
        });
      }
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

      /**
       * 重置页面表单
       */
      $('#resetBtn').unbind('click').bind('click', function(e) {
        var k, ref1, v;
        ref1 = questionInfo.attr();
        for (k in ref1) {
          v = ref1[k];
          if (k !== 'title') {
            questionInfo.attr(k, '');
          }
        }
        $('#teammates :checked').prop('checked', false);
        $('.input-group.date input').datepicker('setDate', null);
        table.clear().draw();
        return $('#attachmentRow').addClass('hide');
      });

      /**
       * 16-7-4 附件修改
       */
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
            data: 'question_id',
            render: function(data, d, row) {
              return $("<button data-id='" + row.id + "'/>").addClass('btn btn-sm btn-danger').text('删除')[0].outerHTML;
            }
          }
        ]
      });
      $('#attachmentList tbody').on('click', 'button.btn-danger', function() {
        table.row($(this).parents('tr')).remove().draw();
        if (table.rows().data().length <= 0) {
          $('#attachmentRow').addClass('hide');
        }
        return reqwest({
          url: Auth.apiHost + "question/attachment/" + ($(this).data('id')),
          method: 'delete',
          type: 'html'
        }).then(function() {});
      });

      /**
       * 保存问题
       */
      return $('#submitBtn').unbind('click').bind('click', function(e) {
        var attachmentList, question;
        question = $('form').serializeObject();
        $('select').each(function(i, e) {
          if (!$(this).attr('name')) {
            return;
          }
          if ($(this).val() === 'unselected') {
            return delete question[$(this).attr('name')];
          }
        });
        $('.input-group.date input').each(function(e, i) {
          return question[$(this).attr('name')] = $(this).datepicker('getDate');
        });

        /**
         * 16-6-20 textarea转纪录
         */
        $('form textarea').each(function() {
          if (question[$(this).attr('name')]) {
            return question[$(this).attr('name')] = (new Date().toLocaleString()) + " 来自 " + (Auth.user().username) + "\n" + (question[$(this).attr('name')].replace(/\n/g, '<br/>')) + "\n";
          }
        });
        question.isCFeedback = !!parseInt(question.isCFeedback);
        if (question.teammates && !_.isArray(question.teammates)) {
          question.teammates = [question.teammates];
        }
        if (question.teammates) {
          question.teammates = JSON.stringify(question.teammates);
        }

        /**
         * 16-7-4 问题附件
         */
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
            $('#resetBtn').trigger('click');
            return $('#filePicker').parent().nextAll('label').remove();
          });
        }).fail(function(err) {
          return bootbox.alert("新增失败！" + err.responseText);
        });
      });
    }
  });
});
