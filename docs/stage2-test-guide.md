# 阶段 2 测试指南

## 准备工作

### 1. 创建数据库
执行以下 SQL 脚本创建数据库和表：
```bash
mysql -u root -p < sql/init.sql
```

或手动执行 `sql/init.sql` 文件中的 SQL 语句。

### 2. 配置数据库连接
修改 `src/main/resources/application.yml` 中的数据库配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/eams?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: 你的密码
```

### 3. 配置 Redis
确保 Redis 服务已启动，并修改 `application.yml` 中的 Redis 配置：
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: # 如果有密码，填写密码
```

### 4. 启动项目
```bash
mvn spring-boot:run
```

## 测试步骤

### 1. 访问 Swagger 文档
启动成功后访问：http://localhost:8080/api/swagger-ui.html

### 2. 测试登录接口

**接口**: POST `/api/auth/login`

**测试用户**:
- 用户名: `admin`, 密码: `123456`
- 用户名: `test`, 密码: `123456`

**请求示例**:
```json
{
  "username": "admin",
  "password": "123456"
}
```

**预期响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 604800,
    "userInfo": {
      "id": 1,
      "username": "admin",
      "nickname": "系统管理员",
      "email": "admin@eams.com",
      "phone": "13800138000",
      "avatar": null
    }
  },
  "timestamp": 1704268800000
}
```

### 3. 测试 Token 认证

在 Swagger 页面右上角点击 `Authorize` 按钮，输入获取到的 Token（不需要添加 "Bearer " 前缀）。

之后访问任何需要认证的接口，都会自动携带 Token。

### 4. 测试未认证访问

不携带 Token 访问需要认证的接口（除了登录接口），应该返回 401 错误：
```json
{
  "code": 401,
  "message": "未认证，请先登录",
  "data": null,
  "timestamp": 1704268800000
}
```

## 验证要点

✅ **登录成功**: 使用正确的用户名和密码，能成功登录并返回 Token
✅ **登录失败**: 使用错误的密码，返回 401 错误
✅ **账号禁用**: 修改用户状态为 0，登录时返回 403 错误
✅ **Token 认证**: 携带有效 Token 访问接口成功
✅ **Token 过期**: Token 过期后访问接口返回 401 错误
✅ **未认证访问**: 不携带 Token 访问接口返回 401 错误
✅ **Swagger 访问**: 不需要认证即可访问 Swagger 文档

## 常见问题

### Q1: 数据库连接失败
检查 MySQL 服务是否启动，数据库配置是否正确。

### Q2: Redis 连接失败
检查 Redis 服务是否启动，Redis 配置是否正确。

### Q3: 登录接口返回 403
检查 Spring Security 配置中是否正确放行了 `/api/auth/login` 路径。

### Q4: Token 无法识别
检查请求头中是否正确添加了 `Authorization: Bearer <token>`。
