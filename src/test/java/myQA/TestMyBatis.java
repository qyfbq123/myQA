package myQA;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONArray;
import com.cn.myQA.dao.MessageMapper;
import com.cn.myQA.pojo.Message;
import com.cn.myQA.web.datatables.TableModel;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;

@RunWith(SpringJUnit4ClassRunner.class)     //表示继承了SpringJUnit4ClassRunner类
@ContextConfiguration(locations = {"classpath:spring-mybatis.xml"})
public class TestMyBatis {
    @Resource
    private MessageMapper msgMapper;
    
    @Test
    public void test1() {
        TableModel model = new TableModel();
        model.setStart(4);
        model.setLength(10);
        PageBounds pb = model.translateToPB();
        pb.setPage(2);
        PageList<Message> msgPage = msgMapper.page(pb, null, null, null);
        System.out.println(msgPage.size());
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
