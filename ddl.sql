
-- 用户表
CREATE TABLE `user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
  `nickname` VARCHAR(50) NOT NULL COMMENT '昵称',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 好友关系表
CREATE TABLE `friend` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `friend_id` BIGINT(20) NOT NULL COMMENT '好友ID',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_friend` (`user_id`, `friend_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_friend_id` (`friend_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='好友关系表';

-- 动态表
CREATE TABLE `moment` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '动态ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '发布者ID',
  `content` VARCHAR(500) DEFAULT NULL COMMENT '文字内容',
  `images` TEXT DEFAULT NULL COMMENT '图片URL列表，JSON格式',
  `location` VARCHAR(100) DEFAULT NULL COMMENT '定位信息',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态表';

-- 插入用户数据
INSERT INTO `user` (`phone`, `nickname`, `avatar`, `create_time`, `update_time`) VALUES
('13800138001', '小明', 'https://example.com/avatar1.jpg', '2025-10-29 18:30:00', '2025-10-29 18:30:00'),
('13800138002', '小红', 'https://example.com/avatar2.jpg', '2025-10-29 18:30:00', '2025-10-29 18:30:00'),
('13800138003', '小刚', 'https://example.com/avatar3.jpg', '2025-10-29 18:30:00', '2025-10-29 18:30:00'),
('13800138004', '小李', 'https://example.com/avatar4.jpg', '2025-10-29 18:30:00', '2025-10-29 18:30:00'),
('13800138005', '小美', 'https://example.com/avatar5.jpg', '2025-10-29 18:30:00', '2025-10-29 18:30:00');

-- 插入好友关系数据
INSERT INTO `friend` (`user_id`, `friend_id`, `create_time`) VALUES
(1, 2, '2025-10-29 18:30:00'),  -- 小明和小红是好友
(1, 3, '2025-10-29 18:30:00'),  -- 小明和小刚是好友
(2, 1, '2025-10-29 18:30:00'),  -- 小红和小明是好友（双向）
(2, 4, '2025-10-29 18:30:00'),  -- 小红和小李是好友
(3, 1, '2025-10-29 18:30:00'),  -- 小刚和小明是好友（双向）
(3, 5, '2025-10-29 18:30:00'),  -- 小刚和小美是好友
(4, 2, '2025-10-29 18:30:00'),  -- 小李和小红是好友（双向）
(5, 3, '2025-10-29 18:30:00');  -- 小美和小刚是好友（双向）

-- 插入动态数据
INSERT INTO `moment` (`user_id`, `content`, `images`, `location`, `create_time`, `update_time`) VALUES
(1, '今天天气真好！', '["https://example.com/image1.jpg", "https://example.com/image2.jpg"]', '北京市朝阳区', '2025-10-29 18:30:00', '2025-10-29 18:30:00'),
(2, '周末去爬山了，风景美不胜收', '["https://example.com/image3.jpg"]', '香山公园', '2025-10-29 18:30:00', '2025-10-29 18:30:00'),
(1, '分享一首好听的歌', NULL, NULL, '2025-10-29 18:30:00', '2025-10-29 18:30:00'),
(3, '努力学习，天天向上！', NULL, '图书馆', '2025-10-29 18:30:00', '2025-10-29 18:30:00'),
(4, '美食分享：今天做的红烧肉', '["https://example.com/image4.jpg", "https://example.com/image5.jpg", "https://example.com/image6.jpg"]', '家里', '2025-10-29 18:30:00', '2025-10-29 18:30:00'),
(5, '旅行日记：美丽的洱海', '["https://example.com/image7.jpg"]', '大理洱海', '2025-10-29 18:30:00', '2025-10-29 18:30:00'),
(2, '新买的书到了，开心！', NULL, NULL, '2025-10-29 18:30:00', '2025-10-29 18:30:00');

