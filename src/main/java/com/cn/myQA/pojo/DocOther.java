package com.cn.myQA.pojo;

import java.util.Date;

public class DocOther {
    private Integer id;
    private Date created;
    private Date modified;
    private Integer creatorId;
    private Integer modifierId;
    private String category;
    private String function;
    private String docType;
    private String docProperty;
    private String content;
    private String importance;
    private String creator;
    private Date date;
    private DocFile file;
    
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
    public Date getModified() {
        return modified;
    }
    public void setModified(Date modified) {
        this.modified = modified;
    }
    public Integer getCreatorId() {
        return creatorId;
    }
    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }
    public Integer getModifierId() {
        return modifierId;
    }
    public void setModifierId(Integer modifierId) {
        this.modifierId = modifierId;
    }
    public String getFunction() {
        return function;
    }
    public void setFunction(String function) {
        this.function = function;
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
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public DocFile getFile() {
        return file;
    }
    public void setFile(DocFile file) {
        this.file = file;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getDocType() {
        return docType;
    }
    public void setDocType(String docType) {
        this.docType = docType;
    }
    public String getDocProperty() {
        return docProperty;
    }
    public void setDocProperty(String docProperty) {
        this.docProperty = docProperty;
    }
}
