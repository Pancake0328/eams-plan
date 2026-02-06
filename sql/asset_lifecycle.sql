-- ============================================
-- 资产生命周期与盘点模块数据库脚本
-- ============================================

-- 1. 资产生命周期记录表
DROP TABLE IF EXISTS `asset_lifecycle`;
CREATE TABLE `asset_lifecycle` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `asset_id` BIGINT NOT NULL COMMENT '资产ID',
    `from_department_id` BIGINT DEFAULT NULL COMMENT '变更前部门ID',
    `from_department` VARCHAR(100) DEFAULT NULL COMMENT '变更前部门',
    `to_department_id` BIGINT DEFAULT NULL COMMENT '变更后部门ID',
    `to_department` VARCHAR(100) DEFAULT NULL COMMENT '变更后部门',
    `from_custodian` VARCHAR(50) DEFAULT NULL COMMENT '变更前责任人',
    `to_custodian` VARCHAR(50) DEFAULT NULL COMMENT '变更后责任人',
    `stage` INT NOT NULL COMMENT '生命周期阶段：1-购入 2-使用中 3-维修中 4-闲置 5-报废 6-取消采购',
    `previous_stage` INT DEFAULT NULL COMMENT '上一阶段',
    `stage_date` DATE NOT NULL COMMENT '阶段变更日期',
    `reason` VARCHAR(500) DEFAULT NULL COMMENT '变更原因',
    `operator` VARCHAR(50) NOT NULL COMMENT '操作人',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_asset_id` (`asset_id`),
    KEY `idx_stage` (`stage`),
    KEY `idx_stage_date` (`stage_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产生命周期记录表';

-- 2. 资产盘点计划表
DROP TABLE IF EXISTS `asset_inventory`;
CREATE TABLE `asset_inventory` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `inventory_number` VARCHAR(50) NOT NULL COMMENT '盘点编号',
    `inventory_name` VARCHAR(100) NOT NULL COMMENT '盘点名称',
    `inventory_type` INT NOT NULL COMMENT '盘点类型：1-全面盘点 2-抽样盘点 3-专项盘点',
    `plan_start_date` DATE NOT NULL COMMENT '计划开始日期',
    `plan_end_date` DATE NOT NULL COMMENT '计划结束日期',
    `actual_start_date` DATE DEFAULT NULL COMMENT '实际开始日期',
    `actual_end_date` DATE DEFAULT NULL COMMENT '实际结束日期',
    `inventory_status` INT NOT NULL DEFAULT 1 COMMENT '盘点状态：1-计划中 2-进行中 3-已完成 4-已取消',
    `total_count` INT DEFAULT 0 COMMENT '应盘资产数量',
    `actual_count` INT DEFAULT 0 COMMENT '实盘资产数量',
    `normal_count` INT DEFAULT 0 COMMENT '正常资产数量',
    `abnormal_count` INT DEFAULT 0 COMMENT '异常资产数量',
    `creator` VARCHAR(50) NOT NULL COMMENT '创建人',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_inventory_number` (`inventory_number`),
    KEY `idx_inventory_status` (`inventory_status`),
    KEY `idx_plan_date` (`plan_start_date`, `plan_end_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产盘点计划表';

