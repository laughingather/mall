CREATE DATABASE IF NOT EXISTS `mall_admin` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
USE `mall_admin`;
-- MySQL dump 10.13  Distrib 8.0.31, for macos12 (x86_64)
--
-- Host: 127.0.0.1    Database: mall_admin
-- ------------------------------------------------------
-- Server version	8.0.28

/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS = @@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION = @@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE = @@TIME_ZONE */;
/*!40103 SET TIME_ZONE = '+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES = @@SQL_NOTES, SQL_NOTES = 0 */;

--
-- Table structure for table `sys_dict`
--

DROP TABLE IF EXISTS `sys_dict`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict`
(
    `dict_id`     bigint unsigned NOT NULL COMMENT '字典id',
    `dict_code`   varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '字典编码',
    `dict_name`   varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '字典名称',
    `enable`      tinyint unsigned                 DEFAULT NULL COMMENT '是否启用',
    `sort`        int                              DEFAULT NULL COMMENT '排序',
    `description` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '字典描述',
    `create_by`   varchar(64) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '创建人',
    `create_time` datetime                         DEFAULT NULL COMMENT '创建时间',
    `update_by`   varchar(64) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '更新人',
    `updateTime`  datetime                         DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`dict_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict`
--

LOCK TABLES `sys_dict` WRITE;
/*!40000 ALTER TABLE `sys_dict`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_dict`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dict_detail`
--

DROP TABLE IF EXISTS `sys_dict_detail`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict_detail`
(
    `dict_detail_id`    bigint unsigned                 NOT NULL COMMENT '字典明细id',
    `dict_detail_name`  varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '字典明细名称',
    `dict_detail_value` varchar(127) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '字典明细值',
    `dict_id`           bigint unsigned                 NOT NULL COMMENT '字典id',
    `description`       varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '字典明细备注',
    `pinyin`            varchar(64) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '拼音',
    `enable`            tinyint unsigned                 DEFAULT NULL COMMENT '是否启用',
    `sort`              int                              DEFAULT NULL COMMENT '排序',
    `create_by`         varchar(64) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '创建人',
    `create_time`       datetime                         DEFAULT NULL COMMENT '创建时间',
    `update_by`         varchar(64) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '更新人',
    `update_time`       datetime                         DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`dict_detail_id`),
    KEY `sys_dict_detail_dict_id_index` (`dict_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict_detail`
--

LOCK TABLES `sys_dict_detail` WRITE;
/*!40000 ALTER TABLE `sys_dict_detail`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_dict_detail`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_permission`
--

DROP TABLE IF EXISTS `sys_permission`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_permission`
(
    `permission_id`    bigint unsigned NOT NULL COMMENT '主键id',
    `permission_name`  varchar(100)                                            DEFAULT NULL COMMENT '菜单标题',
    `permission_value` varchar(55)                                             DEFAULT NULL COMMENT '权限值',
    `path`             varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '路径',
    `type`             tinyint(1)                                              DEFAULT NULL COMMENT '菜单类型(0:一级菜单; 1:子菜单; 2:按钮权限)',
    `icon`             varchar(100)                                            DEFAULT NULL COMMENT '菜单图标',
    `sort`             int                                                     DEFAULT NULL COMMENT '菜单排序',
    `enable`           tinyint unsigned                                        DEFAULT NULL COMMENT '状态 1已启用 0未启用',
    `description`      varchar(255)                                            DEFAULT NULL COMMENT '描述',
    `parent_id`        bigint unsigned                                         DEFAULT NULL COMMENT '父id',
    `create_by`        varchar(32)                                             DEFAULT NULL COMMENT '创建人',
    `create_time`      datetime                                                DEFAULT NULL COMMENT '创建时间',
    `update_by`        varchar(32)                                             DEFAULT NULL COMMENT '更新人',
    `update_time`      datetime                                                DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`permission_id`) USING BTREE,
    KEY `idx_sp_parent_id` (`parent_id`) USING BTREE,
    KEY `idx_sp_sort_no` (`sort`) USING BTREE,
    KEY `idx_sp_menu_type` (`type`) USING BTREE,
    KEY `index_sp_pid` (`parent_id`),
    KEY `index_sp_sort_no` (`sort`),
    KEY `index_sp_menu_type` (`type`),
    KEY `idx_sp_del_flag` (`enable`),
    KEY `index_sp_is_delete` (`enable`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  ROW_FORMAT = DYNAMIC COMMENT ='菜单权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_permission`
--

LOCK TABLES `sys_permission` WRITE;
/*!40000 ALTER TABLE `sys_permission`
    DISABLE KEYS */;
INSERT INTO `sys_permission`
VALUES (1, '系统管理', 'admin', '/system', 1, NULL, 1, 1, '系统管理模块', 0, 'root', '2021-12-02 08:57:59', NULL, NULL),
       (1466209998387417088, '用户管理', 'admin:user', '/system/user', 2, NULL, 1, 1, NULL, 1, 'root',
        '2021-12-02 08:57:59', NULL, NULL),
       (1466211572253855744, '角色管理', 'admin:role', '/system/role', 2, NULL, 1, 1, NULL, 1, 'root',
        '2021-12-02 09:04:14', NULL, NULL),
       (1466211672942317568, '权限管理', 'admin:permission', '/system/permission', 2, NULL, 1, 1, NULL, 1, 'root',
        '2021-12-02 09:04:38', NULL, NULL),
       (1466212316054949888, '用户添加', 'admin:user:add', '/system/user/add', 3, NULL, 1, 1, '用户添加控制',
        1466209998387417088, 'root', '2021-12-02 09:07:12', NULL, NULL),
       (1466212464789164032, '角色添加', 'admin:role:add', '/system/role/add', 3, NULL, 1, 1, '角色添加控制',
        1466211572253855744, 'root', '2021-12-02 09:07:47', NULL, NULL),
       (1517115061393559552, '用户列表', 'admin:user:view', '/system/user/page', 3, '', 1, 1,
        '查询用户列表，分页查询用户列表', 1466209998387417088, 'root', '2022-04-21 20:16:32', NULL, NULL),
       (1518753423003095040, '日志管理', 'admin:log', '/system/log', 2, NULL, 1, 1, '日志管理相关操作', 1, 'root',
        '2022-04-26 08:46:48', NULL, NULL),
       (1518754450641129472, '权限添加', 'admin:permission:add', '/system/permission/add', 3, NULL, 1, 1,
        '权限添加控制', 1466211672942317568, 'root', '2022-04-26 08:50:53', NULL, NULL),
       (1518754705013084160, '日志列表', 'admin:log:view', '/system/log/page', 3, '', 1, 1,
        '查询日志列表，分页查询日志列表', 1518753423003095040, 'root', '2022-04-26 08:51:53', NULL, NULL),
       (1518756469053460480, '日志列表导出', 'admin:log:export', '/system/log/export', 3, '', 1, 1, '导出日志列表',
        1518753423003095040, 'root', '2022-04-26 08:58:54', NULL, NULL),
       (1575013350704812032, '权限修改', 'admin:permission:update', '/system/permission/update', 3, NULL, 1, 1,
        '权限修改控制', 1466211672942317568, 'root', '2022-09-28 14:43:40', NULL, NULL),
       (1575014929319202816, '权限删除', 'admin:permission:delete', '/system/permission/delete', 3, NULL, 1, 1,
        '权限删除控制', 1466211672942317568, 'root', '2022-09-28 14:49:56', NULL, NULL),
       (1575015412792430592, '权限列表', 'admin:permission:view', '/system/permission/page', 3, NULL, 1, 1,
        '查询权限列表，树形结构查询权限列表', 1466211672942317568, 'root', '2022-09-28 14:51:51', NULL, NULL),
       (1575034984991952896, '角色列表', 'admin:role:view', '/system/role/page', 3, NULL, 1, 1,
        '查询角色列表，分页查询角色列表', 1466211572253855744, 'root', '2022-09-28 16:09:38', NULL, NULL),
       (1575036043139354624, '角色更新', 'admin:role:update', '/system/role/update', 3, NULL, 1, 1, '角色更新控制',
        1466211572253855744, 'root', '2022-09-28 16:13:50', NULL, NULL),
       (1575036116166381568, '角色删除', 'admin:role:delete', '/system/role/delete', 3, NULL, 1, 1, '角色删除控制',
        1466211572253855744, 'root', '2022-09-28 16:14:08', NULL, NULL);
/*!40000 ALTER TABLE `sys_permission`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_platform_log`
--

DROP TABLE IF EXISTS `sys_platform_log`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_platform_log`
(
    `log_id`             bigint unsigned NOT NULL AUTO_INCREMENT,
    `request_uri`        varchar(255)                                            DEFAULT NULL COMMENT 'uri',
    `request_url`        varchar(255)                                            DEFAULT NULL COMMENT 'url',
    `request_method`     char(8) CHARACTER SET utf8 COLLATE utf8_general_ci      DEFAULT NULL COMMENT '请求方法',
    `request_params`     varchar(511)                                            DEFAULT NULL COMMENT '请求参数',
    `class_name`         varchar(255)                                            DEFAULT NULL COMMENT '请求类',
    `method_name`        varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '请求函数',
    `method_type`        tinyint                                                 DEFAULT NULL COMMENT '操作类型 增1删2改3查4',
    `method_description` varchar(255)                                            DEFAULT NULL COMMENT '操作描述',
    `server_ip`          varchar(55)                                             DEFAULT NULL COMMENT '服务器地址',
    `client_ip`          varchar(55)                                             DEFAULT NULL COMMENT '客户端地址',
    `is_success`         tinyint                                                 DEFAULT NULL COMMENT '是否成功',
    `is_login`           tinyint                                                 DEFAULT NULL COMMENT '是否为登录请求',
    `spend_time`         bigint                                                  DEFAULT NULL COMMENT '耗时',
    `operation_userid`   bigint                                                  DEFAULT NULL COMMENT '操作用户账号',
    `operation_username` varchar(100)                                            DEFAULT NULL COMMENT '操作用户名称',
    `operation_time`     timestamp       NULL                                    DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`log_id`) USING BTREE,
    KEY `idx_sl_userid` (`operation_userid`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1516320405984661533
  DEFAULT CHARSET = utf8mb3
  ROW_FORMAT = DYNAMIC COMMENT ='系统日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_platform_log`
--

LOCK TABLES `sys_platform_log` WRITE;
/*!40000 ALTER TABLE `sys_platform_log`
    DISABLE KEYS */;
INSERT INTO `sys_platform_log`
VALUES (1514625172097740802, '/mall-admin/admin/user/page', 'http://192.168.1.4:9090/mall-admin/admin/user/page', 'GET',
        '{\"ps\":10,\"pn\":1}', 'com.flipped.mall.admin.controller.SysUserController', 'listUserWithPage()', 4,
        '分页获取用户列表', '127.0.0.1', '0:0:0:0:0:0:0:1%0', 1, 0, 40, 1463452828315029504, 'wangjie',
        '2022-04-14 23:22:36'),
       (1514633982673711106, '/mall-admin/admin/role', 'http://192.168.1.4:9090/mall-admin/admin/role', 'POST',
        '{\"id\":1514633900226383872,\"roleName\":\"管理员\",\"roleCode\":\"admin\",\"description\":\"后台服务管理\",\"createBy\":null,\"createTime\":\"2022-04-14 23:57:17\",\"updateBy\":null,\"updateTime\":null}',
        'com.flipped.mall.admin.controller.SysRoleController', 'saveRole()', 1, '保存角色', '127.0.0.1',
        '0:0:0:0:0:0:0:1%0', 1, 0, 48, 1463452828315029504, 'wangjie', '2022-04-14 23:57:37'),
       (1516040569101811714, '/mall-admin/admin/user/page', 'http://192.168.1.4:9090/mall-admin/admin/user/page', 'GET',
        '{\"ps\":10,\"pn\":1}', 'com.flipped.mall.admin.controller.SysUserController', 'listUserWithPage()', 4,
        '分页获取用户列表', '192.168.1.4', '0:0:0:0:0:0:0:1%0', 1, 0, 47, 1463452828315029504, 'wangjie',
        '2022-04-18 21:06:53'),
       (1516320405984661505, '/mall-admin/admin/user/page', 'http://192.168.137.173:9090/mall-admin/admin/user/page',
        'GET', '{\"ps\":10,\"pn\":1}', 'com.flipped.mall.admin.controller.SysUserController',
        'listUserWithPage()', 4, '分页获取用户列表', '192.168.137.173', '0:0:0:0:0:0:0:1%0', 1, 0, 92,
        1463452828315029504, 'wangjie', '2022-04-19 15:38:51'),
       (1516320405984661506, '/mall-admin/admin/user/page', 'http://192.168.137.173:9090/mall-admin/admin/user/page',
        'GET', '{\"ps\":10,\"pn\":1}', 'com.flipped.mall.admin.controller.SysUserController',
        'listUserWithPage()', 4, '分页获取用户列表', '192.168.137.173', '0:0:0:0:0:0:0:1%0', 1, 0, 36,
        1463452828315029504, 'wangjie', '2022-04-19 19:13:41'),
       (1516320405984661507, '/mall-admin/admin/user/page', 'http://192.168.137.173:9090/mall-admin/admin/user/page',
        'GET', '{\"ps\":10,\"pn\":1}', 'com.flipped.mall.admin.controller.SysUserController',
        'listUserWithPage()', 4, '分页获取用户列表', '192.168.137.173', '0:0:0:0:0:0:0:1%0', 1, 0, 35,
        1463452828315029504, 'wangjie', '2022-04-19 19:14:42'),
       (1516320405984661508, '/mall-admin/admin/user/page', 'http://192.168.137.173:9090/mall-admin/admin/user/page',
        'GET', '{\"ps\":10,\"pn\":1}', 'com.flipped.mall.admin.controller.SysUserController',
        'listUserWithPage()', 4, '分页获取用户列表', '192.168.137.173', '0:0:0:0:0:0:0:1%0', 1, 0, 13,
        1463452828315029504, 'wangjie', '2022-04-19 19:14:47'),
       (1516320405984661509, '/mall-admin/admin/user/page', 'http://192.168.1.4:9090/mall-admin/admin/user/page', 'GET',
        '{\"ps\":10,\"pn\":1}', 'com.flipped.mall.admin.controller.SysUserController', 'listUserWithPage()', 4,
        '分页获取用户列表', '192.168.1.4', '0:0:0:0:0:0:0:1%0', 1, 0, 38, 1463452828315029504, 'wangjie',
        '2022-04-19 20:51:11'),
       (1516320405984661510, '/mall-admin/admin/user/page', 'http://192.168.1.4:9090/mall-admin/admin/user/page', 'GET',
        '{\"ps\":10,\"pn\":1}', 'com.flipped.mall.admin.controller.SysUserController', 'listUserWithPage()', 4,
        '分页获取用户列表', '192.168.1.4', '0:0:0:0:0:0:0:1%0', 1, 0, 40, 1463452828315029504, 'wangjie',
        '2022-04-19 21:09:11'),
       (1516320405984661511, '/mall-admin/admin/user/page', 'http://192.168.1.4:9090/mall-admin/admin/user/page', 'GET',
        '{\"ps\":10,\"pn\":1}', 'com.flipped.mall.admin.controller.SysUserController', 'listUserWithPage()', 4,
        '分页获取用户列表', '192.168.1.4', '0:0:0:0:0:0:0:1%0', 1, 0, 34, 1463452828315029504, 'wangjie',
        '2022-04-19 22:10:52'),
       (1516320405984661512, '/mall-admin/admin/user/page', 'http://192.168.137.173:9090/mall-admin/admin/user/page',
        'GET', '{\"ps\":10,\"pn\":1}', 'com.flipped.mall.admin.controller.SysUserController',
        'listUserWithPage()', 4, '分页获取用户列表', '192.168.137.173', '0:0:0:0:0:0:0:1%0', 1, 0, 38,
        1463452828315029504, 'wangjie', '2022-04-20 13:44:32'),
       (1516320405984661513, '/mall-admin/admin/permission', 'http://192.168.1.4:9090/mall-admin/admin/permission',
        'POST',
        '{\"permissionId\":1517115061393559552,\"permissionName\":\"用户列表\",\"permissionValue\":\"admin:user:view\",\"url\":\"\",\"type\":\"3\",\"parentId\":1466209998387417088,\"icon\":\"\",\"status\":0,\"sortNo\":1,\"description\":\"查询用户列表，分页查询用户列表\",\"createBy\":\"root\",\"createTime\":\"2022-04-21 20:16:31\",\"updateBy\":null,\"updateTime\":null}',
        'com.flipped.mall.admin.controller.SysPermissionController', 'savePermission()', 1, '保存权限',
        '192.168.1.4', '0:0:0:0:0:0:0:1%0', 1, 0, 27, 1, 'root', '2022-04-21 20:16:32'),
       (1516320405984661514, '/mall-admin/admin/permission', 'http://192.168.1.4:9090/mall-admin/admin/permission',
        'POST',
        '{\"permissionId\":1517118626837172224,\"permissionName\":\"用户列表\",\"permissionValue\":\"admin:user:view\",\"url\":\"\",\"type\":\"3\",\"parentId\":1466209998387417088,\"icon\":\"\",\"status\":1,\"sortNo\":1,\"description\":\"查询用户列表，分页查询用户列表\",\"createBy\":\"root\",\"createTime\":\"2022-04-21 20:30:41\",\"updateBy\":null,\"updateTime\":null}',
        'com.flipped.mall.admin.controller.SysPermissionController', 'savePermission()', 1, '保存权限',
        '192.168.1.4', '0:0:0:0:0:0:0:1%0', 1, 0, 29, 1, 'root', '2022-04-21 20:30:42'),
       (1516320405984661515, '/mall-admin/admin/user/page', 'http://192.168.1.4:9090/mall-admin/admin/user/page', 'GET',
        '{\"ps\":10,\"pn\":1}', 'com.flipped.mall.admin.controller.SysUserController', 'listUserWithPage()', 4,
        '分页获取用户列表', '192.168.1.4', '0:0:0:0:0:0:0:1%0', 1, 0, 37, 1, 'root', '2022-04-21 20:42:10'),
       (1516320405984661516, '/mall-admin/admin/permission', 'http://192.168.1.2:9090/mall-admin/admin/permission',
        'POST',
        '{\"permissionId\":1518754705013084160,\"permissionName\":\"日志列表\",\"permissionValue\":\"admin:log:view\",\"url\":\"\",\"type\":3,\"parentId\":1466209998387417088,\"icon\":\"\",\"status\":1,\"sortNo\":1,\"description\":\"查询日志列表，分页查询日志列表\",\"createBy\":\"root\",\"createTime\":\"2022-04-26 08:51:53\",\"updateBy\":null,\"updateTime\":null}',
        'com.flipped.mall.admin.controller.SysPermissionController', 'savePermission()', 1, '保存权限',
        '127.0.0.1', '0:0:0:0:0:0:0:1%0', 1, 0, 30, 1, 'root', '2022-04-26 08:51:53'),
       (1516320405984661517, '/mall-admin/admin/permission', 'http://192.168.1.2:9090/mall-admin/admin/permission',
        'POST',
        '{\"permissionId\":1518756469053460480,\"permissionName\":\"日志列表\",\"permissionValue\":\"admin:log:export\",\"url\":\"\",\"type\":3,\"parentId\":1518753423003095040,\"icon\":\"\",\"status\":1,\"sortNo\":1,\"description\":\"导出日志列表\",\"createBy\":\"root\",\"createTime\":\"2022-04-26 08:58:54\",\"updateBy\":null,\"updateTime\":null}',
        'com.flipped.mall.admin.controller.SysPermissionController', 'savePermission()', 1, '保存权限',
        '127.0.0.1', '0:0:0:0:0:0:0:1%0', 1, 0, 27, 1, 'root', '2022-04-26 08:58:54'),
       (1516320405984661518, '/mall-admin/admin/user/page', 'http://192.168.1.4:9090/mall-admin/admin/user/page', 'GET',
        '{\"ps\":10,\"pn\":1}', 'com.flipped.mall.admin.controller.SysUserController', 'listUserWithPage()', 4,
        '分页获取用户列表', '192.168.1.4', '127.0.0.1', 1, 0, 38, 1, 'root', '2022-07-20 22:34:08'),
       (1516320405984661519, '/mall-admin/admin/user/page', 'http://192.168.1.4:9090/mall-admin/admin/user/page', 'GET',
        '{\"ps\":10,\"pn\":1}', 'com.flipped.mall.admin.controller.SysUserController', 'listUserWithPage()', 4,
        '分页获取用户列表', '192.168.1.4', '127.0.0.1', 1, 0, 28, 1, 'root', '2022-07-24 21:15:41'),
       (1516320405984661520, '/mall-admin/admin/user/page', 'http://192.168.1.4:9090/mall-admin/admin/user/page', 'GET',
        '{\"ps\":10,\"pn\":1}', 'com.flipped.mall.admin.controller.SysUserController', 'listUserWithPage()', 4,
        '分页获取用户列表', '192.168.1.4', '127.0.0.1', 1, 0, 42, 1, 'root', '2022-07-24 21:31:43'),
       (1516320405984661521, '/mall-admin/admin/user/page', 'http://192.168.1.4:9090/mall-admin/admin/user/page', 'GET',
        '{\"ps\":10,\"pn\":1}', 'com.flipped.mall.admin.controller.SysUserController', 'listUserWithPage()', 4,
        '分页获取用户列表', '192.168.1.4', '127.0.0.1', 1, 0, 46, 1, 'root', '2022-07-24 21:44:04'),
       (1516320405984661522, '/mall-admin/admin/user/page', 'http://192.168.1.4:9090/mall-admin/admin/user/page', 'GET',
        '{\"ps\":10,\"pn\":1}', 'com.flipped.mall.admin.controller.SysUserController', 'listUserWithPage()', 4,
        '分页获取用户列表', '192.168.1.4', '127.0.0.1', 1, 0, 37, 1, 'root', '2022-07-24 21:49:52'),
       (1516320405984661523, '/mall-admin/admin/user/page', 'http://192.168.1.4:9090/mall-admin/admin/user/page', 'GET',
        '{\"ps\":10,\"pn\":1}', 'com.flipped.mall.admin.controller.SysUserController', 'listUserWithPage()', 4,
        '分页获取用户列表', '192.168.1.4', '127.0.0.1', 1, 0, 19, 1, 'root', '2022-07-24 21:50:08'),
       (1516320405984661524, '/mall-admin/admin/user/page', 'http://192.168.1.4:9090/mall-admin/admin/user/page', 'GET',
        '{\"ps\":10,\"pn\":1}', 'com.flipped.mall.admin.controller.SysUserController', 'listUserWithPage()', 4,
        '分页获取用户列表', '192.168.1.4', '127.0.0.1', 1, 0, 69, 1, 'root', '2022-07-24 21:52:18'),
       (1516320405984661525, '/mall-admin/admin/user/page', 'http://192.168.1.4:9090/mall-admin/admin/user/page', 'GET',
        '{\"ps\":10,\"pn\":1}', 'com.flipped.mall.admin.controller.SysUserController', 'listUserWithPage()', 4,
        '分页获取用户列表', '192.168.1.4', '127.0.0.1', 1, 0, 20, 1, 'root', '2022-07-24 21:52:44'),
       (1516320405984661526, '/mall-admin/admin/user/page', 'http://192.168.1.4:9090/mall-admin/admin/user/page', 'GET',
        '{\"ps\":10,\"pn\":1}', 'com.flipped.mall.admin.controller.SysUserController', 'listUserWithPage()', 4,
        '分页获取用户列表', '192.168.1.4', '127.0.0.1', 1, 0, 9, 1, 'root', '2022-07-24 21:52:44'),
       (1516320405984661527, '/mall-admin/admin/user/page', 'http://192.168.1.4:9090/mall-admin/admin/user/page', 'GET',
        '{\"ps\":10,\"pn\":1}', 'com.flipped.mall.admin.controller.SysUserController', 'listUserWithPage()', 4,
        '分页获取用户列表', '192.168.1.4', '127.0.0.1', 1, 0, 38, 1, 'root', '2022-07-24 21:55:42'),
       (1516320405984661528, '/mall-admin/admin/user/page', 'http://192.168.1.4:9090/mall-admin/admin/user/page', 'GET',
        '{\"ps\":10,\"pn\":1}', 'com.flipped.mall.admin.controller.SysUserController', 'listUserWithPage()', 4,
        '分页获取用户列表', '192.168.1.4', '127.0.0.1', 1, 0, 6, 1, 'root', '2022-07-24 21:55:45'),
       (1516320405984661529, '/mall-admin/admin/user/page', 'http://192.168.1.4:9090/mall-admin/admin/user/page', 'GET',
        '{\"ps\":10,\"pn\":1}', 'com.flipped.mall.admin.controller.SysUserController', 'listUserWithPage()', 4,
        '分页获取用户列表', '192.168.1.4', '127.0.0.1', 1, 0, 14, 1, 'root', '2022-07-24 21:55:46'),
       (1516320405984661530, '/mall-admin/admin/user/page', 'http://192.168.1.4:9090/mall-admin/admin/user/page', 'GET',
        '{\"ps\":10,\"pn\":1}', 'com.flipped.mall.admin.controller.SysUserController', 'listUserWithPage()', 4,
        '分页获取用户列表', '192.168.1.4', '127.0.0.1', 1, 0, 10589, 1, 'root', '2022-07-24 21:59:04'),
       (1516320405984661531, '/mall-admin/admin/user/page', 'http://192.168.1.4:9090/mall-admin/admin/user/page', 'GET',
        '{\"ps\":10,\"pn\":1}', 'com.flipped.mall.admin.controller.SysUserController', 'listUserWithPage()', 4,
        '分页获取用户列表', '192.168.1.4', '127.0.0.1', 1, 0, 35, 1, 'root', '2022-07-24 22:02:48'),
       (1516320405984661532, '/mall-admin/admin/user/page', 'http://192.168.1.4:9090/mall-admin/admin/user/page', 'GET',
        '{\"ps\":10,\"pn\":1}', 'com.flipped.mall.admin.controller.SysUserController', 'listUserWithPage()', 4,
        '分页获取用户列表', '192.168.1.4', '127.0.0.1', 1, 0, 11, 1, 'root', '2022-07-24 22:02:58');
/*!40000 ALTER TABLE `sys_platform_log`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role`
(
    `role_id`     bigint unsigned NOT NULL COMMENT '主键id',
    `role_code`   varchar(100)     DEFAULT NULL COMMENT '角色编码',
    `role_name`   varchar(200)     DEFAULT NULL COMMENT '角色名称',
    `enable`      tinyint unsigned DEFAULT NULL COMMENT '是否启用',
    `description` varchar(255)     DEFAULT NULL COMMENT '描述',
    `create_by`   varchar(32)      DEFAULT NULL COMMENT '创建人',
    `create_time` datetime         DEFAULT NULL COMMENT '创建时间',
    `update_by`   varchar(32)      DEFAULT NULL COMMENT '更新人',
    `update_time` datetime         DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`role_id`) USING BTREE,
    UNIQUE KEY `uniq_sr_role_code` (`role_code`),
    KEY `idx_sr_role_code` (`role_code`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  ROW_FORMAT = DYNAMIC COMMENT ='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role`
    DISABLE KEYS */;
INSERT INTO `sys_role`
VALUES (1468855748988637184, 'ware_manager', '库存管理员', NULL, '库存服务管理', 'admin', '2021-12-09 16:11:15', NULL,
        NULL),
       (1468855856593506304, 'order_manager', '订单管理员', NULL, '订单服务管理', 'admin', '2021-12-09 16:11:41', NULL,
        NULL),
       (1514631221110509568, 'root', '超级管理员', NULL, '拥有所有权限', NULL, '2022-04-14 23:46:38', NULL, NULL),
       (1514633900226383872, 'admin', '管理员', NULL, '后台服务管理', NULL, '2022-04-14 23:57:17', NULL, NULL);
/*!40000 ALTER TABLE `sys_role`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_permission`
--

DROP TABLE IF EXISTS `sys_role_permission`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_permission`
(
    `id`            bigint NOT NULL,
    `role_id`       bigint DEFAULT NULL COMMENT '角色id',
    `permission_id` bigint DEFAULT NULL COMMENT '权限id',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `index_group_role_per_id` (`role_id`, `permission_id`) USING BTREE,
    KEY `index_group_role_id` (`role_id`) USING BTREE,
    KEY `index_group_per_id` (`permission_id`) USING BTREE,
    KEY `idx_srp_role_per_id` (`role_id`, `permission_id`) USING BTREE,
    KEY `idx_srp_role_id` (`role_id`) USING BTREE,
    KEY `idx_srp_permission_id` (`permission_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  ROW_FORMAT = DYNAMIC COMMENT ='角色权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_permission`
--

LOCK TABLES `sys_role_permission` WRITE;
/*!40000 ALTER TABLE `sys_role_permission`
    DISABLE KEYS */;
INSERT INTO `sys_role_permission`
VALUES (1, 1514631221110509568, 1466209998387417088),
       (2, 1514631221110509568, 1466211572253855744),
       (3, 1514631221110509568, 1466211672942317568),
       (4, 1514631221110509568, 1466212316054949888),
       (5, 1514631221110509568, 1466212464789164032);
/*!40000 ALTER TABLE `sys_role_permission`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_third_account`
--

DROP TABLE IF EXISTS `sys_third_account`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_third_account`
(
    `id`              bigint NOT NULL COMMENT '编号',
    `userid`          bigint       DEFAULT NULL COMMENT '第三方登录id',
    `avatar`          varchar(255) DEFAULT NULL COMMENT '头像',
    `status`          tinyint(1)   DEFAULT NULL COMMENT '状态(1-正常,2-冻结)',
    `is_del`          tinyint(1)   DEFAULT NULL COMMENT '删除状态(0-正常,1-已删除)',
    `realname`        varchar(100) DEFAULT NULL COMMENT '真实姓名',
    `third_user_uuid` varchar(100) DEFAULT NULL COMMENT '第三方账号',
    `third_userid`    varchar(100) DEFAULT NULL COMMENT '第三方app用户账号',
    `third_type`      varchar(50)  DEFAULT NULL COMMENT '登录来源',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uniq_sys_third_account_third_type_third_user_id` (`third_type`, `third_userid`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_third_account`
--

LOCK TABLES `sys_third_account` WRITE;
/*!40000 ALTER TABLE `sys_third_account`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_third_account`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user`
(
    `userid`      varchar(32) NOT NULL COMMENT '主键id',
    `username`    varchar(100)                                           DEFAULT NULL COMMENT '登录账号',
    `real_name`   varchar(100)                                           DEFAULT NULL COMMENT '真实姓名',
    `password`    varchar(255)                                           DEFAULT NULL COMMENT '密码',
    `avatar`      varchar(511)                                           DEFAULT NULL COMMENT '头像',
    `birthday`    date                                                   DEFAULT NULL COMMENT '生日',
    `gender`      tinyint(1)                                             DEFAULT NULL COMMENT '性别(0-默认保密,1-男,2-女)',
    `email`       varchar(45)                                            DEFAULT NULL COMMENT '电子邮件',
    `mobile`      varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '电话',
    `enable`      tinyint(1)                                             DEFAULT NULL COMMENT '状态(1-正常,2-冻结)',
    `create_by`   varchar(32)                                            DEFAULT NULL COMMENT '创建人',
    `create_time` datetime                                               DEFAULT NULL COMMENT '创建时间',
    `update_by`   varchar(32)                                            DEFAULT NULL COMMENT '更新人',
    `update_time` datetime                                               DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`userid`),
    UNIQUE KEY `uniq_su_email` (`email`),
    UNIQUE KEY `uniq_su_username` (`username`),
    UNIQUE KEY `uniq_su_phone` (`mobile`),
    KEY `idx_su_username` (`username`),
    KEY `idx_su_status` (`enable`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  ROW_FORMAT = DYNAMIC COMMENT ='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user`
    DISABLE KEYS */;
INSERT INTO `sys_user`
VALUES ('1', 'root', '超级管理员', '$2a$10$gFobhlfR21ZDCx/GBsA20uQPfSp5QgJs/jNEaALVzMUK5X7CAVrly',
        'https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fup.enterdesk.com%2Fedpic%2F69%2F5f%2Fa7%2F695fa728c162c2cb073d7e0079dfdee5.jpeg&refer=http%3A%2F%2Fup.enterdesk.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1647497570&t=ec87f37033839d93bfc8b10be8031e31',
        '1998-08-20', 0, '0000000@163.com', '13312345678', 1, '', '2022-02-15 14:20:17', NULL, '2022-04-21 14:11:10'),
       ('1463452828315029504', 'wangjie', '王杰', '$2a$10$pabNigivrJNadv7.CGus8.tfqnZ.jpWeYp/C2UoR2Y8.isnRHrykS',
        'https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg9.doubanio.com%2Fview%2Fgroup_topic%2Fl%2Fpublic%2Fp192244425.jpg&refer=http%3A%2F%2Fimg9.doubanio.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1647422947&t=56358da60f52c59b03eec3abc7dea6bf',
        '1998-08-30', 0, '18763096838@163.com', '18763096838', 1, 'admin', '2021-11-24 18:21:59', NULL, NULL),
       ('1493468819329519616', 'admin', '管理员', '$2a$10$KN7XTwFtpUwVgoVJrq2VZOthDHxfSnf0de5kVx7cwSi.Y/uPP8Ty.',
        'https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fup.enterdesk.com%2Fedpic%2F69%2F5f%2Fa7%2F695fa728c162c2cb073d7e0079dfdee5.jpeg&refer=http%3A%2F%2Fup.enterdesk.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1647497570&t=ec87f37033839d93bfc8b10be8031e31',
        '1998-08-20', 0, '123456789@163.com', '13390908080', 1, 'root', '2022-02-15 14:14:49', NULL, NULL),
       ('1493469684618629120', 'zhangsan', '张三', '$2a$10$OAQQ3a6daUk8.cngekzUquQUO6DYCg9egyB2U3TqsMeXHqaGyaxHK',
        'https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fup.enterdesk.com%2Fedpic%2F69%2F5f%2Fa7%2F695fa728c162c2cb073d7e0079dfdee5.jpeg&refer=http%3A%2F%2Fup.enterdesk.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1647497570&t=ec87f37033839d93bfc8b10be8031e31',
        '1998-08-20', 0, '784217549@163.com', '13390907070', 1, 'admin', '2022-02-15 14:18:15', NULL, NULL),
       ('1493470641809133568', 'lisi', '李四', '$2a$10$.3ViS1pYsPZO5Pz6g83R0OTGEa6SmdEqlFZUisvCETvV5hIISn0/q',
        'https://img0.baidu.com/it/u=4044314804,383808458&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500', '1996-09-20', 0,
        '000123141@163.com', '13333335678', 1, 'admin', '2022-02-15 14:22:03', NULL, NULL),
       ('1493471308288233472', 'wangwu', '王五', '$2a$10$ddYGFWXrGy5sCuqaO4qM7eVSqEagGbxAPfibx3QFCGToWswwe2AbW',
        'https://img0.baidu.com/it/u=4044314804,383808458&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500', '1996-09-20', 0,
        '9999999@qq.com', '13377777777', 1, 'admin', '2022-02-15 14:24:42', NULL, NULL),
       ('1598262596522348544', 'xiaomei', '小美', '$2a$10$GEqsyUwoIN5.cKxbxeOSNOrdp6bow9uE/nCgAM7aL68A2tYA.RJA6',
        'https://img0.baidu.com/it/u=4044314804,383808458&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500', '2022-12-01', 0,
        '99999@163.com', '17565439876', 0, 'root', '2022-12-01 18:27:52', NULL, NULL);
/*!40000 ALTER TABLE `sys_user`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role`
(
    `id`      bigint NOT NULL COMMENT '主键id',
    `userid`  bigint DEFAULT NULL COMMENT '用户id',
    `role_id` bigint DEFAULT NULL COMMENT '角色id',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_sur_user_id` (`userid`) USING BTREE,
    KEY `idx_sur_role_id` (`role_id`) USING BTREE,
    KEY `idx_sur_user_role_id` (`userid`, `role_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  ROW_FORMAT = DYNAMIC COMMENT ='用户角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role`
    DISABLE KEYS */;
INSERT INTO `sys_user_role`
VALUES (1, 1463452828315029504, 1514631221110509568);
/*!40000 ALTER TABLE `sys_user_role`
    ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE = @OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE = @OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS = @OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION = @OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES = @OLD_SQL_NOTES */;

-- Dump completed on 2023-02-02 23:40:14
