-- 采购管理优化 - 数据库迁移脚本
-- 修改申请人字段从文本改为用户ID

-- 1. 添加新的 applicant_id 字段
ALTER TABLE asset_purchase_order ADD COLUMN applicant_id BIGINT COMMENT '申请人用户ID';

-- 2. 如果需要，可以尝试迁移已有数据（根据实际情况调整）
-- UPDATE asset_purchase_order SET applicant_id = (
--     SELECT id FROM sys_user WHERE username = applicant LIMIT 1
-- ) WHERE applicant IS NOT NULL;

-- 3. 删除旧的 applicant 字段（可选，建议先备份数据）
-- ALTER TABLE asset_purchase_order DROP COLUMN applicant;

-- 4. 添加外键约束（可选）
-- ALTER TABLE asset_purchase_order ADD CONSTRAINT fk_purchase_applicant
--     FOREIGN KEY (applicant_id) REFERENCES sys_user(id) ON DELETE SET NULL;

-- 5. 创建索引
CREATE INDEX idx_purchase_applicant ON asset_purchase_order(applicant_id);

-- 注意：执行前请备份数据库！
-- 建议分步执行，先添加新字段，迁移数据后再删除旧字段
