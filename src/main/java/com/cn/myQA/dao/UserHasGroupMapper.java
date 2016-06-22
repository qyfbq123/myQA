package com.cn.myQA.dao;

import com.cn.myQA.pojo.UserHasGroup;

public interface UserHasGroupMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserHasGroup record);

    int insertSelective(UserHasGroup record);

    UserHasGroup selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserHasGroup record);

    int updateByPrimaryKey(UserHasGroup record);
}