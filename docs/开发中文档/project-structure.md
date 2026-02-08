# EAMS 项目结构说明

## 整体目录结构

```
eams-backend/
├── docs/                                    # 文档目录
│   └── stage2-test-guide.md                # 阶段2测试指南
├── sql/                                     # SQL 脚本
│   └── init.sql                            # 数据库初始化脚本
├── src/
│   ├── main/
│   │   ├── java/com/eams/
│   │   │   ├── EamsApplication.java        # 主启动类
│   │   │   ├── aop/                        # AOP 切面
│   │   │   │   ├── OperationLog.java       # 操作日志注解
│   │   │   │   └── OperationLogAspect.java # 操作日志切面
│   │   │   ├── common/                     # 通用模块
│   │   │   │   ├── constant/               # 常量定义
│   │   │   │   │   └── CommonConstant.java
│   │   │   │   └── result/                 # 统一返回结果
│   │   │   │       ├── Result.java
│   │   │   │       └── ResultCode.java
│   │   │   ├── config/                     # 配置类
│   │   │   │   ├── CorsConfig.java         # 跨域配置
│   │   │   │   ├── MyBatisMetaObjectHandler.java # 字段自动填充
│   │   │   │   ├── MyBatisPlusConfig.java  # MyBatis Plus配置
│   │   │   │   ├── RedisConfig.java        # Redis配置
│   │   │   │   ├── SecurityConfig.java     # Spring Security配置
│   │   │   │   └── SwaggerConfig.java      # Swagger配置
│   │   │   ├── exception/                  # 异常处理
│   │   │   │   ├── BusinessException.java  # 业务异常
│   │   │   │   └── GlobalExceptionHandler.java # 全局异常处理器
│   │   │   ├── security/                   # 安全框架
│   │   │   │   ├── JwtAccessDeniedHandler.java      # 访问拒绝处理器
│   │   │   │   ├── JwtAuthenticationEntryPoint.java # 认证入口点
│   │   │   │   ├── JwtAuthenticationFilter.java     # JWT认证过滤器
│   │   │   │   ├── JwtProperties.java               # JWT配置属性
│   │   │   │   ├── JwtUtil.java                     # JWT工具类
│   │   │   │   ├── SecurityContextHolder.java       # 安全上下文
│   │   │   │   ├── UserDetailsImpl.java             # UserDetails实现
│   │   │   │   └── UserDetailsServiceImpl.java      # UserDetailsService实现
│   │   │   └── system/                     # 系统管理模块
│   │   │       ├── controller/             # 控制器
│   │   │       │   └── AuthController.java
│   │   │       ├── dto/                    # 数据传输对象
│   │   │       │   └── LoginRequest.java
│   │   │       ├── entity/                 # 实体类
│   │   │       │   └── User.java
│   │   │       ├── mapper/                 # Mapper接口
│   │   │       │   └── UserMapper.java
│   │   │       ├── service/                # 服务接口
│   │   │       │   ├── AuthService.java
│   │   │       │   └── impl/               # 服务实现
│   │   │       │       └── AuthServiceImpl.java
│   │   │       ├── vo/                     # 视图对象
│   │   │       │   └── LoginResponse.java
│   │   │       └── package-info.java       # 包说明
│   │   └── resources/
│   │       └── application.yml             # 应用配置文件
│   └── test/
│       └── java/com/eams/
│           └── EamsApplicationTests.java   # 测试类
├── .gitignore                              # Git 忽略配置
├── pom.xml                                 # Maven 配置
└── README.md                               # 项目说明
```

## 核心模块说明

### 1. aop 包
- **OperationLog**: 操作日志注解，用于标记需要记录日志的方法
- **OperationLogAspect**: 操作日志切面，自动记录方法调用信息

### 2. common 包
通用功能模块，提供项目级别的基础支持

#### constant 子包
- **CommonConstant**: 通用常量定义（状态码、默认值等）

#### result 子包
- **Result**: 统一返回结果封装类
- **ResultCode**: 响应状态码枚举

### 3. config 包
各种配置类

