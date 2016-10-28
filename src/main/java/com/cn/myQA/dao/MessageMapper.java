package com.cn.myQA.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.myQA.pojo.Message;
import com.cn.myQA.pojo.User;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;

public interface MessageMapper {
    PageList<Message> page(PageBounds pb, @Param("userId")Integer userId, @Param("start")Date start, @Param("end") Date end);
    
    Message single(@Param("msgId")Integer msgId);
    
    int insert(Message msg);
    
    int msgBindReceivers(@Param("msgId")Integer msgId, @Param("receivers")List<User> receivers);
}
