package com.example;

import java.util.List;

/**
 * 测试类，包含主函数
 */
public class DaoTest {
    
    public static void main(String[] args) {
        UserDao userDao = new UserDaoImpl();
        
        // 1. 测试创建表
        userDao.createTable();
        
        // 2. 测试插入用户
        System.out.println("\n--- 插入用户 ---");
        User u1 = new User(1, "alice", "123456");
        User u2 = new User(2, "bob", "abcdef");
        User u3 = new User(3, "charlie", "password");
        userDao.insert(u1);
        userDao.insert(u2);
        userDao.insert(u3);
        
        // 3. 测试查询所有用户
        System.out.println("\n--- 查询所有用户 ---");
        List<User> allUsers = userDao.getAll();
        for (User u : allUsers) {
            System.out.println(u);
        }
        
        // 4. 测试登录
        System.out.println("\n--- 登录测试 ---");
        System.out.println("alice (正确密码) 登录结果: " + userDao.login("alice", "123456"));
        System.out.println("bob (错误密码) 登录结果: " + userDao.login("bob", "wrongpass"));
        
        // 5. 测试通过ID获取用户
        System.out.println("\n--- 根据编号查询用户 ---");
        System.out.println("查询编号为2的用户: " + userDao.getUserById(2));
        
        // 6. 测试更新用户
        System.out.println("\n--- 更新用户 ---");
        User updateBob = new User(2, "bob_new", "new_pass");
        userDao.update(updateBob);
        System.out.println("更新后编号为2的用户: " + userDao.getUserById(2));
        
        // 7. 测试分页查询
        System.out.println("\n--- 分页查询测试 ---");
        List<User> page1 = userDao.getUserForPage(2, 1);
        System.out.println("第一页（2条/页）:");
        for (User u : page1) {
            System.out.println(u);
        }
        
        // 8. 测试删除用户
        System.out.println("\n--- 删除用户 ---");
        userDao.delete(new int[]{1, 3});
        System.out.println("删除后剩余用户数: " + userDao.getAll().size());
    }
}
