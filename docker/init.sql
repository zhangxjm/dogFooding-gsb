-- 初始化数据库
CREATE DATABASE IF NOT EXISTS meeting_room CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE meeting_room;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '电话',
    role ENUM('ADMIN', 'USER') DEFAULT 'USER' COMMENT '角色',
    status TINYINT DEFAULT 1 COMMENT '状态 1启用 0禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 会议室表
CREATE TABLE IF NOT EXISTS meeting_rooms (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '会议室名称',
    location VARCHAR(200) COMMENT '位置',
    capacity INT DEFAULT 10 COMMENT '容纳人数',
    facilities JSON COMMENT '设施配置',
    description TEXT COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态 1可用 0不可用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会议室表';

-- 预约记录表
CREATE TABLE IF NOT EXISTS reservations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_id BIGINT NOT NULL COMMENT '会议室ID',
    user_id BIGINT NOT NULL COMMENT '预约人ID',
    title VARCHAR(200) NOT NULL COMMENT '会议主题',
    attendees INT DEFAULT 2 COMMENT '参会人数',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    description TEXT COMMENT '会议描述',
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED', 'COMPLETED') DEFAULT 'PENDING' COMMENT '预约状态',
    approver_id BIGINT COMMENT '审批人ID',
    approval_time TIMESTAMP NULL COMMENT '审批时间',
    approval_remark VARCHAR(500) COMMENT '审批备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES meeting_rooms(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (approver_id) REFERENCES users(id),
    INDEX idx_room_id (room_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_start_time (start_time),
    INDEX idx_time_range (start_time, end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预约记录表';

-- 插入默认管理员 (密码: admin)
INSERT INTO users (username, password, real_name, email, role, status) VALUES
('admin', '$2a$10$4Adr/8nNI9FOkdZ24pIO1O6dhey9vpKrLTw8L7esWGLJTmOznfLwy', '系统管理员', 'admin@company.com', 'ADMIN', 1);

-- 插入默认用户 (密码: user)
INSERT INTO users (username, password, real_name, email, role, status) VALUES
('user', '$2a$10$FLEpiYIc128T/GkFft6lGudrfunYXsuSWel7.E7fzt4SUSjzsZwgK', '普通用户', 'user@company.com', 'USER', 1);

-- 插入示例会议室
INSERT INTO meeting_rooms (name, location, capacity, facilities, description, status) VALUES
('第一会议室', 'A栋3楼301', 20, '["投影仪", "白板", "音响"]', '配备高清投影仪和音响设备，适合中型会议', 1),
('第二会议室', 'A栋3楼302', 10, '["白板", "电视"]', '小型会议室，适合小组讨论', 1),
('第三会议室', 'A栋3楼303', 50, '["投影仪", "音响", "视频会议系统", "同声传译"]', '大型会议室，配备视频会议系统', 1),
('贵宾接待室', 'A栋3楼305', 8, '["茶歇", "沙发", "电视"]', '用于重要客户接待', 1);
