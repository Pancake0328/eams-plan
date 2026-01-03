# EAMS 前端项目

## 项目说明

这是 EAMS （企业资产管理系统）的前端项目，使用 Vue 3 + TypeScript + Vite + Element Plus 开发。

## 技术栈

- **Vue 3** - 渐进式 JavaScript 框架
- **TypeScript** - JavaScript 的超集
- **Vite** - 下一代前端构建工具
- **Element Plus** - 基于 Vue 3 的组件库
- **Axios** - HTTP 请求库
- **Pinia** - Vue 状态管理库

## 项目结构

```
frontend/
├── public/              # 静态资源
├── src/
│   ├── api/            # API 接口
│   │   └── user.ts     # 用户管理接口
│   ├── types/          # TypeScript 类型定义
│   │   └── index.ts    # 通用类型
│   ├── utils/          # 工具函数
│   │   └── request.ts  # Axios 封装
│   ├── views/          # 页面组件
│   │   └── UserManagement.vue  # 用户管理页面
│   ├── App.vue         # 根组件
│   └── main.ts         # 入口文件
├── index.html          # HTML 模板
├── package.json        # 项目配置
├── tsconfig.json       # TypeScript 配置
└── vite.config.ts      # Vite 配置
```

## 快速开始

### 安装依赖

```bash
npm install
```

### 开发环境运行

```bash
npm run dev
```

访问：http://localhost:3000

### 生产环境构建

```bash
npm run build
```

## 功能说明

### 用户管理模块

- ✅ 用户列表展示（分页）
- ✅ 搜索功能（用户名、手机号、状态）
- ✅ 新增用户
- ✅ 编辑用户
- ✅ 删除用户（带确认）
- ✅ 启用/禁用用户
- ✅ 重置密码
- ✅ 表单校验
- ✅ 错误提示

## API 配置

后端 API 地址配置在 `vite.config.ts` 中：

```typescript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8080', // 后端地址
      changeOrigin: true
    }
  }
}
```

## 认证说明

Token 存储在 localStorage 中，请求时会自动添加到请求头：

```typescript
Authorization: Bearer <token>
```

登录后会自动保存 token，401 错误会自动跳转到登录页。

## 注意事项

1. 确保后端服务已启动（默认端口 8080）
2. 首次使用需要先登录获取 token
3. 所有敏感操作都有确认提示
4. 管理员账号受保护，不能删除或禁用

## 浏览器支持

- Chrome（推荐）
- Firefox
- Safari
- Edge

## 开发规范

- 使用 TypeScript 编写代码
- 遵循 Vue 3 Composition API 风格
- 组件使用 `<script setup>` 语法
- 样式使用 scoped 避免污染
- 使用 Element Plus 组件库
