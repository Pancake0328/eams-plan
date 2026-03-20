# 2026-03-20 Portal `/portal/my-assets` 路由跳转修复

## 一、问题现象

访问 ` /portal/my-assets ` 时，无论管理员还是普通用户，都会被重定向到欢迎页（`/welcome`），无法进入“我的资产”自助页。

---

## 二、影响范围

- 员工自助端路由：`/portal/**`
- 特别是：`/portal/my-assets`、`/portal/my-applications`、`/portal/my-repairs`
- 影响人群：拥有自助能力但未授予 `asset:portal:view` 的用户（包括部分管理员角色）

---

## 三、根因分析

问题在前端路由守卫 `frontend/src/router/index.ts` 的 portal 鉴权逻辑：

1. `isPortalOnlyUser` 仅以 `asset:portal:view` 作为“是否有 portal 能力”的前提。
2. 同时存在额外拦截：
   - `if (to.meta.portalOnly && !permissionStore.hasPermission('asset:portal:view')) { ... }`
3. 当用户拥有 `asset:info:my:list`（可访问 `/portal/my-assets` 的页面权限）但没有 `asset:portal:view` 时，会在上述逻辑被强制重定向。

结果：即使目标路由本身权限满足，也被 portal 全局门禁错误拦截，出现“总是跳到欢迎页”。

---

## 四、修复方案

### 1）修正 portal 用户识别逻辑

将 portal 能力判断从“单一权限 `asset:portal:view`”改为“任一自助端能力权限”：

- `asset:portal:view`
- `asset:info:my:list`
- `asset:usage:my:list`
- `repair:own:list`

即：只要具备上述任一权限，就可被识别为具备 portal 能力。

### 2）移除错误的 portal 全局门禁

删除如下守卫分支（该分支与路由本身 `meta.permission` 校验重复且更严格，导致误拦截）：

```ts
if (to.meta.portalOnly && !permissionStore.hasPermission('asset:portal:view')) {
  next(resolveFirstAccessiblePath(permissionStore))
  return
}
```

保留统一的 `to.meta.permission` 校验，以每个页面声明的权限为准。

---

## 五、变更文件

- `frontend/src/router/index.ts`

具体变更点：

- 新增 `portalIndicatorPermissions`；
- 调整 `isPortalOnlyUser` 的判定方式；
- 删除 `portalOnly + asset:portal:view` 的强制拦截分支。

---

## 六、验证记录

### 1）构建验证

- 前端：`npm exec vite build` ✅ 通过
- 后端：`mvn test -q` ✅ 通过

### 2）行为验证（预期）

- 访问 `/portal/my-assets`：
  - 若具备 `asset:info:my:list`，应正常进入页面；
  - 不再因缺少 `asset:portal:view` 被错误重定向。
- 访问其他 portal 页面时，按各页面自身 `meta.permission` 校验放行/拦截。

---

## 七、结论

本次修复将 portal 路由访问控制从“单权限总开关”改为“页面权限精确校验”，消除了 `/portal/my-assets` 被错误重定向到欢迎页的问题，同时保持管理员后台与员工自助端的分流策略不变。
