package com.cn.myQA.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.myQA.dao.UserMapper;
import com.cn.myQA.pojo.Group;
import com.cn.myQA.pojo.Role;
import com.cn.myQA.pojo.SysMenu;
import com.cn.myQA.pojo.User;
import com.cn.myQA.service.IUserService;
import com.cn.myQA.web.datatables.Pagination;
import com.cn.myQA.web.datatables.TableModel;
import com.github.miemiedev.mybatis.paginator.domain.PageList;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByLoginID(String id) {
        return userMapper.findByLoginID(id);
    }

    @Override
    public List<SysMenu> findMenu(Integer userId) {
        return userMapper.findUserMenu(userId);
    }
    
    @Override
    public List<User> all() {
        return userMapper.all();
    }

    @Override
    public Pagination<User> page(TableModel model) {
        PageList<User> userList = userMapper.page(model.translateToPB());
        
        Pagination<User> result = new Pagination<User>();
        
        result.setDraw(model.getDraw());
        result.setRecordsTotal(userList.getPaginator().getTotalCount());
        result.setRecordsFiltered(result.getRecordsTotal());
        result.setData(userList.subList(0, userList.size()));
        
        return result;
    }

    @Override
    public String save(User user) {
        int verifyResult = userMapper.loginIDVerify(user.getId(), user.getLoginid());
        if(verifyResult > 0) {
            return "用户名已被占用";
        }
        
        if(user.getId() == null || user.getId() <= 0) {
            userMapper.insert(user);
            if(!CollectionUtils.isEmpty(user.getRoleList())) {
                userMapper.putUserRoles(user.getId(), user.getRoleList());
            }
        } else {
            userMapper.updateByPrimaryKey(user);
            
            userMapper.removeUserRoles(user.getId());
            if(!CollectionUtils.isEmpty(user.getRoleList())) {
                userMapper.putUserRoles(user.getId(), user.getRoleList());
            }
        }
        return "ok";
    }

    @Override
    public int toggleLocked(Integer id) {
        return userMapper.toggleLocked(id);
    }

    public int del(Integer id) {
        return userMapper.deleteByPrimaryKey(id);
    }
    
    @Override
    public List<Role> allRoles() {
        return userMapper.findAllRoles();
    }

    @Override
    public String saveRole(Role role) {
        int verifyResult = userMapper.verifyRoleName(role.getId(), role.getName());
        if(verifyResult > 0 ) return "角色名称已被占用";
        
        if(role.getId() == null || role.getId() <= 0) {
            userMapper.insertRole(role);
            if(!CollectionUtils.isEmpty(role.getMenuList())) {
                userMapper.rolePutMenus(role.getId(), role.getMenuList());
            }
        } else {
            userMapper.updateRoleByPrimaryKey(role);
            
            userMapper.roleRemoveMenus(role.getId());
            if(!CollectionUtils.isEmpty(role.getMenuList())) {
                userMapper.rolePutMenus(role.getId(), role.getMenuList());
            }
        }
        return "ok";
    }

    @Override
    public int delRole(Integer id) {
        return userMapper.deleteRoleByPrimaryKey(id);
    }
    
    @Override
    public List<SysMenu> allSysMenus() {
        return userMapper.findAllMenus();
    }

    @Override
    public List<Group> allGroups() {
        return userMapper.findAllGroups();
    }

    @Override
    public String saveGroup(Group group) {
        int verifyResult = userMapper.verifyGroupName(group.getId(), group.getName());
        if(verifyResult > 0) return "组名称已被占用";
        
        if(group.getId() == null || group.getId() <= 0) {
            userMapper.insertGroup(group);
            
            if(CollectionUtils.isNotEmpty(group.getUserList())) {
                userMapper.groupPutUsers(group.getId(), group.getUserList());
            }
        } else {
            userMapper.updateGroupByPrimaryKey(group);
            userMapper.groupRemoveUsers(group.getId());
            
            if(CollectionUtils.isNotEmpty(group.getUserList())) {
                userMapper.groupPutUsers(group.getId(), group.getUserList());
            }
        }
        return "ok";
    }

    @Override
    public int delGroup(Integer id) {
        return userMapper.deleteGroupByPrimaryKey(id);
    }

    @Override
    public List<User> findPMMembers() {
        Group pm = userMapper.findPMGroup();
        if( pm != null ) return pm.getUserList();
        
        return null;
    }
    
    @Override
    public List<User> findHanlders() {
        return userMapper.findHanlers();
    }
}
