-- ================================================================
-- 账单与资金管理模块数据库表
-- ================================================================

-- 1. 资产采购记录表
DROP TABLE IF EXISTS `asset_purchase`;
CREATE TABLE `asset_purchase` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '采购记录ID',
  `asset_id` BIGINT NOT NULL COMMENT '资产ID',
  `purchase_amount` DECIMAL(15,2) NOT NULL COMMENT '采购金额',
  `supplier_name` VARCHAR(200) DEFAULT NULL COMMENT '供应商名称',
  `supplier_contact` VARCHAR(100) DEFAULT NULL COMMENT '供应商联系方式',
  `invoice_number` VARCHAR(100) DEFAULT NULL COMMENT '发票号',
  `purchase_date` DATE NOT NULL COMMENT '采购日期',
  `payment_method` VARCHAR(50) DEFAULT NULL COMMENT '付款方式',
  `warranty_period` INT DEFAULT NULL COMMENT '质保期（月）',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `purchaser` VARCHAR(100) DEFAULT NULL COMMENT '采购人',
  `approval_status` TINYINT DEFAULT 0 COMMENT '审批状态：0-待审批，1-已审批，2-已拒绝',
  `approver` VARCHAR(100) DEFAULT NULL COMMENT '审批人',
  `approval_time` DATETIME DEFAULT NULL COMMENT '审批时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_asset_id` (`asset_id`),
  KEY `idx_purchase_date` (`purchase_date`),
  KEY `idx_invoice_number` (`invoice_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产采购记录表';

-- 2. 账单表
DROP TABLE IF EXISTS `asset_bill`;
CREATE TABLE `asset_bill` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '账单ID',
  `bill_number` VARCHAR(50) NOT NULL COMMENT '账单编号',
  `bill_type` TINYINT NOT NULL COMMENT '账单类型：1-月度账单，2-年度账单',
  `bill_year` INT NOT NULL COMMENT '账单年份',
  `bill_month` INT DEFAULT NULL COMMENT '账单月份',
  `total_purchase_amount` DECIMAL(15,2) DEFAULT 0.00 COMMENT '采购总金额',
  `total_depreciation_amount` DECIMAL(15,2) DEFAULT 0.00 COMMENT '折旧总金额',
  `total_asset_value` DECIMAL(15,2) DEFAULT 0.00 COMMENT '资产总价值',
  `total_net_value` DECIMAL(15,2) DEFAULT 0.00 COMMENT '资产净值总额',
  `bill_status` TINYINT DEFAULT 0 COMMENT '账单状态：0-草稿，1-已生成，2-已确认',
  `generate_time` DATETIME DEFAULT NULL COMMENT '生成时间',
  `confirm_time` DATETIME DEFAULT NULL COMMENT '确认时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bill_number` (`bill_number`),
  KEY `idx_bill_year_month` (`bill_year`, `bill_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产账单表';

-- 3. 账单明细表
DROP TABLE IF EXISTS `asset_bill_detail`;
CREATE TABLE `asset_bill_detail` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `bill_id` BIGINT NOT NULL COMMENT '账单ID',
  `asset_id` BIGINT NOT NULL COMMENT '资产ID',
  `asset_number` VARCHAR(50) DEFAULT NULL COMMENT '资产编号',
  `asset_name` VARCHAR(200) DEFAULT NULL COMMENT '资产名称',
  `category_name` VARCHAR(100) DEFAULT NULL COMMENT '分类名称',
  `department` VARCHAR(100) DEFAULT NULL COMMENT '使用部门',
  `purchase_amount` DECIMAL(15,2) DEFAULT 0.00 COMMENT '采购金额',
  `accumulated_depreciation` DECIMAL(15,2) DEFAULT 0.00 COMMENT '累计折旧',
  `current_depreciation` DECIMAL(15,2) DEFAULT 0.00 COMMENT '本期折旧',
  `net_value` DECIMAL(15,2) DEFAULT 0.00 COMMENT '净值',
  `depreciation_rate` DECIMAL(5,4) DEFAULT NULL COMMENT '折旧率',
  `useful_life` INT DEFAULT NULL COMMENT '使用年限（月）',
  `used_months` INT DEFAULT 0 COMMENT '已使用月数',
  `asset_status` TINYINT DEFAULT NULL COMMENT '资产状态',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_bill_id` (`bill_id`),
  KEY `idx_asset_id` (`asset_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产账单明细表';

-- 初始化数据（可选）
-- 插入示例采购记录
INSERT INTO `asset_purchase` (`asset_id`, `purchase_amount`, `supplier_name`, `invoice_number`, `purchase_date`, `purchaser`, `approval_status`) 
VALUES 
(1, 5000.00, '联想电脑公司', 'INV-2024-001', '2024-01-15', '张三', 1),
(2, 8000.00, '戴尔科技', 'INV-2024-002', '2024-02-20', '李四', 1);
