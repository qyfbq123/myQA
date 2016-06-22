package com.cn.myQA.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("登陆请求参数")
public class LoginAccount {
    @ApiModelProperty(value = "登陆账号",required = true)
    private String loginID;
    @ApiModelProperty(value = "登陆密码",required = false)
    private String password;
    public String getLoginID() {
        return loginID;
    }
    public void setLoginID(String loginID) {
        this.loginID = loginID;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    
}
