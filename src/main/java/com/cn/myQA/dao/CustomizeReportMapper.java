package com.cn.myQA.dao;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.myQA.pojo.CustomizeReport;
import com.cn.myQA.pojo.Group;
import com.cn.myQA.pojo.UserCrColumns;

public interface CustomizeReportMapper {
    List<CustomizeReport> all();
    
    List<CustomizeReport> allByUser(Integer uid);
    
    CustomizeReport selectByPrimaryKey(Integer id);
    
    int deleteByPrimaryKey(Integer id);
    
    int add(CustomizeReport report);
    
    int update(CustomizeReport report);
    
    UserCrColumns selectOneUcrColumns(@Param("userId") Integer userId, @Param("crId") Integer crId);
    
    int addUcrColumns(UserCrColumns ucrColumns);
    
    int updateUcrColumns(UserCrColumns ucrColumns);
    
    int deleteUcrColumnsByCrId(Integer id);
    
    List<String> allViews();
    
    List<String> allProcs();
    
    List<LinkedHashMap<String, Object>> callProc(@Param("procedure")String procedure, @Param("paramList")List<Object> paramList);
    
    List<LinkedHashMap<String, Object>> selectView(@Param("view")String view);
    
    int putUseGroups(@Param("id") Integer id, @Param("groupList") List<Group> groupList);
    
    int removeUseGroups(@Param("id") Integer id);
}
