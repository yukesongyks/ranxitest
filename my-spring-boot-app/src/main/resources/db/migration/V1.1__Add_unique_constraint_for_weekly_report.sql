-- B-004: 添加唯一索引解决并发防重问题
-- 确保同一用户同一周只能有一份PENDING或APPROVED状态的周报

-- 添加唯一约束：author_id + week_start_date（仅对已提交状态）
-- 注意：部分数据库不支持带WHERE条件的唯一索引，这里使用函数索引或触发器替代方案

-- 方案1：MySQL 8.0+ / PostgreSQL 支持的部分索引
CREATE UNIQUE INDEX idx_unique_author_week_submitted 
ON weekly_report(author_id, week_start_date) 
WHERE status IN ('PENDING', 'APPROVED');

-- 方案2（备选，适用于不支持部分索引的数据库）：
-- 先清理历史重复数据，再添加完整唯一索引
-- CREATE UNIQUE INDEX idx_unique_author_week ON weekly_report(author_id, week_start_date);

-- 说明：
-- 1. 该索引防止同一用户在同一周提交多份周报
-- 2. 仅对PENDING和APPROVED状态生效，允许草稿（DRAFT）和打回（REJECTED）状态存在
-- 3. 并发提交时，数据库唯一索引作为最终防线