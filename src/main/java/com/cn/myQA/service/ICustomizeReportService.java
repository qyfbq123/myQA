package com.cn.myQA.service;

import java.util.LinkedHashMap;
import java.util.List;

import com.cn.myQA.pojo.CustomizeReport;
import com.cn.myQA.pojo.UserCrColumns;
import com.cn.myQA.web.select2.Option;

public interface ICustomizeReportService {
    public List<CustomizeReport> all();
    
    public List<Option> allViews();
    
    public List<Option> allProcs();
    
    public String save(CustomizeReport customizeReport);
    
    public CustomizeReport single(Integer id);
    
    public String delete(Integer id);
    
    public UserCrColumns columns(Integer id, Integer uid);
    
    public UserCrColumns saveColumns(UserCrColumns ucrColumns);
    
    public List<LinkedHashMap<String, Object>> run(CustomizeReport customizeReport);
    
    public String report(CustomizeReport customizeReport, Integer uid);
    
    public String push(CustomizeReport customizeReport, Integer uid);
    
}
