package com.cn.myQA.web;

import java.util.Date;

public class ContractSearch {
    private String vendorCode;
    private String vendorType;
    private String contractName;
    private Date signDateBegin;
    private Date signDateEnd;
    private String currentStatus;
    public String getVendorCode() {
        return vendorCode;
    }
    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }
    public String getVendorType() {
        return vendorType;
    }
    public void setVendorType(String vendorType) {
        this.vendorType = vendorType;
    }
    public String getContractName() {
        return contractName;
    }
    public void setContractName(String contractName) {
        this.contractName = contractName;
    }
    public Date getSignDateBegin() {
        return signDateBegin;
    }
    public void setSignDateBegin(Date signDateBegin) {
        this.signDateBegin = signDateBegin;
    }
    public String getCurrentStatus() {
        return currentStatus;
    }
    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }
    public Date getSignDateEnd() {
        return signDateEnd;
    }
    public void setSignDateEnd(Date signDateEnd) {
        this.signDateEnd = signDateEnd;
    }
    
}
