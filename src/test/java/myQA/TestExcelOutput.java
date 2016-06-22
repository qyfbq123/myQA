package myQA;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import myQA.pojo.Employee;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.myQA.dao.UserMapper;
import com.cn.myQA.pojo.User;

@RunWith(SpringJUnit4ClassRunner.class)     //表示继承了SpringJUnit4ClassRunner类
@ContextConfiguration(locations = {"classpath:spring-mybatis.xml"})
public class TestExcelOutput {
    @Autowired
    UserMapper userMapper;
    @Test
    public void testOutput() throws ParseException, IOException{
        List<User> users = userMapper.all();
        System.out.println(users.size());
        InputStream is = this.getClass().getResourceAsStream("user.xlsx");
        try(OutputStream os = new FileOutputStream("./user5.xlsx")) {
            Context context = new Context();
            context.putVar("user", users.get(0));
            JxlsHelper.getInstance().processTemplate(is, os, context);
        }
    }
    
    @Test
    public void testOutput1() throws ParseException, IOException{
        List<Employee> employees = new ArrayList<Employee>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd", Locale.US);
        Employee ss = new Employee("Elsa", dateFormat.parse("1970-Jul-10"), 1500.0, 0.15);
        ss.setSuperior(new Employee("Elsa", dateFormat.parse("1970-Jul-10"), 1500.0, 0.25));
        employees.add( ss );
//        employees.add( new Employee("Oleg", dateFormat.parse("1973-Apr-30"), 2300.0, 0.25) );
//        employees.add( new Employee("Neil", dateFormat.parse("1975-Oct-05"), 2500.0, 0.00) );
//        employees.add( new Employee("Maria", dateFormat.parse("1978-Jan-07"), 1700.0, 0.15) );
//        employees.add( new Employee("John", dateFormat.parse("1969-May-30"), 2800.0, 0.20) );
        try(InputStream is = this.getClass().getResourceAsStream("user2.xls")) {
            try (OutputStream os = new FileOutputStream("./user4.xls")) {
                Context context = new Context();
                context.putVar("employees", employees);
                JxlsHelper.getInstance().processTemplate(is, os, context);
            }
        }
    }
}
