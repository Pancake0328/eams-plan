-- 菜单与 RBAC 结构调整脚本（2026-03-20）
-- 目标结构：
-- 1. 仪表盘
-- 2. 系统管理：部门管理，角色管理，菜单管理，用户管理
-- 3. 资产管理：全部资产，持有资产，资产分类，流转记录
-- 4. 采购管理
-- 5. 报修管理：全部报修，我的报修
-- 6. 盘点管理
-- 7. 资产生命周期
--
-- 同时补齐：
-- - 将“报修管理”调整为目录，原权限迁移到“全部报修”
-- - 确保 admin / 系统管理员 / 资产管理员拥有新增菜单

START TRANSACTION;

-- 兼容历史：系统管理用户菜单路由从 / 修正为 /user
UPDATE sys_menu
SET path = '/user'
WHERE id = 9;

-- 统一文案
UPDATE sys_menu SET menu_name = '全部资产' WHERE id = 13;
UPDATE sys_menu SET menu_name = '资产分类' WHERE id = 14;
UPDATE sys_menu SET menu_name = '资产生命周期' WHERE id = 16;
UPDATE sys_menu SET menu_name = '全部报修' WHERE id = 18;
UPDATE sys_menu SET menu_name = '用户管理', sort_order = 3 WHERE id = 9;
UPDATE sys_menu SET menu_name = '角色管理', sort_order = 2 WHERE id = 8;
UPDATE sys_menu SET menu_name = '菜单管理', sort_order = 4 WHERE id = 11;
UPDATE sys_menu SET menu_name = '部门管理', sort_order = 1 WHERE id = 10;

-- 顶层菜单顺序与层级对齐（盘点、资产生命周期独立为顶层）
UPDATE sys_menu SET sort_order = 1 WHERE id = 1;
UPDATE sys_menu SET sort_order = 2 WHERE id = 2;
UPDATE sys_menu SET sort_order = 3 WHERE id = 3;
UPDATE sys_menu SET sort_order = 4 WHERE id = 4;
UPDATE sys_menu SET sort_order = 5 WHERE id = 18;
UPDATE sys_menu
SET parent_id = 0,
    menu_name = '盘点管理',
    menu_type = 'MENU',
    path = '/inventory',
    component = 'InventoryManagement',
    sort_order = 6
WHERE id = 17;
UPDATE sys_menu
SET parent_id = 0,
    menu_name = '资产生命周期',
    menu_type = 'MENU',
    path = '/lifecycle',
    component = 'LifecycleManagement',
    sort_order = 7
WHERE id = 16;

-- 旧目录“生命周期与盘点”隐藏（保留数据以兼容历史）
UPDATE sys_menu
SET status = 0,
    visible = 0
WHERE id = 6;

-- 报修管理从顶级菜单改为目录（便于挂载“全部报修 / 我的报修”）
UPDATE sys_menu
SET menu_type = 'DIR',
    permission_code = NULL,
    path = '/repair-management',
    component = NULL
WHERE id = 18;

-- 新建“全部报修”菜单（承载 repair:list）
INSERT INTO sys_menu (
    id, parent_id, menu_name, menu_type, permission_code, path, component, icon,
    sort_order, status, visible, remark, deleted
) VALUES (
    97, 18, '全部报修', 'MENU', 'repair:list', '/repair', 'RepairManagement', 'Tools',
    1, 1, 1, '报修管理-全部报修', 0
)
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

-- 新建/修正“我的报修”
INSERT INTO sys_menu (
    id, parent_id, menu_name, menu_type, permission_code, path, component, icon,
    sort_order, status, visible, remark, deleted
) VALUES (
    96, 18, '我的报修', 'MENU', 'repair:own:list', '/my-repairs', 'RepairManagement', 'Tools',
    2, 1, 1, '报修管理-我的报修', 0
)
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

-- 新建/修正“持有资产”
INSERT INTO sys_menu (
    id, parent_id, menu_name, menu_type, permission_code, path, component, icon,
    sort_order, status, visible, remark, deleted
) VALUES (
    95, 3, '持有资产', 'MENU', 'asset:info:my:list', '/my-assets', 'MyAssetManagement', 'List',
    2, 1, 1, '资产管理-持有资产', 0
)
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

-- 修正历史数据中角色直接授权旧“报修管理菜单(repair:list)”的情况，切换为“全部报修(97)”
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, 97
FROM sys_role_menu rm
WHERE rm.menu_id = 18
  AND NOT EXISTS (
      SELECT 1
      FROM sys_role_menu x
      WHERE x.role_id = rm.role_id
        AND x.menu_id = 97
  );

-- 普通用户：移除全量报修按钮能力，保留“我的报修”
DELETE rm
FROM sys_role_menu rm
JOIN sys_role r ON r.id = rm.role_id
WHERE r.role_code = 'USER'
  AND rm.menu_id IN (74, 75, 76, 97);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT r.id, m.id
FROM sys_role r
JOIN sys_menu m ON m.id IN (18, 96)
WHERE r.role_code = 'USER'
  AND NOT EXISTS (
      SELECT 1
      FROM sys_role_menu rm
      WHERE rm.role_id = r.id
        AND rm.menu_id = m.id
  );

-- 普通用户：持有资产替代全量资产菜单
DELETE rm
FROM sys_role_menu rm
JOIN sys_role r ON r.id = rm.role_id
WHERE r.role_code = 'USER'
  AND rm.menu_id IN (13, 47, 51, 52, 53, 55, 56);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT r.id, m.id
FROM sys_role r
JOIN sys_menu m ON m.id IN (3, 95, 49, 54, 58)
WHERE r.role_code = 'USER'
  AND NOT EXISTS (
      SELECT 1
      FROM sys_role_menu rm
      WHERE rm.role_id = r.id
        AND rm.menu_id = m.id
  );

-- 管理角色补齐“持有资产 / 全部报修 / 我的报修”
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT r.id, m.id
FROM sys_role r
JOIN sys_menu m ON m.id IN (95, 97, 96)
WHERE r.role_code IN ('ADMIN', 'ADMIN-SYS', 'ASSET_MANAGER')
  AND NOT EXISTS (
      SELECT 1
      FROM sys_role_menu rm
      WHERE rm.role_id = r.id
        AND rm.menu_id = m.id
  );

COMMIT;
