package com.cn.myQA.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.myQA.pojo.Question;
import com.cn.myQA.pojo.QuestionAttachment;
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
    PageList<Question> pmList(PageBounds pb, @Param("associationId") Integer associationId);
    
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
     * 16-6-20 修改问题
     * @param question 问题
     * @return 结果
     */
    int updateByPrimaryKeySelective(Question question);
    
    /**
     * 修改系统问题
     * @param question 问题
     * @return 结果
     */
    int updateOtherByPrimaryKeySelective(Question question);
    
    /**
     * 16-6-20 日、周、月报查询
     * @param days 天数
     * @return 问题列表
     */
    List<Question> questionsByDays(Integer days);
    
    /**
     * 报表查询
     * @param section 区间：day为日报，week为周报,month为月报,year为年报
     * @param date 基准时间
     * @param associationId 关联用户id
     * @return 问题列表
     */
    List<Question> reportByTime(@Param("section")String section, @Param("date")Date date, @Param("associationId") Integer associationId);
    
    /**
     * 插入附件
     * @param attachment 附件信息
     * @return 结果 
     */
    int insertAttachment(QuestionAttachment attachment);
    
    /**
     * 获取附件信息
     * @param id 附件id
     * @return 附件信息
     */
    QuestionAttachment singleAttachment(Integer id);
    
    /**
     * 绑定异步上传的附件
     * @param question 问题
     * @return 结果
     */
    int bindQuestionAndAttachment(Question question);
    
    /**
     * 删除问题附件
     * @param question 问题
     * @return 结果
     */
    int removeAttachment(Question question);
    
    /**
     * 按id删除附件
     * @param id 附件id
     * @return 结果
     */
    int delAttachment(Integer id);
}