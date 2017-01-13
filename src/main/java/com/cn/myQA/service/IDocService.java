package com.cn.myQA.service;

import com.cn.myQA.pojo.DocContract;
import com.cn.myQA.pojo.DocFile;
import com.cn.myQA.pojo.DocOther;
import com.cn.myQA.web.ContractSearch;
import com.cn.myQA.web.OtherDocSearch;
import com.cn.myQA.web.datatables.Pagination;
import com.cn.myQA.web.datatables.TableModel;

public interface IDocService {
    public String getUploadPath();
    
    public Pagination<DocContract> contractPage(TableModel model, ContractSearch search);
    
    public Pagination<DocOther> otherDocPage(TableModel model, OtherDocSearch search);
    
    public DocContract singleContract(Integer id);
    
    public DocOther singleOther(Integer id);
    
    public String addContract(DocContract contract);
    
    public String updateContract(DocContract contract);

    public String addOther(DocOther other);
    
    public String updateOther(DocOther other);
    
    public DocFile singleDocFile(Integer id);
    
    public int insertDocFile(DocFile file);
    
    public String delDocFile(Integer id);
    
    public String delContract(Integer id);
    
    public String delOther(Integer id);
}
