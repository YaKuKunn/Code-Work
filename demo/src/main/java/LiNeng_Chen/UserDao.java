package LiNeng_Chen;

import java.util.List;

public interface UserDao {
    /**
    * 创建表
    */
    public void createTable();

    /**
    * 插入一个用户
    * @param User: 被插入的用户对象
    */
    public void insert(User user);

    /**
    * 登录，用户编号、密码正确，返回 true，否则返回 false
    * @param username: 用户编号
    * @param username: 密码
    */
    public boolean login(String username, String password);

    /**
    * 修改一个用户
    * @param User: 被修改的用户对象，用户对象只含有用户编号，修改此编号的员工信息
    */
    public void update(User user);

    /**
    * 根据用户编号删除多个用户
    * @param ids: 被删除的用户编号数组
    */
    public void delete(int[] ids);
    /**
     * 查询所有用户对象的集合
     * @return
     */
    public List<User> getAll();  

    /**  
    * 查询某一页的用户  
    * @param pagesize: 每页的记录条数  
    * @param pageOrder: 页号（即第几页）  
    * @return  
    */  
    public List<User> getUserForPage(int pagesize, int pageOrder);  

    /**  
    * 根据用户编号，查询出此编号对应的用户  
    * @return  
    */  
    public User getUserById(int id);  
}