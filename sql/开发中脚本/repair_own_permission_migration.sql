-- 普通用户报修权限迁移脚本
-- 目标：
-- 1) 新增“我的报修”菜单（repair:own:list）
-- 2) 普通用户移除“报修管理(全量)”能力，切换为“我的报修”

INSERT INTO sys_menu (
    id, parent_id, menu_name, menu_type, permission_code, path, component, icon,
    sort_order, status, visible, remark, deleted
) VALUES (
    96, 6, '我的报修', 'MENU', 'repair:own:list', '/my-repairs', 'RepairManagement', 'Tools',
    4, 1, 1, '普通用户仅查看自己的报修', 0
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
  AND rm.menu_id IN (18, 74, 75, 76);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT r.id, m.id
FROM sys_role r
JOIN sys_menu m ON m.id IN (6, 96)
WHERE r.role_code = 'USER'
  AND NOT EXISTS (
      SELECT 1
      FROM sys_role_menu rm
      WHERE rm.role_id = r.id
        AND rm.menu_id = m.id
  );

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT r.id, 96
FROM sys_role r
WHERE r.role_code IN ('ADMIN', 'ASSET_MANAGER')
  AND NOT EXISTS (
      SELECT 1
      FROM sys_role_menu rm
      WHERE rm.role_id = r.id
        AND rm.menu_id = 96
  );
