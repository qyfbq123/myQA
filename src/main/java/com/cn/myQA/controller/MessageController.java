package com.cn.myQA.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cn.myQA.pojo.Message;
import com.cn.myQA.pojo.User;
import com.cn.myQA.service.IMessageService;
import com.cn.myQA.web.datatables.TableModel;

@Api(value = "msg", description = "系统公告、短消息")
@Controller
@RequestMapping("/msg")
public class MessageController {
    @Autowired
    private IMessageService msgService;
    
    @ApiOperation(value="公告分页查询", notes="分页查询公告", httpMethod="GET")
    @RequestMapping(value="/page", method=RequestMethod.GET)
    public ResponseEntity<List<Message>> page(TableModel model, HttpSession session) {
        System.out.println(model.getStart());
        Object userObj = session.getAttribute("user");
        User user;
        if(userObj == null) {
            return new ResponseEntity<List<Message>>(HttpStatus.UNAUTHORIZED);
        } else {
            user = (User) userObj;
        }
        
        List<Message> msgList = msgService.page(model, user.getId());
        return new ResponseEntity<List<Message>>(msgList, HttpStatus.OK);
    }
    
    @ApiOperation(value="公告新增", notes="新增公告", httpMethod="POST")
    @RequestMapping(value="/create", method=RequestMethod.POST)
    public ResponseEntity<String> create(@RequestBody Message msg, HttpSession session) {
        Object userObj = session.getAttribute("user");
        if(userObj == null) {
            return new ResponseEntity<String>("error", HttpStatus.UNAUTHORIZED);
        }
        msg.setCreator((User)userObj);
        String result = msgService.create(msg);
        
        return new ResponseEntity<String>(result, result.equals("ok") ? HttpStatus.OK: HttpStatus.BAD_REQUEST);
    }
}
