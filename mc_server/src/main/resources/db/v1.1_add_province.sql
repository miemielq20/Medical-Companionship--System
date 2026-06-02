-- 高德POI同步：hospital新增省份字段
ALTER TABLE hospital ADD COLUMN province VARCHAR(50) DEFAULT '' COMMENT '省份' AFTER name;

