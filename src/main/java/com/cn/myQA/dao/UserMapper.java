package com.cn.myQA.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.myQA.pojo.Group;
import com.cn.myQA.pojo.Role;
import com.cn.myQA.pojo.SysMenu;
import com.cn.myQA.pojo.User;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
    
    /**
     * 根据登陆名查找用户
     * @param id loginID
     * @return 用户信息
     */
    User findByLoginID(String loginID);
    
    /**
     * 获取用户角色菜单
     * @param id 用户id
     * @return 菜单列表
     */
    List<SysMenu> findUserMenu(Integer id);
    
    /**
     * 所有用户
     * @return 用户列表
     */
    List<User> all();
    
    /**
     * 分页用户查询
     * @param pb 分页参数
     * @return 用户列表
     */
    PageList<User> page(PageBounds pb);
    
    /**
     * 登录名验证
     * @param id 用户id
     * @param loginID 登录名
     * @return 验证结果 1代表已被占用 0代表未被占用
     */
    int loginIDVerify(@Param("id") Integer id, @Param("loginID") String loginID);
    
    /**
     * 移除用户角色
     * @param id 用户id
     * @return 结果
     */
    int removeUserRoles(@Param("id") Integer id);
    
    /**
     * 用户添加角色
     * @param id 用户id
     * @param roleList 角色列表
     * @return 结果
     */
    int putUserRoles(@Param("id") Integer id, @Param("roleList")List<Role> roleList);
    
    /**
     * 用户禁用／启用
     * @param id 用户id
     * @return 结果
     */
    int toggleLocked(Integer id);
    
    /**
     * 所有用户角色
     * @return 角色列表
     */
    List<Role> findAllRoles();
    
    /**
     * 验证角色名称
     * @param id 角色id
     * @param name 角色名称
     * @return 验证结果 1代表已被占用
     */
    int verifyRoleName(@Param("id") Integer id, @Param("name") String name);
    
    /**
     * 新增角色
     * @param role 角色
     * @return 结果
     */
    int insertRole(Role role);
    
    /**
     * 更新角色
     * @param role 角色
     * @return 结果
     */
    int updateRoleByPrimaryKey(Role role);
    
    /**
     * 角色设定菜单
     * @param id 角色id
     * @param menuList 菜单列表
     * @return 结果
     */
    int rolePutMenus(@Param("id") Integer id, @Param("menuList") List<SysMenu> menuList);
    
    /**
     * 角色移除菜单
     * @param id 角色id
     * @return 结果
     */
    int roleRemoveMenus(Integer id);
    
    /**
     * 删除角色
     * @param id 角色id
     * @return 结果
     */
    int deleteRoleByPrimaryKey(Integer id);
    
    /**
     * 获取所有系统菜单
     * @return 菜单列表
     */
    List<SysMenu> findAllMenus();
    
    /**
     * 获取所有组
     * @return 组列表
     */
    List<Group> findAllGroups();
    
    /**
     * 验证组名称
     * @param id 组id
     * @param name 组名称
     * @return 结果
     */
    int verifyGroupName(@Param("id") Integer id, @Param("name") String name);
    
    /**
     * 新增组
     * @param group 组信息
     * @return 结果
     */
    int insertGroup(Group group);
    
    /**
     * 修改组
     * @param group 组信息
     * @return 结果
     */
    int updateGroupByPrimaryKey(Group group);
    
    /**
     * 组添加用户
     * @param id 组id
     * @param userList 用户列表
     * @return 结果
     */
    int groupPutUsers(@Param("id") Integer id, @Param("userList") List<User> userList);
    
    /**
     * 组移除用户
     * @param id 组id
     * @return 结果
     */
    int groupRemoveUsers(Integer id);
    
    /**
     * 删除组
     * @param id 组id
     * @return 结果
     */
    int deleteGroupByPrimaryKey(Integer id);
    
    /**
     * 获取PM组
     * @return PM组
     */
    Group findPMGroup();
    
    /**
     * 按角色查找处理人
     * @return 处理人列表
     */
    List<User> findHanlers();
    
    /**
     * 用户保存新密码
     * @param id 用户id
     * @param newPwd 新密码
     * @return 结果
     */
    int updatePwdByPrimaryKey(@Param("id") Integer id, @Param("newPwd") String newPwd);
}