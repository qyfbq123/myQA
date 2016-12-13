package com.cn.myQA.service;

import java.util.Date;

import com.cn.myQA.pojo.Message;
import com.cn.myQA.web.datatables.Pagination;
import com.cn.myQA.web.datatables.TableModel;

public interface IMessageService {
    public Pagination<Message> page(TableModel model, Integer userId, Date start, Date end);
    
    public String create(Message msg); 
}
