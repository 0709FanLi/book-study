-- 创建数据库
CREATE DATABASE `study_room_db` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `study_room_db`;

-- 用户表 (整合了学生和管理员，用role区分)
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password` VARCHAR(100) NOT NULL,
  `student_id` VARCHAR(20) UNIQUE, -- 学号
  `full_name` VARCHAR(50),         -- 姓名
  `role` VARCHAR(20) NOT NULL,     -- 角色: 'ROLE_USER' for student, 'ROLE_ADMIN' for admin
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 公告表
CREATE TABLE `announcement` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(255) NOT NULL,
  `content` TEXT NOT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 座位表
CREATE TABLE `seat` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `seat_number` VARCHAR(10) NOT NULL UNIQUE,
  `is_available` BOOLEAN NOT NULL DEFAULT TRUE,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 预约记录表
CREATE TABLE `reservation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `seat_id` BIGINT NOT NULL,
  `start_time` DATETIME NOT NULL,
  `end_time` DATETIME NOT NULL,
  `status` VARCHAR(20) NOT NULL, -- 'ACTIVE', 'CANCELLED'
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`),
  FOREIGN KEY (`seat_id`) REFERENCES `seat`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 投诉建议表
CREATE TABLE `complaint` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `content` TEXT NOT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入初始数据
-- 插入管理员 (密码是 123456)
-- BCrypt encoded for '123456': $2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07c209chCoSzOAoFSG
INSERT INTO `user` (`username`, `password`, `full_name`, `role`)
VALUES ('admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07c209chCoSzOAoFSG', '系统管理员', 'ROLE_ADMIN');

-- 插入一个学生 (密码是 123456)
INSERT INTO `user` (`username`, `password`, `student_id`, `full_name`, `role`)
VALUES ('student1', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07c209chCoSzOAoFSG', '2024001', '张三', 'ROLE_USER');

-- 插入一些座位
INSERT INTO `seat` (`seat_number`) VALUES 
('A01'), ('A02'), ('A03'), ('A04'), ('A05'), 
('B01'), ('B02'), ('B03'), ('B04'), ('B05'),
('C01'), ('C02'), ('C03'), ('C04'), ('C05');

-- 插入一些示例公告
INSERT INTO `announcement` (`title`, `content`) VALUES 
('欢迎使用自习室管理系统', '本系统提供座位预约、公告查看、投诉建议等功能，请合理使用。'),
('自习室使用规则', '1. 请保持安静 2. 不得占座 3. 爱护公共设施 4. 按时使用预约的座位'); 