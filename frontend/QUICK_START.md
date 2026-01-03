# 前端快速启动指南

## 1. 安装依赖

在 `frontend` 目录下执行：

```bash
cd frontend
npm install
```

## 2. 配置后端地址

确保后端服务已启动（默认 http://localhost:8080）

如需修改后端地址，编辑 `vite.config.ts`：

```typescript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',  // 修改此处
      changeOrigin: true
    }
  }
}
```

## 3. 启动开发服务器

```bash
npm run dev
```

访问：http://localhost:3000

## 4. 登录系统

系统已实现完整的登录页面：

1. 访问 http://localhost:3000 会自动跳转到登录页
2. 使用以下账号登录：
   - 管理员：admin / 123456
   - 测试用户：test / 123456
3. 登录成功后自动跳转到用户管理页面

1. 访问后端 Swagger：http://localhost:8080/api/swagger-ui.html
2. 使用 `/api/auth/login` 接口登录
3. 复制返回的 `token` 值
4. 在浏览器控制台（F12）执行：
   ```javascript
   localStorage.setItem('token', '你的token')
   ```
5. 刷新页面即可看到用户列表

### 方法二：使用 Postman/cURL 获取

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123456"
  }'
```

复制返回的 token，在浏览器控制台设置：
```javascript
localStorage.setItem('token', '你的token')
```

## 5. 功能测试

- ✅ 用户列表展示
- ✅ 搜索功能（用户名、手机号、状态）
- ✅ 新增用户
- ✅ 编辑用户
- ✅ 删除用户
- ✅ 启用/禁用开关
- ✅ 重置密码

## 常见问题

### Q1: 页面显示 401 错误

**原因**：Token 未设置或已过期

**解决**：重新登录获取 Token 并设置到 localStorage

### Q2: 网络请求失败

**原因**：后端服务未启动或端口不正确

**解决**：
1. 确保后端服务已启动
2. 检查控制台网络请求，确认请求地址正确

### Q3: 数据不显示

**原因**：数据库中没有数据

**解决**：
1. 确保已执行 `sql/init.sql` 创建表和初始数据
2. 使用新增功能添加用户

## 下一步

如需添加登录页面，可以创建 `src/views/Login.vue` 并配置路由。
