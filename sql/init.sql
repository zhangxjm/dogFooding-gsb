CREATE DATABASE IF NOT EXISTS meeting_room_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE meeting_room_db;

DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS meeting_room;
DROP TABLE IF EXISTS user;

CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    real_name VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    email VARCHAR(100),
    phone VARCHAR(20),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE meeting_room (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(200) NOT NULL,
    capacity INT NOT NULL DEFAULT 10,
    equipment VARCHAR(500),
    status TINYINT NOT NULL DEFAULT 1,
    description VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE reservation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    approve_user_id BIGINT,
    approve_time DATETIME,
    cancel_reason VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES meeting_room(id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (approve_user_id) REFERENCES user(id),
    INDEX idx_room_time (room_id, start_time, end_time),
    INDEX idx_user (user_id),
    INDEX idx_status (status)
);

INSERT INTO user (username, password, real_name, role, email, phone) VALUES
('admin', 'admin123', '系统管理员', 'ADMIN', 'admin@company.com', '13800000001'),
('zhangsan', '123456', '张三', 'USER', 'zhangsan@company.com', '13800000002'),
('lisi', '123456', '李四', 'USER', 'lisi@company.com', '13800000003'),
('wangwu', '123456', '王五', 'USER', 'wangwu@company.com', '13800000004');

INSERT INTO meeting_room (name, location, capacity, equipment, status, description) VALUES
('会议室A-101', 'A栋1楼', 10, '投影仪,白板,视频会议系统', 1, '小型会议室，适合日常站会'),
('会议室A-201', 'A栋2楼', 20, '投影仪,白板,视频会议系统,音响', 1, '中型会议室，适合部门会议'),
('会议室B-301', 'B栋3楼', 50, '投影仪,白板,视频会议系统,音响,麦克风', 1, '大型会议室，适合全员大会'),
('会议室B-102', 'B栋1楼', 8, '白板,电视', 1, '小型讨论室，适合头脑风暴'),
('会议室C-501', 'C栋5楼', 15, '投影仪,白板,视频会议系统', 0, '中型会议室，当前维护中');
