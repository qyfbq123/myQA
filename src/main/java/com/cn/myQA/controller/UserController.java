package com.cn.myQA.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.awt.Menu;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cn.myQA.pojo.Group;
import com.cn.myQA.pojo.Role;
import com.cn.myQA.pojo.SysMenu;
import com.cn.myQA.pojo.User;
import com.cn.myQA.service.IUserService;
import com.cn.myQA.web.datatables.Pagination;
import com.cn.myQA.web.datatables.TableModel;

@Api(value = "user", description="用户CRUD操作")
@Controller
@RequestMapping("/user")
public class UserController extends BaseController {
    @Autowired
    private IUserService userService;
    
    @ApiOperation(value="获取用户菜单", notes="获取登录用户的角色菜单", httpMethod="GET", response=SysMenu.class)
    @RequestMapping(value = "/menu" , method = RequestMethod.GET)
    public ResponseEntity<List<SysMenu>> menu(HttpSession session) {
        User user = (User)session.getAttribute("user");
        if(user == null) return new ResponseEntity<List<SysMenu>>(HttpStatus.BAD_REQUEST);
        List<SysMenu> menuList = userService.findMenu(user.getId());
        return new ResponseEntity<List<SysMenu>>(menuList, HttpStatus.OK);
    }
    
    @ApiOperation(value="所有用户信息", notes="获取所有用户信息", httpMethod="GET", response=User.class)
    @RequestMapping(value="/all", method=RequestMethod.GET)
    public ResponseEntity<List<User>> all() {
        return new ResponseEntity<List<User>>(userService.all(), HttpStatus.OK);
    }
    
    @ApiOperation(value="用户信息分页", notes="分页获取用户信息", httpMethod="GET", response=Pagination.class)
    @RequestMapping(value="/page", method=RequestMethod.GET)
    public ResponseEntity<Pagination<User>> page(TableModel model) {
        return new ResponseEntity<Pagination<User>>(userService.page(model), HttpStatus.OK);
    }
    
    @ApiOperation(value="用户保存", notes="保存用户信息", httpMethod="POST")
    @RequestMapping(value="/save", method=RequestMethod.POST)
    public ResponseEntity<String> save(@RequestBody User user) {
        String result = userService.save(user);
        if(result.equals("ok")) return new ResponseEntity<String>(result, HttpStatus.OK);
        else return new ResponseEntity<String>(result, HttpStatus.BAD_REQUEST);
    }
    
    @ApiOperation(value="用户禁用/启用", notes="禁用／启用用户", httpMethod="PUT")
    @RequestMapping(value="/toggle/{id}", method=RequestMethod.PUT)
    public ResponseEntity<String> disable(@PathVariable("id") Integer id) {
        int result = userService.toggleLocked(id);
        if(result == 1) return new ResponseEntity<String>("ok", HttpStatus.OK);
        else return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }
    
    @ApiOperation(value="用户删除", notes="删除用户", httpMethod="DELETE")
    @RequestMapping(value="/del/{id}", method=RequestMethod.DELETE)
    public ResponseEntity<String> del(@PathVariable("id") Integer id) {
        int result = userService.del(id);
        if(result == 1) return new ResponseEntity<String>("ok", HttpStatus.OK);
        else return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }
    
    @ApiOperation(value="所有角色", notes="获取所有用户角色", httpMethod="GET", response=Role.class)
    @RequestMapping(value="/role/all", method=RequestMethod.GET)
    public ResponseEntity<List<Role>> allRoles() {
        return new ResponseEntity<List<Role>>(userService.allRoles(), HttpStatus.OK);
    }
    
    @ApiOperation(value="所有菜单", notes="获取所有系统菜单", httpMethod="GET", response=Menu.class)
    @RequestMapping(value="/allSysMenus", method=RequestMethod.GET)
    public ResponseEntity<List<SysMenu>> allMenus() {
        return new ResponseEntity<List<SysMenu>>(userService.allSysMenus(), HttpStatus.OK);
    }
    
    @ApiOperation(value="新增／修改角色", notes="新增／修改用户角色", httpMethod="POST")
    @RequestMapping(value="/role/save", method=RequestMethod.POST)
    public ResponseEntity<String> saveRole(@RequestBody Role role) {
        String result = userService.saveRole(role);
        if(result.equals("ok")) return new ResponseEntity<String>(result, HttpStatus.OK);
        else return new ResponseEntity<String>(result, HttpStatus.BAD_REQUEST);
    }
    
    @ApiOperation(value="删除角色", notes="删除角色", httpMethod="DELETE")
    @RequestMapping(value="/role/del/{id}", method=RequestMethod.DELETE)
    public ResponseEntity<String> delRole(@PathVariable("id") Integer id) {
        int result = userService.delRole(id);
        if(result == 1) return new ResponseEntity<String>("ok", HttpStatus.OK);
        else return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }
    
    @ApiOperation(value="所有组", notes="获取所有组", httpMethod="GET", response=Group.class)
    @RequestMapping(value="/group/all", method=RequestMethod.GET)
    public ResponseEntity<List<Group>> allGroups() {
        return new ResponseEntity<List<Group>>(userService.allGroups(), HttpStatus.OK);
    }
    
    @ApiOperation(value="新增／修改组", notes="新增／修改用户组", httpMethod="POST")
    @RequestMapping(value="/group/save", method=RequestMethod.POST)
    public ResponseEntity<String> saveGroup(@RequestBody Group group) {
        String result = userService.saveGroup(group);
        if(result.equals("ok")) return new ResponseEntity<String>(result, HttpStatus.OK);
        else return new ResponseEntity<String>(result, HttpStatus.BAD_REQUEST);
    }
    
    @ApiOperation(value="删除组", notes="删除组", httpMethod="DELETE")
    @RequestMapping(value="/group/del/{id}", method=RequestMethod.DELETE)
    public ResponseEntity<String> delGroup(@PathVariable("id") Integer id) {
        int result = userService.delGroup(id);
        if(result == 1) return new ResponseEntity<String>("ok", HttpStatus.OK);
        else return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }
    
    @ApiOperation(value="获取PM组成员", notes="获取PM组所有成员", httpMethod="GET")
    @RequestMapping(value="/group/pm", method=RequestMethod.GET)
    public ResponseEntity<List<User>> getPMGroupMember() {
        return new ResponseEntity<List<User>>(userService.findPMMembers(), HttpStatus.OK);
    }
    
    @ApiOperation(value="获取所有处理人", notes="获取所有处理人", httpMethod="GET")
    @RequestMapping(value="/role/handlers", method=RequestMethod.GET)
    public ResponseEntity<List<User>> getHandlers() {
        return new ResponseEntity<List<User>>(userService.findHanlders(), HttpStatus.OK);
    }
    
    @ApiOperation(value="修改用户密码", notes="修改用户密码", httpMethod="POST")
    @RequestMapping(value="/pwd/update", method=RequestMethod.POST)
    public ResponseEntity<String> menu(HttpSession session, String newpwd) {
        User user = (User)session.getAttribute("user");
        if(user == null) return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        int result = userService.savePwd(user.getId(), newpwd);
        if(result == 1) return new ResponseEntity<String>("ok", HttpStatus.OK);
        else return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }
}
