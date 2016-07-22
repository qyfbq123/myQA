package com.cn.myQA.service.impl;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.cn.myQA.service.IMailService;

@Service("mailService")
public class MailServiceImpl implements IMailService {
    
    private static Logger logger = Logger.getLogger(MailServiceImpl.class);
    
    @Value("${mailuser}")
    private String MAIL_USER;
    @Value("${mailpassword}")
    private String MAIL_PASSWORD;
    @Value("${mailfromsmtp}")
    private String MAIL_FROM_SMTP;
    @Value("${mailhost}")
    private String MAIL_HOST;
    private String MAIL_NICK = "ELC跨职能沟通平台";
    
    @Override
    public void sendmail(String[] mailArray,String subject,String content){
//      if(StringUtils.isEmpty(MAIL_USER) || StringUtils.isEmpty(MAIL_PASSWORD) || StringUtils.isEmpty(MAIL_FROM_SMTP) || StringUtils.isEmpty(MAIL_HOST)) {
//      return;
//  	}
    	if(StringUtils.isEmpty(MAIL_HOST) || StringUtils.isEmpty(MAIL_FROM_SMTP)) {
    		return;
    	}
    	
    	boolean isUserAuthen = !(StringUtils.isEmpty(MAIL_USER) || StringUtils.isEmpty(MAIL_PASSWORD));
        Properties props = new Properties();
        //设置服务器验证
        props.setProperty("mail.smtp.auth", Boolean.valueOf(isUserAuthen).toString());
        //设置传输协议
        props.setProperty("mail.transport.protocol", "smtp");
        //选择服务类型
        props.setProperty("mail.host", MAIL_HOST);
        //通过认证创建一个session实例
        Session session = Session.getInstance(props,
                new Authenticator()
                {
                    protected PasswordAuthentication getPasswordAuthentication()
                    {
                    	return isUserAuthen? new PasswordAuthentication(MAIL_USER,MAIL_PASSWORD) : null;
                    }
                }
        );
        //显示邮件发送过程中的交互信息
        session.setDebug(false);
        
        Message msg = new MimeMessage(session);
        Transport transport=null;
        try {
            //邮件发送人
            msg.setFrom(new InternetAddress(MAIL_FROM_SMTP, MimeUtility.encodeText( MAIL_NICK ,"UTF-8","b")));
            //邮件主题
            msg.setSubject(subject);            
            //邮件内容
            msg.setContent(content, "text/html;charset=UTF-8");
            int len=mailArray.length;  
            InternetAddress address[]=new InternetAddress[len];  
            for (int i = 0; i < mailArray.length; i++) {
                address[i]=new InternetAddress(mailArray[i]); 
            }
            //邮件接收方
            msg.addRecipients(Message.RecipientType.TO, address);
            Transport.send(msg);            
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if(transport!=null){
                    transport.close();
                }               
            } catch (Exception e) {
                logger.error("邮件发送失败！", e);
            }
        }       
    }

    @Override
    public void sendmail(String[] mailArray,String subject,String content, String filePath, String fileName){
//      if(StringUtils.isEmpty(MAIL_USER) || StringUtils.isEmpty(MAIL_PASSWORD) || StringUtils.isEmpty(MAIL_FROM_SMTP) || StringUtils.isEmpty(MAIL_HOST)) {
//      	return;
//  	}
    	if(StringUtils.isEmpty(MAIL_HOST) || StringUtils.isEmpty(MAIL_FROM_SMTP)) {
    		return;
    	}
    	boolean isUserAuthen = !(StringUtils.isEmpty(MAIL_USER) || StringUtils.isEmpty(MAIL_PASSWORD));
        Properties props = new Properties();
        //设置服务器验证
        props.setProperty("mail.smtp.auth", Boolean.valueOf(isUserAuthen).toString());
        //设置传输协议
        props.setProperty("mail.transport.protocol", "smtp");
        //选择服务类型
        props.setProperty("mail.host", MAIL_HOST);
        //通过认证创建一个session实例
        Session session = Session.getInstance(props,
                new Authenticator()
                {
                    protected PasswordAuthentication getPasswordAuthentication()
                    {
                    	return isUserAuthen? new PasswordAuthentication(MAIL_USER,MAIL_PASSWORD) : null;
                    }
                }
        );
        //显示邮件发送过程中的交互信息
        session.setDebug(false);
        
        Message msg = new MimeMessage(session);
        Transport transport=null;
        try {
            //邮件发送人
            msg.setFrom(new InternetAddress(MAIL_FROM_SMTP, MimeUtility.encodeText( MAIL_NICK ,"UTF-8","b")));
            //邮件主题
            msg.setSubject(subject);
            // 向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
            Multipart multipart = new MimeMultipart();
            
            // 添加邮件正文
            BodyPart contentPart = new MimeBodyPart();
            contentPart.setText(content);
            multipart.addBodyPart(contentPart);
            // 添加附件的内容
            if (filePath != null) {
                BodyPart attachmentBodyPart = new MimeBodyPart();
                File attachment = new File(filePath);
                DataSource source = new FileDataSource(attachment);
                attachmentBodyPart.setDataHandler(new DataHandler(source));
                
                // 网上流传的解决文件名乱码的方法，其实用MimeUtility.encodeWord就可以很方便的搞定
                // 这里很重要，通过下面的Base64编码的转换可以保证你的中文附件标题名在发送时不会变成乱码
                //sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
                //messageBodyPart.setFileName("=?GBK?B?" + enc.encode(attachment.getName().getBytes()) + "?=");
                
                //MimeUtility.encodeWord可以避免文件名乱码
                if(StringUtils.isEmpty(fileName)) fileName = attachment.getName();
                attachmentBodyPart.setFileName(MimeUtility.encodeWord(fileName));
                multipart.addBodyPart(attachmentBodyPart);
            }
            
            // 将multipart对象放到message中
            msg.setContent(multipart);
            // 保存邮件
            msg.saveChanges();
//            //邮件内容
//            msg.setText(content);
            int len=mailArray.length;  
            InternetAddress address[]=new InternetAddress[len];  
            for (int i = 0; i < mailArray.length; i++) {
                address[i]=new InternetAddress(mailArray[i]); 
            }
            //邮件接收方
            msg.addRecipients(Message.RecipientType.TO, address);
            Transport.send(msg);            
        } catch (Exception e) {
            logger.error("邮件发送失败！", e);
        }finally{
            try {
                if(transport!=null){
                    transport.close();
                }               
            } catch (Exception e) {
                logger.error("邮件发送失败！", e);
            }
        }       
    }

}
