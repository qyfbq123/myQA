package com.cn.myQA.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.cn.myQA.dao.MessageMapper;
import com.cn.myQA.pojo.Message;
import com.cn.myQA.pojo.User;
import com.cn.myQA.service.IMailService;
import com.cn.myQA.service.IMessageService;
import com.cn.myQA.web.datatables.TableModel;
import com.github.miemiedev.mybatis.paginator.domain.Order;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;

@Service("msgService")
public class MessageServiceImpl implements IMessageService {
    @Autowired
    private IMailService mailService;
    
    @Autowired
    private MessageMapper msgMapper;
    
    @Autowired
    private TaskExecutor taskExecutor;

    @Override
    public List<Message> page(TableModel model, Integer userId) {
        PageBounds pb = model.translateToPB();
        pb.setOrders(Order.formString("created.desc"));
        PageList<Message> msgList = msgMapper.page(pb, userId);
        return msgList.subList(0, msgList.size());
    }
    
    @Override
    public String create(Message msg) {
        int rows = msgMapper.insert(msg);
        
        if(CollectionUtils.isNotEmpty(msg.getReceivers())) {
            msgMapper.msgBindReceivers(msg.getId(), msg.getReceivers());
            final Message msg2 = msgMapper.single(msg.getId());
            List<String> mailList = new ArrayList<String>();
            
            for(User u : msg2.getReceivers()) {
                if(!StringUtils.isEmpty(u.getEmail()))
                    mailList.add(u.getEmail());
            }
            
            if(CollectionUtils.isNotEmpty(mailList)) {
                taskExecutor.execute(new Runnable(){    
                    public void run(){
                        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
                        
                        String content = "职能：" + msg2.getFunction();
                        content += "<br/>";
                        content += "ELC：" + msg2.getElc();
                        content += "<br/>";
                        content += "项目：" + msg2.getProjectName();
                        content += "<br/><br/>";
                        content += "工作内容：" + msg2.getWorkContent();
                        content += "<br/><br/>";
                        content += "日期：" + df.format(msg2.getDate());
                        mailService.sendmail(mailList.toArray(new String[mailList.size()]), "系统公告", content);
                    }    
                 });
            }
        }
        
        return rows == 1 ? "ok" : "error";
    }
}
