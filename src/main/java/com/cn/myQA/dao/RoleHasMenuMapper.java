package com.cn.myQA.dao;

import com.cn.myQA.pojo.RoleHasMenu;

public interface RoleHasMenuMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RoleHasMenu record);

    int insertSelective(RoleHasMenu record);

    RoleHasMenu selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RoleHasMenu record);

    int updateByPrimaryKey(RoleHasMenu record);
}