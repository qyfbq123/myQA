package com.cn.myQA.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jxls.reader.ReaderBuilder;
import org.jxls.reader.XLSReadStatus;
import org.jxls.reader.XLSReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import com.cn.myQA.pojo.Question;
import com.cn.myQA.pojo.QuestionAttachment;
import com.cn.myQA.pojo.User;
import com.cn.myQA.service.IQuestionService;
import com.cn.myQA.web.QuestionSearch;
import com.cn.myQA.web.QuestionVO;
import com.cn.myQA.web.QuestionVO2;
import com.cn.myQA.web.datatables.Pagination;
import com.cn.myQA.web.datatables.TableModel;
import com.mysql.jdbc.StringUtils;

@Api(value="question", description="问题CRUD操作")
@Controller
@RequestMapping("/question")
@SessionAttributes("user")
public class QuestionController {
    @Autowired
    IQuestionService questionService;
    
    @ApiOperation(value="按ID查询问题详情", notes="查询问题详情", httpMethod="GET")
    @RequestMapping(value="/{id}", method=RequestMethod.GET)
    public ResponseEntity<Question> single(@PathVariable("id") Integer id) {
        return new ResponseEntity<Question>(questionService.single(id), HttpStatus.OK);
    }
    
    @ApiOperation(value="按编号查询问题详情", notes="查询问题详情", httpMethod="GET")
    @RequestMapping(value="/byNumber/{number}", method=RequestMethod.GET)
    public ResponseEntity<Question> singleByNumber(@PathVariable("number") String number) {
        return new ResponseEntity<Question>(questionService.single(number), HttpStatus.OK);
    }
    
    @ApiOperation(value="问题新增", notes="新增问题", httpMethod="POST")
    @RequestMapping(value="/create", method=RequestMethod.POST)
    public ResponseEntity<String> create(@RequestBody Question question, @ModelAttribute("user") User user) {
        if(user == null) {
            return new ResponseEntity<String>("error", HttpStatus.BAD_REQUEST);
        }
        question.setCreator(user);
        String result = questionService.create(question);
        
        return new ResponseEntity<String>(result, result.equals("ok") ? HttpStatus.OK: HttpStatus.BAD_REQUEST);
    }
    
    @ApiOperation(value="问题更新", notes="更新问题", httpMethod="POST")
    @RequestMapping(value="/update", method=RequestMethod.POST)
    public ResponseEntity<String> update(@RequestBody Question question, @ModelAttribute("user") User user) {
        if(user == null) {
            return new ResponseEntity<String>("error", HttpStatus.BAD_REQUEST);
        }
        question.setModifier(user);
        int rows = questionService.update(question);
        return rows == 1 ? new ResponseEntity<String>("ok", HttpStatus.OK): new ResponseEntity<String>("error", HttpStatus.BAD_REQUEST);
    }
    
    @ApiOperation(value="问题分页查询", notes="分页查询问题信息", httpMethod="GET")
    @RequestMapping(value="/page", method=RequestMethod.GET)
    public ResponseEntity<List<Question>> page(TableModel model, QuestionSearch search, @ModelAttribute("user") User user) {
        if(user == null) {
            user = new User();
        }
        if(search.getHandlerId() != null && search.getHandlerId().intValue() == -1) {
            search.setHandlerId(user.getId());
        }
        return new ResponseEntity<List<Question>>(questionService.page(model, search), HttpStatus.OK);
    }
    
    @ApiOperation(value="问题分页查询", notes="分页查询问题信息", httpMethod="GET")
    @RequestMapping(value="/page2", method=RequestMethod.GET)
    public ResponseEntity<Pagination<Question>> page2(TableModel model, QuestionSearch search, @ModelAttribute("user") User user) {
        if(user == null) {
            user = new User();
        }
        if(search.getHandlerId() != null && search.getHandlerId().intValue() == -1) {
            search.setHandlerId(user.getId());
        }
        return new ResponseEntity<Pagination<Question>>(questionService.page2(model, search), HttpStatus.OK);
    }
    
//    16-6-20
    @ApiOperation(value="事件列表查询", notes="事件列表查询", httpMethod="GET")
    @RequestMapping(value="/pmList", method=RequestMethod.GET)
    public ResponseEntity<List<Question>> pmList(@ModelAttribute("user") User user) {
        if(user == null) {
            user = new User();
        }
        return new ResponseEntity<List<Question>>(questionService.pmList(user.getId()), HttpStatus.OK);
    }
    
