package com.cn.myQA.web;

public class QuestionSearch {
    private String category;
    private String number;
    private String project;
    private String type;
    private String description;
    private Integer creatorId;
    private Boolean closed;
    private Integer handlerId;
    private Integer handleStatus;
    
    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public String getProject() {
        return project;
    }
    public void setProject(String project) {
        this.project = project;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public Integer getCreatorId() {
        return creatorId;
    }
    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }
    public Boolean getClosed() {
        return closed;
    }
    public void setClosed(Boolean closed) {
        this.closed = closed;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Integer getHandleStatus() {
        return handleStatus;
    }
    public void setHandleStatus(Integer handleStatus) {
        this.handleStatus = handleStatus;
    }
    public Integer getHandlerId() {
        return handlerId;
    }
    public void setHandlerId(Integer handlerId) {
        this.handlerId = handlerId;
    }
    
}