-- 3. 资产盘点明细表
DROP TABLE IF EXISTS `asset_inventory_detail`;
CREATE TABLE `asset_inventory_detail` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `inventory_id` BIGINT NOT NULL COMMENT '盘点计划ID',
    `asset_id` BIGINT NOT NULL COMMENT '资产ID',
    `asset_number` VARCHAR(50) NOT NULL COMMENT '资产编号',
    `asset_name` VARCHAR(100) NOT NULL COMMENT '资产名称',
    `expected_location` VARCHAR(100) DEFAULT NULL COMMENT '预期位置',
    `actual_location` VARCHAR(100) DEFAULT NULL COMMENT '实际位置',
    `inventory_result` INT NOT NULL DEFAULT 1 COMMENT '盘点结果：1-未盘点 2-正常 3-位置异常 4-状态异常 5-丢失',
    `inventory_person` VARCHAR(50) DEFAULT NULL COMMENT '盘点人',
    `inventory_time` DATETIME DEFAULT NULL COMMENT '盘点时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_inventory_id` (`inventory_id`),
    KEY `idx_asset_id` (`asset_id`),
    KEY `idx_inventory_result` (`inventory_result`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产盘点明细表';

-- 4. 资产报修记录表
DROP TABLE IF EXISTS `asset_repair`;
CREATE TABLE `asset_repair` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `repair_number` VARCHAR(50) NOT NULL COMMENT '报修编号',
    `asset_id` BIGINT NOT NULL COMMENT '资产ID',
    `asset_number` VARCHAR(50) NOT NULL COMMENT '资产编号',
    `asset_name` VARCHAR(100) NOT NULL COMMENT '资产名称',
    `fault_description` VARCHAR(1000) NOT NULL COMMENT '故障描述',
    `repair_type` INT NOT NULL COMMENT '报修类型：1-日常维修 2-故障维修 3-预防性维修',
    `repair_priority` INT NOT NULL DEFAULT 2 COMMENT '优先级：1-紧急 2-普通 3-低',
    `reporter` VARCHAR(50) NOT NULL COMMENT '报修人',
    `report_time` DATETIME NOT NULL COMMENT '报修时间',
    `repair_status` INT NOT NULL DEFAULT 1 COMMENT '报修状态：1-待审批 2-已审批 3-维修中 4-已完成 5-已拒绝',
    `approver` VARCHAR(50) DEFAULT NULL COMMENT '审批人',
    `approval_time` DATETIME DEFAULT NULL COMMENT '审批时间',
    `repair_person` VARCHAR(50) DEFAULT NULL COMMENT '维修人',
    `repair_start_time` DATETIME DEFAULT NULL COMMENT '维修开始时间',
    `repair_end_time` DATETIME DEFAULT NULL COMMENT '维修完成时间',
    `repair_cost` DECIMAL(10,2) DEFAULT 0.00 COMMENT '维修费用',
    `repair_result` VARCHAR(500) DEFAULT NULL COMMENT '维修结果',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_repair_number` (`repair_number`),
    KEY `idx_asset_id` (`asset_id`),
    KEY `idx_repair_status` (`repair_status`),
    KEY `idx_report_time` (`report_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产报修记录表';

-- ============================================
-- 示例数据
-- ============================================

-- 生命周期记录示例
INSERT INTO `asset_lifecycle` (`asset_id`, `stage`, `previous_stage`, `stage_date`, `reason`, `operator`, `remark`)
VALUES 
(1, 1, NULL, '2025-01-15', '资产购入', 'admin', '新购入资产'),
(1, 2, 1, '2025-01-20', '投入使用', 'admin', '分配给技术部使用'),
(2, 1, NULL, '2025-02-01', '资产购入', 'admin', '新购入资产'),
(2, 2, 1, '2025-02-05', '投入使用', 'admin', '分配给行政部使用');

-- 盘点计划示例
INSERT INTO `asset_inventory` (`inventory_number`, `inventory_name`, `inventory_type`, `plan_start_date`, `plan_end_date`, `inventory_status`, `creator`)
VALUES 
('INV-202601-001', '2026年第一季度资产盘点', 1, '2026-01-01', '2026-01-07', 3, 'admin'),
('INV-202601-002', '办公设备专项盘点', 3, '2026-01-15', '2026-01-20', 1, 'admin');

-- 盘点明细示例
INSERT INTO `asset_inventory_detail` (`inventory_id`, `asset_id`, `asset_number`, `asset_name`, `expected_location`, `actual_location`, `inventory_result`, `inventory_person`, `inventory_time`)
VALUES 
(1, 1, 'ASSET-2025-0001', '联想ThinkPad笔记本', '研发部-A座-301', '研发部-A座-301', 2, 'admin', '2026-01-02 10:30:00'),
(1, 2, 'ASSET-2025-0002', '戴尔显示器', '行政部-B座-201', '行政部-B座-201', 2, 'admin', '2026-01-02 11:00:00');

-- 报修记录示例
INSERT INTO `asset_repair` (`repair_number`, `asset_id`, `asset_number`, `asset_name`, `fault_description`, `repair_type`, `repair_priority`, `reporter`, `report_time`, `repair_status`)
VALUES 
('REP-202601-001', 1, 'ASSET-2025-0001', '联想ThinkPad笔记本', '电池续航时间明显下降', 2, 2, 'user01', '2026-01-03 09:30:00', 1),
('REP-202601-002', 2, 'ASSET-2025-0002', '戴尔显示器', '屏幕闪烁', 2, 1, 'user02', '2026-01-03 10:15:00', 2);