    @ApiOperation(value="关闭问题", notes="关闭问题", httpMethod="PUT")
    @RequestMapping(value="/close/{qid}", method=RequestMethod.PUT)
    public ResponseEntity<String> close(@PathVariable("qid") Integer qid, String feedback, @ModelAttribute("user") User user) {
        int result = questionService.close(qid, feedback, user);
        return new ResponseEntity<String>("ok", result == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
    
    @ApiOperation(value="处理问题", notes="处理问题", httpMethod="PUT")
    @RequestMapping(value="/handle/{qid}", method=RequestMethod.PUT)
    public ResponseEntity<String> handle(@PathVariable("qid") Integer qid, Integer handleStatus, String handleResult, Date handlePromisedate, @ModelAttribute("user") User user) {
        int result = questionService.handle(qid, handleStatus, handlePromisedate, handleResult, user);
        return new ResponseEntity<String>("ok", result == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
    
    @ApiOperation(value="上传问题附件", notes="附件上传", httpMethod="POST")
    @RequestMapping(value="/attachment/upload", method=RequestMethod.POST)
    public ResponseEntity<QuestionAttachment> uploadAttachment(MultipartFile Filedata, HttpSession session) {
        User user = (User)session.getAttribute("user");
        if(user == null) user = new User();
        String fileName = Filedata.getOriginalFilename();
        long l = System.currentTimeMillis();
        String newFileName = l + "_" + fileName;
        File tempFile = new File(questionService.getUploadPath() + System.getProperty("file.separator"), newFileName);
        
        if (!tempFile.getParentFile().exists()) {  
            tempFile.getParentFile().mkdirs();  
        }  
        try {
            if (!tempFile.exists()) {  
                tempFile.createNewFile();
            }  
            Filedata.transferTo(tempFile);
            
            QuestionAttachment qa = new QuestionAttachment();
            qa.setFilename(fileName);
            qa.setPath(newFileName);
            qa.setUploader(user);
            qa.setSize(FileUtils.byteCountToDisplaySize(tempFile.length()));
            
            int result = questionService.insertAttachment(qa);
            if(result == 1)
                return new ResponseEntity<QuestionAttachment>(qa, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<QuestionAttachment>(HttpStatus.BAD_REQUEST);
    }
    
    @ApiOperation(value="上传问题图片", notes="图片上传", httpMethod="POST")
    @RequestMapping(value="/photo/upload", method=RequestMethod.POST)
    public ResponseEntity<Map<String, List<Map<String, String>>>> uploadPhoto(MultipartFile Filedata) {
        String fileName = Filedata.getOriginalFilename();
        long l = System.currentTimeMillis();
        String newFileName = l + "_" + fileName;
        File tempFile = new File(questionService.getUploadPath() + System.getProperty("file.separator"), newFileName);
        
        if (!tempFile.getParentFile().exists()) {  
            tempFile.getParentFile().mkdirs();  
        }  
        try {
            if (!tempFile.exists()) {  
                tempFile.createNewFile();
            }  
            Filedata.transferTo(tempFile); 
            Map<String, List<Map<String, String>>> result = new HashMap<String, List<Map<String, String>>>();
            List<Map<String, String>> files = new ArrayList<Map<String, String>>();
            Map<String, String> newFile = new HashMap<String, String>();
            newFile.put("originalName", fileName);
            newFile.put("name", newFileName);
            files.add(newFile);
            result.put("files", files);
            return new ResponseEntity<Map<String, List<Map<String, String>>>>(result, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return new ResponseEntity<Map<String, List<Map<String, String>>>>(HttpStatus.BAD_REQUEST);
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    @ApiOperation(value="按附件id下载", notes="按附件id下载", httpMethod="GET")
    @RequestMapping(value="/attachment/{id}/download", method=RequestMethod.GET) 
    public ResponseEntity<byte[]> download2(@PathVariable("id") Integer id){
        QuestionAttachment qa = questionService.singleAttachment(id);
        if( qa != null && !StringUtils.isNullOrEmpty(qa.getPath()) ) {
            try {
                File file = new File(questionService.getUploadPath() + System.getProperty("file.separator") + qa.getPath());
                FileInputStream in = new FileInputStream(file);

                final HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", new String(qa.getFilename().getBytes("gb2312"),"iso-8859-1"));
        
                return new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.OK);
            } catch (FileNotFoundException e) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            } catch (IOException e) {
                return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    @ApiOperation(value="附件下载", notes="附件下载", httpMethod="GET")
    @RequestMapping(value="/{id}/attachment/download", method=RequestMethod.GET) 
    public ResponseEntity<byte[]> download(@PathVariable("id") Integer id){
        Question question = questionService.single(id);
        if( question != null && !StringUtils.isNullOrEmpty(question.getAttachmentPath()) ) {
            try {
                File file = new File(questionService.getUploadPath() + System.getProperty("file.separator") + question.getAttachmentPath());
                FileInputStream in = new FileInputStream(file);

                final HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", new String(question.getAttachmentPath().getBytes("gb2312"),"iso-8859-1"));
        
                return new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.OK);
            } catch (FileNotFoundException e) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            } catch (IOException e) {
                return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @ApiOperation(value="查看图片", notes="图片查看", httpMethod="GET")
    @RequestMapping(value="/photo", method=RequestMethod.GET)
    public ResponseEntity<byte[]> getPhoto(String name) {
        try {
            File file = new File(questionService.getUploadPath() + System.getProperty("file.separator") + name);
            FileInputStream in = new FileInputStream(file);

            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
    
            return new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.CREATED);
        } catch (FileNotFoundException e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @ApiOperation(value="置顶问题", notes="问题置顶", httpMethod="PUT")
    @RequestMapping(value="/toggleTop/{id}", method=RequestMethod.PUT)
    public ResponseEntity<String> toggleTop(@PathVariable("id") Integer id) {
        return questionService.toggleTop(id) == 1 ? new ResponseEntity<String>("ok", HttpStatus.OK) : new ResponseEntity<String>("error", HttpStatus.BAD_REQUEST);
    }
    
    @ApiOperation(value="上传dailyReort", notes="dailyReort上传", httpMethod="POST")
    @RequestMapping(value="/dailyReport/upload", method=RequestMethod.POST)
    public ResponseEntity<String> uploadDailyReport(MultipartFile Filedata) {
        InputStream inputXML = new BufferedInputStream(getClass().getResourceAsStream("/template/question.xml"));
        XLSReader mainReader;
        try {
            mainReader = ReaderBuilder.buildFromXML( inputXML );

            InputStream inputXLS = new BufferedInputStream(Filedata.getInputStream());
            List<QuestionVO> questions = new ArrayList<QuestionVO>();
            Map<String, Object> beans = new HashMap<String, Object>();
            beans.put("questions", questions);
            XLSReadStatus readStatus = mainReader.read( inputXLS, beans);
            if(!readStatus.isStatusOK()) return new ResponseEntity<String>("读取excel数据失败", HttpStatus.BAD_REQUEST);
            if(CollectionUtils.isNotEmpty(questions)) {
                if(questions.get(0).getItemId().equals("item_id")) {
                    questions.remove(0);
                }
                String result = questionService.batchImport(questions);
                return new ResponseEntity<String>(result, HttpStatus.OK);
            }
        } catch (IOException | SAXException | InvalidFormatException e) {
            e.printStackTrace();
            return new ResponseEntity<String>("error", HttpStatus.BAD_REQUEST);
        }
        
        return new ResponseEntity<String>("ok", HttpStatus.OK);
    }
    
    @ApiOperation(value="旧系统数据导入", notes="旧系统数据导入", httpMethod="POST")
    @RequestMapping(value="/dailyReport/upload2", method=RequestMethod.POST)
    public ResponseEntity<String> uploadDailyReport2(MultipartFile Filedata) {
        InputStream inputXML = new BufferedInputStream(getClass().getResourceAsStream("/template/question2.xml"));
        XLSReader mainReader;
        try {
            mainReader = ReaderBuilder.buildFromXML( inputXML );

            InputStream inputXLS = new BufferedInputStream(Filedata.getInputStream());
            List<QuestionVO2> questions = new ArrayList<QuestionVO2>();
            Map<String, Object> beans = new HashMap<String, Object>();
            beans.put("questions", questions);
            XLSReadStatus readStatus = mainReader.read( inputXLS, beans);
            if(!readStatus.isStatusOK()) return new ResponseEntity<String>("读取excel数据失败", HttpStatus.BAD_REQUEST);
            if(CollectionUtils.isNotEmpty(questions)) {
                if(questions.get(0).getDbo_ELC_Item_item_id().equals("dbo_ELC_Item_item_id")) {
                    questions.remove(0);
                }
                String result = questionService.batchImport2(questions);
                return new ResponseEntity<String>(result, HttpStatus.OK);
            }
        } catch (IOException | SAXException | InvalidFormatException e) {
            e.printStackTrace();
            return new ResponseEntity<String>("error", HttpStatus.BAD_REQUEST);
        }
        
        return new ResponseEntity<String>("ok", HttpStatus.OK);
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @ApiOperation(value="日、周、月、年报", notes="报表下载", httpMethod="GET")
    @RequestMapping(value="/report/{section}/download", method=RequestMethod.GET)
    public ResponseEntity<byte[]> reportDownload(@PathVariable("section") String section, HttpSession session) {
//        默认周报
        User user = (User)session.getAttribute("user");
        String fileName = (section.equals("day") ? "日" : section.equals("week") ? "周" : section.equals("month") ? "月" : section.equals("year") ? "年" : "") + "问题汇总报表.xls";

        try {
            final HttpHeaders headers = new HttpHeaders();
//          headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentType(new MediaType("application", "vnd.ms-excel"));
            headers.setContentDispositionFormData("attachment", new String(fileName.getBytes("gb2312"),"iso-8859-1"));
            String filePath = questionService.reportByTime(Calendar.getInstance().getTime(), section, user==null?-1:user.getId());
            
            if(filePath == null) {
                return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            
            File file = new File(filePath);
            FileInputStream in = new FileInputStream(file);
    
            return new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @ApiOperation(value="日、周、月、年报", notes="报表推送", httpMethod="GET")
    @RequestMapping(value="/report/{section}/push", method=RequestMethod.GET) 
    public ResponseEntity<String> reportPush(@PathVariable("section") String section, HttpSession session) {
        String result = questionService.reportPush(Calendar.getInstance().getTime(), section, 0);
        return new ResponseEntity<String>(result, result.equals("ok") ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @ApiOperation(value="日、周、月、年报", notes="报表下载", httpMethod="GET")
    @RequestMapping(value="/report/download", method=RequestMethod.GET)
    public ResponseEntity<byte[]> richReportDownload(Integer year, Integer month, Integer date, HttpSession session) {
        User user = (User)session.getAttribute("user");
        String fileName = year.toString();
        String section = "year";
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        if(date != null) {
            fileName += "/" + month + "/" + date;
            section = "day";
            c.set(Calendar.MONTH, month - 1);
            c.set(Calendar.DATE, date);
        }
        else if(month != null) {
            fileName += "/" + month;
            section = "month";
            c.set(Calendar.MONTH, month -1);
        }
        fileName += "问题汇总报表.xls";

        try {
            final HttpHeaders headers = new HttpHeaders();
//          headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentType(new MediaType("application", "vnd.ms-excel"));
            headers.setContentDispositionFormData("attachment", new String(fileName.getBytes("gb2312"),"iso-8859-1"));
            
            String filePath = questionService.richReportByTime(c.getTime(), section, user==null?-1:user.getId());
            
            if(filePath == null) {
                return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            
            File file = new File(filePath);
            FileInputStream in = new FileInputStream(file);
    
            return new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @ApiOperation(value="日、周、月、年报", notes="报表推送", httpMethod="GET")
    @RequestMapping(value="/report/push", method=RequestMethod.GET) 
    public ResponseEntity<String> richReportPush(Integer year, Integer month, Integer date, HttpSession session) {
        String fileName = year.toString();
        String section = "year";
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        if(date != null) {
            fileName += "/" + month + "/" + date;
            section = "day";
            c.set(Calendar.MONTH, month - 1);
            c.set(Calendar.DATE, date);
        }
        else if(month != null) {
            fileName += "/" + month;
            section = "month";
            c.set(Calendar.MONTH, month -1);
        }
        String result = questionService.richReportPush(c.getTime(), section, 0, fileName);
        return new ResponseEntity<String>(result, result.equals("ok") ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
}
