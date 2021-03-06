package com.cn.myQA.service;

import java.util.List;

import com.cn.myQA.pojo.SysDict;
import com.cn.myQA.web.select2.Option;

public interface IDictService {
    /**
     * 获取城市
     * @return 城市列表
     */
    public List<Option> cities();
    
    /**
     * 获取项目
     * @return 项目列表
     */
    public List<Option> projects();
    
    /**
     * 获取事件问题类型
     * @return 类型列表
     */
    public List<Option> pmTypes();
    
    /**
     * 获取WMS&TMS问题类型
     * @return 类型列表
     */
    public List<Option> msTypes();
    
    /**
     * 获取问题严重性
     * @return 列表
     */
    public List<Option> severity();
    
    /**
     * 始发地和涉及库房信息
     * @return 列表
     */
    public List<Option> beginStorehouses();
    
    /**
     * 16-6-20 所属供应商
     * @return 列表
     */
    public List<Option> suppliers();
    
    /**
     * 公告类型
     * @return 列表
     */
    public List<Option> bulletinTypes();
    
    /**
     * 发布职能
     * @return 列表
     */
    public List<Option> functions();
    
    /**
     * 文档类型
     * @return 列表
     */
    public List<Option> docTypes();
    
    /**
     * 可修改的字典
     * @return 字典列表
     */
    public List<SysDict> attriutes();
    
    /**
     * 可修改的字典参数
     * @return 参数列表
     */
    public List<SysDict> params(Integer typeid);
    
    /**
     * 修改字典
     * @param typeid 字典类型id
     * @param dictList 字典列表
     * @return 结果
     */
    public String saveAttributes(Integer typeid, List<SysDict> dictList);
}
