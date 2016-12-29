// Generated by CoffeeScript 1.10.0
define(['can/control', 'can', 'Auth', 'base', 'reqwest', 'bootbox', 'localStorage', 'select2cn', 'datepickercn', 'datatables.net', 'datatables.net-bs', 'es6shim', 'jqueryFileupload'], function(Ctrl, can, Auth, base, reqwest, bootbox, localStorage) {
  return Ctrl.extend({
    init: function(el, data) {
      var contractInfo, isNew, ref, table;
      if (!can.base) {
        new base('', data);
      }
      contractInfo = new can.Map();
      this.element.html(can.view('../public/view/home/administration/contractAdd.html', contractInfo));
      isNew = data.id ? false : true;
      $('select').select2({
        language: 'zh-CN',
        theme: "bootstrap",
        placeholder: '请选择'
      });
      $('.input-group.date input').datepicker({
        language: 'zh-CN',
        todayBtn: true,
        autoclose: true
      });
      $('#filePicker').fileupload({
        url: Auth.apiHost + "doc/file/upload",
        dataType: 'json',
        send: function() {
          return $('#filePicker').prop('disabled', true);
        },
        done: function(e, data) {
          table.row.add(data.result).draw(false);
          $('#fileRow').removeClass('hide');
          return $('#filePicker').closest('span').addClass('hide');
        },
        fail: function() {
          return bootbox.alert('合同上传失败');
        },
        always: function() {
          return $('#filePicker').prop('disabled', false);
        }
      }).prop('disabled', !$.support.fileInput).parent().addClass((ref = $.support.fileInput) != null ? ref : {
        undefined: 'disabled'
      });
      table = $('#fileList').DataTable({
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
              return "<a href='" + Auth.apiHost + "doc/file/" + row.id + "/download'>" + data + "</a>";
            }
          }, {
            data: 'size'
          }, {
            data: 'uploader',
            render: function(data, d, row) {
              return $("<button data-id='" + row.id + "'/>").addClass('btn btn-sm btn-danger').data('id', row.id).text('删除')[0].outerHTML;
            }
          }
        ]
      });
      $('#fileList tbody').on('click', 'button.btn-danger', function() {
        table.row($(this).parents('tr')).remove().draw();
        if (table.rows().data().length <= 0) {
          $('#fileRow').addClass('hide');
          $('#filePicker').closest('span').removeClass('hide');
        }
        return reqwest({
          url: Auth.apiHost + "doc/file/" + ($(this).data('id')),
          method: 'delete',
          type: 'html'
        }).then(function() {});
      });
      $('#saveBtn').click(function() {
        var contract, fileList;
        contract = contractInfo.attr();
        if (isNew) {
          fileList = table.rows().data();
          if (fileList.length > 0) {
            contract.file = {
              id: fileList[0].id
            };
          } else {
            bootbox.alert('必须上传合同！');
            return;
          }
        }
        $('select').each(function(i, e) {
          if (!$(this).attr('name')) {
            return;
          }
          if (!$(this).val()) {
            return delete contract[$(this).attr('name')];
          }
        });
        $('.input-group.date input').each(function(e, i) {
          return contract[$(this).attr('name')] = $(this).datepicker('getDate');
        });
        if (isNew) {
          return reqwest({
            url: Auth.apiHost + "doc/createContract",
            method: 'post',
            data: JSON.stringify(contract),
            contentType: "application/json",
            type: 'html'
          }).then(function() {
            return bootbox.alert('保存成功！', function() {
              var k, ref1, v;
              ref1 = contractInfo.attr();
              for (k in ref1) {
                v = ref1[k];
                contractInfo.attr(k, '');
              }
              $('.input-group.date input').datepicker('setDate', null);
              table.clear().draw();
              $('#fileRow').addClass('hide');
              return $('#filePicker').closest('span').removeClass('hide');
            });
          }).fail(function(err) {
            return bootbox.alert("保存失败！" + err.responseText);
          });
        } else {
          return reqwest({
            url: Auth.apiHost + "doc/updateContract",
            method: 'put',
            data: JSON.stringify(contract),
            contentType: "application/json",
            type: 'html'
          }).then(function() {
            return bootbox.alert('保存成功！', function() {
              return history.go(-1);
            });
          }).fail(function(err) {
            return bootbox.alert("保存失败！" + err.responseText);
          });
        }
      });
      if (data.id) {
        return reqwest(Auth.apiHost + "doc/contract/" + data.id + "?_=" + (Date.now())).then(function(data) {
          contractInfo.attr(data);
          $('select').each(function() {
            if (!$(this).attr('name')) {
              return;
            }
            return $(this).val(data[$(this).attr('name')] || '').change();
          });
          $('.input-group.date input').each(function() {
            var d;
            if (!$(this).attr('name')) {
              return;
            }
            d = data[$(this).attr('name')];
            if (d) {
              return $(this).datepicker('setDate', new Date(d));
            }
          });
          $('#filePicker').closest('span').addClass('hide');
          if (data.file) {
            table.row.add(data.file).draw(false);
            table.column(3).visible(false);
            return $('#fileRow').removeClass('hide');
          }
        });
      }
    }
  });
});