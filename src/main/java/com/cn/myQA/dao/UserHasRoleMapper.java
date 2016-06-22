package com.cn.myQA.dao;

import com.cn.myQA.pojo.UserHasRole;

public interface UserHasRoleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserHasRole record);

    int insertSelective(UserHasRole record);

    UserHasRole selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserHasRole record);

    int updateByPrimaryKey(UserHasRole record);
}