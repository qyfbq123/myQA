package com.cn.myQA.service;

import java.io.File;

public interface IMailService {
    public void sendmail(String[] mailArray,String subject,String content);
    public void sendmail(String[] mailArray,String subject,String content, File attachment);
}
