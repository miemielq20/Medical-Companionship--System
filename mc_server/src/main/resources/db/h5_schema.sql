CREATE TABLE IF NOT EXISTS `medical_service` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `service_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `service_img` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `active` tinyint(1) NOT NULL DEFAULT '1',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `hospital` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  `rank` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `label` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `intro` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `avatar_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `service_id` int unsigned NOT NULL,
  `service_price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `sort` int NOT NULL DEFAULT '0',
  `active` tinyint(1) NOT NULL DEFAULT '1',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_hospital_service_id` (`service_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `h5_home_banner` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `stype` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `stype_link` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `title` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  `stype_text` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `pic_image_url` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sort` int NOT NULL DEFAULT '0',
  `active` tinyint(1) NOT NULL DEFAULT '1',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `h5_home_nav` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `stype` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `stype_link` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `title` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  `stype_text` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `pic_image_url` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `cat_text` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `tcolor` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `position` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'nav',
  `sort` int NOT NULL DEFAULT '0',
  `active` tinyint(1) NOT NULL DEFAULT '1',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_h5_home_nav_position` (`position`, `active`, `sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `medical_order` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `out_trade_no` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `transaction_id` varchar(80) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `user_id` int NOT NULL,
  `hospital_id` int unsigned NOT NULL,
  `hospital_name` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  `service_id` int unsigned NOT NULL,
  `service_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `service_img` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `companion_id` int unsigned NOT NULL,
  `starttime` bigint NOT NULL,
  `receive_address` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tel` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `demand` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `trade_state` tinyint NOT NULL DEFAULT '1' COMMENT '1=wait pay,2=wait service,3=completed,4=cancelled',
  `service_state` tinyint NOT NULL DEFAULT '1',
  `price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `paid_price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `code_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `order_start_time` bigint NOT NULL,
  `time_end` bigint NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_medical_order_out_trade_no` (`out_trade_no`),
  KEY `idx_medical_order_user_state` (`user_id`, `trade_state`, `id`),
  KEY `idx_medical_order_companion_id` (`companion_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `medical_service` (`id`, `service_name`, `service_img`, `price`, `active`)
VALUES (1, 'DIDI陪诊', '/images/ic_server.png', 198.00, 1)
ON DUPLICATE KEY UPDATE
  `service_name` = VALUES(`service_name`),
  `service_img` = VALUES(`service_img`),
  `price` = VALUES(`price`),
  `active` = VALUES(`active`);

INSERT INTO `hospital` (`id`, `name`, `rank`, `label`, `intro`, `avatar_url`, `service_id`, `service_price`, `sort`, `active`)
VALUES
  (1, '中部战区总医院', '三甲', '综合医院', '提供门诊陪诊、检查取号、报告领取等服务。', '/images/card_1.png', 1, 198.00, 1, 1),
  (2, '武汉协和医院', '三甲', '综合医院', '覆盖预约就诊、陪同检查、院内引导等流程。', '/images/card_2.png', 1, 228.00, 2, 1)
ON DUPLICATE KEY UPDATE
  `name` = VALUES(`name`),
  `rank` = VALUES(`rank`),
  `label` = VALUES(`label`),
  `intro` = VALUES(`intro`),
  `avatar_url` = VALUES(`avatar_url`),
  `service_id` = VALUES(`service_id`),
  `service_price` = VALUES(`service_price`),
  `sort` = VALUES(`sort`),
  `active` = VALUES(`active`);

INSERT INTO `h5_home_banner` (`id`, `stype`, `stype_link`, `title`, `stype_text`, `pic_image_url`, `sort`, `active`)
VALUES
  (1, 'order', '/createOrder', '专业陪诊服务', '全程陪同就诊', '/images/card-bg.png', 1, 1),
  (2, 'service', '/createOrder', '省心就医助手', '预约、取号、检查协助', '/images/od_bg.png', 2, 1)
ON DUPLICATE KEY UPDATE
  `stype` = VALUES(`stype`),
  `stype_link` = VALUES(`stype_link`),
  `title` = VALUES(`title`),
  `stype_text` = VALUES(`stype_text`),
  `pic_image_url` = VALUES(`pic_image_url`),
  `sort` = VALUES(`sort`),
  `active` = VALUES(`active`);

INSERT INTO `h5_home_nav` (`id`, `stype`, `stype_link`, `title`, `stype_text`, `pic_image_url`, `cat_text`, `tcolor`, `position`, `sort`, `active`)
VALUES
  (1, 'order', '/createOrder', '陪诊下单', '快速预约陪诊员', '/images/card_1.png', '热门', '#07c160', 'nav2', 1, 1),
  (2, 'order', '/createOrder', '代办服务', '报告领取与院内代办', '/images/card_2.png', '便捷', '#1989fa', 'nav2', 2, 1),
  (3, 'hospital', '/home', '推荐医院', '优选合作医院', '/images/ic_address.png', '医院', '#333333', 'nav', 1, 1)
ON DUPLICATE KEY UPDATE
  `stype` = VALUES(`stype`),
  `stype_link` = VALUES(`stype_link`),
  `title` = VALUES(`title`),
  `stype_text` = VALUES(`stype_text`),
  `pic_image_url` = VALUES(`pic_image_url`),
  `cat_text` = VALUES(`cat_text`),
  `tcolor` = VALUES(`tcolor`),
  `position` = VALUES(`position`),
  `sort` = VALUES(`sort`),
  `active` = VALUES(`active`);
