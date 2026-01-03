-- ====================================================================
-- 部门表（树结构）
-- ====================================================================
CREATE TABLE IF NOT EXISTS sys_dept (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '部门ID',
    parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父部门ID，0表示顶级部门',
    dept_name VARCHAR(100) NOT NULL COMMENT '部门名称',
    dept_code VARCHAR(50) COMMENT '部门编码',
    leader VARCHAR(50) COMMENT '部门负责人',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    sort_order INT DEFAULT 0 COMMENT '显示顺序',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-停用',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    INDEX idx_parent_id (parent_id),
    INDEX idx_dept_code (dept_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- ====================================================================
-- 员工表
-- ====================================================================
CREATE TABLE IF NOT EXISTS sys_employee (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '员工ID',
    emp_no VARCHAR(50) NOT NULL COMMENT '员工工号',
    emp_name VARCHAR(100) NOT NULL COMMENT '员工姓名',
    dept_id BIGINT COMMENT '部门ID',
    position VARCHAR(100) COMMENT '职位',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    gender TINYINT COMMENT '性别：1-男，2-女',
    entry_date DATE COMMENT '入职日期',
    status TINYINT DEFAULT 1 COMMENT '状态：1-在职，0-离职',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    UNIQUE KEY uk_emp_no (emp_no),
    INDEX idx_dept_id (dept_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

-- ====================================================================
-- 资产分配记录表
-- ====================================================================
CREATE TABLE IF NOT EXISTS asset_assign_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    asset_id BIGINT NOT NULL COMMENT '资产ID',
    assign_type TINYINT NOT NULL COMMENT '分配类型：1-分配给员工，2-回收，3-部门内调拨',
    from_emp_id BIGINT COMMENT '原员工ID',
    to_emp_id BIGINT COMMENT '目标员工ID',
    from_dept_id BIGINT COMMENT '原部门ID',
    to_dept_id BIGINT COMMENT '目标部门ID',
    assign_date DATETIME NOT NULL COMMENT '分配日期',
    return_date DATETIME COMMENT '归还日期',
    remark VARCHAR(500) COMMENT '备注',
    operator VARCHAR(50) NOT NULL COMMENT '操作人',
    operate_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_asset_id (asset_id),
    INDEX idx_from_emp_id (from_emp_id),
    INDEX idx_to_emp_id (to_emp_id),
    INDEX idx_assign_type (assign_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产分配记录表';

-- 初始化部门数据
INSERT INTO sys_dept (id, parent_id, dept_name, dept_code, leader, sort_order, status) VALUES
(1, 0, '总公司', 'HEAD', NULL, 0, 1),
(2, 1, '技术部', 'TECH', NULL, 1, 1),
(3, 1, '财务部', 'FINANCE', NULL, 2, 1),
(4, 1, '人力资源部', 'HR', NULL, 3, 1);
