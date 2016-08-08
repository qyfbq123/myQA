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
