package myQA;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import myQA.pojo.Employee;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.myQA.dao.QuestionMapper;
import com.cn.myQA.pojo.Question;

@RunWith(SpringJUnit4ClassRunner.class)     //表示继承了SpringJUnit4ClassRunner类
@ContextConfiguration(locations = {"classpath:spring-mybatis.xml"})
public class TestWeekReport {
    @Autowired
    QuestionMapper questionMapper;
    
    @Test
    public void testWeeklyReport() throws ParseException, IOException{
        List<Question> qList = questionMapper.questionsByDays(100);
        List<Question> pmList = new ArrayList<Question>();
        List<Question> otherList = new ArrayList<Question>();
        for(Question q : qList) {
            if(q.getClosed()) q.setStatus("关闭");
            else if(q.getModified() == null) q.setStatus("新建");
            else q.setStatus("更新");
            if(q.getCategory().equals("PM")) {
                pmList.add(q);
            } else {
                otherList.add(q);
            }
        }
        try(InputStream is = this.getClass().getResourceAsStream("/template/reportTemplate.xls")) {
            try(OutputStream os = new FileOutputStream("./weekly.xls")) {
                Context context = new Context();
                context.putVar("headers", Arrays.asList("status", "number","id","projectName","vendor",
                        "issueDate","attendee","isCustomerFeed","containmentPlanDate","actionPlanDate","issueType",
                        "severity","warehouse","spcName","orderNo","hawb","partInformation","pickupTime","actPickupTime",
                        "problemStatement","issueDescription","correctiveDescription","rootCause","correctiveAction",
                        "createby","modifytime"));
                context.putVar("data", pmList);
                context.putVar("questions", otherList);
                JxlsHelper.getInstance().processGridTemplateAtCell(is, os, context, "status,number,id,project,supplier,"
                        + "issueDate,teammates,isCFeedback,containmentPlanDate,actionPlanDate,type,"
                        + "severity,beginStorehouse,spc,orderNo,hawb,partInformation,scheduledTime,actualTime,"
                        + "problemStatement,issueDescription,recoveryDescription,rootCause,correctiveAction,"
                        + "creator.email,modified", "报表!A1");
            }
        }
    }
    
    @Ignore
    @Test
    public void testBasic() throws ParseException, IOException{
        List<Question> qList = questionMapper.questionsByDays(100);
        try(InputStream is = this.getClass().getResourceAsStream("grid_template.xls")) {
            try(OutputStream os = new FileOutputStream("./grid_output2.xls")) {
                List<Employee> employees = new ArrayList<Employee>();
                Employee e = new Employee();
                e.setName("test");
                e.setBirthDate(new Date());
                e.setPayment(1000.0);
                employees.add(e);
                
                Context context = new Context();
                context.putVar("headers", Arrays.asList("Birthday"));
                context.putVar("data", qList);
                JxlsHelper.getInstance().processGridTemplateAtCell(is, os, context, "modified", "Sheet2!A1");
            }
        }
    }
}
