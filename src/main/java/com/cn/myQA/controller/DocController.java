package com.cn.myQA.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.cn.myQA.pojo.DocContract;
import com.cn.myQA.pojo.DocFile;
import com.cn.myQA.pojo.DocOther;
import com.cn.myQA.pojo.User;
import com.cn.myQA.service.IDocService;
import com.cn.myQA.web.ContractSearch;
import com.cn.myQA.web.OtherDocSearch;
import com.cn.myQA.web.datatables.Pagination;
import com.cn.myQA.web.datatables.TableModel;
import com.mysql.jdbc.StringUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value="doc", description="文档CRUD操作")
@Controller
@RequestMapping("/doc")
public class DocController {
    @Autowired
    private IDocService docService;
    
    @ApiOperation(value="合同分页查询", notes="分页查询合同信息", httpMethod="GET")
    @RequestMapping(value="/contract/page", method=RequestMethod.GET)
    public ResponseEntity<Pagination<DocContract>> contractPage(TableModel model, ContractSearch search) {
        return new ResponseEntity<Pagination<DocContract>>(docService.contractPage(model, search), HttpStatus.OK);
    }
    
    @ApiOperation(value="其他文档分页查询", notes="分页查询其他文档信息", httpMethod="GET")
    @RequestMapping(value="/other/page", method=RequestMethod.GET)
    public ResponseEntity<Pagination<DocOther>> otherPage(TableModel model, OtherDocSearch search) {
        return new ResponseEntity<Pagination<DocOther>>(docService.otherDocPage(model, search), HttpStatus.OK);
    }
    
    @ApiOperation(value="合同详情查询", notes="查询合同信息", httpMethod="GET")
    @RequestMapping(value="/contract/{id}", method=RequestMethod.GET)
    public ResponseEntity<DocContract> singleContract(@PathVariable("id") Integer id) {
        return  new ResponseEntity<DocContract>(docService.singleContract(id), HttpStatus.OK);
    }
    
    @ApiOperation(value="其他文档详情查询", notes="查询其他文档信息", httpMethod="GET")
    @RequestMapping(value="/other/{id}", method=RequestMethod.GET)
    public ResponseEntity<DocOther> singleOther(@PathVariable("id") Integer id) {
        return  new ResponseEntity<DocOther>(docService.singleOther(id), HttpStatus.OK);
    }
    
