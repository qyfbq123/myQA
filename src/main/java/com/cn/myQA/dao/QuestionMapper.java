package com.cn.myQA.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.myQA.pojo.Question;
import com.cn.myQA.web.QuestionSearch;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;

public interface QuestionMapper {
    int deleteByPrimaryKey(Integer id);
    
    int insertSelective(Question record);
    
    int insert(Question record);

    Question selectByPrimaryKey(Integer id);
    
    Question selectByNumber(String number);

    int updateByPrimaryKey(Question record);
    
    /**
     * 按种类查询今天的问题总数
     * @param category 种类
     * @return 总数
     */
    int countTodayQuestion(@Param("category") String category, @Param("today") Date today);
    
    /**
     * 分页查询问题
     * @param pb 分页参数
     * @param category 分类
     * @return 问题列表
     */
    PageList<Question> page(PageBounds pb, @Param("search") QuestionSearch search);
    
    /**
     * 16-6-20 首页事件列表
     * @param associationId 关联id
     * @return 问题列表
     */
    List<Question> pmList(@Param("associationId") Integer associationId);
    
    /**
     * 关闭问题
     * @param id 问题id
     * @return 结果
     */
    int close(@Param("id") Integer id, @Param("feedback") String feedback, @Param("modifierId") Integer modifierId);
    
    /**
     * 处理问题
     * @param id 问题id
     * @param handleStatus 处理状态
     * @param handleResult 处理结果
     * @return 结果
     */
    int handle(@Param("id") Integer id, @Param("handleStatus") Integer handleStatus, @Param("handlePromisedate") Date handlePromisedate, @Param("handleResult") String handleResult, @Param("modifierId") Integer modifierId);
    
    /**
     * 问题置顶
     * @param id 问题id
     * @return 结果
     */
    int toggleTop(Integer id);
    
    /**
     * 修改问题
     * @param question 问题
     * @return 结果
     */
    int updateByPrimaryKeySelective(Question question);
    
    /**
     * 周报查询
     * @return 一周内的问题
     */
    List<Question> questionsThisWeek();
}