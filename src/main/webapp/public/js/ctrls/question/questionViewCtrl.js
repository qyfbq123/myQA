// Generated by CoffeeScript 1.10.0
define(['can/control', 'can', 'Auth', 'base', 'reqwest', 'bootbox', 'localStorage', 'select2cn'], function(Ctrl, can, Auth, base, reqwest, bootbox, localStorage) {
  return Ctrl.extend({
    init: function(el, data) {
      var closeQuestion, generateRow, questionInfo;
      if (!can.base) {
        new base('', data);
      }
      questionInfo = new can.Map({
        category: data.category,
        closed: data.closed,
        pageinfo: "" + (data.category === 'PM' ? '事件' : data.category) + (data.closed ? '历史解决查询' : '关闭'),
        handleStatus: 2
      });
      this.element.html(can.view('../public/view/home/question/questionView.html', questionInfo));
      closeQuestion = function(question, btn) {
        return bootbox.prompt({
          title: "解决回馈：",
          inputType: 'textarea',
          callback: function(feedback) {
            if (feedback !== null) {
              return reqwest({
                url: Auth.apiHost + "question/close/" + question.id,
                data: {
                  feedback: feedback
                },
                method: "put",
                type: 'html'
              }).then(function() {
                bootbox.alert('关闭成功。');
                return $(btn).closest('.row').hide();
              }).fail(function() {
                return bootbox.alert('关闭失败！');
              });
            }
          }
        });
      };
      generateRow = function(question) {
        var $a, $body, $btn, $header, $row, cls, ref, ref1, ref2, ref3, ref4;
        cls = question.closed ? 'panel-warning' : 'panel-info';
        cls = question.toTop ? 'panel-danger' : cls || 'panel-info';
        $row = $('<div class="row question"><div class="col-lg-10 col-lg-offset-1"><div class="panel ' + cls + '"><div></div></div>');
        $header = $('<div class="panel-heading clearfix"><div>');
        $header.append($('<div class="panel-title pull-left"/>').text("编号：" + question.number));
        if (!question.closed) {
          if (questionInfo.category !== 'PM') {
            $btn = $('<button class="btn btn-default btn-sm" type="button"><span class="glyphicon glyphicon-close" aria-hidden="true">关闭</span></button>').bind('click', function(e) {
              return closeQuestion(question, this);
            });
            $header.append($('<div class="btn-group pull-right"></div>').append($btn));
          } else {
            $a = $("<a class='btn btn-default btn-sm' href='#!home/question/" + questionInfo.category + "/" + question.id + "'><span aria-hidden='true'>详情</span></button>");
            $header.append($('<div class="btn-group pull-right"></div>').append($a));
          }
        }
        $('.panel', $row).append($header);
        $body = $('<div class="panel-body"></div>');
        $body.append($('<p/>').text("项目：" + question.project));
        $body.append($('<p/>').text("问题类型：" + question.type));
        if (questionInfo.category !== 'PM') {
          $body.append($('<p/>').text("城市：" + ((ref = question.city) != null ? ref.name : void 0)));
          if (!question.closed) {
            $body.append($('<p/>').text("项目发起时间：" + (question.startdate ? new Date(question.startdate).toLocaleDateString() : '')));
            $body.append($('<p/>').text("要求结束时间：" + (question.promisedate ? new Date(question.promisedate).toLocaleDateString() : '')));
          }
          $body.append($('<p/>').text("问题描述：" + question.description));
          if (question.closed) {
            $body.append($('<p/>').text("解决回馈：" + question.feedback));
          }
        }
        $body.append($('<p/>').text("发起人：" + (((ref1 = question.creator) != null ? ref1.username : void 0) || ((ref2 = question.creator) != null ? ref2.loginid : void 0) || '')));
        if (questionInfo.category !== 'PM') {
          $body.append($('<p/>').text("处理人：" + (((ref3 = question.handler) != null ? ref3.username : void 0) || ((ref4 = question.handler) != null ? ref4.loginid : void 0) || '')));
          $body.append($('<p/>').text("处理结果：" + question.handleResult));
        }
        $body.append($('<p/>').text("提交时间：" + (question.created ? new Date(question.created).toLocaleString() : '')));
        if (question.closed) {
          $body.append($('<p/>').text("关闭时间：" + (question.modified ? new Date(question.modified).toLocaleString() : '')));
        }
        if (questionInfo.category === 'PM') {
          $body.append($('<p/>').text("所属供应商：" + (question.supplier || '')));
          $body.append($('<p/>').text("问题发现时间：" + (question.issueDate ? new Date(question.issueDate).toLocaleString() : '')));
          $body.append($('<p/>').text("是否由客户反馈：" + (question.isCFeedback ? 'Y' : 'N')));
          $body.append($('<p/>').text("补救方案期限：" + (question.containmentPlanDate ? new Date(question.containmentPlanDate).toLocaleString() : '')));
          $body.append($('<p/>').text("改善方案期限：" + (question.actionPlanDate ? new Date(question.actionPlanDate).toLocaleString() : '')));
          $body.append($('<p/>').text("问题严重性：" + (question.severity || '')));
          $body.append($('<p/>').text("始发地和涉及库房信息：" + (question.beginStorehouse || '')));
          $body.append($('<p/>').text("SPC名：" + (question.spc || '')));
          $body.append($('<p/>').text("订单号：" + (question.orderNo || '')));
          $body.append($('<p/>').text("运单号：" + (question.hawb || '')));
          $body.append($('<p/>').text("备案信息：" + (question.partInformation || '')));
          $body.append($('<p/>').text("原定派送／取件时间：" + (question.scheduledTime ? new Date(question.scheduledTime).toLocaleString() : '')));
          $body.append($('<p/>').text("实际派送／取件时间：" + (question.actualTime ? new Date(question.actualTime).toLocaleString() : '')));
          $body.append($('<p/>').text("问题描述：" + (question.problemStatement || '')));

          /**
           * 16-6-20 隐藏情况反馈
           */
          $body.append($('<p/>').text("补救方案描述：" + (question.recoveryDescription || '')));
          $body.append($('<p/>').text("问题根本原因：" + (question.rootCause || '')));
          $body.append($('<p/>').text("改善方案：" + (question.correctiveAction || '')));
        }
        $('.panel', $row).append($body);
        return $('#page-wrapper').append($row);
      };
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
      reqwest(Auth.apiHost + "dict/types?_=" + (Date.now())).then(function(data) {
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
        return $('#type').select2({
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
      $('#searchForm button').unbind('click').bind('click', function(e) {
        var k, search, v;
        $('#page-wrapper>.row.question').remove();
        search = $('#searchForm').serializeObject();
        for (k in search) {
          v = search[k];
          if (!v || v === 'unselected') {
            delete search[k];
          }
        }
        return reqwest({
          url: Auth.apiHost + "question/page?_=" + (Date.now()),
          data: search
        }).then(function(data) {
          return _.each(data, function(question) {
            return generateRow(question);
          });
        });
      });
      return $('#searchForm button').trigger('click');
    }
  });
});
