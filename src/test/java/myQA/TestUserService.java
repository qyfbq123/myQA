package myQA;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.myQA.pojo.User;
import com.cn.myQA.service.IUserService;

@RunWith(SpringJUnit4ClassRunner.class)     //表示继承了SpringJUnit4ClassRunner类
@ContextConfiguration(locations = {"classpath:spring-mybatis.xml"})
public class TestUserService {
    
    @Autowired
    private IUserService userService;

    @Test
    public void test() {
        User user = userService.findByLoginID("admin");
        assertNotNull(user);
    }

}
