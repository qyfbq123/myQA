package com.cn.myQA.web;

import java.util.Date;

public class OtherDocSearch {
    private String category;
    private String function;
    private String docType;
    private String content;
    private String importance;
    private String creator;
    private Date dateBegin;
    private Date dateEnd;
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getImportance() {
        return importance;
    }
    public void setImportance(String importance) {
        this.importance = importance;
    }
    public String getCreator() {
        return creator;
    }
    public void setCreator(String creator) {
        this.creator = creator;
    }
    public Date getDateBegin() {
        return dateBegin;
    }
    public void setDateBegin(Date dateBegin) {
        this.dateBegin = dateBegin;
    }
    public Date getDateEnd() {
        return dateEnd;
    }
    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }
    public String getFunction() {
        return function;
    }
    public void setFunction(String function) {
        this.function = function;
    }
    public String getDocType() {
        return docType;
    }
    public void setDocType(String docType) {
        this.docType = docType;
    }
}
