-- 为数据分析模块添加样本数据
USE `study_room_db`;

-- 添加更多测试用户
INSERT INTO `user` (`username`, `password`, `student_id`, `full_name`, `role`) VALUES
('student2', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07c209chCoSzOAoFSG', '2024002', '李四', 'ROLE_USER'),
('student3', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07c209chCoSzOAoFSG', '2024003', '王五', 'ROLE_USER'),
('student4', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07c209chCoSzOAoFSG', '2024004', '赵六', 'ROLE_USER'),
('student5', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07c209chCoSzOAoFSG', '2024005', '钱七', 'ROLE_USER');

-- 添加更多座位
INSERT INTO `seat` (`seat_number`) VALUES 
('C01'), ('C02'), ('C03'), ('C04'), ('C05');

-- 添加更多公告
INSERT INTO `announcement` (`title`, `content`, `created_at`) VALUES
('自习室开放时间调整', '从下周开始，自习室开放时间调整为 7:00-23:00，请各位同学注意。', DATE_SUB(NOW(), INTERVAL 2 DAY)),
('期末考试期间座位预约规则', '期末考试期间，每人每天最多可预约3小时，请合理安排学习时间。', DATE_SUB(NOW(), INTERVAL 1 DAY));

-- 添加历史预约数据（最近7天）
INSERT INTO `reservation` (`user_id`, `seat_id`, `start_time`, `end_time`, `status`) VALUES
-- 今天的预约
((SELECT id FROM user WHERE username = 'student1'), (SELECT id FROM seat WHERE seat_number = 'A01'), CONCAT(CURDATE(), ' 08:00:00'), CONCAT(CURDATE(), ' 10:00:00'), 'ACTIVE'),
((SELECT id FROM user WHERE username = 'student2'), (SELECT id FROM seat WHERE seat_number = 'A02'), CONCAT(CURDATE(), ' 09:00:00'), CONCAT(CURDATE(), ' 11:00:00'), 'ACTIVE'),
((SELECT id FROM user WHERE username = 'student3'), (SELECT id FROM seat WHERE seat_number = 'B01'), CONCAT(CURDATE(), ' 14:00:00'), CONCAT(CURDATE(), ' 16:00:00'), 'ACTIVE'),

-- 昨天的预约
((SELECT id FROM user WHERE username = 'student1'), (SELECT id FROM seat WHERE seat_number = 'A01'), DATE_SUB(CONCAT(CURDATE(), ' 08:00:00'), INTERVAL 1 DAY), DATE_SUB(CONCAT(CURDATE(), ' 10:00:00'), INTERVAL 1 DAY), 'CANCELLED'),
((SELECT id FROM user WHERE username = 'student2'), (SELECT id FROM seat WHERE seat_number = 'A03'), DATE_SUB(CONCAT(CURDATE(), ' 10:00:00'), INTERVAL 1 DAY), DATE_SUB(CONCAT(CURDATE(), ' 12:00:00'), INTERVAL 1 DAY), 'CANCELLED'),
((SELECT id FROM user WHERE username = 'student3'), (SELECT id FROM seat WHERE seat_number = 'B02'), DATE_SUB(CONCAT(CURDATE(), ' 13:00:00'), INTERVAL 1 DAY), DATE_SUB(CONCAT(CURDATE(), ' 15:00:00'), INTERVAL 1 DAY), 'CANCELLED'),
((SELECT id FROM user WHERE username = 'student4'), (SELECT id FROM seat WHERE seat_number = 'B03'), DATE_SUB(CONCAT(CURDATE(), ' 16:00:00'), INTERVAL 1 DAY), DATE_SUB(CONCAT(CURDATE(), ' 18:00:00'), INTERVAL 1 DAY), 'CANCELLED'),

-- 前天的预约
((SELECT id FROM user WHERE username = 'student1'), (SELECT id FROM seat WHERE seat_number = 'A01'), DATE_SUB(CONCAT(CURDATE(), ' 09:00:00'), INTERVAL 2 DAY), DATE_SUB(CONCAT(CURDATE(), ' 11:00:00'), INTERVAL 2 DAY), 'CANCELLED'),
((SELECT id FROM user WHERE username = 'student2'), (SELECT id FROM seat WHERE seat_number = 'A02'), DATE_SUB(CONCAT(CURDATE(), ' 14:00:00'), INTERVAL 2 DAY), DATE_SUB(CONCAT(CURDATE(), ' 16:00:00'), INTERVAL 2 DAY), 'CANCELLED'),

-- 3天前的预约
((SELECT id FROM user WHERE username = 'student1'), (SELECT id FROM seat WHERE seat_number = 'A01'), DATE_SUB(CONCAT(CURDATE(), ' 08:00:00'), INTERVAL 3 DAY), DATE_SUB(CONCAT(CURDATE(), ' 10:00:00'), INTERVAL 3 DAY), 'CANCELLED'),
((SELECT id FROM user WHERE username = 'student2'), (SELECT id FROM seat WHERE seat_number = 'A02'), DATE_SUB(CONCAT(CURDATE(), ' 10:00:00'), INTERVAL 3 DAY), DATE_SUB(CONCAT(CURDATE(), ' 12:00:00'), INTERVAL 3 DAY), 'CANCELLED'),
((SELECT id FROM user WHERE username = 'student3'), (SELECT id FROM seat WHERE seat_number = 'A03'), DATE_SUB(CONCAT(CURDATE(), ' 14:00:00'), INTERVAL 3 DAY), DATE_SUB(CONCAT(CURDATE(), ' 16:00:00'), INTERVAL 3 DAY), 'CANCELLED'),
((SELECT id FROM user WHERE username = 'student4'), (SELECT id FROM seat WHERE seat_number = 'B01'), DATE_SUB(CONCAT(CURDATE(), ' 16:00:00'), INTERVAL 3 DAY), DATE_SUB(CONCAT(CURDATE(), ' 18:00:00'), INTERVAL 3 DAY), 'CANCELLED'),
((SELECT id FROM user WHERE username = 'student5'), (SELECT id FROM seat WHERE seat_number = 'B02'), DATE_SUB(CONCAT(CURDATE(), ' 19:00:00'), INTERVAL 3 DAY), DATE_SUB(CONCAT(CURDATE(), ' 21:00:00'), INTERVAL 3 DAY), 'CANCELLED'),

-- 4天前的预约
((SELECT id FROM user WHERE username = 'student3'), (SELECT id FROM seat WHERE seat_number = 'A04'), DATE_SUB(CONCAT(CURDATE(), ' 08:00:00'), INTERVAL 4 DAY), DATE_SUB(CONCAT(CURDATE(), ' 10:00:00'), INTERVAL 4 DAY), 'CANCELLED'),
((SELECT id FROM user WHERE username = 'student4'), (SELECT id FROM seat WHERE seat_number = 'A05'), DATE_SUB(CONCAT(CURDATE(), ' 15:00:00'), INTERVAL 4 DAY), DATE_SUB(CONCAT(CURDATE(), ' 17:00:00'), INTERVAL 4 DAY), 'CANCELLED'),

-- 5天前的预约
((SELECT id FROM user WHERE username = 'student1'), (SELECT id FROM seat WHERE seat_number = 'A01'), DATE_SUB(CONCAT(CURDATE(), ' 08:00:00'), INTERVAL 5 DAY), DATE_SUB(CONCAT(CURDATE(), ' 10:00:00'), INTERVAL 5 DAY), 'CANCELLED'),
((SELECT id FROM user WHERE username = 'student2'), (SELECT id FROM seat WHERE seat_number = 'A02'), DATE_SUB(CONCAT(CURDATE(), ' 10:00:00'), INTERVAL 5 DAY), DATE_SUB(CONCAT(CURDATE(), ' 12:00:00'), INTERVAL 5 DAY), 'CANCELLED'),
((SELECT id FROM user WHERE username = 'student3'), (SELECT id FROM seat WHERE seat_number = 'A03'), DATE_SUB(CONCAT(CURDATE(), ' 14:00:00'), INTERVAL 5 DAY), DATE_SUB(CONCAT(CURDATE(), ' 16:00:00'), INTERVAL 5 DAY), 'CANCELLED'),
((SELECT id FROM user WHERE username = 'student4'), (SELECT id FROM seat WHERE seat_number = 'B01'), DATE_SUB(CONCAT(CURDATE(), ' 16:00:00'), INTERVAL 5 DAY), DATE_SUB(CONCAT(CURDATE(), ' 18:00:00'), INTERVAL 5 DAY), 'CANCELLED'),

-- 6天前的预约
((SELECT id FROM user WHERE username = 'student5'), (SELECT id FROM seat WHERE seat_number = 'B03'), DATE_SUB(CONCAT(CURDATE(), ' 13:00:00'), INTERVAL 6 DAY), DATE_SUB(CONCAT(CURDATE(), ' 15:00:00'), INTERVAL 6 DAY), 'CANCELLED');

-- 添加投诉建议数据
INSERT INTO `complaint` (`user_id`, `title`, `content`, `created_at`) VALUES
((SELECT id FROM user WHERE username = 'student1'), '座位桌面需要清洁', 'A01座位的桌面有污渍，希望能及时清理。', DATE_SUB(NOW(), INTERVAL 1 DAY)),
((SELECT id FROM user WHERE username = 'student2'), '空调温度建议', '自习室空调温度偏低，建议调高2-3度。', DATE_SUB(NOW(), INTERVAL 2 DAY)),
((SELECT id FROM user WHERE username = 'student3'), '增加充电插座', '希望在座位附近增加更多充电插座，方便使用电子设备。', DATE_SUB(NOW(), INTERVAL 3 DAY)); 