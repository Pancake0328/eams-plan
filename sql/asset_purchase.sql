-- ============================================
-- 资产采购管理表
-- ============================================

-- 采购单表
DROP TABLE IF EXISTS asset_purchase_order;
CREATE TABLE asset_purchase_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '采购单ID',
    purchase_number VARCHAR(50) NOT NULL UNIQUE COMMENT '采购单号',
    purchase_date DATE NOT NULL COMMENT '采购日期',
    supplier VARCHAR(200) COMMENT '供应商',
    total_amount DECIMAL(15,2) DEFAULT 0 COMMENT '采购总金额',
    purchase_status INT DEFAULT 1 COMMENT '采购状态：1-待入库，2-部分入库，3-已入库，4-已取消',
    applicant VARCHAR(100) COMMENT '申请人',
    approver VARCHAR(100) COMMENT '审批人',
    approve_time DATETIME COMMENT '审批时间',
    remark TEXT COMMENT '备注',
    deleted TINYINT(1) DEFAULT 0 COMMENT '删除标记',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_purchase_number (purchase_number),
    INDEX idx_purchase_status (purchase_status),
    INDEX idx_purchase_date (purchase_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产采购单表';

-- 采购明细表
DROP TABLE IF EXISTS asset_purchase_detail;
CREATE TABLE asset_purchase_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '明细ID',
    purchase_id BIGINT NOT NULL COMMENT '采购单ID',
    asset_name VARCHAR(200) NOT NULL COMMENT '资产名称',
    category_id BIGINT COMMENT '分类ID',
    specifications VARCHAR(500) COMMENT '规格型号',
    manufacturer VARCHAR(200) COMMENT '生产厂商',
    unit_price DECIMAL(15,2) COMMENT '单价',
    quantity INT DEFAULT 1 COMMENT '采购数量',
    inbound_quantity INT DEFAULT 0 COMMENT '已入库数量',
    total_amount DECIMAL(15,2) COMMENT '小计金额',
    expected_life INT COMMENT '预计使用年限',
    remark VARCHAR(500) COMMENT '备注',
    detail_status INT DEFAULT 1 COMMENT '明细状态：1-待入库，2-已入库',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_purchase_id (purchase_id),
    INDEX idx_detail_status (detail_status),
    FOREIGN KEY (purchase_id) REFERENCES asset_purchase_order(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产采购明细表';

-- 初始化示例数据
INSERT INTO asset_purchase_order (purchase_number, purchase_date, supplier, total_amount, purchase_status, applicant) VALUES
('PUR-2024-001', '2024-01-15', '联想集团', 30000.00, 1, 'admin'),
('PUR-2024-002', '2024-02-20', '戴尔科技', 45000.00, 2, 'admin');

INSERT INTO asset_purchase_detail (purchase_id, asset_name, category_id, specifications, manufacturer, unit_price, quantity, total_amount, expected_life) VALUES
(1, '联想ThinkPad笔记本', 1, 'T14 Gen3 i7-16G-512G', '联想', 6000.00, 5, 30000.00, 5),
(2, '戴尔显示器', 2, 'U2720Q 27英寸4K', '戴尔', 3000.00, 10, 30000.00, 8),
(2, '戴尔台式机', 1, 'OptiPlex 7090 i5-16G-512G', '戴尔', 5000.00, 3, 15000.00, 5);
