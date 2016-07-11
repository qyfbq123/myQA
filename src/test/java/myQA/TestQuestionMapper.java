package myQA;

import java.util.Calendar;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.myQA.dao.QuestionMapper;
import com.cn.myQA.pojo.Question;

@RunWith(SpringJUnit4ClassRunner.class)     //表示继承了SpringJUnit4ClassRunner类
@ContextConfiguration(locations = {"classpath:spring-mybatis.xml"})
public class TestQuestionMapper {
    @Autowired
    private QuestionMapper questionMapper;
    
    @Test
    @Ignore
    public void testPage() throws Exception {
        Question q = questionMapper.selectByPrimaryKey(5328);
        System.out.println(q.getAttachmentList().size());
//        PageList<Question> list = questionMapper.page(new PageBounds(1, 10), "PM");
//        assertNotNull(list.getPaginator());
    }
    
    @Test
    public void testReport() throws Exception {
        List<Question> list = questionMapper.reportByTime("dd", Calendar.getInstance().getTime(), 1);
        for(Question q : list) {
            System.out.println(q.getNumber());
        }
    }
}
