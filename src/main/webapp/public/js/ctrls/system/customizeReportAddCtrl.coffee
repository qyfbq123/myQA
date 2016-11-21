define ['can/control', 'can', 'Auth', 'base', 'reqwest', 'bootbox', 'localStorage', 'select2cn', '_', 'validate'], (Ctrl, can, Auth, base, reqwest, bootbox, localStorage)->

  return Ctrl.extend
    init: (el, data)->
      if !can.base
        new base('', data)

      addPageData = new can.Map
        pageType: '新增'

      this.element.html can.view('../public/view/home/system/customizeReportAdd.html', addPageData)

      isNew = window.location.hash.endsWith 'customizeReportAdd'
      if !isNew
        addPageData.attr( 'pageType',  '修改' )
        tmpCustomizeReport = localStorage.get 'tmpCustomizeReport'
        if tmpCustomizeReport
          for k, v of tmpCustomizeReport
            addPageData.attr k, v if !_.isObject v

      $('select[name^="type"]').select2 {
          language: 'zh-CN'
          theme: "bootstrap"
          data: [{id: 'number', text:'数字'}, {id:'string', text:'字符串'}, {id:'date', text:'日期'}]
          placeholder: '选择一个类型'
        }

      reqwest({
        url: "#{Auth.apiHost}customize/report/allViews?_=#{Date.now()}"
        method: "get"
      }).then( (views)->
        reqwest({
          url: "#{Auth.apiHost}customize/report/allProcs?_=#{Date.now()}"
          method: "get"
        }).then( (procs)->

          $('#value').select2 {
            language: 'zh-CN'
            theme: "bootstrap"
            placeholder: '选择一个来源'
            data: [
              id: 'views'
              text: '视图'
              children:_.map views, (e)-> (id: e.text, text: e.text)
            ,
              id: 'procs'
              text: '存储过程'
              children:_.map procs, (e)-> (id: e.text, text: e.text)
            ]
          }

          $('#operates button[data-action="refresh"]').click()
        ).fail (err)->
          bootbox.alert "存储过程列表获取失败！"
      ).fail (err)->
        bootbox.alert "视图列表获取失败！"

      $('#value').on 'change', (e)->
        switch $(':selected', this).parent('optGroup').attr 'label'
          when '视图'
            $('select[name^="type"],input[name^="param"]').val('').prop 'disabled', true
          when '存储过程'
            $('select[name^="type"],input[name^="param"]').prop 'disabled', false


      $('#operates button').unbind('click').bind 'click', ->
        switch $(this).data 'action'
          when 'save'
            return if !$('form').valid()

            # 默认是存储过程
            report = {}
            for k, v of addPageData.attr()
              report[k] = v if k != 'pageType'  and !_.isObject v

            report.type = if $('#value :selected').parent('optGroup').attr('label') is '视图' then 'view' else 'proc'
            report.id = Number report.id

            reqwest({
              url: "#{Auth.apiHost}customize/report/save"
              method: "post"
              contentType: 'application/json'
              type: 'html'
              data: JSON.stringify report
            }).then(->
              localStorage.remove 'tmpCustomizeReport'
              bootbox.alert "自定义报表#{if isNew then '新增' else '修改'}成功！", ->
                window.location.hash = "home/system/customizeReportView"
            ).fail (err)->
              bootbox.alert "自定义报表#{if isNew then '新增' else '修改'}失败！#{err.responseText}"
          when 'refresh'
            if tmpCustomizeReport
              for k, v of tmpCustomizeReport
                addPageData.attr k, v if !_.isObject v

              $('select').each (i, e)->
                if $(e).attr 'can-value'
                  $(e).val(addPageData.attr $(e).attr 'can-value').change()

            else
              for k, v of addPageData.attr()
                continue if k == 'pageType'
                addPageData.attr k, 0 if typeof v == 'number'
                addPageData.attr k, '' if typeof v == 'string'
              $('select').val('').trigger 'change'
          when 'cancel'
            window.location.hash = "home/system/customizeReportView"
