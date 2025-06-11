# 自习室管理系统部署指南

## 完整的部署流程

### 第一步：环境准备

#### 1. 安装JDK 1.8+
确保安装的是JDK（Java Development Kit），而不是JRE（Java Runtime Environment）。

**macOS:**
```bash
# 使用Homebrew安装
brew install openjdk@8

# 设置JAVA_HOME
export JAVA_HOME=/usr/local/opt/openjdk@8/libexec/openjdk.jdk/Contents/Home
```

**Windows:**
下载并安装 Oracle JDK 8 或 OpenJDK 8，设置JAVA_HOME环境变量。

**Linux (Ubuntu):**
```bash
sudo apt update
sudo apt install openjdk-8-jdk
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
```

#### 2. 安装Maven
```bash
# macOS
brew install maven

# Ubuntu
sudo apt install maven

# Windows: 下载Maven并配置环境变量
```

#### 3. 安装MySQL
```bash
# macOS
brew install mysql
brew services start mysql

# Ubuntu
sudo apt install mysql-server
sudo systemctl start mysql

# Windows: 下载MySQL安装包
```

### 第二步：数据库配置

1. **启动MySQL服务**
2. **执行数据库初始化脚本**

```bash
# 方式1：直接执行SQL文件
mysql -u root -p < database-setup.sql

# 方式2：登录MySQL后执行
mysql -u root -p
source database-setup.sql;
```

3. **修改数据库连接配置**

编辑 `src/main/resources/application.properties`：

```properties
# 修改为您的MySQL密码
spring.datasource.password=您的MySQL密码

# 如果MySQL端口不是3306，请修改URL
spring.datasource.url=jdbc:mysql://localhost:3306/study_room_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
```

### 第三步：编译和运行

在项目根目录执行：

```bash
# 1. 清理并编译项目
mvn clean compile

# 2. 打包项目
mvn clean package

# 3. 运行项目
mvn spring-boot:run

# 或者运行打包后的jar文件
java -jar target/study-room-management-0.0.1-SNAPSHOT.jar
```

### 第四步：访问系统

项目成功启动后，浏览器访问：
- **系统首页**: http://localhost:8080
- **登录页面**: http://localhost:8080/login
- **注册页面**: http://localhost:8080/register

### 第五步：测试功能

#### 管理员测试
1. 使用管理员账号登录：
   - 用户名：`admin`
   - 密码：`123456`

2. 测试功能：
   - 访问管理后台：http://localhost:8080/admin/dashboard
   - 发布公告：http://localhost:8080/admin/announcements
   - 查看预约记录：http://localhost:8080/admin/reservations
   - 查看投诉建议：http://localhost:8080/admin/complaints

#### 学生用户测试
1. 使用学生账号登录：
   - 用户名：`student1`
   - 密码：`123456`

2. 测试功能：
   - 查看公告（首页）
   - 预约座位：http://localhost:8080/reservations
   - 提交投诉建议

3. 注册新用户：
   - 点击"注册"按钮
   - 填写用户信息

## 常见问题解决

### 1. 编译错误："No compiler is provided"
**原因**: 系统安装的是JRE而不是JDK  
**解决**: 安装JDK并正确设置JAVA_HOME环境变量

### 2. 数据库连接失败
**检查项目**:
- MySQL服务是否启动
- 用户名密码是否正确
- 数据库是否已创建
- 端口是否正确

### 3. 页面显示异常
**检查项目**:
- 是否执行了数据库初始化脚本
- 控制台是否有错误信息
- 静态资源是否正确加载

### 4. 端口冲突
如果8080端口被占用，修改`application.properties`：
```properties
server.port=8081
```

## 项目结构说明

```
study-room-management/
├── src/main/java/          # Java源代码
│   └── com/example/studyroom/
│       ├── StudyRoomApplication.java    # 启动类
│       ├── config/                      # 配置类
│       ├── controller/                  # 控制器
│       ├── model/                       # 实体类
│       ├── repository/                  # 数据访问层
│       └── service/                     # 服务层
├── src/main/resources/     # 资源文件
│   ├── application.properties           # 配置文件
│   ├── static/                         # 静态资源
│   └── templates/                      # 页面模板
├── database-setup.sql      # 数据库初始化脚本
├── pom.xml                # Maven配置
└── README.md              # 项目说明
```

## 开发模式运行

开发时推荐设置：

```properties
# application.properties 开发配置
spring.thymeleaf.cache=false
spring.jpa.show-sql=true
logging.level.com.example.studyroom=DEBUG
```

## 生产部署建议

1. **修改默认密码**: 更改管理员和测试用户的默认密码
2. **启用HTTPS**: 配置SSL证书
3. **数据库优化**: 调整MySQL配置以适应生产环境
4. **日志配置**: 配置适当的日志级别和输出
5. **备份策略**: 设置数据库定期备份

## 扩展功能

系统已具备扩展能力，可以添加：
- 用户头像上传
- 邮件通知功能
- 座位地图显示
- 预约时间限制
- 统计报表功能 