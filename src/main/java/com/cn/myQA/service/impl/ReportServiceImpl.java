package com.cn.myQA.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.cn.myQA.pojo.User;
import com.cn.myQA.service.IMailService;
import com.cn.myQA.service.IQuestionService;
import com.cn.myQA.service.IReportService;
import com.cn.myQA.service.IUserService;

@Component
public class ReportServiceImpl implements IReportService {
    private static Logger logger = Logger.getLogger(ReportServiceImpl.class);
    
    @Autowired
    private IQuestionService questionService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IMailService mailService;
    @Autowired
    private TaskExecutor taskExecutor;
//    @Value("${reportMails}")
//    private String reportMails;
    
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
//    每周星期五下午1点实行一次
    @Override
    @Scheduled(cron = "0 0 13 ? * FRI")
    public void weekReport() throws Exception {
        Calendar now = Calendar.getInstance();
        String reportPath = questionService.reportByTime(now.getTime(), "week", 0);
        
        List<User> pmUsers = userService.findPMMembers();
        List<String> mailList = new ArrayList<String>();
        for(User u : pmUsers) {
            if(!StringUtils.isEmpty(u.getEmail())) {
                mailList.add(u.getEmail());
            }
        }
        
        if(mailList.size() > 0) {
            taskExecutor.execute(new Runnable(){    
                public void run(){
                    mailService.sendmail(mailList.toArray(new String[mailList.size()]), "每周报表", "附件为"+ now.get(Calendar.YEAR)+"第"+now.get(Calendar.WEEK_OF_YEAR)+"周问题汇总报表。", reportPath, "每周问题汇总报表.xls");
                    logger.info("发送完毕");
                }    
             }); 
        }
    }
    
    /**
     * 每天上午7点触发
     */
    @Override
    @Scheduled(cron = "0 0 7 * * MON-FRI")
    public void dailyReport() throws Exception {
        Calendar time = Calendar.getInstance();
        time.add(Calendar.DATE, -1);
        String reportPath = questionService.reportByTime(time.getTime(), "day", 0);
        
        List<User> pmUsers = userService.findPMMembers();
        List<String> mailList = new ArrayList<String>();
        for(User u : pmUsers) {
            if(!StringUtils.isEmpty(u.getEmail())) {
                mailList.add(u.getEmail());
            }
        }
        
        if(mailList.size() > 0) {
            taskExecutor.execute(new Runnable(){    
                public void run(){
                    mailService.sendmail(mailList.toArray(new String[mailList.size()]), "每日报表", "附件为"+time.get(Calendar.YEAR) + "年" + (time.get(Calendar.MONTH) + 1) + "月" + time.get(Calendar.DATE) +"日问题汇总报表。", reportPath, "每日问题汇总报表.xls");
                    logger.info("发送完毕");
                }    
             }); 
        }
    }
}
