-- ============================================
-- EAMS 生产数据库表结构（不含示例数据）
-- 执行顺序：先建库/建表，再导入管理员与权限数据
-- ============================================

-- 可按需启用：
 CREATE DATABASE IF NOT EXISTS eams_plan DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
 USE eams;

-- ============================
-- 系统用户与部门
-- ============================
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '工号',
    password VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    nickname VARCHAR(50) COMMENT '姓名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像URL',
    department_id BIGINT COMMENT '所属部门ID',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    KEY idx_department_id (department_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

DROP TABLE IF EXISTS sys_dept;
CREATE TABLE sys_dept (
    id BIGINT AUTO_INCREMENT COMMENT '部门ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父部门ID，0表示顶级部门',
    dept_name VARCHAR(50) NOT NULL COMMENT '部门名称',
    dept_code VARCHAR(50) COMMENT '部门编码',
    leader VARCHAR(50) COMMENT '负责人',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    sort_order INT DEFAULT 0 COMMENT '显示顺序',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-停用',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_parent_id (parent_id),
    KEY idx_dept_code (dept_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- ============================
-- RBAC 权限表
-- ============================
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码（唯一）',
    role_name VARCHAR(100) NOT NULL COMMENT '角色名称',
    status TINYINT(1) DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    remark VARCHAR(500) COMMENT '备注说明',
    deleted TINYINT(1) DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_role_code (role_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '菜单ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID，0表示顶级菜单',
    menu_name VARCHAR(100) NOT NULL COMMENT '菜单名称',
    menu_type VARCHAR(20) NOT NULL COMMENT '菜单类型：DIR-目录，MENU-菜单，BUTTON-按钮',
    permission_code VARCHAR(200) COMMENT '权限标识（如：system:user:add）',
    path VARCHAR(200) COMMENT '路由路径',
    component VARCHAR(200) COMMENT '组件路径',
    icon VARCHAR(100) COMMENT '菜单图标',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    status TINYINT(1) DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    visible TINYINT(1) DEFAULT 1 COMMENT '是否可见：1-可见，0-隐藏',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT(1) DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_parent_id (parent_id),
    INDEX idx_menu_type (menu_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_role_menu (role_id, menu_id),
    INDEX idx_role_id (role_id),
    INDEX idx_menu_id (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- ============================
-- 资产管理
-- ============================
DROP TABLE IF EXISTS asset_category;
CREATE TABLE asset_category (
    id BIGINT AUTO_INCREMENT COMMENT '分类ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID，0表示顶级分类',
    category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
    category_code VARCHAR(50) COMMENT '分类编码',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    description VARCHAR(200) COMMENT '分类描述',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_parent_id (parent_id),
    UNIQUE KEY uk_category_code (category_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产分类表';

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

DROP TABLE IF EXISTS asset_info;
CREATE TABLE asset_info (
    id BIGINT AUTO_INCREMENT COMMENT '资产ID',
    asset_number VARCHAR(50) NOT NULL COMMENT '资产编号（唯一）',
    asset_name VARCHAR(100) NOT NULL COMMENT '资产名称',
    category_id BIGINT NOT NULL COMMENT '资产分类ID',
    purchase_detail_id BIGINT COMMENT '采购明细ID',
    purchase_amount DECIMAL(15,2) COMMENT '采购金额',
    purchase_date DATE COMMENT '采购日期',
    department_id BIGINT COMMENT '使用部门ID',
    custodian VARCHAR(50) COMMENT '责任人',
    asset_status TINYINT NOT NULL DEFAULT 1 COMMENT '资产状态：0-采购，1-闲置，2-使用中，3-维修中，4-报废，6-取消采购',
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

DROP TABLE IF EXISTS asset_record;
CREATE TABLE asset_record (
    id BIGINT AUTO_INCREMENT COMMENT '记录ID',
    asset_id BIGINT NOT NULL COMMENT '资产ID',
    record_type TINYINT NOT NULL COMMENT '记录类型：1-入库，2-分配，3-调拨，4-归还，5-报废，6-送修，7-维修完成，8-报修拒绝',
    from_department_id BIGINT COMMENT '原部门ID',
    from_department VARCHAR(100) COMMENT '原部门名称',
    to_department_id BIGINT COMMENT '目标部门ID',
    to_department VARCHAR(100) COMMENT '目标部门名称',
    from_custodian VARCHAR(50) COMMENT '原责任人',
    to_custodian VARCHAR(50) COMMENT '目标责任人',
    old_status TINYINT COMMENT '原状态',
    new_status TINYINT COMMENT '新状态',
    remark TEXT COMMENT '备注',
    operator VARCHAR(50) NOT NULL COMMENT '操作人',
    operate_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_asset_id (asset_id),
    KEY idx_record_type (record_type),
    KEY idx_operate_time (operate_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产流转记录表';

-- ============================
-- 采购管理
-- ============================
DROP TABLE IF EXISTS asset_purchase_order;
CREATE TABLE asset_purchase_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '采购单ID',
    purchase_number VARCHAR(50) NOT NULL UNIQUE COMMENT '采购单号',
    purchase_date DATE NOT NULL COMMENT '采购日期',
    supplier VARCHAR(200) COMMENT '供应商',
    total_amount DECIMAL(15,2) DEFAULT 0 COMMENT '采购总金额',
    purchase_status INT DEFAULT 1 COMMENT '采购状态：1-待入库，2-部分入库，3-已入库，4-已取消',
    applicant_id BIGINT COMMENT '申请人用户ID',
    approver VARCHAR(100) COMMENT '审批人',
    approve_time DATETIME COMMENT '审批时间',
    remark TEXT COMMENT '备注',
    deleted TINYINT(1) DEFAULT 0 COMMENT '删除标记',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_purchase_number (purchase_number),
    INDEX idx_purchase_status (purchase_status),
    INDEX idx_purchase_date (purchase_date),
    INDEX idx_purchase_applicant (applicant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产采购单表';

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
    INDEX idx_detail_status (detail_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产采购明细表';

-- ============================
-- 生命周期 / 盘点 / 报修
-- ============================
DROP TABLE IF EXISTS asset_lifecycle;
CREATE TABLE asset_lifecycle (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    asset_id BIGINT NOT NULL COMMENT '资产ID',
    from_department_id BIGINT DEFAULT NULL COMMENT '变更前部门ID',
    from_department VARCHAR(100) DEFAULT NULL COMMENT '变更前部门',
    to_department_id BIGINT DEFAULT NULL COMMENT '变更后部门ID',
    to_department VARCHAR(100) DEFAULT NULL COMMENT '变更后部门',
    from_custodian VARCHAR(50) DEFAULT NULL COMMENT '变更前责任人',
    to_custodian VARCHAR(50) DEFAULT NULL COMMENT '变更后责任人',
    stage INT NOT NULL COMMENT '生命周期阶段：1-购入 2-使用中 3-维修中 4-闲置 5-报废 6-取消采购',
    previous_stage INT DEFAULT NULL COMMENT '上一阶段',
    stage_date DATE NOT NULL COMMENT '阶段变更日期',
    reason VARCHAR(500) DEFAULT NULL COMMENT '变更原因',
    operator VARCHAR(50) NOT NULL COMMENT '操作人',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (id),
    KEY idx_asset_id (asset_id),
    KEY idx_stage (stage),
    KEY idx_stage_date (stage_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产生命周期记录表';

DROP TABLE IF EXISTS asset_inventory;
CREATE TABLE asset_inventory (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    inventory_number VARCHAR(50) NOT NULL COMMENT '盘点编号',
    inventory_name VARCHAR(100) NOT NULL COMMENT '盘点名称',
    inventory_type INT NOT NULL COMMENT '盘点类型：1-全面盘点 2-抽样盘点 3-专项盘点',
    sample_count INT DEFAULT NULL COMMENT '抽样数量',
    category_id BIGINT DEFAULT NULL COMMENT '专项盘点分类ID',
    plan_start_date DATE NOT NULL COMMENT '计划开始日期',
    plan_end_date DATE NOT NULL COMMENT '计划结束日期',
    actual_start_date DATE DEFAULT NULL COMMENT '实际开始日期',
    actual_end_date DATE DEFAULT NULL COMMENT '实际结束日期',
    inventory_status INT NOT NULL DEFAULT 1 COMMENT '盘点状态：1-计划中 2-进行中 3-已完成 4-已取消',
    total_count INT DEFAULT 0 COMMENT '应盘资产数量',
    actual_count INT DEFAULT 0 COMMENT '实盘资产数量',
    normal_count INT DEFAULT 0 COMMENT '正常资产数量',
    abnormal_count INT DEFAULT 0 COMMENT '异常资产数量',
    creator VARCHAR(50) NOT NULL COMMENT '创建人',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_inventory_number (inventory_number),
    KEY idx_inventory_status (inventory_status),
    KEY idx_plan_date (plan_start_date, plan_end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产盘点计划表';

DROP TABLE IF EXISTS asset_inventory_detail;
CREATE TABLE asset_inventory_detail (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    inventory_id BIGINT NOT NULL COMMENT '盘点计划ID',
    asset_id BIGINT NOT NULL COMMENT '资产ID',
    asset_number VARCHAR(50) NOT NULL COMMENT '资产编号',
    asset_name VARCHAR(100) NOT NULL COMMENT '资产名称',
    expected_location VARCHAR(100) DEFAULT NULL COMMENT '预期位置',
    actual_location VARCHAR(100) DEFAULT NULL COMMENT '实际位置',
    inventory_result INT NOT NULL DEFAULT 1 COMMENT '盘点结果：1-未盘点 2-正常 3-位置异常 4-状态异常 5-丢失',
    inventory_person VARCHAR(50) DEFAULT NULL COMMENT '盘点人',
    inventory_time DATETIME DEFAULT NULL COMMENT '盘点时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (id),
    KEY idx_inventory_id (inventory_id),
    KEY idx_asset_id (asset_id),
    KEY idx_inventory_result (inventory_result)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产盘点明细表';

DROP TABLE IF EXISTS asset_repair;
CREATE TABLE asset_repair (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    repair_number VARCHAR(50) NOT NULL COMMENT '报修编号',
    asset_id BIGINT NOT NULL COMMENT '资产ID',
    asset_number VARCHAR(50) NOT NULL COMMENT '资产编号',
    asset_name VARCHAR(100) NOT NULL COMMENT '资产名称',
    fault_description VARCHAR(1000) NOT NULL COMMENT '故障描述',
    repair_type INT NOT NULL COMMENT '报修类型：1-日常维修 2-故障维修 3-预防性维修',
    repair_priority INT NOT NULL DEFAULT 2 COMMENT '优先级：1-紧急 2-普通 3-低',
    reporter VARCHAR(50) NOT NULL COMMENT '报修人',
    report_time DATETIME NOT NULL COMMENT '报修时间',
    repair_status INT NOT NULL DEFAULT 1 COMMENT '报修状态：1-待审批 2-已审批 3-维修中 4-已完成 5-已拒绝',
    original_status TINYINT DEFAULT NULL COMMENT '报修前资产状态',
    approver VARCHAR(50) DEFAULT NULL COMMENT '审批人',
    approval_time DATETIME DEFAULT NULL COMMENT '审批时间',
    repair_person VARCHAR(50) DEFAULT NULL COMMENT '维修人',
    repair_start_time DATETIME DEFAULT NULL COMMENT '维修开始时间',
    repair_end_time DATETIME DEFAULT NULL COMMENT '维修完成时间',
    repair_cost DECIMAL(10,2) DEFAULT 0.00 COMMENT '维修费用',
    repair_result VARCHAR(500) DEFAULT NULL COMMENT '维修结果',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_repair_number (repair_number),
    KEY idx_asset_id (asset_id),
    KEY idx_repair_status (repair_status),
    KEY idx_report_time (report_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产报修记录表';
