package myQA;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.myQA.dao.UserMapper;
import com.cn.myQA.pojo.Group;
import com.cn.myQA.pojo.Role;
import com.cn.myQA.pojo.SysMenu;
import com.cn.myQA.pojo.User;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;

@RunWith(SpringJUnit4ClassRunner.class)     //表示继承了SpringJUnit4ClassRunner类
@ContextConfiguration(locations = {"classpath:spring-mybatis.xml"})
public class TestUserMapper {
    @Autowired
    UserMapper userMapper;

    @Test
    public void testFindByLoginID() {
        String loginID = "admin";
        User user = userMapper.findByLoginID(loginID);
        assertNotNull("登录名" + loginID + "的用户不存在", user);
    }
    
    @Test
    public void testFindUserMenu() throws Exception {
        Integer userId = 1;
        List<SysMenu> menuList = userMapper.findUserMenu(userId);
        assertNotNull(menuList);
    }
    
    @Test
    public void testPage() throws Exception {
        PageList<User> userList = userMapper.page(new PageBounds(1, 10));
        assertNotNull(userList);
    }
    
    @Test
    public void testLoginIDVerify() throws Exception {
        Integer userId=null;
        String loginID = "admin";
        Integer result = userMapper.loginIDVerify(userId, loginID);
        //admin登录名已被占用
        assertTrue(result == 1);
    }
    
    @Test
    public void testRemoveRoles() throws Exception {
        int result = userMapper.removeUserRoles(1);
        assertTrue( result == 1);
    }
    
    @Test
    public void testPutUserRoles() throws Exception {
        Integer userId = 1;
        List<Role> roleList = new ArrayList<Role>();
        Role role = new Role();
        role.setId(1);
        roleList.add(role);
        
        int result = userMapper.putUserRoles(userId, roleList);
        
        assertTrue(result == 1);
    }
    
    @Test
    public void testFindAllRoles() throws Exception {
        List<Role> roleList = userMapper.findAllRoles();
        
        assertNotNull(roleList);
    }
    
    @Test
    public void testVerifyRoleName() throws Exception {
        String name = "testrole";
        int result =userMapper.verifyRoleName(null, name);
        
        assertTrue(result > 0);
    }
    
    @Test
    public void testDeleteRole() throws Exception {
        Integer id = 0;
        int result = userMapper.deleteRoleByPrimaryKey(id);
        
        assertFalse(result == 1);
    }
    
    @Test
    public void testRolePutMenus() throws Exception {
        Integer roleId = 1;
        List<SysMenu> menuList = new ArrayList<SysMenu>();
        SysMenu menu = new SysMenu();
        menu.setId(1);
        menuList.add(menu);
        int result = userMapper.rolePutMenus(roleId, menuList);
        
        assertTrue(result == 1);
    }
    
    @Test
    public void testRoleRemoveMenus() throws Exception {
        Integer id = 1;
        int result = userMapper.roleRemoveMenus(id);
        assertTrue(result > 0);
    }
    
    @Test
    public void testFindAllGroups() throws Exception {
        List<Group> groupList = userMapper.findAllGroups();
        
        assertNotNull(groupList);
    }
}
