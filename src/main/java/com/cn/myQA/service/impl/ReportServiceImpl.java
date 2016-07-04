package com.cn.myQA.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cn.myQA.dao.QuestionMapper;
import com.cn.myQA.pojo.Question;
import com.cn.myQA.service.IMailService;
import com.cn.myQA.service.IReportService;

@Component
public class ReportServiceImpl implements IReportService {
    private static Logger logger = Logger.getLogger(ReportServiceImpl.class);
    
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private IMailService mailService;
    @Autowired
    private TaskExecutor taskExecutor;
    @Value("${reportMails}")
    private String reportMails;
    
    /*
    字段 允许值 允许的特殊字符 
    秒 0-59 , - * / 
    分 0-59 , - * / 
    小时 0-23 , - * / 
    日期 1-31 , - * ? / L W C 
    月份 1-12 或者 JAN-DEC , - * / 
    星期 1-7 或者 SUN-SAT , - * ? / L C # 
    年（可选） 留空, 1970-2099 , - * / 
    表达式 意义 
    "0 0 12 * * ?" 每天中午12点触发 
    "0 15 10 ? * *" 每天上午10:15触发 
    "0 15 10 * * ?" 每天上午10:15触发 
    "0 15 10 * * ? *" 每天上午10:15触发 
    "0 15 10 * * ? 2005" 2005年的每天上午10:15触发 
    "0 * 14 * * ?" 在每天下午2点到下午2:59期间的每1分钟触发 
    "0 0/5 14 * * ?" 在每天下午2点到下午2:55期间的每5分钟触发 
    "0 0/5 14,18 * * ?" 在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发 
    "0 0-5 14 * * ?" 在每天下午2点到下午2:05期间的每1分钟触发 
    "0 10,44 14 ? 3 WED" 每年三月的星期三的下午2:10和2:44触发 
    "0 15 10 ? * MON-FRI" 周一至周五的上午10:15触发 
    "0 15 10 15 * ?" 每月15日上午10:15触发 
    "0 15 10 L * ?" 每月最后一日的上午10:15触发 
    "0 15 10 ? * 6L" 每月的最后一个星期五上午10:15触发 
    "0 15 10 ? * 6L 2002-2005" 2002年至2005年的每月的最后一个星期五上午10:15触发 
    "0 15 10 ? * 6#3" 每月的第三个星期五上午10:15触发 
    "0 *\/1 * * * ?" 每分钟（去除中间的转译符）
     * */
//    每周星期天凌晨1点实行一次
    @Override
    @Scheduled(cron = "0 0 1 ? * SUN")
    public void monthReport() throws Exception {
        List<Question> questions = questionMapper.questionsByDays(7);
        try {
            File tempQuestionFile = File.createTempFile("temp-问题每周总结", ".xlsx");
            
            InputStream is = this.getClass().getResourceAsStream("/template/questions.xlsx");
            
            OutputStream os = new FileOutputStream(tempQuestionFile);
            String[] mailArray = reportMails.split(",");
            Context context = new Context();
            context.putVar("questions", questions);
            JxlsHelper.getInstance().processTemplate(is, os, context);
            
            if(mailArray.length > 0) {
                taskExecutor.execute(new Runnable(){    
                    public void run(){
                        mailService.sendmail(mailArray, "每周总结", "本周问题总数为：" + questions.size(), tempQuestionFile);
                        System.out.println("发送完毕");
                    }    
                 }); 
            }
        } catch (IOException e) {
            logger.error("", e);
        }
    }

}