    @ApiOperation(value="新增合同", notes="新增合同", httpMethod="POST")
    @RequestMapping(value="/createContract", method=RequestMethod.POST)
    public ResponseEntity<String> createContract(@RequestBody DocContract contract, HttpSession session) {
        Object userObj = session.getAttribute("user");
        User user;
        if(userObj == null) {
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        } else {
            user = (User) userObj;
        }
        contract.setCreatorId(user.getId());
        String result = docService.addContract(contract);
        return new ResponseEntity<String>(result, "ok".equals(result)? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ApiOperation(value="修改合同", notes="修改合同", httpMethod="PUT")
    @RequestMapping(value="/updateContract", method=RequestMethod.PUT)
    public ResponseEntity<String> updateContract(@RequestBody DocContract contract, HttpSession session) {
        Object userObj = session.getAttribute("user");
        User user;
        if(userObj == null) {
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        } else {
            user = (User) userObj;
        }
        contract.setModifierId(user.getId());
        String result = docService.updateContract(contract);
        return new ResponseEntity<String>(result, "ok".equals(result)? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ApiOperation(value="新增其他", notes="新增其他", httpMethod="POST")
    @RequestMapping(value="/createOther", method=RequestMethod.POST)
    public ResponseEntity<String> createOther(@RequestBody DocOther other, HttpSession session) {
        Object userObj = session.getAttribute("user");
        User user;
        if(userObj == null) {
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        } else {
            user = (User) userObj;
        }
        other.setCreatorId(user.getId());
        String result = docService.addOther(other);
        return new ResponseEntity<String>(result, "ok".equals(result)? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ApiOperation(value="修改其他", notes="修改其他", httpMethod="PUT")
    @RequestMapping(value="/updateOther", method=RequestMethod.PUT)
    public ResponseEntity<String> updateOther(@RequestBody DocOther other, HttpSession session) {
        Object userObj = session.getAttribute("user");
        User user;
        if(userObj == null) {
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        } else {
            user = (User) userObj;
        }
        other.setModifierId(user.getId());
        String result = docService.updateOther(other);
        return new ResponseEntity<String>(result, "ok".equals(result)? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ApiOperation(value="上传文档", notes="文档上传", httpMethod="POST")
    @RequestMapping(value="/file/upload", method=RequestMethod.POST)
    public ResponseEntity<DocFile> uploadFile(MultipartFile Filedata, HttpSession session) {
        User user = (User)session.getAttribute("user");
        if(user == null) user = new User();
        String fileName = Filedata.getOriginalFilename();
        long l = System.currentTimeMillis();
        String newFileName = l + "_" + fileName;
        File tempFile = new File(docService.getUploadPath() + System.getProperty("file.separator") + "doc" + System.getProperty("file.separator"), newFileName);
        
        if (!tempFile.getParentFile().exists()) {  
            tempFile.getParentFile().mkdirs();  
        }  
        try {
            if (!tempFile.exists()) {  
                tempFile.createNewFile();
            }  
            Filedata.transferTo(tempFile);
            
            DocFile df = new DocFile();
            df.setFilename(fileName);
            df.setPath("doc" + System.getProperty("file.separator") + newFileName);
            df.setSize(FileUtils.byteCountToDisplaySize(tempFile.length()));
            
            int result = docService.insertDocFile(df);
            if(result == 1)
                return new ResponseEntity<DocFile>(df, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<DocFile>(HttpStatus.BAD_REQUEST);
    }
    
    @ApiOperation(value="文档删除", notes="文档删除", httpMethod="DELETE")
    @RequestMapping(value="/file/{id}", method=RequestMethod.DELETE) 
    public ResponseEntity<String> delete(@PathVariable("id") Integer id){
        String result = docService.delDocFile(id);
        return new ResponseEntity<String>(result , "ok".equals(result) ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    @ApiOperation(value="文档下载", notes="文档下载", httpMethod="GET")
    @RequestMapping(value="/file/{id}/{filename}", method=RequestMethod.GET) 
    public ResponseEntity<byte[]> download(@PathVariable("id") Integer id){
        DocFile docFile = docService.singleDocFile(id);
        if( docFile != null && !StringUtils.isNullOrEmpty(docFile.getPath()) ) {
            try {
                File file = new File(docService.getUploadPath()  + System.getProperty("file.separator") + docFile.getPath());
                FileInputStream in = new FileInputStream(file);

                final HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType("application/force-download"));
                headers.setContentDispositionFormData("attachment", new String(docFile.getFilename().getBytes("gb2312"),"iso-8859-1"));
        
                return new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.OK);
            } catch (FileNotFoundException e) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            } catch (IOException e) {
                return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
    
    @ApiOperation(value="视频下载", notes="视频下载", httpMethod="GET")
    @RequestMapping(value="/video/{id}/{filename}", method=RequestMethod.GET) 
    public void download(@PathVariable("id") Integer id, HttpServletRequest request, HttpServletResponse response) throws Exception{
        DocFile docFile = docService.singleDocFile(id);
        if( docFile != null && !StringUtils.isNullOrEmpty(docFile.getPath()) ) {
            File file = new File(docService.getUploadPath()  + System.getProperty("file.separator") + docFile.getPath());
            if(file.exists()) {
                MultipartFileSender.fromFile(file).with(request).with(response).serveResource();
            } else {
                response.setStatus(404);
            }
        } 
    }
}
