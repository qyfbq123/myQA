package myQA;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import myQA.pojo.Department;
import myQA.pojo.Employee;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jxls.reader.ReaderBuilder;
import org.jxls.reader.XLSReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;
import org.xml.sax.SAXException;

import com.cn.myQA.dao.SysDictMapper;
import com.cn.myQA.dao.UserMapper;
import com.cn.myQA.pojo.SysDict;
import com.cn.myQA.pojo.User;
import com.cn.myQA.web.BackImportVO;
import com.cn.myQA.web.QuestionVO;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

@RunWith(SpringJUnit4ClassRunner.class)     //表示继承了SpringJUnit4ClassRunner类
@ContextConfiguration(locations = {"classpath:spring-mybatis.xml"})
public class TestExcelReader {
    @Autowired
    UserMapper userMapper;
    @Autowired
    SysDictMapper dictMapper;
    
    @Ignore
    @Test
    public void backImportUsers() throws IOException, SAXException, InvalidFormatException {
        InputStream inputXML = new BufferedInputStream(getClass().getResourceAsStream("/template/dataBackImport.xml"));
        XLSReader mainReader = ReaderBuilder.buildFromXML( inputXML );
        InputStream inputXLS = new BufferedInputStream(getClass().getResourceAsStream("/template/users.xls"));
        List<BackImportVO> datas = new ArrayList<BackImportVO>();
        Map<String, Object> beans = new HashMap<String, Object>();
        beans.put("datas", datas);
        mainReader.read( inputXLS, beans);
        for(BackImportVO d : datas) {
            System.out.println(d.getCol0());
            if(d.getCol2().equals("0")) {
                User u = new User();
                u.setUsername(d.getCol0());
                u.setPassword("1");
                u.setEmail(d.getCol1());
                u.setLocked(true);
                u.setLoginid(PinyinHelper.convertToPinyinString(d.getCol0(), "", PinyinFormat.WITHOUT_TONE));
//                userMapper.insert(u);
//                userMapper.toggleLocked(u.getId());
            }
        }
    }
    
    @Ignore
    @Test
    public void backImportProjects() throws IOException, SAXException, InvalidFormatException {
        InputStream inputXML = new BufferedInputStream(getClass().getResourceAsStream("/template/dataBackImport.xml"));
        XLSReader mainReader = ReaderBuilder.buildFromXML( inputXML );
        InputStream inputXLS = new BufferedInputStream(getClass().getResourceAsStream("/template/projects.xlsx"));
        List<BackImportVO> datas = new ArrayList<BackImportVO>();
        Map<String, Object> beans = new HashMap<String, Object>();
        beans.put("datas", datas);
        mainReader.read( inputXLS, beans);
        int typeid = 2;
        List<SysDict> dictList = new ArrayList<SysDict>();
        int i = 1;
        for(BackImportVO d : datas) {
            SysDict dict = new SysDict();
            dict.setDictId(i);
            dict.setDictValue(d.getCol0());
            dictList.add(dict);
            i++;
        }
        dictMapper.saveAttributes(typeid, dictList);
    }
    
    @Ignore
    @Test
    public void backImportWarehousesTypes() throws IOException, SAXException, InvalidFormatException {
        InputStream inputXML = new BufferedInputStream(getClass().getResourceAsStream("/template/dataBackImport.xml"));
        XLSReader mainReader = ReaderBuilder.buildFromXML( inputXML );
        InputStream inputXLS = new BufferedInputStream(getClass().getResourceAsStream("/template/WT.xlsx"));
        List<BackImportVO> datas = new ArrayList<BackImportVO>();
        Map<String, Object> beans = new HashMap<String, Object>();
        beans.put("datas", datas);
        mainReader.read( inputXLS, beans);
        List<SysDict> wList = new ArrayList<SysDict>();
        List<SysDict> tList = new ArrayList<SysDict>();
        int i = 1;
        for(BackImportVO d : datas) {
            if(!StringUtils.isEmpty(d.getCol0())) {
                SysDict dict = new SysDict();
                dict.setDictId(i);
                dict.setDictValue(d.getCol0());
                wList.add(dict);
            }
            if(!StringUtils.isEmpty(d.getCol1())) {
                SysDict dict = new SysDict();
                dict.setDictId(i);
                dict.setDictValue(d.getCol1());
                tList.add(dict);
            }
            i++;
        }
        dictMapper.saveAttributes(5, wList);
        dictMapper.saveAttributes(3, tList);
    }
    
    private static Logger logger = Logger.getLogger(TestExcelReader.class);
    @Ignore
    @Test
    public void test123() throws IOException, SAXException, InvalidFormatException {
        logger.info("Reading xml config file and constructing XLSReader");
        String xmlConfig = "/template/departments.xml";
        String dataFile =  "/template/department_data.xls";
        try(InputStream xmlInputStream = TestExcelReader.class.getResourceAsStream(xmlConfig)) {
            XLSReader reader = ReaderBuilder.buildFromXML(xmlInputStream);
            try (InputStream xlsInputStream = TestExcelReader.class.getResourceAsStream(dataFile)) {
                Department department = new Department();
                Department hrDepartment = new Department();
                List<Department> departments = new ArrayList<>();
                Map<String, Object> beans = new HashMap<>();
                beans.put("department", department);
                beans.put("hrDepartment", hrDepartment);
                beans.put("departments", departments);
                logger.info("Reading the data...");
                reader.read(xlsInputStream, beans);
                logger.info("Read " + departments.size() + " departments into `departments` list");
                logger.info("Read " + department.getName() + " department into `department` variable");
                logger.info("Read " + hrDepartment.getHeadcount() + " employees in `hrDepartment`");
                logger.info("Printing IT department employees and birthdays:");
                for (Employee employee : department.getStaff()) {
                    logger.info(employee.getName() + ": " + employee.getBirthDate());
                }
            }
        }
    }
    
    @Test
    public void testReadELC() throws IOException, SAXException, InvalidFormatException {
        InputStream inputXML = new BufferedInputStream(getClass().getResourceAsStream("/template/question.xml"));
        XLSReader mainReader = ReaderBuilder.buildFromXML( inputXML );
        InputStream inputXLS = new BufferedInputStream(getClass().getResourceAsStream("/template/dbo_ELC_Content.xlsx"));
        List<QuestionVO> questions = new ArrayList<QuestionVO>();
        Map<String, Object> beans = new HashMap<String, Object>();
        beans.put("questions", questions);
        mainReader.read( inputXLS, beans);
        for(QuestionVO vo : questions) {
            logger.info(vo.getIssueDate());
        }
        
    }
    
    public static void main(String[] args) {
//        String a = "􁓃􁡯 􄇒􄍗􀨧􂸱 PartsNo PartsName Qty RackNo BJGE2 JG-1583 BSX14-1284*C CHILLER 1 NPC040101B";
        System.out.println();
    }
}
