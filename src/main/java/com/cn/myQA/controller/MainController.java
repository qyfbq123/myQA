package com.cn.myQA.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cn.myQA.pojo.User;
import com.cn.myQA.service.IUserService;
import com.cn.myQA.web.LoginAccount;

@Api(value = "main", description = "系统的主入口，登录登出")
@Controller
@RequestMapping("/main")
public class MainController {
    @Autowired
    private IUserService userService;
    
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ApiOperation(value = "登陆", notes="用户登陆账号检查",httpMethod = "POST")
    public ResponseEntity<User> login(@RequestBody LoginAccount account, HttpSession session) {
        User user = userService.findByLoginID(account.getLoginID());
        if(user == null) return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        else {
            if(user.getLocked()) return new ResponseEntity<User>(HttpStatus.FORBIDDEN);
            else if(userService.isVerifiedByLdap()) {
            	if(userService.verifiedByLdap(user.getLoginid(), account.getPassword())) {
            		session.setAttribute("user", user);
                    return new ResponseEntity<User>(user, HttpStatus.OK);
            	}
            	else return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
            }
            else if(user.getPassword().equals(account.getPassword())) {
                session.setAttribute("user", user);
                return new ResponseEntity<User>(user, HttpStatus.OK);
            }
            else return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @RequestMapping(value = "/logout", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation(value = "登出", notes="用户登出帐号",httpMethod = "GET")
    public ResponseEntity<String> logout(HttpSession session) {
        session.removeAttribute("user");
        return new ResponseEntity<String>("ok", HttpStatus.OK);
    }
}
