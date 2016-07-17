package com.cn.myQA.service;

import java.util.List;

import com.cn.myQA.pojo.Group;
import com.cn.myQA.pojo.Role;
import com.cn.myQA.pojo.SysMenu;
import com.cn.myQA.pojo.User;
import com.cn.myQA.web.datatables.Pagination;
import com.cn.myQA.web.datatables.TableModel;

public interface IUserService {
    /**
     * 根据登陆ID获取用户信息
     * @param id loginID
     * @return   用户信息
     */
    public User findByLoginID(String id);
    
    /**
     * 获取用户菜单
     * @param userId 用户ID
     * @return 菜单列表
     */
    public List<SysMenu> findMenu(Integer userId);
    
    /**
     * 获取所有用户信息
     * @return 用户列表
     */
    public List<User> all();
    
    /**
     * 用户分页查询
     * @param model 分页参数
     * @return  用户列表
     */
    public Pagination<User> page(TableModel model);
    
    /**
     * 保存用户信息，新增或修改
     * @param user 用户信息
     * @return 返回信息
     */
    public String save(User user);
    
    /**
     * 转换用户状态
     * @param id 用户id
     * @return 转化结果
     */
    public int toggleLocked(Integer id);
    
    /**
     * 删除用户
     * @param id 用户id
     * @return 结果
     */
    public int del(Integer id);
    
    /**
     * 获取所有用户角色
     * @return 角色列表
     */
    public List<Role> allRoles();
    
    /**
     * 新增／修改角色
     * @param role 角色
     * @return 结果
     */
    public String saveRole(Role role);
    
    /**
     * 删除角色
     * @param id 角色id
     * @return 结果
     */
    public int delRole(Integer id);
    
    /**
     * 获取所有系统菜单
     * @return 菜单列表
     */
    public List<SysMenu> allSysMenus();
    
    /**
     * 获取所有用户组
     * @return 组列表
     */
    public List<Group> allGroups();
    
    /**
     * 保存组
     * @param group 组信息
     * @return 结果
     */
    public String saveGroup(Group group);
    
    /**
     * 删除组
     * @param id 组id
     * @return 结果
     */
    public int delGroup(Integer id);
    
    /**
     * 获取PM组成员
     * @return PM组成员
     */
    public List<User> findPMMembers();
    
    /**
     * 获取所有处理人
     * @return 处理人列表
     */
    public List<User> findHanlders();
    
    /**
     * 用户保存新密码
     * @param id 用户id
     * @param newPwd 新密码
     * @return 结果
     */
    public int savePwd(Integer id, String newPwd);
}
