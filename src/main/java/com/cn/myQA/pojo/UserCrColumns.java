package com.cn.myQA.pojo;

import java.util.Date;

public class UserCrColumns {
    private Integer id;
    private Integer userId;
    private Integer crId;
    private String columns;
    private Date created;
    private Date modified;
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getCrId() {
        return crId;
    }
    public void setCrId(Integer crId) {
        this.crId = crId;
    }
    public String getColumns() {
        return columns;
    }
    public void setColumns(String columns) {
        this.columns = columns;
    }
    public Date getCreated() {
        return created;
    }
    public void setCreated(Date created) {
        this.created = created;
    }
    public Date getModified() {
        return modified;
    }
    public void setModified(Date modified) {
        this.modified = modified;
    }
    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
}
