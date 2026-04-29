package com.example;

import java.util.ArrayList;
import java.util.List;

/**
 * UserDao 的实现类
 * 使用 List 模拟数据库存储，方便测试
 */
public class UserDaoImpl implements UserDao {
    
    private List<User> userDatabase = new ArrayList<>();

    @Override
    public void createTable() {
        System.out.println("模拟创建用户表成功！");
    }

    @Override
    public void insert(User user) {
        userDatabase.add(user);
        System.out.println("成功插入用户: " + user.getUsername());
    }

    @Override
    public boolean login(String username, String password) {
        for (User user : userDatabase) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void update(User user) {
        for (int i = 0; i < userDatabase.size(); i++) {
            if (userDatabase.get(i).getId() == user.getId()) {
                userDatabase.set(i, user);
                System.out.println("成功更新用户编号为 " + user.getId() + " 的信息");
                return;
            }
        }
        System.out.println("未找到要更新的用户。");
    }

    @Override
    public void delete(int[] ids) {
        for (int id : ids) {
            userDatabase.removeIf(user -> user.getId() == id);
            System.out.println("成功删除用户编号为 " + id + " 的用户");
        }
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(userDatabase);
    }

    @Override
    public List<User> getUserForPage(int pagesize, int pageOrder) {
        int start = (pageOrder - 1) * pagesize;
        int end = Math.min(start + pagesize, userDatabase.size());
        if (start >= userDatabase.size()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(userDatabase.subList(start, end));
    }

    @Override
    public User getUserById(int id) {
        for (User user : userDatabase) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }
}
