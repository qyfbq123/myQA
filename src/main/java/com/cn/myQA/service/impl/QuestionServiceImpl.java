package com.cn.myQA.service.impl;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.cn.myQA.dao.GroupMapper;
import com.cn.myQA.dao.QuestionMapper;
import com.cn.myQA.dao.UserMapper;
import com.cn.myQA.pojo.Group;
import com.cn.myQA.pojo.Question;
import com.cn.myQA.pojo.User;
import com.cn.myQA.service.IDictService;
import com.cn.myQA.service.IMailService;
import com.cn.myQA.service.IQuestionService;
import com.cn.myQA.web.QuestionSearch;
import com.cn.myQA.web.QuestionVO;
import com.cn.myQA.web.datatables.Pagination;
import com.cn.myQA.web.datatables.TableModel;
import com.cn.myQA.web.select2.Option;
import com.github.miemiedev.mybatis.paginator.domain.Order;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;

@Service
public class QuestionServiceImpl implements IQuestionService {
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(QuestionServiceImpl.class);
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private IDictService dictService;
    @Autowired
    private IMailService mailService;
    
    @Autowired
    private TaskExecutor taskExecutor;
    
    @Value("${host}")
    private String host;
    @Value("${uploadPath}")
    private String uploadPath;
    
    public String getUploadPath() {
        return uploadPath;
    }
    
    private String[] getMailArray(Question question) {
        Set<String> mailList = new HashSet<String>();
        if(!StringUtils.isEmpty(question.getEmailto())) {
            mailList.add(question.getEmailto());
        }
//        16-6-20 leaderEmail换成leader.email
        if(question.getCreator().getLeader() != null && !StringUtils.isEmpty(question.getCreator().getLeader().getEmail())) {
            mailList.add(question.getCreator().getLeader().getEmail());
        }
        if(question.getGroup() != null && question.getGroup().getId() > 0) {
            Group group = groupMapper.selectByPrimaryKey(question.getGroup().getId());
            for(User u : group.getUserList()) {
                if(!StringUtils.isEmpty(u.getEmail()))
                    mailList.add(u.getEmail());
            }
        }
        if(question.getHandler() != null && !StringUtils.isEmpty(question.getHandler().getEmail())) {
            mailList.add(question.getHandler().getEmail());
        }
        
        if(!StringUtils.isEmpty(question.getTeammates())) {
            Group pm = userMapper.findPMGroup();
            if(pm != null) {
                List<User> pmMembers = pm.getUserList();
                for(User u : pmMembers) {
                    if(question.getTeammates().indexOf("\"" + u.getId() + "\"") != -1 && !StringUtils.isEmpty(u.getEmail())) {
                        mailList.add(u.getEmail());
                    }
                }
            }
        }
        
        return mailList.toArray(new String[mailList.size()]);
    }
    
    @Override
    public Question single(Integer id) {
        Question q = questionMapper.selectByPrimaryKey(id);
        if(q!=null)
            q.setCreator(userMapper.selectByPrimaryKey(q.getCreator().getId()));
        return q;
    }
    
    @Override
    public Question single(String number) {
        Question q = questionMapper.selectByNumber(number);
        if(q!=null)
            q.setCreator(userMapper.selectByPrimaryKey(q.getCreator().getId()));
        return q;
    }

    @Override
    public String create(Question question) {
        
        question.setNumber(generateNumber(question.getCategory(), question.getCreated()));
        
        if(question.getCategory().equals("PM")) question.setHandleStatus(2);
        
        int rows = questionMapper.insertSelective(question);
        
        if(question.getId() != null && question.getId() > 0) {
            Question newQuestion = questionMapper.selectByPrimaryKey(question.getId());
            question.setCreated(newQuestion.getCreated());
            question.setCity(newQuestion.getCity());
            question.setHandler(newQuestion.getHandler());
        }
        
        if(question.getClosed() == null || !question.getClosed()) {
            String[] mailArray = getMailArray(question);
            
            if(mailArray.length > 0) {
                taskExecutor.execute(new Runnable(){    
                    public void run(){
                        String content = "发起人：" + question.getCreator().getEmail();
                        content += "<br/><br/>";
                        content += "项目名称：" + question.getProject();
                        content += "<br/><br/>";
                        content += "问题类型：" + question.getType();
                        content += "<br/><br/>";
                        content += "详细信息请点击：<a href='http://" + host + "/#question/" + question.getCategory() + "/" + question.getId() + "'>http://" + host + "/#question/" + question.getCategory() + "/" + question.getId() + "</a>";
                        mailService.sendmail(mailArray, question.getCategory() + " Issue 发起", content);
                    }    
                 }); 
            }
        }
        
        return rows == 1 ? "ok" : "error";
    }
    
