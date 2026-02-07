-- ============================================
-- RBAC 权限管理系统 - 数据库表结构
-- ============================================

-- 1. 角色表
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码（唯一）',
    role_name VARCHAR(100) NOT NULL COMMENT '角色名称',
    status TINYINT(1) DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    remark VARCHAR(500) COMMENT '备注说明',
    deleted TINYINT(1) DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_role_code (role_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

-- 2. 用户角色关联表
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 3. 菜单权限表
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '菜单ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID，0表示顶级菜单',
    menu_name VARCHAR(100) NOT NULL COMMENT '菜单名称',
    menu_type VARCHAR(20) NOT NULL COMMENT '菜单类型：DIR-目录，MENU-菜单，BUTTON-按钮',
    permission_code VARCHAR(200) COMMENT '权限标识（如：system:user:add）',
    path VARCHAR(200) COMMENT '路由路径',
    component VARCHAR(200) COMMENT '组件路径',
    icon VARCHAR(100) COMMENT '菜单图标',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    status TINYINT(1) DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    visible TINYINT(1) DEFAULT 1 COMMENT '是否可见：1-可见，0-隐藏',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT(1) DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_parent_id (parent_id),
    INDEX idx_menu_type (menu_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

-- 4. 角色菜单关联表
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_role_menu (role_id, menu_id),
    INDEX idx_role_id (role_id),
    INDEX idx_menu_id (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- ============================================
-- 初始化数据
-- ============================================

-- 插入默认角色
INSERT INTO sys_role (role_code, role_name, status, remark) VALUES
('ADMIN', '系统管理员', 1, '拥有系统所有权限'),
('USER', '普通用户', 1, '普通业务用户'),
('ASSET_MANAGER', '资产管理员', 1, '负责资产管理相关功能');

-- 插入菜单数据（目录、菜单、按钮）
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, permission_code, path, component, icon, sort_order) VALUES
-- 顶级菜单
(1, 0, '仪表盘', 'MENU', 'dashboard:view', '/dashboard', 'Dashboard', 'DataLine', 1),
(2, 0, '系统管理', 'DIR', NULL, '/system', NULL, 'Setting', 2),
(3, 0, '资产管理', 'DIR', NULL, '/asset', NULL, 'Grid', 3),
(4, 0, '采购管理', 'MENU', 'purchase:list', '/purchase', 'PurchaseManagement', 'ShoppingCart', 4),
(5, 0, '人员管理', 'DIR', NULL, '/personnel', NULL, 'User', 5),
(6, 0, '生命周期与盘点', 'DIR', NULL, '/lifecycle', NULL, 'Refresh', 6),

-- 系统管理子菜单
(8, 2, '角色管理', 'MENU', 'system:role:list', '/role', 'RoleManagement', 'UserFilled', 1),

-- 人员管理子菜单
(9, 5, '用户管理', 'MENU', 'system:user:list', '/', 'UserManagement', 'User', 1),
(10, 5, '部门管理', 'MENU', 'system:department:list', '/departments', 'DepartmentManagement', 'OfficeBuilding', 2),

-- 资产管理子菜单
(13, 3, '资产信息管理', 'MENU', 'asset:info:list', '/assets', 'AssetManagement', 'List', 1),
(14, 3, '资产分类管理', 'MENU', 'asset:category:list', '/categories', 'CategoryManagement', 'Files', 2),
(15, 3, '流转记录', 'MENU', 'asset:record:list', '/records', 'RecordManagement', 'Document', 3),

-- 生命周期与盘点子菜单
(16, 6, '生命周期管理', 'MENU', 'lifecycle:list', '/lifecycle', 'LifecycleManagement', 'Clock', 1),
(17, 6, '盘点管理', 'MENU', 'inventory:list', '/inventory', 'InventoryManagement', 'Checked', 2),
(18, 6, '报修管理', 'MENU', 'repair:list', '/repair', 'RepairManagement', 'Tools', 3),

-- 角色管理按钮权限
(20, 8, '新增角色', 'BUTTON', 'system:role:add', NULL, NULL, NULL, 1),
(21, 8, '编辑角色', 'BUTTON', 'system:role:edit', NULL, NULL, NULL, 2),
(22, 8, '删除角色', 'BUTTON', 'system:role:delete', NULL, NULL, NULL, 3),
(23, 8, '分配权限', 'BUTTON', 'system:role:permission', NULL, NULL, NULL, 4),
(25, 8, '更新状态', 'BUTTON', 'system:role:status', NULL, NULL, NULL, 6),

-- 用户管理按钮权限
(26, 9, '新增用户', 'BUTTON', 'system:user:add', NULL, NULL, NULL, 1),
(27, 9, '编辑用户', 'BUTTON', 'system:user:edit', NULL, NULL, NULL, 2),
(28, 9, '删除用户', 'BUTTON', 'system:user:delete', NULL, NULL, NULL, 3),
(29, 9, '分配角色', 'BUTTON', 'system:user:assign', NULL, NULL, NULL, 4),
(30, 9, '查看用户', 'BUTTON', 'system:user:view', NULL, NULL, NULL, 5),
(31, 9, '更新状态', 'BUTTON', 'system:user:status', NULL, NULL, NULL, 6),
(32, 9, '重置密码', 'BUTTON', 'system:user:reset', NULL, NULL, NULL, 7),

-- 部门管理按钮权限
(33, 10, '新增部门', 'BUTTON', 'system:department:add', NULL, NULL, NULL, 1),
(34, 10, '编辑部门', 'BUTTON', 'system:department:edit', NULL, NULL, NULL, 2),
(35, 10, '删除部门', 'BUTTON', 'system:department:delete', NULL, NULL, NULL, 3),
(36, 10, '查看部门', 'BUTTON', 'system:department:view', NULL, NULL, NULL, 4),

-- 资产信息按钮权限
(47, 13, '编辑资产', 'BUTTON', 'asset:info:edit', NULL, NULL, NULL, 2),
(49, 13, '查看资产', 'BUTTON', 'asset:info:view', NULL, NULL, NULL, 4),
(51, 13, '资产入库', 'BUTTON', 'asset:record:in', NULL, NULL, NULL, 6),
(52, 13, '分配资产', 'BUTTON', 'asset:record:allocate', NULL, NULL, NULL, 7),
(53, 13, '调拨资产', 'BUTTON', 'asset:record:transfer', NULL, NULL, NULL, 8),
(54, 13, '归还资产', 'BUTTON', 'asset:record:return', NULL, NULL, NULL, 9),
(55, 13, '报废资产', 'BUTTON', 'asset:record:scrap', NULL, NULL, NULL, 10),
(56, 13, '送修资产', 'BUTTON', 'asset:record:repair', NULL, NULL, NULL, 11),
(58, 13, '查看流转历史', 'BUTTON', 'asset:record:history', NULL, NULL, NULL, 13),

-- 资产分类按钮权限
(59, 14, '新增分类', 'BUTTON', 'asset:category:add', NULL, NULL, NULL, 1),
(60, 14, '编辑分类', 'BUTTON', 'asset:category:edit', NULL, NULL, NULL, 2),
(61, 14, '删除分类', 'BUTTON', 'asset:category:delete', NULL, NULL, NULL, 3),
(62, 14, '查看分类', 'BUTTON', 'asset:category:view', NULL, NULL, NULL, 4),

-- 生命周期按钮权限
(63, 16, '创建生命周期', 'BUTTON', 'lifecycle:create', NULL, NULL, NULL, 1),
(64, 16, '查询历史', 'BUTTON', 'lifecycle:history', NULL, NULL, NULL, 2),
(65, 16, '当前阶段', 'BUTTON', 'lifecycle:current', NULL, NULL, NULL, 3),

-- 盘点按钮权限
(67, 17, '创建盘点', 'BUTTON', 'inventory:create', NULL, NULL, NULL, 1),
(68, 17, '开始盘点', 'BUTTON', 'inventory:start', NULL, NULL, NULL, 2),
(69, 17, '执行盘点', 'BUTTON', 'inventory:execute', NULL, NULL, NULL, 3),
(70, 17, '完成盘点', 'BUTTON', 'inventory:complete', NULL, NULL, NULL, 4),
(71, 17, '取消盘点', 'BUTTON', 'inventory:cancel', NULL, NULL, NULL, 5),
(72, 17, '查看盘点', 'BUTTON', 'inventory:view', NULL, NULL, NULL, 6),

-- 报修按钮权限
(73, 18, '创建报修', 'BUTTON', 'repair:create', NULL, NULL, NULL, 1),
(74, 18, '审批报修', 'BUTTON', 'repair:approve', NULL, NULL, NULL, 2),
(75, 18, '开始维修', 'BUTTON', 'repair:start', NULL, NULL, NULL, 3),
(76, 18, '完成维修', 'BUTTON', 'repair:complete', NULL, NULL, NULL, 4),
(77, 18, '查看报修', 'BUTTON', 'repair:view', NULL, NULL, NULL, 5),

-- 采购管理按钮权限
(78, 4, '新建采购', 'BUTTON', 'purchase:create', NULL, NULL, NULL, 1),
(79, 4, '查看采购', 'BUTTON', 'purchase:view', NULL, NULL, NULL, 2),
(80, 4, '取消采购', 'BUTTON', 'purchase:cancel', NULL, NULL, NULL, 3),

-- 账单与资金统计权限（挂载在采购管理下）
(89, 4, '账单列表', 'BUTTON', 'finance:bill:list', NULL, NULL, NULL, 6),
(90, 4, '生成账单', 'BUTTON', 'finance:bill:generate', NULL, NULL, NULL, 7),
(91, 4, '确认账单', 'BUTTON', 'finance:bill:confirm', NULL, NULL, NULL, 8),
(92, 4, '删除账单', 'BUTTON', 'finance:bill:delete', NULL, NULL, NULL, 9),
(93, 4, '查看账单', 'BUTTON', 'finance:bill:view', NULL, NULL, NULL, 10),
(94, 4, '资金统计', 'BUTTON', 'finance:statistics:view', NULL, NULL, NULL, 11);

-- 为管理员角色分配所有权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu WHERE deleted = 0;

-- 为普通用户分配部分权限（仅查看权限）
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 2, id FROM sys_menu WHERE menu_type IN ('DIR', 'MENU') AND deleted = 0;
