-- 续约和退座功能数据库扩展脚本
-- 为reservation表添加续约和退座相关字段

USE study_room_db;

-- 添加续约和退座相关字段
ALTER TABLE reservation 
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN IF NOT EXISTS last_extended_at TIMESTAMP NULL COMMENT '最后续约时间',
ADD COLUMN IF NOT EXISTS extension_count INT DEFAULT 0 COMMENT '续约次数',
ADD COLUMN IF NOT EXISTS actual_end_time TIMESTAMP NULL COMMENT '实际结束时间（退座时间）',
ADD COLUMN IF NOT EXISTS remarks VARCHAR(255) NULL COMMENT '备注信息';

-- 更新现有数据的created_at字段
UPDATE reservation SET created_at = NOW() WHERE created_at IS NULL;

-- 插入一些测试数据（包含续约和退座的各种状态）
-- 清理现有测试数据
DELETE FROM reservation WHERE user_id IN (
    SELECT id FROM user WHERE username IN ('student1', 'student2', 'student3')
);

-- 插入新的测试数据
INSERT INTO reservation (user_id, seat_id, start_time, end_time, status, created_at, extension_count, last_extended_at, actual_end_time, remarks) VALUES

-- 当前活跃预约（可以测试续约和退座）
((SELECT id FROM user WHERE username = 'student1'), 1, '2025-06-12 08:00:00', '2025-06-12 10:00:00', 'ACTIVE', '2025-06-12 07:30:00', 0, NULL, NULL, '准备期末考试'),
((SELECT id FROM user WHERE username = 'student1'), 2, '2025-06-12 14:00:00', '2025-06-12 16:00:00', 'ACTIVE', '2025-06-12 13:30:00', 0, NULL, NULL, '复习高等数学'),

-- 已续约的预约（显示续约功能效果）
((SELECT id FROM user WHERE username = 'student1'), 3, '2025-06-11 08:00:00', '2025-06-11 12:00:00', 'EXTENDED', '2025-06-11 07:30:00', 2, '2025-06-11 11:30:00', NULL, '毕业论文写作'),

-- 已完成的预约（通过退座功能完成）
((SELECT id FROM user WHERE username = 'student1'), 4, '2025-06-10 08:00:00', '2025-06-10 12:00:00', 'COMPLETED', '2025-06-10 07:45:00', 1, '2025-06-10 11:30:00', '2025-06-10 11:45:00', '计算机网络实验'),
((SELECT id FROM user WHERE username = 'student1'), 5, '2025-06-10 14:00:00', '2025-06-10 18:00:00', 'COMPLETED', '2025-06-10 13:30:00', 0, NULL, '2025-06-10 17:30:00', '数据结构课程设计');

-- 添加系统公告
INSERT INTO announcement (title, content, created_at) VALUES
('续约与退座功能上线通知', 
'亲爱的同学们，系统新增了预约续约和提前退座功能：

1. 续约功能：
   - 在预约结束前30分钟内可申请续约
   - 每次最多续约2小时，每个预约最多续约3次
   - 每位用户每天最多可进行5次续约操作

2. 退座功能：
   - 在预约开始后可随时申请退座
   - 系统会记录实际使用时长
   - 退座后座位立即释放给其他用户

请合理使用座位资源，为其他同学让出更多学习机会！', 
NOW()); 