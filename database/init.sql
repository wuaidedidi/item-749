-- 洗衣店订单管理系统 数据库初始化脚本
-- 编码: UTF-8
SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS laundry_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE laundry_db;

-- =====================================================
-- 表: sys_user (系统用户)
-- =====================================================
DROP TABLE IF EXISTS order_detail;
DROP TABLE IF EXISTS laundry_order;
DROP TABLE IF EXISTS service_item;
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS sys_user;

CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录用户名',
    password VARCHAR(255) NOT NULL COMMENT '加密密码',
    real_name VARCHAR(100) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号码',
    email VARCHAR(100) COMMENT '电子邮箱',
    role TINYINT DEFAULT 0 COMMENT '角色: 0-员工, 1-管理员',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户';

-- =====================================================
-- 表: customer (客户)
-- =====================================================
CREATE TABLE customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '客户姓名',
    phone VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号码',
    address VARCHAR(255) COMMENT '地址',
    remark VARCHAR(500) COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_phone (phone),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户';

-- =====================================================
-- 表: service_item (服务项目)
-- =====================================================
CREATE TABLE service_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '服务名称',
    category VARCHAR(50) NOT NULL COMMENT '分类: wash-水洗, dry_clean-干洗, iron-熨烫, repair-修补',
    price DECIMAL(10,2) NOT NULL COMMENT '单价',
    unit VARCHAR(20) DEFAULT 'piece' COMMENT '单位: piece-件, kg-公斤, set-套',
    description VARCHAR(500) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务项目';

-- =====================================================
-- 表: laundry_order (订单)
-- =====================================================
CREATE TABLE laundry_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    user_id BIGINT NOT NULL COMMENT '操作员ID',
    total_amount DECIMAL(12,2) DEFAULT 0.00 COMMENT '总金额',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-待处理, 1-处理中, 2-待取件, 3-已完成, 4-已取消',
    receive_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收件时间',
    expect_time DATETIME COMMENT '预计完成时间',
    complete_time DATETIME COMMENT '实际完成时间',
    deliver_time DATETIME COMMENT '取件时间',
    remark VARCHAR(500) COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_order_no (order_no),
    INDEX idx_customer_id (customer_id),
    INDEX idx_status (status),
    INDEX idx_receive_time (receive_time),
    FOREIGN KEY (customer_id) REFERENCES customer(id),
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='洗衣订单';

