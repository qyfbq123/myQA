package myQA;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONArray;
import com.cn.myQA.dao.UserMapper;
import com.cn.myQA.pojo.User;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;

@RunWith(SpringJUnit4ClassRunner.class)     //表示继承了SpringJUnit4ClassRunner类
@ContextConfiguration(locations = {"classpath:spring-mybatis.xml"})
public class TestMyBatis {
    @Resource
    private UserMapper userMapper;
    
    @Test
    public void test1() {
        PageList<User> userPage = userMapper.page(new PageBounds());
        System.out.println(userPage.size());
    }
    
    public static void main(String[] args) {
        List<String> a = new ArrayList<String>();
        a.add("123");
        a.add("234");
        JSONArray array = new JSONArray();
        array.addAll(a);
        System.out.println(array.toJSONString());
    }
}
