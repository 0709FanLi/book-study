-- 为现有座位添加位置信息
-- 先添加新列
ALTER TABLE seat ADD COLUMN seat_row INT;
ALTER TABLE seat ADD COLUMN seat_col INT;
ALTER TABLE seat ADD COLUMN seat_area VARCHAR(10);

-- 更新现有座位的位置信息
-- A区座位 (A01-A10, A11-A20)
UPDATE seat SET seat_area='A', seat_row=1, seat_col=1 WHERE seat_number='A01';
UPDATE seat SET seat_area='A', seat_row=1, seat_col=2 WHERE seat_number='A02';
UPDATE seat SET seat_area='A', seat_row=1, seat_col=3 WHERE seat_number='A03';
UPDATE seat SET seat_area='A', seat_row=1, seat_col=4 WHERE seat_number='A04';
UPDATE seat SET seat_area='A', seat_row=1, seat_col=5 WHERE seat_number='A05';
UPDATE seat SET seat_area='A', seat_row=2, seat_col=1 WHERE seat_number='B01';
UPDATE seat SET seat_area='A', seat_row=2, seat_col=2 WHERE seat_number='B02';
UPDATE seat SET seat_area='A', seat_row=2, seat_col=3 WHERE seat_number='B03';
UPDATE seat SET seat_area='A', seat_row=2, seat_col=4 WHERE seat_number='B04';
UPDATE seat SET seat_area='A', seat_row=2, seat_col=5 WHERE seat_number='B05';

-- 如果没有更多座位，我们添加一些测试座位
INSERT IGNORE INTO seat (seat_number, seat_area, seat_row, seat_col) VALUES
-- A区第一排
('A06', 'A', 1, 6), ('A07', 'A', 1, 7), ('A08', 'A', 1, 8), ('A09', 'A', 1, 9), ('A10', 'A', 1, 10),
-- A区第二排  
('A11', 'A', 2, 1), ('A12', 'A', 2, 2), ('A13', 'A', 2, 3), ('A14', 'A', 2, 4), ('A15', 'A', 2, 5),
('A16', 'A', 2, 6), ('A17', 'A', 2, 7), ('A18', 'A', 2, 8), ('A19', 'A', 2, 9), ('A20', 'A', 2, 10),
-- B区第一排
('B06', 'B', 1, 6), ('B07', 'B', 1, 7), ('B08', 'B', 1, 8), ('B09', 'B', 1, 9), ('B10', 'B', 1, 10),
('B11', 'B', 1, 1), ('B12', 'B', 1, 2), ('B13', 'B', 1, 3), ('B14', 'B', 1, 4), ('B15', 'B', 1, 5),
-- B区第二排
('B16', 'B', 2, 1), ('B17', 'B', 2, 2), ('B18', 'B', 2, 3), ('B19', 'B', 2, 4), ('B20', 'B', 2, 5),
('B21', 'B', 2, 6), ('B22', 'B', 2, 7), ('B23', 'B', 2, 8), ('B24', 'B', 2, 9), ('B25', 'B', 2, 10);

-- 修改列为非空
ALTER TABLE seat MODIFY COLUMN seat_row INT NOT NULL;
ALTER TABLE seat MODIFY COLUMN seat_col INT NOT NULL;
ALTER TABLE seat MODIFY COLUMN seat_area VARCHAR(10) NOT NULL; 