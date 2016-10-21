// Generated by CoffeeScript 1.10.0
define(['can/control', 'can', 'base', 'Auth', 'reqwest', 'bootbox', '_', 'select2cn', 'datepickercn', 'validate'], function(Control, can, base, Auth, reqwest, bootbox) {
  return Control.extend({
    init: function(el, data) {
      var msgData;
      msgData = new can.Map();
      this.element.html(can.view('../public/view/home/dashboard/bulletinNew.html', msgData));
      $('#function').select2({
        language: 'zh-CN',
        theme: "bootstrap",
        placeholder: 'Select a Function'
      });
      $('#elc').select2({
        language: 'zh-CN',
        theme: "bootstrap",
        placeholder: 'Select an ELC'
      });
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
        return $('#projectName').select2({
          language: 'zh-CN',
          theme: "bootstrap",
          data: data,
          placeholder: 'Select a Project'
        });
      });
      reqwest(Auth.apiHost + "dict/bulletinTypes?_=" + (Date.now())).then(function(data) {
        data = _.map(data, function(d) {
          return {
            id: d.text,
            text: d.text
          };
        });
        return $('#type').select2({
          language: 'zh-CN',
          theme: "bootstrap",
          data: data,
          placeholder: 'Select a Type'
        });
      });

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
          $("#teammates").append($('<div class="col-sm-2 text-right"/>').html("<input type='checkbox'/>" + cityName + ":"));
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
      $('#teammates').on('change', '.col-sm-2 :checkbox', function() {
        return $(':checkbox', $(this).parent().next('.col-sm-10')).prop('checked', $(this).is(':checked'));
      });
      return $('#submitBtn').unbind('click').bind('click', function() {
        var msgObj;
        if (!$('form').valid()) {
          return;
        }
        msgObj = $('form').serializeObject();
        msgObj.date = $('#date').datepicker('getDate');
        if (msgObj.teammates && msgObj.teammates.length > 0) {
          if (_.isString(msgObj.teammates)) {
            msgObj.teammates = [msgObj.teammates];
          }
          msgObj.receivers = _.map(msgObj.teammates, function(e, i) {
            return {
              id: e
            };
          });
        }
        delete msgObj.teammates;
        return reqwest({
          url: Auth.apiHost + "msg/create",
          method: 'post',
          data: JSON.stringify(msgObj),
          contentType: "application/json",
          type: 'html'
        }).then(function() {
          return bootbox.alert('发布成功！', function() {
            return location.hash = '#!home/msgDashboard';
          });
        }).fail(function(err) {
          return bootbox.alert("发布失败！" + err.responseText);
        });
      });
    }
  });
});
