package com.cn.myQA.service;

import java.util.Date;
import java.util.List;

import com.cn.myQA.pojo.Message;
import com.cn.myQA.web.datatables.TableModel;

public interface IMessageService {
    public List<Message> page(TableModel model, Integer userId, Date start, Date end);
    
    public String create(Message msg); 
}
