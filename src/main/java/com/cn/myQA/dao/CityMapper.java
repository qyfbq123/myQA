package com.cn.myQA.dao;

import com.cn.myQA.pojo.City;

public interface CityMapper {
    int insert(City record);

    int insertSelective(City record);
}