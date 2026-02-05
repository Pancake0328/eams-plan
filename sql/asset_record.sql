-- ============================
-- 资产流转记录表
-- ============================
DROP TABLE IF EXISTS asset_record;

CREATE TABLE asset_record (
    id BIGINT AUTO_INCREMENT COMMENT '记录ID',
    asset_id BIGINT NOT NULL COMMENT '资产ID',
    record_type TINYINT NOT NULL COMMENT '记录类型：1-入库，2-分配，3-调拨，4-归还，5-报废，6-送修，7-维修完成',
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
