package com.cn.myQA.service.impl;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cn.myQA.dao.DocMapper;
import com.cn.myQA.pojo.DocContract;
import com.cn.myQA.pojo.DocFile;
import com.cn.myQA.pojo.DocOther;
import com.cn.myQA.service.IDocService;
import com.cn.myQA.web.ContractSearch;
import com.cn.myQA.web.OtherDocSearch;
import com.cn.myQA.web.datatables.Pagination;
import com.cn.myQA.web.datatables.TableModel;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;

@Service("docService")
public class DocServiceImpl implements IDocService {
    @Autowired
    private DocMapper docMapper;

    @Value("${uploadPath}")
    private String uploadPath;

    public String getUploadPath() {
        return uploadPath;
    }
    
    public Pagination<DocContract> contractPage(TableModel model, ContractSearch search) {
        PageBounds pb = model.translateToPB();
        PageList<DocContract> dList = docMapper.contractPage(pb, search);
        Pagination<DocContract> page = new Pagination<DocContract>();
        page.setDraw(model.getDraw());
        page.setData(dList.subList(0, dList.size()));
        page.setRecordsFiltered(dList.getPaginator().getTotalCount());
        page.setRecordsTotal(dList.getPaginator().getTotalCount());
        return page;
    }
    
    public Pagination<DocOther> otherDocPage(TableModel model, OtherDocSearch search) {
        PageBounds pb = model.translateToPB();
        PageList<DocOther> dList = docMapper.otherPage(pb, search);
        Pagination<DocOther> page = new Pagination<DocOther>();
        page.setDraw(model.getDraw());
        page.setData(dList.subList(0, dList.size()));
        page.setRecordsFiltered(dList.getPaginator().getTotalCount());
        page.setRecordsTotal(dList.getPaginator().getTotalCount());
        return page;
    }
    
    public DocContract singleContract(Integer id) {
        return docMapper.singleContract(id);
    }
    
    public DocOther singleOther(Integer id) {
        return docMapper.singleOther(id);
    }

    @Override
    public String addContract(DocContract contract) {
        int result = docMapper.insertContract(contract);

        return result == 1 ? "ok" : "error";
    }
    
    @Override
    public String updateContract(DocContract contract) {
        int result = docMapper.updateContract(contract);

        return result == 1 ? "ok" : "error";
    }

    @Override
    public String addOther(DocOther other) {
        int result = docMapper.insertOther(other);

        return result == 1 ? "ok" : "error";
    }
    
    @Override
    public String updateOther(DocOther other) {
        int result = docMapper.updateOther(other);

        return result == 1 ? "ok" : "error";
    }
    
    public DocFile singleDocFile(Integer id) {
        return docMapper.singleDocFile(id);
    }

    public int insertDocFile(DocFile file) {
        return docMapper.insertDocFile(file);
    }

    @Override
    public String delDocFile(Integer id) {
        DocFile qa = docMapper.singleDocFile(id);
        if(qa != null) {
            File file = new File(uploadPath + System.getProperty("file.separator") + qa.getPath());
            if (file.exists()) {
                file.delete();
            }
        }
        int result = docMapper.delDocFile(id);
        return result == 1 ? "ok" : "error";
    }
    
    @Override
    public String delContract(Integer id) {
        DocContract contract = docMapper.singleContract(id);
        if(contract != null && contract.getFile() != null && contract.getFile().getId() != null ) {
            this.delDocFile(contract.getFile().getId());
        }
        int result = docMapper.delContract(id);
        return result == 1 ? "ok" : "error";
    }
    
    @Override
    public String delOther(Integer id) {
        DocOther other = docMapper.singleOther(id);
        if(other != null && other.getFile() != null && other.getFile().getId()!=null) {
            this.delDocFile(other.getFile().getId());
        }
        int result = docMapper.delOther(id);
        return result == 1 ? "ok" : "error";
    }
}
