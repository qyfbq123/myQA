package com.cn.myQA.web.datatables;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;

@ApiModel("分页请求参数")
public class TableModel {
    
    @ApiModelProperty(value="请求次数计数器", example="2", required=true)
    private Integer draw;
    
    @ApiModelProperty(value="第一条数据的起始位置", example="0", required=true)
    private Integer start;
    
    @ApiModelProperty(value="每页显示的条数", example="10", required=true)
    private Integer length;
    
//    @ApiModelProperty(value="全局的搜索条件", required=false)
//    private Search search;

    public Integer getDraw() {
        return draw;
    }

    public void setDraw(Integer draw) {
        this.draw = draw;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

//    public Search getSearch() {
//        return search;
//    }
//
//    public void setSearch(Search search) {
//        this.search = search;
//    }
    
    public PageBounds translateToPB() {
        PageBounds pb = new PageBounds();
        pb.setContainsTotalCount(true);
        
        if(this.length == null || this.length <= 0) this.length = 20;
        pb.setLimit(this.length);
        
        if(this.start == null || this.start <= 0) this.start = 0;
        pb.setPage(this.start/this.length + 1);
        return pb;
    }
}
