package com.cn.myQA.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.myQA.dao.SysDictMapper;
import com.cn.myQA.pojo.SysDict;
import com.cn.myQA.service.IDictService;
import com.cn.myQA.web.select2.Option;

@Service
public class DictServiceImpl implements IDictService {
    
    @Autowired
    private SysDictMapper dictMapper;
    
    private List<Option> transDict2Option(List<SysDict> dictList){
        List<Option> optionList = new ArrayList<Option>();
        for (SysDict dict : dictList) {
            Option o = new Option(dict.getDictId(), dict.getDictValue());
            optionList.add(o);
        }
        return optionList;
    }

    @Override
    public List<Option> cities() {
        return transDict2Option( dictMapper.findByType(1) );
    }

    @Override
    public List<Option> projects() {
        return transDict2Option( dictMapper.findByType(2) );
    }

    @Override
    public List<Option> pmTypes() {
        List<SysDict> typeList = dictMapper.findByType(3);
//        16-6-20 问题类型排序
        Collections.sort(typeList, new Comparator<SysDict>() {
            public int compare(SysDict a, SysDict b) {
                return a.getDictValue().compareToIgnoreCase(b.getDictValue());
            }
        });
        return transDict2Option(typeList);
    }
    
    @Override
    public List<Option> msTypes() {
        List<SysDict> typeList = dictMapper.findByType(7);
//        16-6-20 问题类型排序
        Collections.sort(typeList, new Comparator<SysDict>() {
            public int compare(SysDict a, SysDict b) {
                return a.getDictValue().compareToIgnoreCase(b.getDictValue());
            }
        });
        return transDict2Option(typeList);
    }
    
    @Override
    public List<Option> severity() {
        return transDict2Option( dictMapper.findByType(4) );
    }
    
    @Override
    public List<Option> beginStorehouses() {
        return transDict2Option( dictMapper.findByType(5) );
    }
    
//    16-6-20
    @Override
    public List<Option> suppliers() {
        return transDict2Option( dictMapper.findByType(6) );
    }
    
    @Override
    public List<Option> bulletinTypes() {
        return transDict2Option( dictMapper.findByType(8) );
    }
    
    @Override
    public List<Option> functions() {
        return transDict2Option( dictMapper.findByType(9) );
    }
    
    @Override
    public List<Option> docTypes() {
        return transDict2Option( dictMapper.findByType(10) );
    }
    
    @Override
    public List<SysDict> attriutes() {
        return dictMapper.findByType(0);
    }
    
    @Override
    public List<SysDict> params(Integer typeid) {
        return dictMapper.findByType(typeid);
    }

    @Override
    public String saveAttributes(Integer typeid, List<SysDict> dictList) {
        dictMapper.deleteByType(typeid);
        
        if(CollectionUtils.isNotEmpty(dictList)) {
            int rows = dictMapper.saveAttributes(typeid, dictList);
            if(rows <= 0) return "error";
        }
        return "ok";
    }

}
