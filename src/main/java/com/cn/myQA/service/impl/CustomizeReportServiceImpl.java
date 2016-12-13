package com.cn.myQA.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cn.myQA.dao.CustomizeReportMapper;
import com.cn.myQA.dao.UserMapper;
import com.cn.myQA.pojo.CustomizeReport;
import com.cn.myQA.pojo.Group;
import com.cn.myQA.pojo.User;
import com.cn.myQA.pojo.UserCrColumns;
import com.cn.myQA.service.ICustomizeReportService;
import com.cn.myQA.service.IMailService;
import com.cn.myQA.web.select2.Option;

@Service
public class CustomizeReportServiceImpl implements ICustomizeReportService {
    private static Logger logger = Logger.getLogger(CustomizeReportServiceImpl.class);

    @Autowired
    private CustomizeReportMapper crMapper;
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private IMailService mailService;
    
    @Autowired
    private TaskExecutor taskExecutor;

    @Override
    public List<CustomizeReport> all() {
        return crMapper.all();
    }

    @Override
    public List<Option> allViews() {
        List<String> list = crMapper.allViews();

        List<Option> optionList = new ArrayList<Option>();

        for (String str : list) {
            Option o = new Option();
            o.setText(str);
            optionList.add(o);
        }
        return optionList;
    }

    @Override
    public List<Option> allProcs() {
        List<String> list = crMapper.allProcs();

        List<Option> optionList = new ArrayList<Option>();

        for (String str : list) {
            Option o = new Option();
            o.setText(str);
            optionList.add(o);
        }
        return optionList;
    }

    @Override
    public String save(CustomizeReport customizeReport) {
        if (customizeReport.getId() == null || customizeReport.getId() <= 0) {
            int rows = crMapper.add(customizeReport);
            if (rows == 1) {
                return "ok";
            } else
                return "error";
        } else {
            crMapper.deleteUcrColumnsByCrId(customizeReport.getId());
            int rows = crMapper.update(customizeReport);
            if (rows == 1) {
                return "ok";
            } else
                return "error";
        }
    }

    @Override
    public CustomizeReport single(Integer id) {
        return crMapper.selectByPrimaryKey(id);
    }
    
    @Override
    public String delete(Integer id) {
        int rows = crMapper.deleteByPrimaryKey(id);
        
        return rows == 1 ? "ok" : "error";
    }

    @Override
    public UserCrColumns columns(Integer id, Integer uid) {
        return crMapper.selectOneUcrColumns(uid, id);
    }

    @Override
    public UserCrColumns saveColumns(UserCrColumns ucrColumns) {
        if (ucrColumns.getId() != null && ucrColumns.getId() > 0) {
            crMapper.updateUcrColumns(ucrColumns);
        } else {
            crMapper.addUcrColumns(ucrColumns);
        }
        return ucrColumns;
    }

    @Override
    public List<LinkedHashMap<String, Object>> run(CustomizeReport cr) {
        CustomizeReport ocr = crMapper.selectByPrimaryKey(cr.getId());
        if(ocr.getType().equals("proc")) {
            List<Object> paramList = new ArrayList<Object>();
            if (!StringUtils.isEmpty(cr.getParam1()) && !StringUtils.isEmpty(ocr.getType1())) {
                paramList.add(transParam(cr.getParam1(), ocr.getType1()));
            }
            if (!StringUtils.isEmpty(cr.getParam2()) && !StringUtils.isEmpty(ocr.getType2())) {
                paramList.add(transParam(cr.getParam2(), ocr.getType2()));
            }
            if (!StringUtils.isEmpty(cr.getParam3()) && !StringUtils.isEmpty(ocr.getType3())) {
                paramList.add(transParam(cr.getParam3(), ocr.getType3()));
            }
            if (!StringUtils.isEmpty(cr.getParam4()) && !StringUtils.isEmpty(ocr.getType4())) {
                paramList.add(transParam(cr.getParam4(), ocr.getType4()));
            }
            if (!StringUtils.isEmpty(cr.getParam5()) && !StringUtils.isEmpty(ocr.getType5())) {
                paramList.add(transParam(cr.getParam5(), ocr.getType5()));
            }
            if (!StringUtils.isEmpty(cr.getParam6()) && !StringUtils.isEmpty(ocr.getType6())) {
                paramList.add(transParam(cr.getParam6(), ocr.getType6()));
            }
            return crMapper.callProc(ocr.getValue(), paramList);
        } else if(ocr.getType().equals("view")) {
            return crMapper.selectView(ocr.getValue());
        }
        
        return null;
    }

    private Object transParam(String value, String type) {
        switch (type) {
            case "date":
                return new Date(Long.parseLong(value));
            case "number":
                return Integer.valueOf(value);
            default:
                return value;
        }
    }

    public String report(CustomizeReport customizeReport, Integer uid) {
        Set<String> headers = null;
        if (customizeReport.getId() != null && uid != null) {
            UserCrColumns ucrColumns = crMapper.selectOneUcrColumns(uid, customizeReport.getId());
            if (!StringUtils.isEmpty(ucrColumns.getColumns())) {
                headers = new LinkedHashSet<String>();
                JSONArray arr = JSONArray.parseArray(ucrColumns.getColumns());
                for(int i = 0; i < arr.size(); i++) {
                    JSONObject json = arr.getJSONObject(i);
                    Boolean visible = json.getBoolean("visible");
                    if(visible == null ? true : visible ) {
                        headers.add(json.getString("title"));
                    }
                }
            }
        }

        List<LinkedHashMap<String, Object>> list = this.run(customizeReport);
        if (list.size() > 0) {
            File outFile;
            try (InputStream is =
                    this.getClass().getResourceAsStream("/template/customizeReportTemplate.xls")) {
                outFile = File.createTempFile("temp-" + customizeReport.getName(), ".xls");
                OutputStream os = new FileOutputStream(outFile);
                Context context = new Context();

                if (CollectionUtils.isEmpty(headers)) {
                    headers = list.get(0).keySet();
                }
                context.putVar("headers", new ArrayList<String>(headers));
                context.putVar("data", list);
                JxlsHelper.getInstance().processGridTemplateAtCell(is, os, context,
                        String.join(",", headers), "报表!A1");
                return outFile.getAbsolutePath();
            } catch (IOException e) {
                logger.error("自定义报表生成失败！", e);
            }
        }
        return null;
    }
    
    public String push(CustomizeReport customizeReport, Integer uid) {
        String filePath = this.report(customizeReport, uid);
        
        if(filePath == null) {
            logger.error("报表生成失败！");
            return "error";
        } else {
            Group group = userMapper.findMailPushGroup();
            if(group==null || group.getId()==null || group.getId()<=0) {
                group = userMapper.findPMGroup();
            }
            List<String> mailList = new ArrayList<String>();
            for(User u : group.getUserList()) {
                if(!StringUtils.isEmpty(u.getEmail()))
                    mailList.add(u.getEmail());
            }
            if(mailList.size() > 0) {
                taskExecutor.execute(new Runnable(){    
                    public void run(){
                        mailService.sendmail(mailList.toArray(new String[mailList.size()]), "自定义报表", "附件为自定义报表——" + customizeReport.getName() + "。", filePath, customizeReport.getName() + ".xls");
                    }    
                 }); 
            }
        }
        return "ok";
    }
}
