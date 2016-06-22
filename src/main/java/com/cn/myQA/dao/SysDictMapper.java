package com.cn.myQA.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.myQA.pojo.SysDict;

public interface SysDictMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysDict record);

    int insertSelective(SysDict record);

    SysDict selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysDict record);

    int updateByPrimaryKey(SysDict record);
    
    /**
     * 根据类型id获取字典值
     * @param typeid 类型id
     * @return 字典列表
     */
    List<SysDict> findByType(int typeid);
    
    /**
     * 根绝类型id删除字典
     * @param typeid 类型id
     * @return 结果
     */
    int deleteByType(Integer typeid);
    
    /**
     * 保存字典
     * @param typeid 类型id
     * @param dictList 字典列表
     * @return 结果
     */
    int saveAttributes(@Param("id") Integer typeid, @Param("dictList") List<SysDict> dictList);
}