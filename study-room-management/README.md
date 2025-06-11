# 自习室会员管理系统

## 项目简介

基于Spring Boot的自习室会员管理系统，实现了学生座位预约、公告管理、投诉建议等核心功能。

## 技术栈

- **后端**: Spring Boot 2.5.14, Spring Data JPA, Spring Security
- **前端**: Thymeleaf, Bootstrap 5, HTML/CSS
- **数据库**: MySQL 5.7+
- **构建工具**: Maven 3.6+
- **运行环境**: JDK 1.8+

## 核心功能

### 学生用户功能
- 用户注册与登录
- 查看系统公告
- 座位预约与取消
- 提交投诉建议

### 管理员功能
- 管理员登录
- 发布与删除公告
- 查看所有预约记录
- 查看投诉建议

## 部署步骤

### 1. 环境准备

确保已安装以下软件：
- **JDK 1.8+**
- **Maven 3.6+**
- **MySQL 5.7+**

### 2. 数据库配置

1. 启动MySQL服务
2. 执行 `database-setup.sql` 文件创建数据库和表：

```bash
mysql -u root -p < database-setup.sql
```

3. 修改 `src/main/resources/application.properties` 中的数据库连接信息：

```properties
spring.datasource.password=你的MySQL密码
```

### 3. 编译和运行

**重要：设置正确的JAVA_HOME**

在项目根目录执行：

```bash
# 设置正确的JAVA_HOME (macOS)
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_161.jdk/Contents/Home

# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run

# 或者直接使用启动脚本
./start-project.sh
```

项目启动后访问：http://localhost:8080

### 4. 测试账号

**管理员账号：**
- 用户名: `admin`
- 密码: `123456`

**学生账号：**
- 用户名: `student1`
- 密码: `123456`

## 系统使用说明

### 学生操作流程
1. 注册学生账号或使用测试账号登录
2. 在首页查看最新公告
3. 进入"座位预约"页面预约座位
4. 可以查看和取消自己的预约
5. 可以提交投诉或建议

### 管理员操作流程
1. 使用管理员账号登录
2. 进入"管理后台"
3. 在公告管理中发布/删除公告
4. 查看所有学生的预约记录
5. 查看学生提交的投诉建议

## 目录结构

```
study-room-management/
├── src/main/java/com/example/studyroom/
│   ├── StudyRoomApplication.java        # 主启动类
│   ├── config/SecurityConfig.java       # 安全配置
│   ├── controller/                      # 控制器
│   ├── model/                          # 实体类
│   ├── repository/                     # 数据访问层
│   └── service/                        # 服务层
├── src/main/resources/
│   ├── application.properties          # 应用配置
│   ├── static/css/                     # 样式文件
│   └── templates/                      # 页面模板
├── database-setup.sql                  # 数据库初始化脚本
├── pom.xml                            # Maven配置
└── README.md                          # 项目说明
```

## 常见问题

**Q: 启动时出现数据库连接错误？**
A: 检查MySQL服务是否启动，确认用户名密码是否正确。

**Q: 页面显示异常？**
A: 确保已执行数据库初始化脚本，检查控制台错误信息。

**Q: 如何修改端口？**
A: 在application.properties中修改server.port配置。

## 扩展功能建议

- 添加座位状态实时显示
- 实现邮件通知功能
- 增加用户头像上传
- 添加预约时间限制
- 实现座位地图可视化 