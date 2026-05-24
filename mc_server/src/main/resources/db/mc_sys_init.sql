-- ============================================
-- Medical Companion System (mc_sys) Database
-- 医疗陪诊系统 数据库初始化脚本
-- ============================================

CREATE DATABASE IF NOT EXISTS mc_sys
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE mc_sys;

-- ============================================
-- 1. 权限组表
-- ============================================
DROP TABLE IF EXISTS permission_group;
CREATE TABLE permission_group (
    id int NOT NULL AUTO_INCREMENT,
    name varchar(100) NOT NULL,
    permissions json DEFAULT NULL,
    create_time datetime DEFAULT CURRENT_TIMESTAMP,
    create_user_id int NOT NULL DEFAULT '1' COMMENT '创建用户ID',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 2. 用户表
-- ============================================
DROP TABLE IF EXISTS user;
CREATE TABLE user (
    id int NOT NULL AUTO_INCREMENT,
    user_name varchar(20) NOT NULL COMMENT '用户名(手机号)',
    mobile varchar(30) NOT NULL COMMENT '手机号',
    nickname varchar(100) DEFAULT 'admin' COMMENT '昵称',
    permissions_id int DEFAULT NULL COMMENT '权限组ID',
    active tinyint DEFAULT '1' COMMENT '1=正常 0=禁用',
    create_time datetime DEFAULT CURRENT_TIMESTAMP,
    avatar varchar(255) DEFAULT 'http://159.75.169.224:5500/avatar.jpeg' COMMENT '头像',
    password varchar(255) NOT NULL COMMENT '密码',
    PRIMARY KEY (id),
    KEY permissions_id (permissions_id),
    CONSTRAINT user_ibfk_1 FOREIGN KEY (permissions_id) REFERENCES permission_group (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 3. 菜单表
-- ============================================
DROP TABLE IF EXISTS menu;
CREATE TABLE menu (
    id int NOT NULL AUTO_INCREMENT,
    parent_id int DEFAULT NULL COMMENT '父级菜单ID',
    name varchar(100) NOT NULL COMMENT '菜单名称',
    path varchar(255) DEFAULT NULL COMMENT '路由路径',
    icon varchar(100) DEFAULT NULL COMMENT '图标',
    component varchar(255) DEFAULT NULL COMMENT '组件路径',
    order_index int DEFAULT '0' COMMENT '排序',
    disabled tinyint(1) DEFAULT NULL COMMENT '是否禁用',
    `describe` varchar(100) NOT NULL COMMENT '描述',
    PRIMARY KEY (id),
    KEY id (id, parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 4. 陪护师表
-- ============================================
DROP TABLE IF EXISTS companion;
CREATE TABLE companion (
    id int unsigned NOT NULL AUTO_INCREMENT,
    name varchar(100) NOT NULL COMMENT '陪护师昵称',
    mobile varchar(50) NOT NULL COMMENT '陪护师手机号',
    age tinyint unsigned NOT NULL DEFAULT '0' COMMENT '年龄',
    sex char(1) NOT NULL COMMENT '性别 1=男 2=女',
    avatar varchar(255) DEFAULT NULL COMMENT '头像URL',
    active tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否生效',
    create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_user_id int NOT NULL DEFAULT '0' COMMENT '创建者',
    PRIMARY KEY (id),
    UNIQUE KEY uk_companion_mobile (mobile)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 5. 医疗服务平台表
-- ============================================
DROP TABLE IF EXISTS medical_service;
CREATE TABLE medical_service (
    id int unsigned NOT NULL AUTO_INCREMENT,
    service_name varchar(100) NOT NULL COMMENT '服务名称',
    service_img varchar(255) DEFAULT NULL COMMENT '服务图片',
    price decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '价格',
    active tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
    create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 6. 医院表
-- ============================================
DROP TABLE IF EXISTS hospital;
CREATE TABLE hospital (
    id int unsigned NOT NULL AUTO_INCREMENT,
    name varchar(120) NOT NULL COMMENT '医院名称',
    `rank` varchar(50) DEFAULT NULL COMMENT '等级(三甲等)',
    label varchar(100) DEFAULT NULL COMMENT '标签',
    intro varchar(500) DEFAULT NULL COMMENT '简介',
    avatar_url varchar(255) DEFAULT NULL COMMENT '医院图片',
    service_id int unsigned NOT NULL COMMENT '关联服务ID',
    service_price decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '服务价格',
    sort int NOT NULL DEFAULT '0' COMMENT '排序',
    active tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
    create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_hospital_service_id (service_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 7. 订单表
-- ============================================
DROP TABLE IF EXISTS medical_order;
CREATE TABLE medical_order (
    id bigint unsigned NOT NULL AUTO_INCREMENT,
    out_trade_no varchar(64) NOT NULL COMMENT '商户订单号',
    transaction_id varchar(80) DEFAULT '' COMMENT '微信交易号',
    user_id int NOT NULL COMMENT '用户ID',
    hospital_id int unsigned NOT NULL COMMENT '医院ID',
    hospital_name varchar(120) NOT NULL COMMENT '医院名称',
    service_id int unsigned NOT NULL COMMENT '服务ID',
    service_name varchar(100) NOT NULL COMMENT '服务名称',
    service_img varchar(255) DEFAULT NULL COMMENT '服务图片',
    companion_id int unsigned NOT NULL COMMENT '陪护师ID',
    starttime bigint NOT NULL COMMENT '就诊时间',
    receive_address varchar(255) NOT NULL COMMENT '接送地址',
    tel varchar(30) NOT NULL COMMENT '联系电话',
    demand varchar(500) DEFAULT '' COMMENT '服务需求',
    trade_state tinyint NOT NULL DEFAULT '1' COMMENT '1=待支付 2=待服务 3=已完成 4=已取消',
    service_state tinyint NOT NULL DEFAULT '1' COMMENT '服务状态',
    price decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '价格',
    paid_price decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '实付金额',
    code_url varchar(255) DEFAULT '' COMMENT '支付二维码',
    order_start_time bigint NOT NULL COMMENT '下单时间',
    time_end bigint NOT NULL COMMENT '支付截止时间',
    create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_medical_order_out_trade_no (out_trade_no),
    KEY idx_medical_order_user_state (user_id, trade_state, id),
    KEY idx_medical_order_companion_id (companion_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 8. 头像/图片表
-- ============================================
DROP TABLE IF EXISTS photo;
CREATE TABLE photo (
    id int unsigned NOT NULL AUTO_INCREMENT,
    name varchar(100) DEFAULT NULL COMMENT '图片名称',
    url varchar(255) NOT NULL COMMENT '图片地址',
    create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 9. H5首页轮播图表
-- ============================================
DROP TABLE IF EXISTS h5_home_banner;
CREATE TABLE h5_home_banner (
    id int unsigned NOT NULL AUTO_INCREMENT,
    stype varchar(50) DEFAULT '' COMMENT '类型',
    stype_link varchar(255) DEFAULT '' COMMENT '链接',
    title varchar(120) NOT NULL COMMENT '标题',
    stype_text varchar(120) DEFAULT '' COMMENT '类型文本',
    pic_image_url varchar(255) NOT NULL COMMENT '图片URL',
    sort int NOT NULL DEFAULT '0' COMMENT '排序',
    active tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
    create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 10. H5首页导航表
-- ============================================
DROP TABLE IF EXISTS h5_home_nav;
CREATE TABLE h5_home_nav (
    id int unsigned NOT NULL AUTO_INCREMENT,
    stype varchar(50) DEFAULT '' COMMENT '类型',
    stype_link varchar(255) DEFAULT '' COMMENT '链接',
    title varchar(120) NOT NULL COMMENT '标题',
    stype_text varchar(120) DEFAULT '' COMMENT '类型文本',
    pic_image_url varchar(255) NOT NULL COMMENT '图片URL',
    cat_text varchar(120) DEFAULT '' COMMENT '分类文本',
    tcolor varchar(30) DEFAULT '' COMMENT '文字颜色',
    position varchar(20) NOT NULL DEFAULT 'nav' COMMENT '位置: nav=导航 nav2=第二行导航',
    sort int NOT NULL DEFAULT '0' COMMENT '排序',
    active tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
    create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_h5_home_nav_position (position, active, sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 初始化数据
-- ============================================

-- 权限组
INSERT INTO permission_group (id, name, permissions, create_time, create_user_id) VALUES
(1, '超级管理员', '[1, 2, 4, 5, 3, 6, 7]', '2026-05-09 20:22:45', 0),
(10, '普通用户', '[1, 3, 6, 7]', '2026-05-14 08:48:05', 4),
(11, '陪护员', '[1, 2, 4, 5, 3, 6, 7]', '2026-05-18 15:02:31', 1);

-- 菜单
INSERT INTO menu (id, parent_id, name, path, icon, component, order_index, disabled, `describe`) VALUES
(1, NULL, '控制台', '/dashboard', 'Platform', '/dashboard', 1, 0, '用于展示当前系统中的统计数据、统计报表及重要实时数据'),
(2, NULL, '权限管理', '/auth', 'Grid', NULL, 2, 1, ''),
(3, NULL, 'DIDI陪诊', '/vppz', 'Bell', NULL, 3, 0, ''),
(4, 2, '账号管理', '/auth/admin', 'Avatar', '/auth/admin', 1, 1, '管理员可以进行编辑，权限修改后需要登出才会生效'),
(5, 2, '菜单管理', '/auth/group', 'Menu', '/auth/group', 2, 1, '菜单规则通常对应一个控制器的方法,同时菜单栏数据也从规则中获取'),
(6, 3, '陪护管理', '/vppz/staff', 'Checked', '/vppz/staff', 1, 0, '陪护师可以进行创建和修改，设置对应生效状态控制C端选择'),
(7, 3, '订单管理', '/vppz/order', 'List', '/vppz/order', 2, 0, 'C端下单后可以查看所有订单状态，已支付的订单可以完成陪护状态修改');

-- 服务
INSERT INTO medical_service (id, service_name, service_img, price, active) VALUES
(1, '就医陪诊（尊享）', 'https://upz.itndedu.com/uploads/20231105/90ded71dd1868829b08dae540412c039.jpeg', 198.00, 1);

-- 医院
INSERT INTO hospital (id, name, `rank`, label, intro, avatar_url, service_id, service_price, sort, active) VALUES
(1, '湘雅分院', '三甲', '综合病院', '湘雅分院是湘雅医院的附属医院，提供全科医疗服务和各种专科诊疗服务。', 'https://upz.itndedu.com/uploads/20231105/341dcc6aca9679917a9fd50988a2f082.jpeg', 1, 198.00, 3, 1),
(2, '中医药大学', '三甲', '综合病院', '湖南中医药大学的附属医院，也是湖南省中医药系统的重点医疗机构之一。', 'https://upz.itndedu.com/uploads/20231105/e22cd5cd5b3481e14bbdc52d9e237999.png', 1, 198.00, 4, 1),
(3, '株洲市三医院', '三甲', '综合病院', '医院拥有一支专业的医疗团队，提供高质量的医疗服务。', 'https://upz.itndedu.com/uploads/20231105/7adb0d6877e16ace0c3725e7e42a496d.jpeg', 1, 198.00, 5, 1),
(4, '协和医院', '三甲', '综合病院', '协和医院是中国的一家著名综合性医院，拥有一流的医疗设施和技术。', 'https://upz.itndedu.com/uploads/20231105/308e36361cf273fdefe35c80c3134f92.jpeg', 1, 198.00, 2, 1),
(5, '武汉中心医院', '三甲', '综合病院', '武汉市中心医院是一家位于中国湖北省武汉市的大型综合性医院。', 'https://upz.itndedu.com/uploads/20231105/d64c396c15bd003d45f79e939180301c.jpeg', 1, 198.00, 1, 1);

-- 头像图片
INSERT INTO photo (id, name, url) VALUES
(1, '1.jpeg', 'http://159.75.169.224:5500/1.jpeg'),
(2, '2.jpeg', 'http://159.75.169.224:5500/2.jpeg'),
(3, '3.jpeg', 'http://159.75.169.224:5500/3.jpeg'),
(4, '4.jpeg', 'http://159.75.169.224:5500/4.jpeg'),
(5, '5.jpeg', 'http://159.75.169.224:5500/5.jpeg'),
(6, '6.jpeg', 'http://159.75.169.224:5500/6.jpeg');

-- 首页轮播图
INSERT INTO h5_home_banner (id, stype, stype_link, title, stype_text, pic_image_url, sort, active) VALUES
(1, '0', '', 'banner01', 'Stype 0', 'https://upz.itndedu.com/uploads/20231105/6a284b70666a936caca0af7f8268c9d8.jpg', 2, 1),
(2, '0', '', 'banner02', 'Stype 0', 'https://upz.itndedu.com/uploads/20231105/80221cd6111536ab328cda2ea0eef452.jpg', 1, 1);

-- 首页导航
INSERT INTO h5_home_nav (id, stype, stype_link, title, stype_text, pic_image_url, cat_text, tcolor, position, sort, active) VALUES
(1, '1', '/pages/hospital/index?hid=4', 'img1', 'Stype 1', 'https://upz.itndedu.com/uploads/20231105/ac905fd8f9f1ca96ce614f0960a74820.png', '', '', 'nav2', 2, 1),
(2, '1', '/pages/hospital/index?hid=5', 'img2', 'Stype 1', 'https://upz.itndedu.com/uploads/20231105/299ec231895953ff27de0f63c7703f6b.png', '', '', 'nav2', 1, 1),
(3, '1', '/pages/service/index?svid=1&hid=5', '代跑取药', 'Stype 1', 'https://upz.itndedu.com/uploads/20231105/212b8060d23a23d254699debb3a8c86d.png', '', '', 'nav', 1, 1),
(4, '1', '/pages/service/index?svid=4&hid=5', '上门助浴', 'Stype 1', 'https://upz.itndedu.com/uploads/20231105/e398aab14567d1fa039d328e09f6fc6f.png', '', '', 'nav', 2, 1),
(5, '1', '/pages/service/index?svid=4&hid=5', '送取结果', 'Stype 1', 'https://upz.itndedu.com/uploads/20231105/038a12b6bb3486e79d3062b1ee1795d7.png', '', '', 'nav', 3, 1),
(6, '1', '/pages/service/index?svid=4&hid=5', '诊前约号', 'Stype 1', 'https://upz.itndedu.com/uploads/20231105/9928b17ae7981ca591629a66748f76b4.png', '', '', 'nav', 4, 1),
(7, '1', '/pages/service/index?svid=4&hid=5', '尊享陪诊', 'Stype 1', 'https://upz.itndedu.com/uploads/20231105/56c5ab7c1b579767c0733f1c3820defd.png', '', '', 'nav', 5, 1);