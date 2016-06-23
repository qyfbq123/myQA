// Generated by CoffeeScript 1.10.0

/**
 * 16-6-20 添加这个文件处理事件关闭
 */
define(['can/control', 'can', 'Auth', 'base', 'reqwest', 'bootbox', 'localStorage', 'select2cn', 'datepickercn', 'uploader', 'datatables.net', 'datatables.net-bs', 'es6shim', 'jqueryFileupload'], function(Ctrl, can, Auth, base, reqwest, bootbox, localStorage) {
  return Ctrl.extend({
    init: function(el, data) {
      var dialogLogin, fetchQuestion, pageInfo;
      pageInfo = new can.Map({
        pageinfo: location.hash.indexOf('home') !== -1 ? '事件关闭' : '事件详情'
      });
      this.element.html(can.view('../public/view/home/question/pmQuestionClose.html', pageInfo));
      if (!Auth.logined()) {
        $('#submitBtn').addClass('hide');
      } else if (!can.base) {
        new base('', data);
      }

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
        return $('#project').select2({
          language: 'zh-CN',
          theme: "bootstrap",
          data: data
        });
      });
      reqwest(Auth.apiHost + "dict/types?_=" + (Date.now())).then(function(data) {
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
        return $('#supplier').select2({
          language: 'zh-CN',
          theme: "bootstrap",
          data: data
        });
      });
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
      fetchQuestion = function(fetchUrl) {
        $('#question .row .chat-panel').remove();
        return reqwest(fetchUrl).then(function(data) {
          var ref;
          if ($('#question').css('visibility') === 'hidden') {
            $('#question').css('visibility', 'inherit');
          }
          $('#question').addClass('hide');
          if (!data.id) {
            return;
          }
          $('#number').val(data.number);
          data.isCFeedback = Number(data.isCFeedback);
          if (data.teammates) {
            data.teammates = JSON.parse(data.teammates);
          }
          $('#question form select').each(function(i, e) {
            if (!$(this).attr('name')) {
              return;
            }
            return $(this).val(data[$(this).attr('name')]).change();
          });
          $('#question form input').each(function(i, e) {
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
          $('#question form textarea').each(function(i, e) {
            var $chat, records;
            $(this).val('');
            if (!$(this).attr('name')) {
              return;
            }
            if (records = data[$(this).attr('name')]) {
              records = records.replace(/\n$/, '');
              $chat = $('#sampleChat').clone().removeAttr('id');
              _.each(records.split('\n'), function(record, index) {
                var $li;
                if (index % 2 === 0) {
                  if (index % 4 < 2) {
                    $li = $('#sampleOdd', $chat).clone().removeAttr('id');
                  } else {
                    $li = $('#sampleEven', $chat).clone().removeAttr('id');
                  }
                  $('ul.chat', $chat).append($li);
                }
                if (index % 2 === 0) {
                  return $('li:last strong', $chat).text(record);
                } else {
                  return $('li:last p', $chat).html(record);
                }
              });
              $('#sampleOdd, #sampleEven', $chat).remove();
              return $chat.removeClass('hide').insertBefore($(this));
            }
          });
          if (data.attachmentPath) {
            $('#attachment').attr('href', Auth.apiHost + "question/" + data.id + "/attachment/download").removeClass('hide');
          } else {
            $('#attachment').closest('.row').addClass('hide');
          }
          $('#question').removeClass('hide');
          if (!Auth.logined()) {
            $('#question').bind('change', function() {
              if (Auth.logined()) {
                $('#question').unbind('change');
                $('#submitBtn').removeClass('hide');
                if (data.creator.leader && data.creator.leader.loginid === Auth.user().loginID) {
                  $('#suggest').removeClass('hide');
                  $('#closeBtn').removeClass('hide');
                }
                return;
              }
              return dialogLogin(function() {
                $('#submitBtn').removeClass('hide');
                if (data.creator.leader && data.creator.leader.loginid === Auth.user().loginID) {
                  $('#suggest').removeClass('hide');
                  return $('#closeBtn').removeClass('hide');
                }
              });
            });
          } else {
            if (data.creator.leader && data.creator.leader.loginid === ((ref = Auth.user()) != null ? ref.loginID : void 0)) {
              $('#suggest').removeClass('hide');
              $('#closeBtn').removeClass('hide');
            }
          }
          console.log(data.closed);
          if (data.closed) {
            return $('#submitBtn, #closeBtn').remove();
          }
        }).fail(function() {
          $('#question').addClass('hide');
          return bootbox.alert('获取问题详细信息失败！');
        });
      };
      if (data.id) {
        fetchQuestion(Auth.apiHost + "question/" + data.id);
      }
      if (data.number) {
        fetchQuestion(Auth.apiHost + "question/byNumber/" + data.number);
      }
      $('#searchForm button').unbind('click').bind('click', function(e) {
        if (!$('#number').val()) {
          return $('#question').addClass('hide');
        }
        return fetchQuestion(Auth.apiHost + "question/byNumber/" + ($('#number').val()));
      });
      $('#submitBtn').unbind('click').bind('click', function(e) {
        var question;
        question = $('#question form').serializeObject();
        $('.input-group.date input').each(function(e, i) {
          return question[$(this).attr('name')] = $(this).datepicker('getDate');
        });
        $('#question form textarea').each(function() {
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
        return reqwest({
          url: Auth.apiHost + "question/update",
          method: 'post',
          data: JSON.stringify(question),
          contentType: "application/json",
          type: 'html'
        }).then(function() {
          return bootbox.alert('保存成功！', function() {
            return fetchQuestion(Auth.apiHost + "question/" + question.id);
          });
        }).fail(function(err) {
          return bootbox.alert("保存失败！" + err.responseText);
        });
      });
      return $('#closeBtn').unbind('click').bind('click', function(e) {
        return bootbox.prompt({
          title: "解决回馈：",
          inputType: 'textarea',
          callback: function(feedback) {
            if (feedback !== null) {
              return reqwest({
                url: Auth.apiHost + "question/close/" + ($('#question form input[name="id"]').val()),
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
    }
  });
});
