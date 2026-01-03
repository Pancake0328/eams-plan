-- ============================================
-- 系统通知表
-- ============================================
DROP TABLE IF EXISTS sys_notify;

CREATE TABLE sys_notify (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '通知ID',
    notify_type INT NOT NULL COMMENT '通知类型：1-系统通知，2-到期提醒，3-报修提醒，4-盘点提醒',
    notify_level INT NOT NULL DEFAULT 1 COMMENT '通知级别：1-普通，2-重要，3-紧急',
    title VARCHAR(200) NOT NULL COMMENT '通知标题',
    content TEXT COMMENT '通知内容',
    receiver VARCHAR(100) COMMENT '接收人（用户名或角色）',
    receiver_type INT DEFAULT 1 COMMENT '接收人类型：1-用户，2-角色，3-全体',
    is_read TINYINT(1) DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
    read_time DATETIME COMMENT '已读时间',
    related_id BIGINT COMMENT '关联业务ID',
    related_type VARCHAR(50) COMMENT '关联业务类型：asset-资产，repair-报修，inventory-盘点',
    send_time DATETIME COMMENT '发送时间',
    expire_time DATETIME COMMENT '过期时间',
    deleted TINYINT(1) DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_receiver (receiver),
    INDEX idx_is_read (is_read),
    INDEX idx_notify_type (notify_type),
    INDEX idx_send_time (send_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统通知表';

-- 初始化数据
INSERT INTO sys_notify (notify_type, notify_level, title, content, receiver, receiver_type, send_time) VALUES
(1, 1, '系统维护通知', '系统将于本周六晚上22:00-24:00进行维护，期间将无法访问，请提前安排工作。', 'admin', 3, NOW()),
(2, 2, '资产折旧到期提醒', '资产编号AST-2024-001的折旧即将到期，请及时处理。', 'admin', 1, NOW()),
(3, 3, '紧急报修提醒', '资产编号AST-2024-002有紧急报修单待审批，请尽快处理。', 'admin', 1, NOW());
