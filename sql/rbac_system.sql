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
INSERT INTO sys_menu (parent_id, menu_name, menu_type, permission_code, path, component, icon, sort_order) VALUES
-- 一级目录
(0, '系统管理', 'DIR', NULL, '/system', NULL, 'Setting', 1),
(0, '资产管理', 'DIR', NULL, '/asset', NULL, 'Grid', 2),
(0, '财务管理', 'DIR', NULL, '/finance', NULL, 'Money', 3),

-- 系统管理子菜单
(1, '用户管理', 'MENU', 'system:user:list', '/system/user', 'UserManagement', 'User', 1),
(1, '角色管理', 'MENU', 'system:role:list', '/system/role', 'RoleManagement', 'UserFilled', 2),
(1, '菜单管理', 'MENU', 'system:menu:list', '/system/menu', 'MenuManagement', 'Menu', 3),

-- 用户管理按钮权限
(4, '新增用户', 'BUTTON', 'system:user:add', NULL, NULL, NULL, 1),
(4, '编辑用户', 'BUTTON', 'system:user:edit', NULL, NULL, NULL, 2),
(4, '删除用户', 'BUTTON', 'system:user:delete', NULL, NULL, NULL, 3),
(4, '分配角色', 'BUTTON', 'system:user:assign', NULL, NULL, NULL, 4),

-- 角色管理按钮权限
(5, '新增角色', 'BUTTON', 'system:role:add', NULL, NULL, NULL, 1),
(5, '编辑角色', 'BUTTON', 'system:role:edit', NULL, NULL, NULL, 2),
(5, '删除角色', 'BUTTON', 'system:role:delete', NULL, NULL, NULL, 3),
(5, '分配权限', 'BUTTON', 'system:role:permission', NULL, NULL, NULL, 4),

-- 资产管理子菜单
(2, '资产信息', 'MENU', 'asset:info:list', '/asset/info', 'AssetManagement', 'List', 1),
(2, '资产分类', 'MENU', 'asset:category:list', '/asset/category', 'CategoryManagement', 'Files', 2),

-- 资产信息按钮权限
(16, '新增资产', 'BUTTON', 'asset:info:add', NULL, NULL, NULL, 1),
(16, '编辑资产', 'BUTTON', 'asset:info:edit', NULL, NULL, NULL, 2),
(16, '删除资产', 'BUTTON', 'asset:info:delete', NULL, NULL, NULL, 3),
(16, '导出资产', 'BUTTON', 'asset:info:export', NULL, NULL, NULL, 4);

-- 为管理员角色分配所有权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu WHERE deleted = 0;

-- 为普通用户分配部分权限（仅查看权限）
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 2, id FROM sys_menu WHERE menu_type IN ('DIR', 'MENU') AND deleted = 0;
