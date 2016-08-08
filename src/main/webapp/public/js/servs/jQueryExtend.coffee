
define ['$', 'validate'], ()->
  # $.extend(jQuery.validator.messages, {
  #   required: "必选字段",
  #   remote: "请修正该字段",
  #   email: "请输入正确格式的电子邮件",
  #   url: "请输入合法的网址",
  #   date: "请输入合法的日期",
  #   dateISO: "请输入合法的日期 (ISO).",
  #   number: "请输入合法的数字",
  #   digits: "只能输入整数",
  #   creditcard: "请输入合法的信用卡号",
  #   equalTo: "请再次输入相同的值",
  #   accept: "请输入拥有合法后缀名的字符串",
  #   maxlength: jQuery.validator.format("请输入一个 长度最多是 {0} 的字符串"),
  #   minlength: jQuery.validator.format("请输入一个 长度最少是 {0} 的字符串"),
  #   rangelength: jQuery.validator.format("请输入 一个长度介于 {0} 和 {1} 之间的字符串"),
  #   range: jQuery.validator.format("请输入一个介于 {0} 和 {1} 之间的值"),
  #   max: jQuery.validator.format("请输入一个最大为{0} 的值"),
  #   min: jQuery.validator.format("请输入一个最小为{0} 的值")
  # });

  # override jquery validate plugin defaults
  $.validator.setDefaults
    highlight: (element)->
        $(element).closest('.form-group').addClass 'has-error'
    unhighlight: (element)->
        $(element).closest('.form-group').removeClass 'has-error'
    errorElement: 'span'
    errorClass: 'help-block'
    errorPlacement: (error, element)->
        if element.parent('.input-group').length
            error.insertAfter element.parent()
        else
            error.insertAfter element

  $.getJSON = (url, data, success, error)->
    return jQuery.ajax
      headers:
          'Accept': 'application/json'
          'Content-Type': 'application/json'
      'type': 'GET'
      'url': url
      'data': data
      'success': success
      'error': error

  $.postJSON = (url, data, success, error)->
    return jQuery.ajax
      headers:
          'Accept': 'application/json'
          'Content-Type': 'application/json'
      'type': 'POST'
      'url': url
      'data': JSON.stringify(data)
      'dataType': 'json'
      'success': success
      'error': error

  $.fn.serializeObject = ->
    o = {}
    a = this.serializeArray()
    $.each a, ->
      if(o[this.name] != undefined) 
          if(!o[this.name].push)
            o[this.name] = [o[this.name]]
          o[this.name].push(this.value || '')
      else
          o[this.name] = this.value || ''
    o
