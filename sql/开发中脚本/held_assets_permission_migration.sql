-- 持有资产权限迁移脚本
-- 目的：
-- 1) 新增“持有资产”菜单（asset:info:my:list）
-- 2) 普通用户改为“持有资产”能力，移除“资产信息管理”全量能力
-- 3) 同步用户管理路由为 /user

UPDATE sys_menu
SET path = '/user'
WHERE id = 9;

INSERT INTO sys_menu (
    id, parent_id, menu_name, menu_type, permission_code, path, component, icon,
    sort_order, status, visible, remark, deleted
) VALUES (
    95, 3, '持有资产', 'MENU', 'asset:info:my:list', '/my-assets', 'MyAssetManagement', 'List',
    2, 1, 1, '普通用户持有资产列表', 0
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

DELETE rm
FROM sys_role_menu rm
JOIN sys_role r ON r.id = rm.role_id
WHERE r.role_code = 'USER'
  AND rm.menu_id IN (13, 47, 51, 52, 53, 55);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT r.id, m.id
FROM sys_role r
JOIN sys_menu m ON m.id IN (3, 95, 49, 54, 56, 58)
WHERE r.role_code = 'USER'
  AND NOT EXISTS (
      SELECT 1
      FROM sys_role_menu rm
      WHERE rm.role_id = r.id
        AND rm.menu_id = m.id
  );

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT r.id, 95
FROM sys_role r
WHERE r.role_code IN ('ADMIN', 'ASSET_MANAGER')
  AND NOT EXISTS (
      SELECT 1
      FROM sys_role_menu rm
      WHERE rm.role_id = r.id
        AND rm.menu_id = 95
  );