    public int update(Question question) {
        Question oldQuestion = questionMapper.selectByPrimaryKey(question.getId());
        if(oldQuestion.getCreator() != null) {
            question.setCreator(userMapper.selectByPrimaryKey(oldQuestion.getCreator().getId()));
            String[] mailArray = getMailArray(question);
            
            if(mailArray.length > 0) {
                taskExecutor.execute(new Runnable(){    
                    public void run(){
                        String content = "更新人：" + question.getModifier().getEmail();
                        content += "<br/><br/>";
                        content += "项目名称：" + question.getProject();
                        content += "<br/><br/>";
                        content += "问题类型：" + question.getType();
                        content += "<br/><br/>";
                        content += "详细信息请点击：<a href='http://" + host + "/#question/" + question.getCategory() + "/" + question.getId() + "'>http://" + host + "/#question/" + question.getCategory() + "/" + question.getId() + "</a>";
                        mailService.sendmail(mailArray, question.getCategory() + " Issue 更新", content);
                    }    
                 }); 
            }
        }
        return questionMapper.updateByPrimaryKeySelective(question);
    }
    
    private String generateNumber(String category, Date today) {
        if(today == null)
            today = Calendar.getInstance().getTime(); 
        int todayInNo = questionMapper.countTodayQuestion(category, today);
        
        DateFormat df = new SimpleDateFormat("yyMMdd");

        String reportDate = df.format(today);
        
        String no = String.valueOf(todayInNo + 1);
        
        return category + reportDate + (no.length() > 4 ? no : ("0000".substring(no.length()) + no));
    }

    @Override
    public List<Question> page(TableModel model, QuestionSearch search) {
        PageBounds pb = model.translateToPB();
        pb.setOrders(Order.formString("to_top.desc, created.desc"));
        PageList<Question> qList = questionMapper.page(pb, search);
        return qList.subList(0, qList.size());
    }
    
    @Override
    public Pagination<Question> page2(TableModel model, QuestionSearch search) {
        PageBounds pb = model.translateToPB();
        pb.setOrders(Order.formString("to_top.desc, created.desc"));
        PageList<Question> qList = questionMapper.page(pb, search);
        Pagination<Question> page = new Pagination<Question>();
        page.setDraw(model.getDraw());
        page.setData(qList.subList(0, qList.size()));
        page.setRecordsFiltered(qList.getPaginator().getTotalCount());
        page.setRecordsTotal(qList.getPaginator().getTotalCount());
        return page;
    }
    
//    16-6-20 首页事件列表
    @Override
    public List<Question> pmList(Integer associationId) {
        return questionMapper.pmList(associationId);
    }

