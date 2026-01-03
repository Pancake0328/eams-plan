-- ============================
-- 资产分类表
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

-- 插入初始分类数据
INSERT INTO asset_category (parent_id, category_name, category_code, sort_order, description) VALUES
(0, '办公设备', 'OFFICE', 1, '办公相关设备'),
(1, '电脑设备', 'COMPUTER', 1, '各类电脑设备'),
(2, '台式机', 'DESKTOP', 1, '台式电脑'),
(2, '笔记本', 'LAPTOP', 2, '笔记本电脑'),
(1, '办公家具', 'FURNITURE', 2, '办公桌椅等家具'),
(0, '生产设备', 'PRODUCTION', 2, '生产相关设备'),
(6, '机械设备', 'MACHINERY', 1, '各类机械设备'),
(6, '电子设备', 'ELECTRONIC', 2, '各类电子设备');
