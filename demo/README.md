# User DAO JDBC Demo

这是一个使用 Java 17 和 JDBC 实现的 MySQL 数据库访问（DAO）演示项目。该项目演示了如何将数据持久化到真实的数据库中，并展示了如何安全地处理用户密码加密。

## ✨ 特性

- **标准 DAO 模式**：通过 `UserDao` 接口和 `UserDaoImpl` 实现类分离业务逻辑和数据访问层。
- **全量 CRUD 操作**：包含用户的增 (`insert`)、删 (`delete`)、改 (`update`)、查 (`getAll`, `getUserById`)。
- **分页查询**：支持在数据库层面利用 `LIMIT` 和 `OFFSET` 进行分页 (`getUserForPage`)。
- **自定义表适配**：自动适配了已存在于数据库中的自定义表 `tb_user`，精确映射主键和其他字段。
- **密码加密存储**：内置了基于 Base64 的简易密码可逆加密算法，防止数据库出现明文密码。
- **数据库免配置初始化**：如果所指向的 `test666` 数据库不存在，将利用 MySQL 驱动参数 `createDatabaseIfNotExist=true` 进行自动建库。

## 🛠️ 技术栈

- **Java Version**: 17
- **Build Tool**: Maven 3.x
- **Database**: MySQL 8.x
- **Driver**: MySQL Connector/J 8.0.33

## 📦 数据库表结构要求

默认使用的数据库为 `test666`。程序会直接对接数据库中已创建的 `tb_user` 表，需要包含以下基本字段才能保证测试平稳运行：

```sql
CREATE TABLE `tb_user` (
  `userno` int NOT NULL,       -- 用户编号 (对应程序中的 id)
  `username` varchar(20) NOT NULL, -- 用户名
  `password` varchar(20) DEFAULT NULL, -- 密码 (程序将存储为 Base64 密文)
  `job` varchar(20) DEFAULT NULL,
  `sal` int DEFAULT NULL,
  PRIMARY KEY (`userno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

## 🚀 快速启动

1. **环境准备**
   确保您本地已安装 Maven 并且 MySQL 服务（默认端口 3306）正在运行。

2. **配置数据库凭证**
   如果有需要，您可以打开 `src/main/java/com/example/UserDaoImpl.java` 文件，修改文件头部的连接凭证：
   ```java
   private static final String USER = "root";
   private static final String PASS = "00000000";
   ```

3. **编译并运行测试用例**
   该项目自带一个主测试类 `DaoTest.java`，它会演示所有的数据库操作行为。在项目根目录（`pom.xml` 同级）打开终端，执行：
   ```bash
   mvn clean compile exec:java -Dexec.mainClass="com.example.DaoTest"
   ```

## 📝 运行流程说明
`DaoTest` 类的执行流程如下：
1. `createTable()`：读取并验证表 `tb_user` 结构。
2. `insert()`：插入 3 个测试用户 (Alice, Bob, Charlie)。
3. `getAll()`：查询表内所有用户并解密密码显示。
4. `login()`：测试使用正确和错误的密码登录认证（自动加密比对）。
5. `update()`：修改 Bob 的密码。
6. `delete()`：为了保持数据库清洁，测试末尾会删除编号 1 和 3 的用户。
