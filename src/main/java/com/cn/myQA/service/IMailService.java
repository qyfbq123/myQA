package com.cn.myQA.service;


public interface IMailService {
    public void sendmail(String[] mailArray,String subject,String content);
    public void sendmail(String[] mailArray,String subject,String content, String filePath, String fileName);
}
