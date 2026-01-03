# EAMS - 企业资产管理系统

## 项目简介

EAMS (Enterprise Asset Management System) 是一个企业级资产管理系统，提供资产全生命周期管理能力。

## 技术栈

### 后端技术
- **Java**: 17
- **Spring Boot**: 3.1.5
- **MyBatis Plus**: 3.5.7
- **MySQL**: 8.x
- **Redis**: 缓存和会话管理
- **Spring Security + JWT**: 认证和授权
- **Swagger 3**: API 文档
- **Lombok**: 简化代码
- **Hutool**: 工具类库
- **MapStruct**: 对象映射

### 前端技术（后续）
- Vue 3 + TypeScript
- Vite
- Element Plus
- Pinia
- Vue Router
- Axios
- ECharts

## 项目结构

```
eams-backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/eams/
│   │   │       ├── EamsApplication.java          # 主启动类
│   │   │       ├── common/                        # 通用层
│   │   │       │   ├── constant/                  # 常量定义
│   │   │       │   └── result/                    # 统一返回结果
│   │   │       ├── config/                        # 配置类
│   │   │       │   ├── CorsConfig.java           # 跨域配置
│   │   │       │   ├── MyBatisPlusConfig.java    # MyBatis配置
│   │   │       │   ├── RedisConfig.java          # Redis配置
│   │   │       │   └── SwaggerConfig.java        # Swagger配置
│   │   │       ├── exception/                     # 异常处理
│   │   │       │   ├── BusinessException.java    # 业务异常
│   │   │       │   └── GlobalExceptionHandler.java # 全局异常处理器
│   │   │       ├── security/                      # 安全框架
│   │   │       │   ├── JwtProperties.java        # JWT配置属性
│   │   │       │   ├── JwtUtil.java              # JWT工具类
│   │   │       │   └── SecurityContextHolder.java # 安全上下文
│   │   │       ├── aop/                          # AOP切面
│   │   │       │   ├── OperationLog.java         # 操作日志注解
│   │   │       │   └── OperationLogAspect.java   # 操作日志切面
│   │   │       └── system/                       # 系统模块（占位）
│   │   └── resources/
│   │       └── application.yml                   # 应用配置
│   └── test/                                     # 测试代码
├── pom.xml                                       # Maven配置
└── README.md                                     # 项目说明
```

## 核心功能

### 已实现（基础工程）
- ✅ Maven 工程配置
- ✅ 统一返回结果封装
- ✅ 全局异常处理
- ✅ MyBatis Plus 分页插件
- ✅ Redis 序列化配置
- ✅ Swagger 3 API 文档
- ✅ CORS 跨域配置
- ✅ JWT 认证工具
- ✅ AOP 操作日志

### 待开发
- ⏳ 用户管理
- ⏳ 角色管理
- ⏳ 权限管理
- ⏳ 资产管理
- ⏳ 资产分类
- ⏳ 资产盘点
- ⏳ 资产维护
- ⏳ 报表统计

## 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

## 快速开始

### 1. 数据库配置

修改 `application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/eams?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

### 2. Redis 配置

修改 `application.yml` 中的 Redis 连接信息：

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: your_password
```

### 3. 编译运行

```bash
# 编译项目
mvn clean package

# 运行项目
mvn spring-boot:run
```

### 4. 访问 Swagger 文档

启动成功后，访问：http://localhost:8080/api/swagger-ui.html

## 开发规范

### 代码规范
- 三层架构：Controller → Service → Mapper
- DTO / VO 严格分离
- 所有方法、字段必须有中文注释
- 统一使用 Result<T> 返回
- RESTful API 设计

### 命名规范
- 类名：大驼峰（PascalCase）
- 方法名/变量名：小驼峰（camelCase）
- 常量：全大写下划线分隔（UPPER_SNAKE_CASE）
- 包名：全小写

### 数据库规范
- 表名：小写下划线分隔
- 字段名：小写下划线分隔
- 必须字段：id, create_time, update_time, deleted

## 许可证

Apache License 2.0

## 联系方式

- 项目组：EAMS Team
- 邮箱：eams@enterprise.com
