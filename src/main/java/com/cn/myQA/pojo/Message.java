package com.cn.myQA.pojo;

import java.util.Date;
import java.util.List;

public class Message {
    private Integer id;
    private Date created;
    private String function;
    private String workContent;
    private String elc;
    private String projectName;
    private Date date;
    private Boolean isMailPush;
    private Boolean isTelPush;
    private String type;
    private String solution;
    private String remark;
    
    private User creator;
    private List<User> receivers;
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Date getCreated() {
        return created;
    }
    public void setCreated(Date created) {
        this.created = created;
    }
    public String getFunction() {
        return function;
    }
    public void setFunction(String function) {
        this.function = function;
    }
    public String getWorkContent() {
        return workContent;
    }
    public void setWorkContent(String workContent) {
        this.workContent = workContent;
    }
    public String getElc() {
        return elc;
    }
    public void setElc(String elc) {
        this.elc = elc;
    }
    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public Boolean getIsMailPush() {
        return isMailPush;
    }
    public void setIsMailPush(Boolean isMailPush) {
        this.isMailPush = isMailPush;
    }
    public Boolean getIsTelPush() {
        return isTelPush;
    }
    public void setIsTelPush(Boolean isTelPush) {
        this.isTelPush = isTelPush;
    }
    public User getCreator() {
        return creator;
    }
    public void setCreator(User creator) {
        this.creator = creator;
    }
    public List<User> getReceivers() {
        return receivers;
    }
    public void setReceivers(List<User> receivers) {
        this.receivers = receivers;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getSolution() {
        return solution;
    }
    public void setSolution(String solution) {
        this.solution = solution;
    }
    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
}
