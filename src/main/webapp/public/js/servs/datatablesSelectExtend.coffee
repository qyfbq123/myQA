define [], ()->
  return {
    updateDataTableSelectAllCtrl: (table)->
      $table             = table.table().node()
      $chkbox_all        = $('tbody input[type="checkbox"]', $table)
      $chkbox_checked    = $('tbody input[type="checkbox"]:checked', $table)
      chkbox_select_all  = $('thead input[name="select_all"]', $table).get(0)

      if $chkbox_checked.length == 0
        chkbox_select_all.checked = false
        if 'indeterminate' in chkbox_select_all
           chkbox_select_all.indeterminate = false

      else if $chkbox_checked.length == $chkbox_all.length
        chkbox_select_all.checked = true;
        if 'indeterminate' in chkbox_select_all
           chkbox_select_all.indeterminate = false
      else 
        chkbox_select_all.checked = true;
        if 'indeterminate' in chkbox_select_all
           chkbox_select_all.indeterminate = true
      
}