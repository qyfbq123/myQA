package myQA;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.myQA.dao.QuestionMapper;
import com.cn.myQA.dao.UserMapper;
import com.cn.myQA.pojo.Question;
import com.cn.myQA.web.QuestionSearch;
import com.github.miemiedev.mybatis.paginator.domain.Order;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;

@RunWith(SpringJUnit4ClassRunner.class)     //表示继承了SpringJUnit4ClassRunner类
@ContextConfiguration(locations = {"classpath:spring-mybatis.xml"})
public class TestSqlServerConnect {
    @Autowired
    QuestionMapper questionMapper;
    @Autowired
    UserMapper userMapper;
    
    @Test
    @Ignore
    public void testUser() throws Exception {
//        List<User> users = userMapper.all();
        
    }
    
    @Test
    public void testQuestion() throws Exception {
        PageBounds pb = new PageBounds(1, 10, Order.formString("to_top.desc, created.desc"));
        PageList<Question> qList = questionMapper.page(pb, new QuestionSearch());
        for(Question q : qList) {
            System.out.println(q.getId());
        }
    }
}
