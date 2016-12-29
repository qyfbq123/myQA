package com.cn.myQA.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpSession;

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
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.myQA.pojo.CustomizeReport;
import com.cn.myQA.pojo.User;
import com.cn.myQA.pojo.UserCrColumns;
import com.cn.myQA.service.ICustomizeReportService;
import com.cn.myQA.web.select2.Option;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "customize", description = "自定义报表CRUD操作")
@Controller
@RequestMapping("/customize/report")
public class CustomizeReportCotroller {
    @Autowired
    ICustomizeReportService crService;

    @ApiOperation(value = "所有自定义报表", notes = "获取所有自定义报表", httpMethod = "GET")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @ResponseBody
    public List<CustomizeReport> all() {
        return crService.all();
    }
    
    @ApiOperation(value = "用户可使用的自定义报表", notes = "获取用户的所有自定义报表", httpMethod = "GET")
    @RequestMapping(value = "/allByUser", method = RequestMethod.GET)
    @ResponseBody
    public List<CustomizeReport> allByUser(HttpSession session) {
        Object userObj = session.getAttribute("user");
        if(userObj == null) {
            return null;
        }
        return crService.all( ((User)userObj).getId() );
    }

    @ApiOperation(value = "所有视图", notes = "获取所有视图", httpMethod = "GET")
    @RequestMapping(value = "/allViews", method = RequestMethod.GET)
    @ResponseBody
    public List<Option> allViews() {
        return crService.allViews();
    }

    @ApiOperation(value = "所有存储过程", notes = "获取所有存储过程", httpMethod = "GET")
    @RequestMapping(value = "/allProcs", method = RequestMethod.GET)
    @ResponseBody
    public List<Option> allProcs() {
        return crService.allProcs();
    }

    @ApiOperation(value = "存储自定义报表", notes = "自定义报表存储", httpMethod = "POST")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> save(@RequestBody CustomizeReport customizeReport) {
        String result = crService.save(customizeReport);

        return result.equals("ok") ? new ResponseEntity<String>(result, HttpStatus.OK)
                : new ResponseEntity<String>(result, HttpStatus.BAD_REQUEST);
    }

    @ApiOperation(value = "自定义报表详情", notes = "自定义报表详情", httpMethod = "GET")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<CustomizeReport> single(@PathVariable("id") Integer id) {
        if (id == null || id <= 0) {
            return new ResponseEntity<CustomizeReport>(HttpStatus.BAD_REQUEST);
        } else {
            CustomizeReport cr = crService.single(id);
            if(cr != null)
                return new ResponseEntity<CustomizeReport>(cr, HttpStatus.OK);
            else return new ResponseEntity<CustomizeReport>(HttpStatus.NOT_FOUND);
        }
    }
    
    @ApiOperation(value = "自定义报表删除", notes = "自定义报表删除", httpMethod = "DELETE")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<String> delete(@PathVariable("id") Integer id) {
        if (id == null || id <= 0) {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<String>(crService.delete(id), HttpStatus.OK);
        }
    }

    @ApiOperation(value = "自定义列", notes = "自定义列", httpMethod = "GET")
    @RequestMapping(value = "/{id}/columns", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<UserCrColumns> columns(@PathVariable("id") Integer id,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return new ResponseEntity<UserCrColumns>(HttpStatus.UNAUTHORIZED);
        if (id == null || id <= 0) {
            return new ResponseEntity<UserCrColumns>(HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<UserCrColumns>(crService.columns(id, user.getId()),
                    HttpStatus.OK);
        }
    }

    @ApiOperation(value = "保存自定义列", notes = "保存自定义列", httpMethod = "POST")
    @RequestMapping(value = "/columns/save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<UserCrColumns> saveColumns(UserCrColumns ucrColumns,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return new ResponseEntity<UserCrColumns>(HttpStatus.UNAUTHORIZED);

        ucrColumns.setUserId(user.getId());
        if (ucrColumns.getCrId() == null || ucrColumns.getCrId() <= 0) {
            return new ResponseEntity<UserCrColumns>(HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<UserCrColumns>(crService.saveColumns(ucrColumns),
                    HttpStatus.OK);
        }
    }

    @ApiOperation(value = "自定义报表运行", notes = "自定义报表运行", httpMethod = "GET")
    @RequestMapping(value = "/run", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List> run(CustomizeReport customizeReport) {
        return new ResponseEntity<List>(crService.run(customizeReport), HttpStatus.OK);
    }

    @ApiOperation(value = "自定义报表下载", notes = "自定义报表下载", httpMethod = "GET")
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> download(CustomizeReport customizeReport, HttpSession session) {
        User user = (User) session.getAttribute("user");

        String fileName = customizeReport.getName() + ".xls";
        try {
            final HttpHeaders headers = new HttpHeaders();
            // headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentType(new MediaType("application", "vnd.ms-excel"));
            headers.setContentDispositionFormData("attachment",
                    new String(fileName.getBytes("gb2312"), "iso-8859-1"));

            String filePath = crService.report(customizeReport, user == null ? null : user.getId());

            if (filePath == null) {
                return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            File file = new File(filePath);
            FileInputStream in = new FileInputStream(file);

            return new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "自定义报表推送", notes = "自定义报表推送", httpMethod = "GET")
    @RequestMapping(value = "/push", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> push(CustomizeReport customizeReport, HttpSession session) {
        User user = (User) session.getAttribute("user");

        String result = crService.push(customizeReport, user == null ? null : user.getId());

        return new ResponseEntity<String>(result, result.equals("ok") ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
}
