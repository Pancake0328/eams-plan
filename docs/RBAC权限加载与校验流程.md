# RBAC 权限加载与校验流程说明

## 一、权限加载流程

### 1. 用户登录成功后权限加载

```java
// 1. 用户登录成功后，调用 PermissionService.getUserPermissions(userId)
Set<String> permissions = permissionService.getUserPermissions(userId);

// 2. 权限加载流程：
//    步骤1：查询用户的角色ID列表（sys_user_role表）
//    步骤2：查询角色的菜单ID列表（sys_role_menu表）
//    步骤3：查询菜单的权限标识（sys_menu表，permission_code字段）
//    步骤4：去重并返回权限标识集合
```

### 2. 权限标识格式规范

权限标识采用冒号分隔的三段式格式：

```
模块:资源:操作

示例：
- system:user:add       // 系统模块-用户资源-添加操作
- system:user:edit      // 系统模块-用户资源-编辑操作
- system:user:delete    // 系统模块-用户资源-删除操作
- system:role:permission // 系统模块-角色资源-分配权限操作
- asset:info:add        // 资产模块-信息资源-添加操作
- asset:info:export     // 资产模块-信息资源-导出操作
```

## 二、权限校验流程

### 1. 接口权限校验

在需要权限控制的Controller方法上使用 `@PreAuthorize` 注解：

```java
@RestController
@RequestMapping("/user")
public class UserController {

    @PreAuthorize("hasAuthority('system:user:add')")
    @PostMapping
    public Result<Long> createUser(@RequestBody UserCreateRequest request) {
        // 只有拥有 system:user:add 权限的用户才能调用此接口
    }

    @PreAuthorize("hasAuthority('system:user:edit')")
    @PutMapping("/{id}")
    public Result<Void> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        // 只有拥有 system:user:edit 权限的用户才能调用此接口
    }

    @PreAuthorize("hasAuthority('system:user:delete')")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        // 只有拥有 system:user:delete 权限的用户才能调用此接口
    }
}
```

### 2. 多权限校验

支持AND、OR逻辑：

```java
// AND：同时拥有两个权限
@PreAuthorize("hasAuthority('system:user:view') and hasAuthority('system:user:edit')")

// OR：拥有任一权限即可
@PreAuthorize("hasAuthority('system:user:add') or hasAuthority('system:user:edit')")

// 复杂组合
@PreAuthorize("hasAuthority('system:user:view') and (hasAuthority('system:user:add') or hasAuthority('system:user:edit'))")
```

## 三、前端权限控制

### 1. 菜单加载

```javascript
// 用户登录后，调用接口获取用户菜单树
GET /permission/user/{userId}/menu-tree

// 返回数据结构：
[
  {
    id: 1,
    menuName: "系统管理",
    menuType: "DIR",
    path: "/system",
    icon: "Setting",
    children: [
      {
        id: 4,
        menuName: "用户管理",
        menuType: "MENU",
        permissionCode: "system:user:list",
        path: "/system/user",
        component: "UserManagement",
        children: []
      }
    ]
  }
]
```

### 2. 按钮权限控制

```vue
<!-- 在Vue组件中使用 v-if 控制按钮显示 -->
<template>
  <el-button 
    v-if="hasPermission('system:user:add')" 
    @click="handleAdd"
  >
    新增用户
  </el-button>
  
  <el-button 
    v-if="hasPermission('system:user:edit')" 
    @click="handleEdit"
  >
    编辑
  </el-button>
</template>

<script setup>
import { usePermission } from '@/composables/usePermission'
const { hasPermission } = usePermission()
</script>
```

## 四、权限数据关系

```
用户（User）
  ↓ sys_user_role
角色（Role）
  ↓ sys_role_menu
菜单权限（Menu）
  → permission_code（权限标识）
```

### 查询示例

```sql
-- 查询用户ID为1的所有权限标识
SELECT DISTINCT m.permission_code
FROM sys_user_role ur
INNER JOIN sys_role_menu rm ON ur.role_id = rm.role_id
INNER JOIN sys_menu m ON rm.menu_id = m.id
WHERE ur.user_id = 1
  AND m.permission_code IS NOT NULL
  AND m.permission_code != '';
```

## 五、权限配置步骤

### 1. 创建角色

```bash
POST /role
{
  "roleCode": "ASSET_MANAGER",
  "roleName": "资产管理员",
  "status": 1,
  "remark": "负责资产管理相关功能"
}
```

### 2. 为角色分配权限

```bash
POST /role/assign-permissions
{
  "roleId": 3,
  "menuIds": [2, 16, 17, 18, 19, 20]  // 资产管理目录及相关菜单/按钮权限
}
```

### 3. 为用户分配角色

```bash
POST /permission/user/1/assign-roles
[3]  // 角色ID列表
```

### 4. 验证权限

```bash
# 查询用户权限
GET /permission/user/1/permissions

# 返回结果：
{
  "code": 200,
  "data": [
    "asset:info:list",
    "asset:info:add",
    "asset:info:edit",
    "asset:info:delete",
    "asset:info:export"
  ]
}
```

## 六、权限扩展建议

### 1. 数据权限

在现有权限标识基础上，可扩展数据权限：

```sql
-- 添加数据权限字段到 sys_role 表
ALTER TABLE sys_role ADD COLUMN data_scope INT DEFAULT 1 COMMENT '数据范围：1-全部，2-本部门，3-本部门及下级，4-仅本人';
```

### 2. 缓存优化

```java
// 使用Redis缓存用户权限，减少数据库查询
@Cacheable(value = "user:permissions", key = "#userId")
public Set<String> getUserPermissions(Long userId) {
    // ...
}
```

### 3. 权限刷新

```java
// 用户权限变更后，清除缓存
@CacheEvict(value = "user:permissions", key = "#userId")
public void refreshUserPermissions(Long userId) {
    // ...
}
```

## 七、注意事项

1. **权限标识唯一性**：每个权限标识必须全局唯一
2. **菜单类型区分**：DIR（目录）和MENU（菜单）用于前端展示，BUTTON（按钮）用于按钮级权限控制
3. **软删除处理**：删除角色或菜单时使用逻辑删除，避免影响历史数据
4. **权限继承**：建议定期清理无效的权限关联数据
5. **超级管理员**：建议保留一个拥有所有权限的超级管理员角色，roleCode为ADMIN
