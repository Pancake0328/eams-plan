# 用户管理模块 API 文档

## 接口列表

### 1. 创建用户
- **接口**: `POST /api/users`
- **描述**: 创建新用户
- **请求体**:
```json
{
  "username": "zhangsan",
  "password": "123456",
  "nickname": "张三",
  "email": "zhangsan@example.com",
  "phone": "13800138002",
  "avatar": "",
  "status": 1
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "创建用户成功",
  "data": 3,
  "timestamp": 1704268800000
}
```

### 2. 更新用户
- **接口**: `PUT /api/users/{id}`
- **描述**: 更新用户信息
- **路径参数**: `id` - 用户ID
- **请求体**:
```json
{
  "nickname": "张三",
  "email": "zhangsan@example.com",
  "phone": "13800138002",
  "avatar": "",
  "status": 1
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "更新用户成功",
  "data": null,
  "timestamp": 1704268800000
}
```

### 3. 删除用户
- **接口**: `DELETE /api/users/{id}`
- **描述**: 逻辑删除用户
- **路径参数**: `id` - 用户ID
- **响应**:
```json
{
  "code": 200,
  "message": "删除用户成功",
  "data": null,
  "timestamp": 1704268800000
}
```

### 4. 获取用户详情
- **接口**: `GET /api/users/{id}`
- **描述**: 根据ID获取用户详细信息
- **路径参数**: `id` - 用户ID
- **响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "admin",
    "nickname": "系统管理员",
    "email": "admin@eams.com",
    "phone": "13800138000",
    "avatar": null,
    "status": 1,
    "createTime": "2026-01-03 10:00:00",
    "updateTime": "2026-01-03 10:00:00"
  },
  "timestamp": 1704268800000
}
```

### 5. 分页查询用户
- **接口**: `GET /api/users`
- **描述**: 根据条件分页查询用户列表
- **查询参数**:
  - `current`: 当前页码（默认1）
  - `size`: 每页数量（默认10）
  - `username`: 用户名（模糊查询）
  - `nickname`: 昵称（模糊查询）
  - `phone`: 手机号（模糊查询）
  - `status`: 状态（0-禁用，1-正常）
  
- **示例**: `GET /api/users?current=1&size=10&username=admin`
- **响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "username": "admin",
        "nickname": "系统管理员",
        "email": "admin@eams.com",
        "phone": "13800138000",
        "avatar": null,
        "status": 1,
        "createTime": "2026-01-03 10:00:00",
        "updateTime": "2026-01-03 10:00:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  },
  "timestamp": 1704268800000
}
```

### 6. 更新用户状态
- **接口**: `PUT /api/users/{id}/status`
- **描述**: 启用或禁用用户
- **路径参数**: `id` - 用户ID
- **查询参数**: `status` - 状态（0-禁用，1-正常）
- **示例**: `PUT /api/users/1/status?status=0`
- **响应**:
```json
{
  "code": 200,
  "message": "更新用户状态成功",
  "data": null,
  "timestamp": 1704268800000
}
```

### 7. 重置密码
- **接口**: `PUT /api/users/{id}/password`
- **描述**: 重置用户密码
- **路径参数**: `id` - 用户ID
- **请求体**:
```json
{
  "newPassword": "newpassword123"
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "重置密码成功",
  "data": null,
  "timestamp": 1704268800000
}
```

## 参数校验规则

### 创建用户
- `username`: 必填，4-20字符，只能包含字母、数字和下划线
- `password`: 必填，6-20字符
- `nickname`: 必填，最多50字符
- `email`: 可选，必须是有效的邮箱格式
- `phone`: 可选，必须是有效的11位手机号（1开头）
- `avatar`: 可选
- `status`: 可选，默认1（正常）

### 更新用户
- `nickname`: 必填，最多50字符
- `email`: 可选，必须是有效的邮箱格式
- `phone`: 可选，必须是有效的11位手机号
- `avatar`: 可选
- `status`: 可选

### 重置密码
- `newPassword`: 必填，6-20字符

## 业务规则

### 唯一性校验
- 用户名必须唯一
- 手机号必须唯一（如果填写）

### 管理员保护
- 不允许删除用户名为 `admin` 的账号
- 不允许禁用用户名为 `admin` 的账号

### 密码安全
- 所有密码使用 BCrypt 加密存储
- 密码不会在任何接口中返回

## 错误码说明

- `200`: 操作成功
- `400`: 参数校验失败
- `401`: 未认证（需要登录）
- `404`: 数据不存在
- `600`: 业务异常（如用户名已存在、手机号已被使用等）

## 注意事项

1. 所有接口（除登录外）都需要在请求头中携带 Token：`Authorization: Bearer <token>`
2. 分页查询支持多条件组合查询
3. 删除操作为逻辑删除，数据不会真正从数据库中删除
4. 状态为0表示禁用，禁用后用户无法登录
