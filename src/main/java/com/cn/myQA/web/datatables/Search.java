package com.cn.myQA.web.datatables;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("搜索条件")
public class Search {
    @ApiModelProperty(value="条件", required=false)
    private String value;
    
    @ApiModelProperty(value="正则匹配", required=false)
    private Boolean regex;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getRegex() {
        return regex;
    }

    public void setRegex(Boolean regex) {
        this.regex = regex;
    }
    
}
