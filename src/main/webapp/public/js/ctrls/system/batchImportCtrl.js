// Generated by CoffeeScript 1.10.0
define(['can/control', 'can', 'Auth', 'base', 'reqwest', 'bootbox', 'uploader'], function(Ctrl, can, Auth, base, reqwest, bootbox) {
  return Ctrl.extend({
    init: function(el, data) {
      if (!can.base) {
        new base('', data);
      }
      this.element.html(can.view('../public/view/home/system/batchImport.html'));
      return $('#dailyReport').uploadify({
        'successTimeout': 2 * 60,
        'width': 200,
        'fileSizeLimit': '50960k',
        'buttonText': "提交ELC File",
        'fileTypeDesc': '文件',
        'swf': './lib/uploadify/uploadify.swf',
        'uploader': Auth.apiHost + "question/dailyReport/upload",
        'onUploadSuccess': function(file, data, response) {
          if (data === "ok") {
            return bootbox.alert('上传成功');
          } else {
            return bootbox.alert('ELC File上传失败:' + data);
          }
        },
        'onUploadError': function(file, errorCode, errorMsg, errorString) {
          return bootbox.alert('ELC File上传失败:' + errorMsg);
        }
      });
    }
  });
});
