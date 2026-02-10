-- 管理员全量权限初始化脚本
-- 适用场景：已完成表结构创建，并手动插入 user_id=1 的管理员账号
-- 目标：初始化角色与菜单权限，并给 user_id=1 赋予全部权限

-- 1) 角色数据
INSERT INTO sys_role (id, role_code, role_name, status, remark, deleted)
VALUES
  (1, 'ADMIN', '系统管理员', 1, '拥有系统所有权限', 0),
  (2, 'USER', '普通用户', 1, '普通业务用户', 0),
  (3, 'ASSET_MANAGER', '资产管理员', 1, '负责资产管理相关功能', 0)
ON DUPLICATE KEY UPDATE
  role_name = VALUES(role_name),
  status = VALUES(status),
  remark = VALUES(remark),
  deleted = VALUES(deleted);

-- 2) 菜单与按钮权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, permission_code, path, component, icon, sort_order, status, visible, remark, deleted)
VALUES
  (1, 0, '仪表盘', 'MENU', 'dashboard:view', '/dashboard', 'Dashboard', 'DataLine', 1, 1, 1, NULL, 0),
  (2, 0, '系统管理', 'DIR', NULL, '/system', NULL, 'Setting', 2, 1, 1, NULL, 0),
  (3, 0, '资产管理', 'DIR', NULL, '/asset', NULL, 'Grid', 3, 1, 1, NULL, 0),
  (4, 0, '采购管理', 'MENU', 'purchase:list', '/purchase', 'PurchaseManagement', 'ShoppingCart', 4, 1, 1, NULL, 0),
  (5, 0, '人员管理', 'DIR', NULL, '/personnel', NULL, 'User', 5, 1, 1, NULL, 0),
  (6, 0, '生命周期与盘点', 'DIR', NULL, '/lifecycle', NULL, 'Refresh', 6, 1, 1, NULL, 0),

  (8, 2, '角色管理', 'MENU', 'system:role:list', '/role', 'RoleManagement', 'UserFilled', 1, 1, 1, NULL, 0),
  (11, 2, '菜单管理', 'MENU', 'system:permission:list', '/permissions', 'PermissionManagement', 'Key', 2, 1, 1, NULL, 0),

  (9, 5, '用户管理', 'MENU', 'system:user:list', '/', 'UserManagement', 'User', 1, 1, 1, NULL, 0),
  (10, 5, '部门管理', 'MENU', 'system:department:list', '/departments', 'DepartmentManagement', 'OfficeBuilding', 2, 1, 1, NULL, 0),

  (13, 3, '资产信息管理', 'MENU', 'asset:info:list', '/assets', 'AssetManagement', 'List', 1, 1, 1, NULL, 0),
  (14, 3, '资产分类管理', 'MENU', 'asset:category:list', '/categories', 'CategoryManagement', 'Files', 2, 1, 1, NULL, 0),
  (15, 3, '流转记录', 'MENU', 'asset:record:list', '/records', 'RecordManagement', 'Document', 3, 1, 1, NULL, 0),

  (16, 6, '生命周期管理', 'MENU', 'lifecycle:list', '/lifecycle', 'LifecycleManagement', 'Clock', 1, 1, 1, NULL, 0),
  (17, 6, '盘点管理', 'MENU', 'inventory:list', '/inventory', 'InventoryManagement', 'Checked', 2, 1, 1, NULL, 0),
  (18, 6, '报修管理', 'MENU', 'repair:list', '/repair', 'RepairManagement', 'Tools', 3, 1, 1, NULL, 0),

  (20, 8, '新增角色', 'BUTTON', 'system:role:add', NULL, NULL, NULL, 1, 1, 1, NULL, 0),
  (21, 8, '编辑角色', 'BUTTON', 'system:role:edit', NULL, NULL, NULL, 2, 1, 1, NULL, 0),
  (22, 8, '删除角色', 'BUTTON', 'system:role:delete', NULL, NULL, NULL, 3, 1, 1, NULL, 0),
  (23, 8, '分配权限', 'BUTTON', 'system:role:permission', NULL, NULL, NULL, 4, 1, 1, NULL, 0),
  (24, 8, '查看角色', 'BUTTON', 'system:role:view', NULL, NULL, NULL, 5, 1, 1, NULL, 0),
  (25, 8, '更新状态', 'BUTTON', 'system:role:status', NULL, NULL, NULL, 6, 1, 1, NULL, 0),

  (37, 11, '新增权限', 'BUTTON', 'system:permission:add', NULL, NULL, NULL, 1, 1, 1, NULL, 0),
  (38, 11, '编辑权限', 'BUTTON', 'system:permission:edit', NULL, NULL, NULL, 2, 1, 1, NULL, 0),
  (39, 11, '删除权限', 'BUTTON', 'system:permission:delete', NULL, NULL, NULL, 3, 1, 1, NULL, 0),

  (26, 9, '新增用户', 'BUTTON', 'system:user:add', NULL, NULL, NULL, 1, 1, 1, NULL, 0),
  (27, 9, '编辑用户', 'BUTTON', 'system:user:edit', NULL, NULL, NULL, 2, 1, 1, NULL, 0),
  (28, 9, '删除用户', 'BUTTON', 'system:user:delete', NULL, NULL, NULL, 3, 1, 1, NULL, 0),
  (29, 9, '分配角色', 'BUTTON', 'system:user:assign', NULL, NULL, NULL, 4, 1, 1, NULL, 0),
  (30, 9, '查看用户', 'BUTTON', 'system:user:view', NULL, NULL, NULL, 5, 1, 1, NULL, 0),
  (31, 9, '更新状态', 'BUTTON', 'system:user:status', NULL, NULL, NULL, 6, 1, 1, NULL, 0),
  (32, 9, '重置密码', 'BUTTON', 'system:user:reset', NULL, NULL, NULL, 7, 1, 1, NULL, 0),

  (33, 10, '新增部门', 'BUTTON', 'system:department:add', NULL, NULL, NULL, 1, 1, 1, NULL, 0),
  (34, 10, '编辑部门', 'BUTTON', 'system:department:edit', NULL, NULL, NULL, 2, 1, 1, NULL, 0),
  (35, 10, '删除部门', 'BUTTON', 'system:department:delete', NULL, NULL, NULL, 3, 1, 1, NULL, 0),
  (36, 10, '查看部门', 'BUTTON', 'system:department:view', NULL, NULL, NULL, 4, 1, 1, NULL, 0),

  (47, 13, '编辑资产', 'BUTTON', 'asset:info:edit', NULL, NULL, NULL, 2, 1, 1, NULL, 0),
  (49, 13, '查看资产', 'BUTTON', 'asset:info:view', NULL, NULL, NULL, 4, 1, 1, NULL, 0),
  (51, 13, '资产入库', 'BUTTON', 'asset:record:in', NULL, NULL, NULL, 6, 1, 1, NULL, 0),
  (52, 13, '分配资产', 'BUTTON', 'asset:record:allocate', NULL, NULL, NULL, 7, 1, 1, NULL, 0),
  (53, 13, '调拨资产', 'BUTTON', 'asset:record:transfer', NULL, NULL, NULL, 8, 1, 1, NULL, 0),
  (54, 13, '归还资产', 'BUTTON', 'asset:record:return', NULL, NULL, NULL, 9, 1, 1, NULL, 0),
  (55, 13, '报废资产', 'BUTTON', 'asset:record:scrap', NULL, NULL, NULL, 10, 1, 1, NULL, 0),
  (56, 13, '送修资产', 'BUTTON', 'asset:record:repair', NULL, NULL, NULL, 11, 1, 1, NULL, 0),
  (58, 13, '查看流转历史', 'BUTTON', 'asset:record:history', NULL, NULL, NULL, 13, 1, 1, NULL, 0),

  (59, 14, '新增分类', 'BUTTON', 'asset:category:add', NULL, NULL, NULL, 1, 1, 1, NULL, 0),
  (60, 14, '编辑分类', 'BUTTON', 'asset:category:edit', NULL, NULL, NULL, 2, 1, 1, NULL, 0),
  (61, 14, '删除分类', 'BUTTON', 'asset:category:delete', NULL, NULL, NULL, 3, 1, 1, NULL, 0),
  (62, 14, '查看分类', 'BUTTON', 'asset:category:view', NULL, NULL, NULL, 4, 1, 1, NULL, 0),

  (63, 16, '创建生命周期', 'BUTTON', 'lifecycle:create', NULL, NULL, NULL, 1, 1, 1, NULL, 0),
  (64, 16, '查询历史', 'BUTTON', 'lifecycle:history', NULL, NULL, NULL, 2, 1, 1, NULL, 0),
  (65, 16, '当前阶段', 'BUTTON', 'lifecycle:current', NULL, NULL, NULL, 3, 1, 1, NULL, 0),

  (67, 17, '创建盘点', 'BUTTON', 'inventory:create', NULL, NULL, NULL, 1, 1, 1, NULL, 0),
  (68, 17, '开始盘点', 'BUTTON', 'inventory:start', NULL, NULL, NULL, 2, 1, 1, NULL, 0),
  (69, 17, '执行盘点', 'BUTTON', 'inventory:execute', NULL, NULL, NULL, 3, 1, 1, NULL, 0),
  (70, 17, '完成盘点', 'BUTTON', 'inventory:complete', NULL, NULL, NULL, 4, 1, 1, NULL, 0),
  (71, 17, '取消盘点', 'BUTTON', 'inventory:cancel', NULL, NULL, NULL, 5, 1, 1, NULL, 0),
  (72, 17, '查看盘点', 'BUTTON', 'inventory:view', NULL, NULL, NULL, 6, 1, 1, NULL, 0),

  (73, 18, '创建报修', 'BUTTON', 'repair:create', NULL, NULL, NULL, 1, 1, 1, NULL, 0),
  (74, 18, '审批报修', 'BUTTON', 'repair:approve', NULL, NULL, NULL, 2, 1, 1, NULL, 0),
  (75, 18, '开始维修', 'BUTTON', 'repair:start', NULL, NULL, NULL, 3, 1, 1, NULL, 0),
  (76, 18, '完成维修', 'BUTTON', 'repair:complete', NULL, NULL, NULL, 4, 1, 1, NULL, 0),
  (77, 18, '查看报修', 'BUTTON', 'repair:view', NULL, NULL, NULL, 5, 1, 1, NULL, 0),

  (78, 4, '新建采购', 'BUTTON', 'purchase:create', NULL, NULL, NULL, 1, 1, 1, NULL, 0),
  (79, 4, '查看采购', 'BUTTON', 'purchase:view', NULL, NULL, NULL, 2, 1, 1, NULL, 0),
  (80, 4, '取消采购', 'BUTTON', 'purchase:cancel', NULL, NULL, NULL, 3, 1, 1, NULL, 0),

  (89, 4, '账单列表', 'BUTTON', 'finance:bill:list', NULL, NULL, NULL, 6, 1, 1, NULL, 0),
  (90, 4, '生成账单', 'BUTTON', 'finance:bill:generate', NULL, NULL, NULL, 7, 1, 1, NULL, 0),
  (91, 4, '确认账单', 'BUTTON', 'finance:bill:confirm', NULL, NULL, NULL, 8, 1, 1, NULL, 0),
  (92, 4, '删除账单', 'BUTTON', 'finance:bill:delete', NULL, NULL, NULL, 9, 1, 1, NULL, 0),
  (93, 4, '查看账单', 'BUTTON', 'finance:bill:view', NULL, NULL, NULL, 10, 1, 1, NULL, 0),
  (94, 4, '资金统计', 'BUTTON', 'finance:statistics:view', NULL, NULL, NULL, 11, 1, 1, NULL, 0)
ON DUPLICATE KEY UPDATE
  parent_id = VALUES(parent_id),
  menu_name = VALUES(menu_name),
  menu_type = VALUES(menu_type),
  permission_code = VALUES(permission_code),
  path = VALUES(path),
  component = VALUES(component),
  icon = VALUES(icon),
  sort_order = VALUES(sort_order),
  status = VALUES(status),
  visible = VALUES(visible),
  remark = VALUES(remark),
  deleted = VALUES(deleted);

-- 3) 为 ADMIN 角色分配全部权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT r.id, m.id
FROM sys_role r
JOIN sys_menu m ON m.deleted = 0
WHERE r.role_code = 'ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_menu rm
    WHERE rm.role_id = r.id AND rm.menu_id = m.id
  );

-- 4) 管理员用户（user_id=1）绑定 ADMIN 角色
INSERT INTO sys_user_role (user_id, role_id)
SELECT 1, r.id
FROM sys_role r
WHERE r.role_code = 'ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM sys_user_role ur
    WHERE ur.user_id = 1 AND ur.role_id = r.id
  );