    @Override
    public int close(Integer id, String feedback, User modifier) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if(question.getCreator() != null) {
            question.setCreator(userMapper.selectByPrimaryKey(question.getCreator().getId()));
            String[] mailArray = getMailArray(question);
            
            if(mailArray.length > 0) {
                taskExecutor.execute(new Runnable(){    
                    public void run(){
                        String content = "关闭人：" + modifier.getEmail();
                        content += "<br/><br/>";
                        content += "项目名称：" + question.getProject();
                        content += "<br/><br/>";
                        content += "问题类型：" + question.getType();
                        content += "<br/><br/>";
                        content += "详细信息请点击：<a href='http://" + host + "/#question/" + question.getCategory() + "/" + question.getId() + "'>http://" + host + "/#question/" + question.getCategory() + "/" + question.getId() + "</a>";
                        mailService.sendmail(mailArray, question.getCategory() + " Issue 关闭", content);
                    }    
                 }); 
            }
        }
        return questionMapper.close(id, feedback, modifier.getId());
    }
    
    @Override
    public int handle(Integer id, Integer handleStatus, Date handlePromisedate, String handleResult, User modifier) {
        Question question = questionMapper.selectByPrimaryKey(id);
//        File tempQuestionFile = File.createTempFile("temp-问题", ".xlsx");
//        
//        InputStream is = this.getClass().getResourceAsStream("/template/question.xlsx");
//        
//        OutputStream os = new FileOutputStream(tempQuestionFile);
//        Context context = new Context();
//        context.putVar("question", question);
//        JxlsHelper.getInstance().processTemplate(is, os, context);
        String[] mailArray = getMailArray(question);
        
        if(mailArray.length > 0) {
            taskExecutor.execute(new Runnable(){    
                public void run(){
                    String content = "处理人：" + modifier.getEmail();
                    content += "<br/><br/>";
                    content += "项目名称：" + question.getProject();
                    content += "<br/><br/>";
                    content += "问题类型：" + question.getType();
                    content += "<br/><br/>";
                    content += "详细信息请点击：<a href='http://" + host + "/#question/" + question.getCategory() + "/" + question.getId() + "'>http://" + host + "/#question/" + question.getCategory() + "/" + question.getId() + "</a>";
                    mailService.sendmail(mailArray, question.getCategory() + " Issue 处理", content);
                }    
             }); 
        }
        return questionMapper.handle(id, handleStatus, handlePromisedate, handleResult, modifier.getId());
    }
    
    @Override
    public int toggleTop(Integer id) {
        return questionMapper.toggleTop(id);
    }
    
    @Override
    public String batchImport(List<QuestionVO> questionVOs) {
        List<User> allUsers = userMapper.all();
        List<Option> warehouses = dictService.beginStorehouses();
        List<Option> projects = dictService.projects();
        List<Option> types = dictService.types();
        
        List<String> allUseremails = new ArrayList<String>();
        List<String> allWNames = new ArrayList<String>();
        List<String> allPNames = new ArrayList<String>();
        List<String> allTNames = new ArrayList<String>();
        for(User u : allUsers) {
            allUseremails.add(u.getEmail());
        }
        for(Option o : warehouses) {
            allWNames.add(o.getText());
        }
        for(Option o : projects) {
            allPNames.add(o.getText());
        }
        for(Option o : types) {
            allTNames.add(o.getText());
        }
        
        for(QuestionVO vo : questionVOs) {
            if(!StringUtils.isEmpty(vo.getModifyby()) && !allUseremails.contains( vo.getModifyby() )) {
//                return "用户 “" + vo.getModifyby() + "” 不存在";
            } else if(!allWNames.contains(vo.getWarehouse())) {
                return "始发地和涉及库房信息 “" + vo.getWarehouse() + "” 不存在";
            } else if(!allPNames.contains(vo.getProjectName())) {
                return "项目名称 “" + vo.getProjectName() + "” 不存在";
            } else if(!allTNames.contains(vo.getIssueType())) {
                return "问题类型 “" + vo.getIssueType() + "” 不存在";
            }
        }
        Charset charset = Charset.forName("UTF-8");
        for(QuestionVO vo : questionVOs) {
            Question q = new Question();
            q.setCategory("PM");
            q.setHandleStatus(2);
            q.setClosed(true);
            
            q.setProject(vo.getProjectName());
            q.setSupplier(vo.getVendor());
            if(!StringUtils.isEmpty(vo.getIssueDate())) {
                q.setIssueDate(DateUtil.getJavaDate(Double.parseDouble(vo.getIssueDate())));
            }
            
            q.setIsCFeedback("Y".equals(vo.getIsCustomerFeed()) ? true: false);
            if(!StringUtils.isEmpty(vo.getContainmentPlanDate())) {
                q.setContainmentPlanDate(DateUtil.getJavaDate(Double.parseDouble(vo.getContainmentPlanDate())));
            }
            if(!StringUtils.isEmpty(vo.getActionPlanDate())) {
                q.setActionPlanDate(DateUtil.getJavaDate(Double.parseDouble(vo.getActionPlanDate())));
            }
            q.setType(vo.getIssueType());
            q.setSeverity(vo.getSeverity());
            q.setBeginStorehouse(vo.getWarehouse());
            q.setSpc(vo.getSpcName());
            q.setOrderNo(vo.getOrderNo());
            q.setHawb(vo.getHawb());
            q.setPartInformation(vo.getPartInformation() == null ? null : charset.decode(charset.encode(vo.getPartInformation())).toString());
            if(!StringUtils.isEmpty(vo.getPickupTime())) {
                q.setScheduledTime(DateUtil.getJavaDate(Double.parseDouble(vo.getPickupTime())));
            }
            if(!StringUtils.isEmpty(vo.getActPickupTime())) {
                q.setActualTime(DateUtil.getJavaDate(Double.parseDouble(vo.getActPickupTime())));
            }
            q.setProblemStatement(vo.getProblemStatement());
            q.setIssueDescription(vo.getIssueDescription());
            q.setRecoveryDescription(vo.getCorrectiveDescription());
            q.setRootCause(vo.getRootCause());
            q.setCorrectiveAction(vo.getCorrectiveAction());
            if( !StringUtils.isEmpty(vo.getModifyby()) && allUseremails.contains(vo.getModifyby()) ) {
                User user = allUsers.get(allUseremails.indexOf(vo.getModifyby()));
                q.setCreator(user);
                q.setModifier(user);
            }
            if(!StringUtils.isEmpty(vo.getModifytime())) {
                Date modifyTime = DateUtil.getJavaDate(Double.parseDouble(vo.getModifytime()));
                q.setCreated(modifyTime);
                q.setModified(modifyTime);
            }
            
            this.create(q);
        }
        return "ok";
    }
}
