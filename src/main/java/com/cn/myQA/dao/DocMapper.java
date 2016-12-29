package com.cn.myQA.dao;

import org.apache.ibatis.annotations.Param;

import com.cn.myQA.pojo.DocContract;
import com.cn.myQA.pojo.DocFile;
import com.cn.myQA.pojo.DocOther;
import com.cn.myQA.web.ContractSearch;
import com.cn.myQA.web.OtherDocSearch;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;

public interface DocMapper {
    
    PageList<DocContract> contractPage(PageBounds pb, @Param("search") ContractSearch search);
    
    PageList<DocOther> otherPage(PageBounds pb, @Param("search") OtherDocSearch search);
    
    DocContract singleContract(Integer id);
    
    DocOther singleOther(Integer id);
    
    int insertContract(DocContract contract);
    
    int updateContract(DocContract contract);
    
    int insertOther(DocOther contract);
    
    int updateOther(DocOther other);
    
    int insertDocFile(DocFile file);
    
    DocFile singleDocFile(Integer id);
    
    int delDocFile(Integer id);
}
