-- 清理现有数据和更新表结构
SET FOREIGN_KEY_CHECKS = 0;

-- 清空现有数据
DELETE FROM reservation;
DELETE FROM complaint;
DELETE FROM announcement;
DELETE FROM user;
DELETE FROM seat;

SET FOREIGN_KEY_CHECKS = 1;

-- 更新reservation表结构，添加续约和退座相关字段
ALTER TABLE reservation 
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN IF NOT EXISTS last_extended_at TIMESTAMP NULL,
ADD COLUMN IF NOT EXISTS extension_count INT DEFAULT 0,
ADD COLUMN IF NOT EXISTS actual_end_time TIMESTAMP NULL,
ADD COLUMN IF NOT EXISTS remarks VARCHAR(255) NULL;

-- 插入管理员用户
INSERT INTO user (username, password, full_name, role)
VALUES ('admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07c209chCoSzOAoFSG', '系统管理员', 'ROLE_ADMIN');

-- 插入测试学生用户
INSERT INTO user (username, password, student_id, full_name, role) VALUES
('student1', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07c209chCoSzOAoFSG', '2024001', '张三', 'ROLE_USER'),
('student2', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07c209chCoSzOAoFSG', '2024002', '李四', 'ROLE_USER'),
('student3', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07c209chCoSzOAoFSG', '2024003', '王五', 'ROLE_USER'),
('student4', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07c209chCoSzOAoFSG', '2024004', '赵六', 'ROLE_USER'),
('student5', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07c209chCoSzOAoFSG', '2024005', '钱七', 'ROLE_USER'),
('student6', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07c209chCoSzOAoFSG', '2024006', '孙八', 'ROLE_USER');

-- 插入座位数据
INSERT INTO seat (seat_number) VALUES 
('A01'), ('A02'), ('A03'), ('A04'), ('A05'),
('B01'), ('B02'), ('B03'), ('B04'), ('B05'),
('C01'), ('C02'), ('C03'), ('C04'), ('C05');

-- 插入样本预约数据（包含续约和退座功能的各种状态）
INSERT INTO reservation (user_id, seat_id, start_time, end_time, status, created_at, extension_count, last_extended_at, actual_end_time, remarks) VALUES

-- 当前活跃预约（可以测试续约和退座）
(2, 1, '2025-06-12 08:00:00', '2025-06-12 10:00:00', 'ACTIVE', '2025-06-12 07:30:00', 0, NULL, NULL, '准备期末考试'),
(3, 2, '2025-06-12 09:00:00', '2025-06-12 11:00:00', 'ACTIVE', '2025-06-12 08:45:00', 0, NULL, NULL, '复习高等数学'),
(4, 3, '2025-06-12 14:00:00', '2025-06-12 16:00:00', 'ACTIVE', '2025-06-12 13:30:00', 0, NULL, NULL, '自主学习'),

-- 已续约的预约（显示续约功能效果）
(2, 4, '2025-06-11 08:00:00', '2025-06-11 12:00:00', 'EXTENDED', '2025-06-11 07:30:00', 2, '2025-06-11 11:30:00', NULL, '毕业论文写作'),
(3, 5, '2025-06-11 14:00:00', '2025-06-11 17:00:00', 'EXTENDED', '2025-06-11 13:45:00', 1, '2025-06-11 16:30:00', NULL, '英语四级复习'),

-- 已完成的预约（通过退座功能完成）
(2, 1, '2025-06-10 08:00:00', '2025-06-10 12:00:00', 'COMPLETED', '2025-06-10 07:45:00', 1, '2025-06-10 11:30:00', '2025-06-10 11:45:00', '计算机网络实验'),
(3, 2, '2025-06-10 14:00:00', '2025-06-10 18:00:00', 'COMPLETED', '2025-06-10 13:30:00', 0, NULL, '2025-06-10 17:30:00', '数据结构课程设计'),
(4, 3, '2025-06-09 09:00:00', '2025-06-09 11:00:00', 'COMPLETED', '2025-06-09 08:30:00', 0, NULL, '2025-06-09 10:45:00', '软件工程项目'),
(5, 6, '2025-06-09 15:00:00', '2025-06-09 17:00:00', 'COMPLETED', '2025-06-09 14:45:00', 2, '2025-06-09 16:30:00', '2025-06-09 16:50:00', '操作系统复习'),

-- 已取消的预约
(2, 7, '2025-06-08 08:00:00', '2025-06-08 10:00:00', 'CANCELLED', '2025-06-08 07:30:00', 0, NULL, NULL, '临时有事'),
(6, 8, '2025-06-08 14:00:00', '2025-06-08 16:00:00', 'CANCELLED', '2025-06-08 13:45:00', 0, NULL, NULL, '课程冲突'),

-- 历史数据（更早的预约记录）
(2, 1, '2025-06-07 08:00:00', '2025-06-07 10:00:00', 'COMPLETED', '2025-06-07 07:30:00', 0, NULL, '2025-06-07 09:55:00', '算法与数据结构'),
(3, 5, '2025-06-07 14:00:00', '2025-06-07 16:00:00', 'COMPLETED', '2025-06-07 13:30:00', 1, '2025-06-07 15:30:00', '2025-06-07 16:15:00', '数据库系统'),
(4, 9, '2025-06-06 10:00:00', '2025-06-06 12:00:00', 'COMPLETED', '2025-06-06 09:45:00', 0, NULL, '2025-06-06 11:30:00', '编译原理'),
(5, 10, '2025-06-06 15:00:00', '2025-06-06 17:00:00', 'CANCELLED', '2025-06-06 14:30:00', 0, NULL, NULL, '身体不适'),

-- 更多历史预约数据用于测试
(6, 11, '2025-06-05 09:00:00', '2025-06-05 11:00:00', 'COMPLETED', '2025-06-05 08:30:00', 1, '2025-06-05 10:30:00', '2025-06-05 11:05:00', '计算机组成原理'),
(2, 12, '2025-06-05 14:00:00', '2025-06-05 16:00:00', 'COMPLETED', '2025-06-05 13:45:00', 0, NULL, '2025-06-05 15:45:00', '软件测试'),
(3, 13, '2025-06-04 08:00:00', '2025-06-04 10:00:00', 'COMPLETED', '2025-06-04 07:30:00', 2, '2025-06-04 09:30:00', '2025-06-04 10:20:00', '人工智能导论'),
(4, 14, '2025-06-04 14:00:00', '2025-06-04 16:00:00', 'CANCELLED', '2025-06-04 13:30:00', 0, NULL, NULL, '临时加课'),
(5, 15, '2025-06-03 10:00:00', '2025-06-03 12:00:00', 'COMPLETED', '2025-06-03 09:45:00', 0, NULL, '2025-06-03 11:50:00', '机器学习基础');

-- 插入系统公告
INSERT INTO announcement (title, content, created_at) VALUES
('续约与退座功能上线通知', '亲爱的同学们，系统新增了预约续约和提前退座功能：\n\n1. 续约功能：在预约结束前30分钟内可申请续约，每次最多续约2小时，每个预约最多续约3次\n2. 退座功能：在预约开始后可随时申请退座，系统会记录实际使用时长\n3. 每位用户每天最多可进行5次续约操作\n\n请合理使用座位资源，为其他同学让出更多学习机会！', '2025-06-12 00:00:00'),
('自习室使用规范', '为营造良好的学习环境，请遵守以下规范：\n1. 保持安静，禁止大声喧哗\n2. 保持座位整洁，离开时请收拾好个人物品\n3. 合理预约时间，避免长时间占座不用\n4. 如需续约，请提前申请；如需提前离开，请及时退座\n5. 爱护公共设施，节约用电', '2025-06-11 08:00:00'),
('期末考试期间座位预约提醒', '期末考试临近，座位需求增加。请同学们：\n1. 合理安排学习时间，避免长时间预约\n2. 如临时有事无法到场，请及时取消预约\n3. 提倡短时间高效学习，为更多同学提供机会\n4. 续约功能请谨慎使用，优先保证其他同学的预约需求', '2025-06-10 15:00:00');

-- 插入样本投诉建议
INSERT INTO complaint (user_id, title, content, created_at) VALUES
(2, '空调温度建议', '建议将自习室空调温度调整至24-26度，现在有点偏冷，影响学习效率。', '2025-06-11 16:30:00'),
(3, '续约功能很好用', '新的续约功能很方便，不用重新预约了，点赞！', '2025-06-11 14:20:00'),
(4, '座位A05椅子问题', '座位A05的椅子有些松动，坐着不太稳，建议维修。', '2025-06-10 10:15:00'),
(5, '建议增加充电插座', '希望能在每个座位附近增加充电插座，方便使用笔记本电脑学习。', '2025-06-09 18:45:00');

-- 显示插入结果统计
SELECT 'Users' as Table_Name, COUNT(*) as Record_Count FROM user
UNION ALL
SELECT 'Seats', COUNT(*) FROM seat
UNION ALL
SELECT 'Reservations', COUNT(*) FROM reservation
UNION ALL
SELECT 'Announcements', COUNT(*) FROM announcement
UNION ALL
SELECT 'Complaints', COUNT(*) FROM complaint; 