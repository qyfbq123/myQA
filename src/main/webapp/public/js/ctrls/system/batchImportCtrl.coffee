define ['can/control', 'can', 'Auth', 'base', 'reqwest', 'bootbox', 'uploader'], (Ctrl, can, Auth, base, reqwest, bootbox)->
  return Ctrl.extend
    init: (el, data)->
      if !can.base
        new base('', data)

      this.element.html can.view('../public/view/home/system/batchImport.html')

      $('#dailyReport').uploadify({
        'successTimeout': 2*60
        'width': 200
        'fileSizeLimit': '50960k'
        'buttonText': "提交ELC File"
        'fileTypeDesc' : '文件',
        'swf': './lib/uploadify/uploadify.swf',
        'uploader': "#{Auth.apiHost}question/dailyReport/upload2",
        'onUploadSuccess': (file, data, response)->
          if data == "ok"
            bootbox.alert '上传成功'
          else
            bootbox.alert 'ELC File上传失败:' + data

        'onUploadError': (file, errorCode, errorMsg, errorString)->
          bootbox.alert 'ELC File上传失败:' + errorMsg
      });