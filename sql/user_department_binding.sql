-- 用户部门绑定功能 - 数据库迁移脚本
-- 为 sys_user 表添加 department_id 字段

-- 添加部门ID字段
ALTER TABLE sys_user ADD COLUMN department_id BIGINT COMMENT '所属部门ID';

-- 添加外键约束（可选，如果需要严格的数据完整性）
-- ALTER TABLE sys_user ADD CONSTRAINT fk_user_department 
--     FOREIGN KEY (department_id) REFERENCES sys_department(id) ON DELETE SET NULL;

-- 为已存在的用户数据设置默认部门（可选，根据实际需求调整）
-- UPDATE sys_user SET department_id = 1 WHERE department_id IS NULL;

-- 创建索引以提高查询性能
CREATE INDEX idx_user_department ON sys_user(department_id);
