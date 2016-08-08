package com.cn.myQA.service;

import java.util.Date;
import java.util.List;

import com.cn.myQA.pojo.Question;
import com.cn.myQA.pojo.QuestionAttachment;
import com.cn.myQA.pojo.User;
import com.cn.myQA.web.QuestionSearch;
import com.cn.myQA.web.QuestionVO;
import com.cn.myQA.web.QuestionVO2;
import com.cn.myQA.web.datatables.Pagination;
import com.cn.myQA.web.datatables.TableModel;

public interface IQuestionService {
    
    public String getUploadPath();
    
    /**
     * 按id获取问题详情
     * @param id
     * @return 问题详情
     */
    public Question single(Integer id);
    
    /**
     * 按number获取问题详情
     * @param number
     * @return 问题详情
     */
    public Question single(String number);
    
    /**
     * 新增问题
     * @param question 问题
     * @return 结果
     */
    public String create(Question question);
    
    /**
     * 更新问题
     * @param question 问题
     * @return 结果
     */
    public int update(Question question);
    
    /**
     * 分页查询问题
     * @param model 分页参数
     * @param search 查询参数
     * @return 问题列表
     */
    public List<Question> page(TableModel model, QuestionSearch search);
    
    public Pagination<Question> page2(TableModel model, QuestionSearch search);
    
    /**
     * 16-6-20 事件列表
     * @param associationId 关联者id
     * @return 问题列表
     */
    public List<Question> pmList(Integer associationId);
    
    /**
     * 关闭问题
     * @param id 问题id
     * @return 结果
     */
    public int close(Integer id, String feedback, User modifier);
    
    /**
     * 处理问题
     * @param id 问题id
     * @param handleStatus 处理状态
     * @param handleResult 处理结果
     * @return 结果
     */
    public int handle(Integer id, Integer handleStatus, Date handlePromisedate, String handleResult, User modifier);
    
    /**
     * 问题置顶
     * @param id 问题id
     * @return 结果
     */
    public int toggleTop(Integer id);
    
    /**
     * 问题批量导入
     * @param questionVOs
     * @return
     */
    public String batchImport(List<QuestionVO> questionVOs);
    
    /**
     * 问题批量导入
     * @param questionVOs
     * @return
     */
    public String batchImport2(List<QuestionVO2> questionVOs);
    
    /**
     * 日、周、月报
     * @param days 天数
     * @return 生成的文件路径
     */
    @Deprecated
    public String reportByDays(Integer days);
    
    /**
     * 报表
     * @param time 基准时间
     * @param section 区段：day日报，week周报，month月报，year年报
     * @return 生成的文件路径
     */
    public String reportByTime(Date time, String section, Integer userId);
    
    /**
     * 报表
     * @param time 基准时间
     * @return 生成的文件路径
     */
    public String richReportByTime(Date time, String section, Integer userId);
    
    /**
     * 日、周、月报推送
     * @param time 基准时间
     * @param section 区段：day日报，week周报，month月报，year年报
     * @return 执行结果
     */
    public String reportPush(Date time, String section, Integer userId);
    
    /**
     * 日、周、月报推送
     * @param time 基准时间
     * @param section 区段：day日报，week周报，month月报，year年报
     * @return 执行结果
     */
    public String richReportPush(Date time, String section, Integer userId, String fileName);
    
    /**
     * 上传附件
     * @param attachment 附件信息
     * @return 结果
     */
    public int insertAttachment(QuestionAttachment attachment);
    
    /**
     * 根据id获取附件信息
     * @param aid 附件id
     * @return 附件信息
     */
    public QuestionAttachment singleAttachment(Integer aid);
}
