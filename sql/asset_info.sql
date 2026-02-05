-- ============================
-- 资产信息表
-- ============================
DROP TABLE IF EXISTS asset_info;

CREATE TABLE asset_info (
    id BIGINT AUTO_INCREMENT COMMENT '资产ID',
    asset_number VARCHAR(50) NOT NULL COMMENT '资产编号（唯一）',
    asset_name VARCHAR(100) NOT NULL COMMENT '资产名称',
    category_id BIGINT NOT NULL COMMENT '资产分类ID',
    purchase_amount DECIMAL(15,2) COMMENT '采购金额',
    purchase_date DATE COMMENT '采购日期',
    department_id BIGINT COMMENT '使用部门ID',
    custodian VARCHAR(50) COMMENT '责任人',
    asset_status TINYINT NOT NULL DEFAULT 1 COMMENT '资产状态：1-闲置，2-使用中，3-维修中，4-报废',
    specifications TEXT COMMENT '规格型号',
    manufacturer VARCHAR(100) COMMENT '生产厂商',
    remark TEXT COMMENT '备注',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_asset_number (asset_number),
    KEY idx_category_id (category_id),
    KEY idx_asset_status (asset_status),
    KEY idx_department_id (department_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产信息表';

-- ============================
-- 资产编号序列表（用于生成唯一编号）
-- ============================
DROP TABLE IF EXISTS asset_number_sequence;

CREATE TABLE asset_number_sequence (
    id BIGINT AUTO_INCREMENT COMMENT '序列ID',
    prefix VARCHAR(20) NOT NULL COMMENT '编号前缀',
    current_number INT NOT NULL DEFAULT 0 COMMENT '当前序号',
    date_part VARCHAR(10) COMMENT '日期部分（可选）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_prefix_date (prefix, date_part)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产编号序列表';

-- 插入初始序列记录
INSERT INTO asset_number_sequence (prefix, current_number, date_part) VALUES
('ZC', 0, NULL);
