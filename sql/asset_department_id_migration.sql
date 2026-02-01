-- 资产表部门字段迁移脚本
-- 将 department 字段从 VARCHAR 改为 department_id (BIGINT)

-- 1. 添加新的 department_id 字段
ALTER TABLE asset_info ADD COLUMN department_id BIGINT COMMENT '使用部门ID' AFTER purchase_date;

-- 2. 删除旧的 department 字段
ALTER TABLE asset_info DROP COLUMN department;

-- 说明：
-- 原 department 字段存储部门名称（VARCHAR）
-- 现改为 department_id 存储部门ID（BIGINT）
-- 与 sys_department 表关联
