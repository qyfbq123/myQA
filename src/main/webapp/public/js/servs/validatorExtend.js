// Generated by CoffeeScript 1.10.0
$.validator.setDefaults({
  highlight: function(element) {
    return $(element).closest('.form-group').addClass('has-error');
  },
  unhighlight: function(element) {
    return $(element).closest('.form-group').removeClass('has-error');
  },
  errorElement: 'span',
  errorClass: 'help-block',
  errorPlacement: function(error, element) {
    if (element.parent('.input-group').length) {
      return error.insertAfter(element.parent());
    } else {
      return error.insertAfter(element);
    }
  }
});