- **CorsConfig**: 跨域配置
- **MyBatisMetaObjectHandler**: MyBatis Plus 字段自动填充（create_time, update_time）
- **MyBatisPlusConfig**: MyBatis Plus 分页插件配置
- **RedisConfig**: Redis 序列化配置
- **SecurityConfig**: Spring Security 安全配置（核心）
- **SwaggerConfig**: Swagger API 文档配置

### 4. exception 包
异常处理模块

- **BusinessException**: 自定义业务异常
- **GlobalExceptionHandler**: 全局异常处理器，统一处理各类异常

### 5. security 包
安全框架核心模块

- **JwtAccessDeniedHandler**: 处理权限不足（403）
- **JwtAuthenticationEntryPoint**: 处理未认证（401）
- **JwtAuthenticationFilter**: JWT 认证过滤器，从请求头提取 Token
- **JwtProperties**: JWT 配置属性（密钥、过期时间等）
- **JwtUtil**: JWT 工具类（生成、解析、验证 Token）
- **SecurityContextHolder**: 线程级别的安全上下文，存储当前登录用户
- **UserDetailsImpl**: Spring Security UserDetails 实现
- **UserDetailsServiceImpl**: Spring Security UserDetailsService 实现

### 6. system 包
系统管理业务模块

#### controller 子包
- **AuthController**: 认证控制器（登录接口）

#### dto 子包
- **LoginRequest**: 登录请求 DTO

#### entity 子包
- **User**: 用户实体

#### mapper 子包
- **UserMapper**: 用户 Mapper 接口

#### service 子包
- **AuthService**: 认证服务接口
- **impl/AuthServiceImpl**: 认证服务实现

#### vo 子包
- **LoginResponse**: 登录响应 VO

## 设计模式与架构

### 1. 三层架构
- **Controller 层**: 接收请求，参数校验，返回响应
- **Service 层**: 业务逻辑处理
- **Mapper 层**: 数据访问

### 2. DTO/VO 分离
- **DTO (Data Transfer Object)**: 用于接收前端请求参数
- **VO (View Object)**: 用于返回给前端的数据
- **Entity**: 数据库实体，不直接暴露给前端

### 3. AOP 切面编程
- 操作日志自动记录
- 与业务代码解耦

### 4. 过滤器链模式
- Spring Security 过滤器链
- JWT 认证过滤器

### 5. 策略模式
- 不同的认证方式（当前为用户名密码，可扩展）
- 不同的密码编码器

## 技术栈对应

### Spring Boot 3.1.5
- 核心框架
- 自动配置

### Spring Security
- 安全框架
- 认证和授权

### JWT (Json Web Token)
- 无状态认证
- Token 生成和验证

### MyBatis Plus 3.5.7
- ORM 框架
- 自动生成 CRUD
- 分页插件

### Redis
- 缓存（预留）
- 会话管理（预留）

### Swagger 3
- API 文档自动生成
- 接口测试

## 核心流程

### 登录流程
1. 前端发送用户名和密码到 `/api/auth/login`
2. `AuthController` 接收请求，调用 `AuthService`
3. `AuthService` 使用 `AuthenticationManager` 进行认证
4. `AuthenticationManager` 调用 `UserDetailsService` 加载用户
5. `UserDetailsService` 从数据库查询用户
6. 比对密码（BCrypt）
7. 认证成功后生成 JWT Token
8. 返回 Token 和用户信息给前端

### 认证流程
1. 前端在每个请求头中携带 Token（`Authorization: Bearer <token>`）
2. `JwtAuthenticationFilter` 拦截请求
3. 从请求头提取 Token
4. 验证 Token 有效性
5. 从 Token 中解析出用户名
6. 加载用户详情
7. 设置 Spring Security 认证上下文
8. 设置自定义安全上下文（方便业务代码使用）
9. 继续执行业务逻辑
10. 请求结束后清除上下文

## 待扩展功能

- 权限管理（角色、权限）
- Token 刷新机制
- 登出功能（黑名单）
- 多端登录控制
- 登录日志记录