-- =====================================================
-- 表: order_detail (订单明细)
-- =====================================================
CREATE TABLE order_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL COMMENT '订单ID',
    service_id BIGINT NOT NULL COMMENT '服务ID',
    service_name VARCHAR(100) COMMENT '服务名称快照',
    quantity INT DEFAULT 1 COMMENT '数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    subtotal DECIMAL(10,2) NOT NULL COMMENT '小计',
    remark VARCHAR(255) COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_order_id (order_id),
    FOREIGN KEY (order_id) REFERENCES laundry_order(id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES service_item(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单明细';

-- =====================================================
-- 初始数据: 管理员账号 (密码: 123456)
-- BCrypt 加密后的 "123456"
-- =====================================================
INSERT INTO sys_user (username, password, real_name, phone, email, role, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', '13800000000', 'admin@laundry.com', 1, 1),
('staff001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张三', '13800000001', 'zhangsan@laundry.com', 0, 1),
('staff002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '李四', '13800000002', 'lisi@laundry.com', 0, 1);

-- =====================================================
-- 初始数据: 客户
-- =====================================================
INSERT INTO customer (name, phone, address, remark) VALUES
('王先生', '15800001111', '北京市朝阳区建国路88号', 'VIP客户'),
('刘女士', '15800002222', '上海市浦东新区陆家嘴', '常客'),
('陈经理', '15800003333', '广州市天河区珠江新城', '企业账户'),
('赵小姐', '15800004444', '深圳市南山区科技园', NULL),
('周先生', '15800005555', '杭州市西湖区文三路', '偏好环保洗涤剂');

-- =====================================================
-- 初始数据: 服务项目
-- =====================================================
INSERT INTO service_item (name, category, price, unit, description, status) VALUES
-- 水洗服务
('水洗-衬衫', 'wash', 15.00, 'piece', '标准水洗衬衫', 1),
('水洗-裤子', 'wash', 18.00, 'piece', '标准水洗裤子', 1),
('水洗-连衣裙', 'wash', 25.00, 'piece', '标准水洗连衣裙', 1),
('水洗-按重量', 'wash', 30.00, 'kg', '按公斤计费的大件水洗', 1),
-- 干洗服务
('干洗-西装', 'dry_clean', 80.00, 'set', '专业西装干洗', 1),
('干洗-大衣', 'dry_clean', 60.00, 'piece', '大衣/外套干洗', 1),
('干洗-连衣裙', 'dry_clean', 50.00, 'piece', '精致连衣裙干洗', 1),
('干洗-真丝', 'dry_clean', 45.00, 'piece', '真丝衣物特殊护理', 1),
-- 熨烫服务
('熨烫-衬衫', 'iron', 8.00, 'piece', '专业衬衫熨烫', 1),
('熨烫-裤子', 'iron', 10.00, 'piece', '专业裤子熨烫', 1),
('熨烫-西装', 'iron', 25.00, 'set', '全套西装熨烫', 1),
-- 修补服务
('更换纽扣', 'repair', 5.00, 'piece', '更换缺失或损坏的纽扣', 1),
('修理拉链', 'repair', 20.00, 'piece', '修复或更换损坏的拉链', 1),
('修改衣长', 'repair', 30.00, 'piece', '调整衣物长度', 1);

-- =====================================================
-- 初始数据: 示例订单
-- =====================================================
INSERT INTO laundry_order (order_no, customer_id, user_id, total_amount, status, receive_time, expect_time, remark) VALUES
('ORD20260124001', 1, 1, 145.00, 3, '2026-01-20 10:00:00', '2026-01-22 18:00:00', '需要加急处理'),
('ORD20260124002', 2, 2, 80.00, 2, '2026-01-21 14:30:00', '2026-01-24 18:00:00', NULL),
('ORD20260124003', 3, 1, 230.00, 1, '2026-01-22 09:00:00', '2026-01-25 18:00:00', '企业订单'),
('ORD20260124004', 4, 2, 55.00, 0, '2026-01-24 08:00:00', '2026-01-27 18:00:00', NULL),
('ORD20260124005', 1, 1, 165.00, 2, '2026-01-23 11:00:00', '2026-01-26 18:00:00', '小心处理');

-- 订单明细 ORD20260124001
INSERT INTO order_detail (order_id, service_id, service_name, quantity, unit_price, subtotal, remark) VALUES
(1, 5, '干洗-西装', 1, 80.00, 80.00, NULL),
(1, 1, '水洗-衬衫', 3, 15.00, 45.00, '白衬衫'),
(1, 13, '修理拉链', 1, 20.00, 20.00, '夹克拉链');

-- 订单明细 ORD20260124002
INSERT INTO order_detail (order_id, service_id, service_name, quantity, unit_price, subtotal) VALUES
(2, 5, '干洗-西装', 1, 80.00, 80.00);

-- 订单明细 ORD20260124003
INSERT INTO order_detail (order_id, service_id, service_name, quantity, unit_price, subtotal) VALUES
(3, 5, '干洗-西装', 2, 80.00, 160.00),
(3, 6, '干洗-大衣', 1, 60.00, 60.00),
(3, 9, '熨烫-衬衫', 1, 8.00, 8.00);

-- 订单明细 ORD20260124004
INSERT INTO order_detail (order_id, service_id, service_name, quantity, unit_price, subtotal) VALUES
(4, 1, '水洗-衬衫', 2, 15.00, 30.00),
(4, 3, '水洗-连衣裙', 1, 25.00, 25.00);

-- 订单明细 ORD20260124005
INSERT INTO order_detail (order_id, service_id, service_name, quantity, unit_price, subtotal) VALUES
(5, 7, '干洗-连衣裙', 2, 50.00, 100.00),
(5, 8, '干洗-真丝', 1, 45.00, 45.00),
(5, 13, '修理拉链', 1, 20.00, 20.00);
