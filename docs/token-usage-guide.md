# Token 使用说明

## 问题现象
访问需要认证的接口时返回 401 错误：
```json
{
    "code": 401,
    "message": "未认证，请先登录",
    "timestamp": 1767421020176
}
```

日志显示：`Compact JWT strings may not contain whitespace.`

## 问题原因
Token 字符串中包含空格，导致 JWT 解析失败。

## 解决方案

### 在 Swagger UI 中使用 Token

1. **登录获取 Token**
   - 访问 `/api/auth/login` 接口
   - 输入用户名和密码
   - 获得响应中的 `token` 字段，例如：
     ```json
     {
       "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTcwNDI2ODgwMCwiZXhwIjoxNzA0ODczNjAwfQ.xxxxx"
     }
     ```

2. **在 Swagger 中配置认证**
   - 点击页面右上角的 **"Authorize"** 按钮（或锁图标 🔒）
   - 在弹出的对话框中，**直接粘贴 Token 值**
   - ⚠️ **重要**：不要添加 "Bearer " 前缀，只粘贴纯 Token
   - 点击 "Authorize" 确认
   - 点击 "Close" 关闭对话框

3. **正确示例**
   ```
   ✅ 正确：eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTcwNDI2ODgwMCwiZXhwIjoxNzA0ODczNjAwfQ.xxxxx
   
   ❌ 错误：Bearer eyJhbGciOiJIUzI1NiJ9...（不要加 Bearer 前缀）
   ❌ 错误： eyJhbGciOiJIUzI1NiJ9...（前面不要有空格）
   ```

### 在 Postman 中使用 Token

1. 选择请求
2. 进入 "Authorization" 标签页
3. Type 选择 "Bearer Token"
4. 在 Token 输入框中粘贴纯 Token（不要加 "Bearer " 前缀）

### 在代码中使用 Token

**方式一：使用 Bearer Token（推荐）**
```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTcwNDI2ODgwMCwiZXhwIjoxNzA0ODczNjAwfQ.xxxxx
```

**方式二：直接使用 Token**
```http
Authorization: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTcwNDI2ODgwMCwiZXhwIjoxNzA0ODczNjAwfQ.xxxxx
```

## 常见错误

### 错误 1：Token 包含空格
```
❌ Authorization: Bearer  eyJhbGciOiJIUzI1NiJ9...
                        ↑ 多余的空格
```

### 错误 2：Token 不完整
```
❌ Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWI...
                                                    ↑ Token 被截断
```

### 错误 3：Swagger 中输入格式错误
```
❌ 在 Swagger Authorize 对话框中输入: Bearer eyJhbGciOiJIUzI1NiJ9...
   （Swagger 会自动添加 Bearer 前缀，不需要手动输入）
```

## 验证 Token 是否正确

### 检查 Token 格式
正确的 JWT Token 应该：
- 由三部分组成，用两个点 `.` 分隔
- 格式：`<header>.<payload>.<signature>`
- 示例：`eyJhbGci...eyJzdWI...xxxxx`

### 测试步骤
1. 登录获取 Token
2. 复制完整的 Token（所有字符）
3. 在 Swagger 点击 Authorize
4. 粘贴 Token（不要加任何前缀或空格）
5. 测试任意需要认证的接口

### 日志检查
如果仍然失败，查看后台日志：
- ✅ 成功：`已认证用户: xxx`
- ❌ 失败：`解析 Token 失败: ...`

## 已优化功能

系统已优化 Token 处理逻辑：
- ✅ 自动清理首尾空格
- ✅ 支持带 "Bearer " 前缀的 Token
- ✅ 支持不带前缀的裸 Token
- ✅ 更详细的错误日志

## 快速测试

1. 登录（admin/123456）
2. 复制响应中的 token 值
3. 点击 Swagger 右上角 Authorize
4. 粘贴 token
5. 访问 GET /api/users 接口
6. 应该返回用户列表而不是 401
