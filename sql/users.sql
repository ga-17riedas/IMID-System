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

 Date: 02/03/2025 14:29:19
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID，主键',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名，唯一',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码，加密存储',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电子邮箱',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ROLE_PATIENT' COMMENT '用户角色：ROLE_ADMIN/ROLE_DOCTOR/ROLE_PATIENT',
  `enabled` tinyint(1) NULL DEFAULT 1 COMMENT '账号是否启用',
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `last_login` datetime(6) NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username`) USING BTREE,
  INDEX `idx_user_username`(`username`) USING BTREE,
  INDEX `idx_user_email`(`email`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 48 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户基本信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'admin@imid.com', 'ROLE_ADMIN', 1, '2025-03-01 19:44:17', '2025-03-01 19:44:17', NULL, 'ACTIVE');
INSERT INTO `users` VALUES (2, 'doctor1', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'doctor1@hospital.com', 'ROLE_DOCTOR', 1, '2025-03-01 19:44:17', '2025-03-01 19:44:17', NULL, NULL);
INSERT INTO `users` VALUES (3, 'doctor2', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'doctor2@hospital.com', 'ROLE_DOCTOR', 1, '2025-03-01 19:44:17', '2025-03-01 19:44:17', NULL, NULL);
INSERT INTO `users` VALUES (4, 'doctor3', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'doctor3@hospital.com', 'ROLE_DOCTOR', 1, '2025-03-01 19:44:17', '2025-03-01 19:44:17', NULL, NULL);
INSERT INTO `users` VALUES (5, 'patient1', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient1@example.com', 'ROLE_PATIENT', 1, '2025-03-01 19:44:17', '2025-03-01 19:44:17', NULL, NULL);
INSERT INTO `users` VALUES (6, 'patient2', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient2@example.com', 'ROLE_PATIENT', 1, '2025-03-01 19:44:17', '2025-03-01 19:44:17', NULL, NULL);
INSERT INTO `users` VALUES (7, 'patient3', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient3@example.com', 'ROLE_PATIENT', 1, '2025-03-01 19:44:17', '2025-03-01 19:44:17', NULL, NULL);
INSERT INTO `users` VALUES (8, 'doctor4', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'doctor4@hospital.com', 'ROLE_DOCTOR', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (9, 'doctor5', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'doctor5@hospital.com', 'ROLE_DOCTOR', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (10, 'doctor6', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'doctor6@hospital.com', 'ROLE_DOCTOR', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (11, 'doctor7', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'doctor7@hospital.com', 'ROLE_DOCTOR', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (12, 'doctor8', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'doctor8@hospital.com', 'ROLE_DOCTOR', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (13, 'doctor9', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'doctor9@hospital.com', 'ROLE_DOCTOR', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (14, 'doctor10', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'doctor10@hospital.com', 'ROLE_DOCTOR', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (15, 'doctor11', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'doctor11@hospital.com', 'ROLE_DOCTOR', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (16, 'patient4', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient4@example.com', 'ROLE_PATIENT', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (17, 'patient5', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient5@example.com', 'ROLE_PATIENT', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (18, 'patient6', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient6@example.com', 'ROLE_PATIENT', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (19, 'patient7', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient7@example.com', 'ROLE_PATIENT', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (20, 'patient8', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient8@example.com', 'ROLE_PATIENT', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (21, 'patient9', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient9@example.com', 'ROLE_PATIENT', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (22, 'patient10', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient10@example.com', 'ROLE_PATIENT', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (23, 'patient11', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient11@example.com', 'ROLE_PATIENT', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (24, 'patient12', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient12@example.com', 'ROLE_PATIENT', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (25, 'patient13', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient13@example.com', 'ROLE_PATIENT', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (26, 'patient14', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient14@example.com', 'ROLE_PATIENT', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (27, 'patient15', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient15@example.com', 'ROLE_PATIENT', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (28, 'patient16', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient16@example.com', 'ROLE_PATIENT', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (29, 'patient17', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient17@example.com', 'ROLE_PATIENT', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (30, 'patient18', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient18@example.com', 'ROLE_PATIENT', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (31, 'patient19', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient19@example.com', 'ROLE_PATIENT', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (32, 'patient20', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient20@example.com', 'ROLE_PATIENT', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (33, 'patient21', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient21@example.com', 'ROLE_PATIENT', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (34, 'patient22', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient22@example.com', 'ROLE_PATIENT', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (35, 'patient23', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient23@example.com', 'ROLE_PATIENT', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (36, 'patient24', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient24@example.com', 'ROLE_PATIENT', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (37, 'patient25', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient25@example.com', 'ROLE_PATIENT', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (38, 'patient26', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient26@example.com', 'ROLE_PATIENT', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (39, 'patient27', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient27@example.com', 'ROLE_PATIENT', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (40, 'patient28', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient28@example.com', 'ROLE_PATIENT', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (41, 'patient29', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient29@example.com', 'ROLE_PATIENT', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (42, 'patient30', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient30@example.com', 'ROLE_PATIENT', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (43, 'patient31', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient31@example.com', 'ROLE_PATIENT', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (44, 'patient32', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient32@example.com', 'ROLE_PATIENT', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (45, 'patient33', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient33@example.com', 'ROLE_PATIENT', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (46, 'patient34', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient34@example.com', 'ROLE_PATIENT', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);
INSERT INTO `users` VALUES (47, 'patient35', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient35@example.com', 'ROLE_PATIENT', 1, '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
