/*
 Navicat Premium Data Transfer

 Source Server         : imid
 Source Server Type    : MySQL
 Source Server Version : 50727
 Source Host           : localhost:3306
 Source Schema         : imid_db

 Target Server Type    : MySQL
 Target Server Version : 50727
 File Encoding         : 65001

 Date: 02/03/2025 14:29:14
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for system_settings
-- ----------------------------
DROP TABLE IF EXISTS `system_settings`;
CREATE TABLE `system_settings`  (
  `setting_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '设置项ID，主键，自增',
  `setting_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '设置项键名，用于程序中引用该设置项，如\"system.name\"',
  `setting_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '设置项的值，可以是文本、数字、布尔值等',
  `setting_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '设置项值的数据类型：STRING、NUMBER、BOOLEAN、JSON等',
  `setting_group` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '设置项分组：BASIC(基础设置)、SECURITY(安全设置)、LOG(日志设置)、STORAGE(存储设置)、BACKUP(备份设置)',
  `display_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '设置项的显示名称，用于在界面上显示',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设置项的描述信息，说明该设置项的用途',
  `default_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '设置项的默认值',
  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '可选值列表，JSON格式，如下拉选项',
  `is_system` tinyint(1) NULL DEFAULT 1 COMMENT '是否为系统设置项，TRUE表示不可由用户删除',
  `is_readonly` tinyint(1) NULL DEFAULT 0 COMMENT '是否为只读设置项，TRUE表示用户不可修改',
  `validation_rule` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '验证规则，用于验证设置值的有效性，如正则表达式',
  `order_num` int(11) NULL DEFAULT 0 COMMENT '排序号，控制在界面上的显示顺序',
  `created_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `created_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `updated_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  PRIMARY KEY (`setting_id`) USING BTREE,
  UNIQUE INDEX `uk_setting_key`(`setting_key`) USING BTREE COMMENT '设置键名唯一索引'
) ENGINE = InnoDB AUTO_INCREMENT = 36 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统设置表，存储系统的各项配置参数' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_settings
-- ----------------------------
INSERT INTO `system_settings` VALUES (1, 'system.name', 'IMID医学影像诊断系统', 'STRING', 'BASIC', '系统名称', '系统的显示名称，用于前端界面展示', 'IMID医学影像诊断系统', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);
INSERT INTO `system_settings` VALUES (2, 'system.description', '基于人工智能的医学影像辅助诊断平台', 'STRING', 'BASIC', '系统描述', '系统的简要描述', '基于人工智能的医学影像辅助诊断平台', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);
INSERT INTO `system_settings` VALUES (3, 'system.logo', '/uploads/logo/default_logo.png', 'STRING', 'BASIC', '系统Logo', '系统Logo的路径', '/uploads/logo/default_logo.png', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);
INSERT INTO `system_settings` VALUES (4, 'system.maintenance.mode', 'false', 'BOOLEAN', 'BASIC', '维护模式', '系统是否处于维护模式，开启时普通用户无法访问系统', 'false', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);
INSERT INTO `system_settings` VALUES (5, 'security.password.min_length', '8', 'NUMBER', 'SECURITY', '密码最小长度', '用户密码的最小长度要求', '8', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);
INSERT INTO `system_settings` VALUES (6, 'security.password.require_special_char', 'true', 'BOOLEAN', 'SECURITY', '密码需要特殊字符', '密码是否必须包含特殊字符', 'true', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);
INSERT INTO `system_settings` VALUES (7, 'security.password.require_number', 'true', 'BOOLEAN', 'SECURITY', '密码需要数字', '密码是否必须包含数字', 'true', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);
INSERT INTO `system_settings` VALUES (8, 'security.password.require_uppercase', 'true', 'BOOLEAN', 'SECURITY', '密码需要大写字母', '密码是否必须包含大写字母', 'true', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);
INSERT INTO `system_settings` VALUES (9, 'security.login.max_attempts', '5', 'NUMBER', 'SECURITY', '最大登录尝试次数', '用户连续登录失败的最大次数，超过将锁定账户', '5', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);
INSERT INTO `system_settings` VALUES (10, 'security.login.lock_duration', '30', 'NUMBER', 'SECURITY', '账户锁定时长', '账户锁定的时长（分钟）', '30', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);
INSERT INTO `system_settings` VALUES (11, 'security.session.timeout', '120', 'NUMBER', 'SECURITY', '会话超时时间', '用户会话的超时时间（分钟）', '120', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);
INSERT INTO `system_settings` VALUES (12, 'security.jwt.expiration', '86400', 'NUMBER', 'SECURITY', 'JWT令牌过期时间', 'JWT令牌的有效期（秒）', '86400', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);
INSERT INTO `system_settings` VALUES (13, 'security.2fa.enabled', 'false', 'BOOLEAN', 'SECURITY', '启用两因素认证', '是否启用两因素认证', 'false', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);
INSERT INTO `system_settings` VALUES (14, 'log.retention.days', '90', 'NUMBER', 'LOG', '日志保留天数', '系统日志的保留天数', '90', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);
INSERT INTO `system_settings` VALUES (15, 'log.level', 'INFO', 'STRING', 'LOG', '日志级别', '系统日志记录的级别：DEBUG, INFO, WARN, ERROR', 'INFO', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);
INSERT INTO `system_settings` VALUES (16, 'log.user_action.enabled', 'true', 'BOOLEAN', 'LOG', '记录用户操作', '是否记录用户的操作行为', 'true', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);
INSERT INTO `system_settings` VALUES (17, 'log.login.record', 'true', 'BOOLEAN', 'LOG', '记录登录日志', '是否记录用户的登录行为', 'true', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);
INSERT INTO `system_settings` VALUES (18, 'storage.type', 'LOCAL', 'STRING', 'STORAGE', '存储类型', '医学影像的存储类型：LOCAL(本地存储), S3(对象存储)', 'LOCAL', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);
INSERT INTO `system_settings` VALUES (19, 'storage.local.path', '/uploads/images', 'STRING', 'STORAGE', '本地存储路径', '医学影像的本地存储路径', '/uploads/images', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);
INSERT INTO `system_settings` VALUES (20, 'storage.s3.endpoint', '', 'STRING', 'STORAGE', 'S3存储端点', 'S3兼容存储的端点地址', '', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);
INSERT INTO `system_settings` VALUES (21, 'storage.s3.bucket', '', 'STRING', 'STORAGE', 'S3存储桶', 'S3存储桶名称', '', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);
INSERT INTO `system_settings` VALUES (22, 'storage.s3.access_key', '', 'STRING', 'STORAGE', 'S3访问密钥', 'S3访问密钥ID', '', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);
INSERT INTO `system_settings` VALUES (23, 'storage.s3.secret_key', '', 'STRING', 'STORAGE', 'S3秘密密钥', 'S3秘密访问密钥', '', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);
INSERT INTO `system_settings` VALUES (24, 'storage.max_upload_size', '10', 'NUMBER', 'STORAGE', '最大上传大小', '单个文件最大上传大小（MB）', '10', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);
INSERT INTO `system_settings` VALUES (25, 'storage.allowed_file_types', 'jpg,jpeg,png,dcm,dicom', 'STRING', 'STORAGE', '允许的文件类型', '允许上传的文件类型，逗号分隔', 'jpg,jpeg,png,dcm,dicom', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);
INSERT INTO `system_settings` VALUES (26, 'backup.auto.enabled', 'true', 'BOOLEAN', 'BACKUP', '启用自动备份', '是否启用自动备份功能', 'true', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);
INSERT INTO `system_settings` VALUES (27, 'backup.frequency', 'DAILY', 'STRING', 'BACKUP', '备份频率', '自动备份的频率：DAILY(每天), WEEKLY(每周), MONTHLY(每月)', 'DAILY', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);
INSERT INTO `system_settings` VALUES (28, 'backup.time', '02:00', 'STRING', 'BACKUP', '备份时间', '进行备份的时间（24小时制）', '02:00', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);
INSERT INTO `system_settings` VALUES (29, 'backup.retention.count', '7', 'NUMBER', 'BACKUP', '备份保留数量', '保留的备份文件数量', '7', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);
INSERT INTO `system_settings` VALUES (30, 'backup.include.images', 'true', 'BOOLEAN', 'BACKUP', '备份包含影像', '备份是否包含医学影像文件', 'true', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);
INSERT INTO `system_settings` VALUES (31, 'ai.model.path', '/models/yolov8n.onnx', 'STRING', 'AI', '模型路径', 'AI模型文件的路径', '/models/yolov8n.onnx', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);
INSERT INTO `system_settings` VALUES (32, 'ai.model.version', '1.0.0', 'STRING', 'AI', '模型版本', '当前使用的AI模型版本', '1.0.0', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);
INSERT INTO `system_settings` VALUES (33, 'ai.detection.confidence', '0.5', 'NUMBER', 'AI', '检测置信度阈值', '目标检测的置信度阈值，低于此值的检测结果将被过滤', '0.5', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);
INSERT INTO `system_settings` VALUES (34, 'ai.max_detections', '100', 'NUMBER', 'AI', '最大检测数量', '单张影像的最大检测结果数量', '100', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);
INSERT INTO `system_settings` VALUES (35, 'ai.processing.timeout', '30', 'NUMBER', 'AI', '处理超时时间', '单张影像的AI处理超时时间（秒）', '30', NULL, 1, 0, NULL, 0, '2025-03-02 14:08:32', '2025-03-02 14:08:32', NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
