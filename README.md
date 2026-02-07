# EAMS - 企业资产管理系统

## 项目简介
EAMS (Enterprise Asset Management System) 是一个覆盖采购、入库、资产流转、生命周期、盘点、报修与统计分析的企业资产管理系统，包含后端 Spring Boot 服务与前端 Vue 3 管理端。

## 功能概览
- 仪表盘统计：资产概览、部门/分类/状态分布、时间趋势
- 系统管理与 RBAC：用户、角色、部门、权限、菜单/按钮级别鉴权
- 资产台账：资产信息、分类树、状态维护
- 资产流转：入库、分配、调拨、归还、报废、送修、维修完成
- 生命周期管理：阶段流转、历史追踪
- 盘点管理：计划创建、执行、结果处理、明细分页
- 报修管理：申请、审批、维修、完成
- 采购管理：采购单、入库/批量入库、采购资金与账单统计
- 折旧与账单：月度/年度折旧与账单统计（详见 docs）

## 技术栈
### 后端
- Java 17, Spring Boot 3.1.5
- MyBatis Plus 3.5.7
- Spring Security + JWT
- MySQL 8.x, Redis
- SpringDoc OpenAPI (Swagger)
- MapStruct, Lombok, Hutool
- WebSocket, Apache POI

### 前端
- Vue 3 + TypeScript
- Vite, Element Plus, Pinia, Vue Router
- Axios, ECharts

## 项目结构
```
EAMS-plan/
├── docs/               # 设计与变更说明
├── frontend/           # Vue 3 管理端
├── sql/                # 初始化与迁移脚本
├── sql_pro/            # 生产环境脚本
├── src/                # 后端源码
├── pom.xml
└── README.md
```

```
src/main/java/com/eams/
├── asset       # 资产信息/分类/流转
├── lifecycle   # 生命周期/盘点/报修
├── purchase    # 采购与资金统计
├── system      # 用户/角色/权限/部门
├── dashboard   # 仪表盘统计
└── common/config/security/aop/...
```

## 快速开始
### 1. 数据库初始化
执行基础脚本（默认库名为 `eams`，如需其他库名请同步修改脚本与配置）：
```
sql/init.sql
```

初始化 RBAC 权限与菜单：
```
sql/rbac_system.sql
```

首次初始化管理员全量权限（可选）：
```
sql_pro/admin_full_permission.sql
```

其它迁移脚本按需执行（例如 `purchase_optimization.sql`、`user_department_binding.sql`、`asset_department_id_migration.sql`）。

### 2. 后端配置
编辑 `src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/eams_plan?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
```

### 3. 启动后端
```bash
mvn clean package
mvn spring-boot:run
```

### 4. 启动前端
```bash
cd frontend
npm install
npm run dev
```

构建：
```bash
npm run build
```

### 5. 访问入口
- API 基础路径：`http://localhost:8080/api`
- Swagger UI：`http://localhost:8080/api/swagger-ui.html`
- 登录接口：`POST /api/auth/login`
- Token 使用说明：`docs/token-usage-guide.md`
- 初始化账号：`admin/123456`（见 `sql/init.sql`）

## 相关文档
- `docs/权限管理说明.md`
- `docs/RBAC权限加载与校验流程.md`
- `docs/用户部门绑定功能.md`
- `docs/user-management-api.md`
- `docs/生命周期状态说明.md`
- `docs/盘点管理更新说明.md`
- `docs/采购管理优化.md`
- `docs/账单管理优化说明.md`
- `docs/折旧计算逻辑说明.md`
- `docs/统计SQL说明.md`
- `docs/token-usage-guide.md`

## 开发约定
- Controller → Service → Mapper 分层
- DTO / VO 分离
- 统一 Result<T> 返回
- RESTful API 设计

## 许可证
Apache License 2.0

## 联系方式
- 项目组：EAMS Team
- 邮箱：eams@enterprise.com
