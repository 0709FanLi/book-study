-- 手动更新数据库脚本
-- 为reservation表添加续约和退座相关字段

-- 添加新字段（如果不存在）
ALTER TABLE reservation 
ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN last_extended_at TIMESTAMP NULL,
ADD COLUMN extension_count INT DEFAULT 0,
ADD COLUMN actual_end_time TIMESTAMP NULL,
ADD COLUMN remarks VARCHAR(255) NULL;

-- 更新现有数据
UPDATE reservation SET created_at = NOW() WHERE created_at IS NULL;
UPDATE reservation SET extension_count = 0 WHERE extension_count IS NULL; 