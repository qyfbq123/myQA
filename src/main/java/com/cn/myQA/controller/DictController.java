package com.cn.myQA.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cn.myQA.pojo.SysDict;
import com.cn.myQA.service.IDictService;
import com.cn.myQA.web.select2.Option;

@Api(value="dict", description="字典CRUD操作")
@Controller
@RequestMapping("/dict")
class DictController {
    @Autowired
    private IDictService dictService;
    
    @ApiOperation(value="城市", notes="获取所有城市字典", httpMethod="GET")
    @RequestMapping(value="/cities", method=RequestMethod.GET)
    public ResponseEntity<List<Option>> cities() {
        return new ResponseEntity<List<Option>>(dictService.cities(), HttpStatus.OK);
    }
    
    @ApiOperation(value="项目", notes="获取所有项目字典", httpMethod="GET")
    @RequestMapping(value="/projects", method=RequestMethod.GET)
    public ResponseEntity<List<Option>> projects() {
        return new ResponseEntity<List<Option>>(dictService.projects(), HttpStatus.OK);
    }
    
    @ApiOperation(value="事件问题类型", notes="获取所有事件问题类型字典", httpMethod="GET")
    @RequestMapping(value="/types/pm", method=RequestMethod.GET)
    public ResponseEntity<List<Option>> pmTypes() {
        return new ResponseEntity<List<Option>>(dictService.pmTypes(), HttpStatus.OK);
    }
    
    @ApiOperation(value="WMS、TMS事件问题类型", notes="获取所有WMS、TMS问题类型字典", httpMethod="GET")
    @RequestMapping(value="/types/ms", method=RequestMethod.GET)
    public ResponseEntity<List<Option>> msTypes() {
        return new ResponseEntity<List<Option>>(dictService.msTypes(), HttpStatus.OK);
    }
    
    @ApiOperation(value="问题严重性", notes="获取所有问题严重性字典", httpMethod="GET")
    @RequestMapping(value="/severity", method=RequestMethod.GET)
    public ResponseEntity<List<Option>> severity() {
        return new ResponseEntity<List<Option>>(dictService.severity(), HttpStatus.OK);
    }
    
    @ApiOperation(value="始发地和设计库房信息", notes="始发地和设计库房信息", httpMethod="GET")
    @RequestMapping(value="/beginStorehouses", method=RequestMethod.GET)
    public ResponseEntity<List<Option>> beginStorehouses() {
        return new ResponseEntity<List<Option>>(dictService.beginStorehouses(), HttpStatus.OK);
    }
    
    /**
     * 16-6-20 添加供应商
     * @return
     */
    @ApiOperation(value="所属供应商", notes="所属供应商", httpMethod="GET")
    @RequestMapping(value="/suppliers", method=RequestMethod.GET)
    public ResponseEntity<List<Option>> suppliers() {
        return new ResponseEntity<List<Option>>(dictService.suppliers(), HttpStatus.OK);
    }
    
    @ApiOperation(value="公告类型", notes="公告类型", httpMethod="GET")
    @RequestMapping(value="/bulletinTypes", method=RequestMethod.GET)
    public ResponseEntity<List<Option>> bulletinTypes() {
        return new ResponseEntity<List<Option>>(dictService.bulletinTypes(), HttpStatus.OK);
    }
    
    @ApiOperation(value="发布职能", notes="发布职能", httpMethod="GET")
    @RequestMapping(value="/functions", method=RequestMethod.GET)
    public ResponseEntity<List<Option>> functions() {
        return new ResponseEntity<List<Option>>(dictService.functions(), HttpStatus.OK);
    }
    
    @ApiOperation(value="文档类型", notes="文档类型", httpMethod="GET")
    @RequestMapping(value="/docTypes", method=RequestMethod.GET)
    public ResponseEntity<List<Option>> docTypes() {
        return new ResponseEntity<List<Option>>(dictService.docTypes(), HttpStatus.OK);
    }
    
    @ApiOperation(value="可维护字典", notes="获取所有可维护字典", httpMethod="GET")
    @RequestMapping(value="/attributes", method=RequestMethod.GET)
    public ResponseEntity<List<SysDict>> attributes() {
        return new ResponseEntity<List<SysDict>>(dictService.attriutes(), HttpStatus.OK);
    }
    
    @ApiOperation(value="可维护字典参数", notes="获取可维护字典的参数", httpMethod="GET")
    @RequestMapping(value="/attributes/{typeid}", method=RequestMethod.GET)
    public ResponseEntity<List<SysDict>> attributes(@PathVariable("typeid") Integer typeid) {
        return new ResponseEntity<List<SysDict>>(dictService.params(typeid), HttpStatus.OK);
    }
    
    @ApiOperation(value="更新字典", notes="更新字典", httpMethod="PUT")
    @RequestMapping(value="/attributes/{typeid}", method=RequestMethod.PUT)
    public ResponseEntity<String> saveAttributes(@PathVariable("typeid") Integer typeid, @RequestBody List<SysDict> dictList) {
        String result = dictService.saveAttributes(typeid, dictList);
        return new ResponseEntity<String>(result, result.equals("ok") ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
}
