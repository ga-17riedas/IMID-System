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

 Date: 02/03/2025 14:29:09
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for system_logs
-- ----------------------------
DROP TABLE IF EXISTS `system_logs`;
CREATE TABLE `system_logs`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID，主键',
  `timestamp` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '日志记录时间',
  `level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '日志级别：ERROR/WARNING/INFO/DEBUG',
  `source` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '日志来源（模块/类名）',
  `message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '日志内容',
  `stack_trace` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '错误堆栈信息（仅ERROR级别）',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '相关用户ID（如果有）',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作IP地址',
  `user_agent` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户代理信息',
  `details` varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_system_logs_timestamp`(`timestamp`) USING BTREE,
  INDEX `idx_system_logs_level`(`level`) USING BTREE,
  INDEX `idx_system_logs_level_timestamp`(`level`, `timestamp`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 36 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统日志表，记录系统运行状态和错误信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_logs
-- ----------------------------
INSERT INTO `system_logs` VALUES (1, '2025-03-01 10:29:29', 'INFO', 'SystemStartup', '系统启动成功', NULL, NULL, '127.0.0.1', 'Server/1.0', '系统版本: 1.0.0');
INSERT INTO `system_logs` VALUES (2, '2025-03-01 11:29:29', 'INFO', 'UserService', '用户登录成功', NULL, 1, '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', '管理员登录');
INSERT INTO `system_logs` VALUES (3, '2025-03-01 12:29:29', 'INFO', 'UserService', '用户登录成功', NULL, 2, '192.168.1.101', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', '医生登录');
INSERT INTO `system_logs` VALUES (4, '2025-03-01 13:29:29', 'WARNING', 'SecurityService', '检测到多次失败的登录尝试', NULL, NULL, '192.168.1.102', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', '5次失败尝试');
INSERT INTO `system_logs` VALUES (5, '2025-03-01 14:29:29', 'ERROR', 'DatabaseService', '数据库连接失败', 'java.sql.SQLException: Connection refused', NULL, '127.0.0.1', 'Server/1.0', '尝试重新连接');
INSERT INTO `system_logs` VALUES (6, '2025-03-01 15:29:29', 'INFO', 'DatabaseService', '数据库连接恢复', NULL, NULL, '127.0.0.1', 'Server/1.0', '自动恢复成功');
INSERT INTO `system_logs` VALUES (7, '2025-03-01 16:29:29', 'INFO', 'UserService', '用户登录成功', NULL, 3, '192.168.1.103', 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_7_1)', '患者登录');
INSERT INTO `system_logs` VALUES (8, '2025-03-01 17:29:29', 'INFO', 'MedicalService', '新增诊断报告', NULL, 2, '192.168.1.101', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', '报告ID: 1001');
INSERT INTO `system_logs` VALUES (9, '2025-03-01 18:29:29', 'DEBUG', 'ImageProcessingService', '图像处理完成', NULL, 2, '192.168.1.101', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', '处理时间: 2.3秒');
INSERT INTO `system_logs` VALUES (10, '2025-03-01 19:29:29', 'INFO', 'NotificationService', '发送通知成功', NULL, NULL, '127.0.0.1', 'Server/1.0', '发送给用户ID: 3');
INSERT INTO `system_logs` VALUES (11, '2025-03-01 20:29:29', 'WARNING', 'StorageService', '存储空间不足', NULL, NULL, '127.0.0.1', 'Server/1.0', '剩余空间: 15%');
INSERT INTO `system_logs` VALUES (12, '2025-03-01 21:29:29', 'INFO', 'UserService', '用户登录成功', NULL, 1, '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', '管理员登录');
INSERT INTO `system_logs` VALUES (13, '2025-03-01 22:29:29', 'INFO', 'AdminService', '系统配置更新', NULL, 1, '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', '更新缓存设置');
INSERT INTO `system_logs` VALUES (14, '2025-03-01 23:29:29', 'ERROR', 'EmailService', '邮件发送失败', 'javax.mail.MessagingException: Connection timeout', NULL, '127.0.0.1', 'Server/1.0', '收件人: patient@example.com');
INSERT INTO `system_logs` VALUES (15, '2025-03-02 00:29:29', 'INFO', 'BackupService', '系统备份开始', NULL, NULL, '127.0.0.1', 'Server/1.0', '完整备份');
INSERT INTO `system_logs` VALUES (16, '2025-03-02 01:29:29', 'INFO', 'BackupService', '系统备份完成', NULL, NULL, '127.0.0.1', 'Server/1.0', '备份大小: 1.2GB');
INSERT INTO `system_logs` VALUES (17, '2025-03-02 02:29:29', 'INFO', 'UserService', '用户登录成功', NULL, 4, '192.168.1.104', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', '医生登录');
INSERT INTO `system_logs` VALUES (18, '2025-03-02 03:29:29', 'DEBUG', 'AIService', 'AI模型加载成功', NULL, NULL, '127.0.0.1', 'Server/1.0', '模型版本: 2.3.0');
INSERT INTO `system_logs` VALUES (19, '2025-03-02 04:29:29', 'INFO', 'MedicalService', '新增诊断报告', NULL, 4, '192.168.1.104', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', '报告ID: 1002');
INSERT INTO `system_logs` VALUES (20, '2025-03-02 05:29:29', 'WARNING', 'PerformanceMonitor', 'CPU使用率过高', NULL, NULL, '127.0.0.1', 'Server/1.0', '使用率: 85%');
INSERT INTO `system_logs` VALUES (21, '2025-03-02 06:29:29', 'INFO', 'SchedulerService', '定时任务执行成功', NULL, NULL, '127.0.0.1', 'Server/1.0', '任务: 清理临时文件');
INSERT INTO `system_logs` VALUES (22, '2025-03-02 07:29:29', 'ERROR', 'AIService', 'AI诊断失败', 'java.lang.OutOfMemoryError: Java heap space', NULL, '127.0.0.1', 'Server/1.0', '图像ID: 5001');
INSERT INTO `system_logs` VALUES (23, '2025-03-02 08:29:29', 'INFO', 'SystemService', '系统资源释放', NULL, NULL, '127.0.0.1', 'Server/1.0', '释放内存: 500MB');
INSERT INTO `system_logs` VALUES (24, '2025-03-02 09:29:29', 'INFO', 'UserService', '用户登录成功', NULL, 5, '192.168.1.105', 'Mozilla/5.0 (iPad; CPU OS 14_7_1)', '患者登录');
INSERT INTO `system_logs` VALUES (25, '2025-03-02 09:39:29', 'INFO', 'UserService', '密码重置请求', NULL, NULL, '192.168.1.106', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', '用户邮箱: reset@example.com');
INSERT INTO `system_logs` VALUES (26, '2025-03-02 09:49:29', 'DEBUG', 'CacheService', '缓存刷新完成', NULL, NULL, '127.0.0.1', 'Server/1.0', '刷新项目: 用户数据');
INSERT INTO `system_logs` VALUES (27, '2025-03-02 09:59:29', 'INFO', 'UserService', '用户登出', NULL, 1, '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', '管理员登出');
INSERT INTO `system_logs` VALUES (28, '2025-03-02 10:09:29', 'WARNING', 'SecurityService', '检测到可疑访问', NULL, NULL, '192.168.1.110', 'Mozilla/5.0 (compatible; Googlebot/2.1)', '访问受限资源');
INSERT INTO `system_logs` VALUES (29, '2025-03-02 10:19:29', 'INFO', 'UserService', '用户登录成功', NULL, 1, '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', '管理员登录');
INSERT INTO `system_logs` VALUES (30, '2025-03-02 10:24:29', 'INFO', 'SystemMonitor', '系统状态正常', NULL, NULL, '127.0.0.1', 'Server/1.0', 'CPU: 45%, 内存: 60%, 存储: 75%');
INSERT INTO `system_logs` VALUES (31, '2025-03-02 10:44:08', 'INFO', 'UserService', '新用户注册', NULL, NULL, '192.168.1.120', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', '用户名: newuser1');
INSERT INTO `system_logs` VALUES (32, '2025-03-02 10:45:08', 'DEBUG', 'AuthService', '验证码发送', NULL, NULL, '192.168.1.121', 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_7_1)', '手机号: 139****1234');
INSERT INTO `system_logs` VALUES (33, '2025-03-02 10:46:08', 'WARNING', 'FileService', '上传文件过大', NULL, 2, '192.168.1.122', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', '文件大小: 15MB, 限制: 10MB');
INSERT INTO `system_logs` VALUES (34, '2025-03-02 10:47:08', 'INFO', 'PatientService', '患者信息更新', NULL, 5, '192.168.1.123', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', '患者ID: 1');
INSERT INTO `system_logs` VALUES (35, '2025-03-02 10:48:08', 'INFO', 'AdminService', '系统设置更新', NULL, 1, '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', '更新项: 安全设置');

SET FOREIGN_KEY_CHECKS = 1;
