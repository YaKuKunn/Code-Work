package LiNeng_Chen;

import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * UserDao 的实现类
 * 使用 JDBC 连接真实的 MySQL 数据库
 */
public class UserDaoImpl implements UserDao {

    // 数据库连接配置
    private static final String DB_URL = "jdbc:mysql://localhost:3306/test666?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&characterEncoding=UTF8";
    private static final String USER = "root";
    private static final String PASS = "00000000";

    static {
        try {
            // 加载 MySQL JDBC 驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            try {
                // 兼容较老版本的驱动类名
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                System.err.println("未找到 MySQL JDBC 驱动，请检查依赖配置！");
                ex.printStackTrace();
            }
        }
    }

    /**
     * 获取数据库连接
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    /**
     * 密码加密：由于您的表 password 字段为 varchar(20)，长度受限
     * 此处使用 Base64 作为简单可逆加密演示。在真实系统中建议加长字段并使用 BCrypt 散列。
     */
    private String encryptPassword(String password) {
        if (password == null) return null;
        return Base64.getEncoder().encodeToString(password.getBytes());
    }

    /**
     * 密码解密
     */
    private String decryptPassword(String encryptedPassword) {
        if (encryptedPassword == null) return null;
        try {
            return new String(Base64.getDecoder().decode(encryptedPassword));
        } catch (IllegalArgumentException e) {
            // 如果解析失败（例如之前存入的是旧明文数据），则直接返回原数据
            return encryptedPassword; 
        }
    }

    @Override
    public void createTable() {
        // 用户已经建好表 tb_user，且要求保留原表结构，所以不进行 DROP 和 CREATE 操作
        // 这里改为执行 DESC tb_user 并打印表结构，以证明使用了已存在的表
        String sql = "DESC tb_user";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("成功连接到已存在的表 tb_user，保留原表结构。表结构信息如下：");
            while (rs.next()) {
                String field = rs.getString("Field");
                String type = rs.getString("Type");
                System.out.println("- 字段: " + field + "\t 类型: " + type);
            }
        } catch (SQLException e) {
            System.err.println("读取表 tb_user 结构失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void insert(User user) {
        String sql = "INSERT INTO tb_user (userno, username, password) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, user.getId());
            pstmt.setString(2, user.getUsername());
            // 存入前进行加密
            pstmt.setString(3, encryptPassword(user.getPassword()));
            pstmt.executeUpdate();
            System.out.println("成功插入用户: " + user.getUsername());
        } catch (SQLException e) {
            System.err.println("插入用户失败（可能是该ID已存在）：" + e.getMessage());
        }
    }

    @Override
    public boolean login(String username, String password) {
        String sql = "SELECT 1 FROM tb_user WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            // 将用户输入的明文密码加密后，再与数据库内的密文进行比对
            pstmt.setString(2, encryptPassword(password));
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // 如果能查到记录，说明认证成功
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE tb_user SET username = ?, password = ? WHERE userno = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            // 更新时存入加密后密码
            pstmt.setString(2, encryptPassword(user.getPassword()));
            pstmt.setInt(3, user.getId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("成功更新用户编号为 " + user.getId() + " 的信息");
            } else {
                System.out.println("未找到要更新的用户。");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int[] ids) {
        if (ids == null || ids.length == 0) return;
        
        String sql = "DELETE FROM tb_user WHERE userno = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int id : ids) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
                System.out.println("成功删除用户编号为 " + id + " 的用户");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<User> getAll() {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM tb_user";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("userno"));
                user.setUsername(rs.getString("username"));
                // 查询时将密文解密回明文
                user.setPassword(decryptPassword(rs.getString("password")));
                userList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }

    @Override
    public List<User> getUserForPage(int pagesize, int pageOrder) {
        List<User> userList = new ArrayList<>();
        int offset = (pageOrder - 1) * pagesize;
        String sql = "SELECT * FROM tb_user LIMIT ? OFFSET ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, pagesize);
            pstmt.setInt(2, offset);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("userno"));
                    user.setUsername(rs.getString("username"));
                    // 查询时将密文解密回明文
                    user.setPassword(decryptPassword(rs.getString("password")));
                    userList.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }

    @Override
    public User getUserById(int id) {
        String sql = "SELECT * FROM tb_user WHERE userno = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("userno"));
                    user.setUsername(rs.getString("username"));
                    // 查询时将密文解密回明文
                    user.setPassword(decryptPassword(rs.getString("password")));
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
