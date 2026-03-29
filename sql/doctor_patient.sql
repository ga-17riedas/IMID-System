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

 Date: 02/03/2025 14:22:03
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for doctor_patient
-- ----------------------------
DROP TABLE IF EXISTS `doctor_patient`;
CREATE TABLE `doctor_patient`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '关联ID，主键',
  `doctor_id` bigint(20) NOT NULL COMMENT '医生ID',
  `patient_id` bigint(20) NOT NULL COMMENT '患者ID',
  `relationship_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关系类型：PRIMARY/CONSULTING',
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date NULL DEFAULT NULL COMMENT '结束日期',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注',
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_doctor_patient`(`doctor_id`, `patient_id`) USING BTREE,
  INDEX `idx_doctor_patient_doctor`(`doctor_id`) USING BTREE,
  INDEX `idx_doctor_patient_patient`(`patient_id`) USING BTREE,
  CONSTRAINT `doctor_patient_ibfk_1` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `doctor_patient_ibfk_2` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 61 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '医生-患者关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of doctor_patient
-- ----------------------------
INSERT INTO `doctor_patient` VALUES (1, 1, 1, 'PRIMARY', '2025-03-01', NULL, '初始主治医生关系', '2025-03-01 19:44:17', '2025-03-01 19:44:17');
INSERT INTO `doctor_patient` VALUES (2, 1, 2, 'PRIMARY', '2025-03-01', NULL, '初始主治医生关系', '2025-03-01 19:44:17', '2025-03-01 19:44:17');
INSERT INTO `doctor_patient` VALUES (4, 2, 1, 'CONSULTING', '2025-03-01', NULL, '会诊医生关系', '2025-03-01 19:44:17', '2025-03-01 19:44:17');
INSERT INTO `doctor_patient` VALUES (5, 3, 3, 'PRIMARY', '2024-05-17', NULL, '主治医生关系', '2025-03-02 10:48:08', '2025-03-02 10:48:08');
INSERT INTO `doctor_patient` VALUES (6, 3, 4, 'PRIMARY', '2024-06-04', NULL, '主治医生关系', '2025-03-02 10:48:08', '2025-03-02 10:48:08');
INSERT INTO `doctor_patient` VALUES (7, 3, 5, 'PRIMARY', '2024-10-30', NULL, '主治医生关系', '2025-03-02 10:48:08', '2025-03-02 10:48:08');
INSERT INTO `doctor_patient` VALUES (8, 3, 6, 'PRIMARY', '2024-09-17', NULL, '主治医生关系', '2025-03-02 10:48:08', '2025-03-02 10:48:08');
INSERT INTO `doctor_patient` VALUES (12, 4, 10, 'PRIMARY', '2024-11-23', NULL, '主治医生关系', '2025-03-02 10:48:08', '2025-03-02 10:48:08');
INSERT INTO `doctor_patient` VALUES (13, 4, 9, 'PRIMARY', '2024-03-06', NULL, '主治医生关系', '2025-03-02 10:48:08', '2025-03-02 10:48:08');
INSERT INTO `doctor_patient` VALUES (14, 4, 8, 'PRIMARY', '2025-01-13', NULL, '主治医生关系', '2025-03-02 10:48:08', '2025-03-02 10:48:08');
INSERT INTO `doctor_patient` VALUES (15, 4, 7, 'PRIMARY', '2024-06-23', NULL, '主治医生关系', '2025-03-02 10:48:08', '2025-03-02 10:48:08');
INSERT INTO `doctor_patient` VALUES (19, 5, 13, 'PRIMARY', '2025-02-08', NULL, '主治医生关系', '2025-03-02 10:48:08', '2025-03-02 10:48:08');
INSERT INTO `doctor_patient` VALUES (20, 5, 12, 'PRIMARY', '2024-12-08', NULL, '主治医生关系', '2025-03-02 10:48:08', '2025-03-02 10:48:08');
INSERT INTO `doctor_patient` VALUES (21, 5, 14, 'PRIMARY', '2024-03-10', NULL, '主治医生关系', '2025-03-02 10:48:08', '2025-03-02 10:48:08');
INSERT INTO `doctor_patient` VALUES (22, 5, 11, 'PRIMARY', '2024-12-20', NULL, '主治医生关系', '2025-03-02 10:48:08', '2025-03-02 10:48:08');
INSERT INTO `doctor_patient` VALUES (26, 6, 18, 'PRIMARY', '2025-02-10', NULL, '主治医生关系', '2025-03-02 10:48:08', '2025-03-02 10:48:08');
INSERT INTO `doctor_patient` VALUES (27, 6, 17, 'PRIMARY', '2024-06-26', NULL, '主治医生关系', '2025-03-02 10:48:08', '2025-03-02 10:48:08');
INSERT INTO `doctor_patient` VALUES (28, 6, 16, 'PRIMARY', '2024-12-02', NULL, '主治医生关系', '2025-03-02 10:48:08', '2025-03-02 10:48:08');
INSERT INTO `doctor_patient` VALUES (29, 6, 15, 'PRIMARY', '2024-12-24', NULL, '主治医生关系', '2025-03-02 10:48:08', '2025-03-02 10:48:08');
INSERT INTO `doctor_patient` VALUES (33, 7, 21, 'PRIMARY', '2024-12-22', NULL, '主治医生关系', '2025-03-02 10:48:08', '2025-03-02 10:48:08');
INSERT INTO `doctor_patient` VALUES (34, 7, 20, 'PRIMARY', '2024-10-05', NULL, '主治医生关系', '2025-03-02 10:48:08', '2025-03-02 10:48:08');
INSERT INTO `doctor_patient` VALUES (35, 7, 22, 'PRIMARY', '2024-09-18', NULL, '主治医生关系', '2025-03-02 10:48:08', '2025-03-02 10:48:08');
INSERT INTO `doctor_patient` VALUES (36, 7, 19, 'PRIMARY', '2025-02-12', NULL, '主治医生关系', '2025-03-02 10:48:08', '2025-03-02 10:48:08');
INSERT INTO `doctor_patient` VALUES (40, 8, 26, 'PRIMARY', '2024-04-13', NULL, '主治医生关系', '2025-03-02 10:48:08', '2025-03-02 10:48:08');
INSERT INTO `doctor_patient` VALUES (41, 8, 25, 'PRIMARY', '2024-11-18', NULL, '主治医生关系', '2025-03-02 10:48:08', '2025-03-02 10:48:08');
INSERT INTO `doctor_patient` VALUES (42, 8, 24, 'PRIMARY', '2024-05-24', NULL, '主治医生关系', '2025-03-02 10:48:08', '2025-03-02 10:48:08');
INSERT INTO `doctor_patient` VALUES (43, 8, 23, 'PRIMARY', '2025-03-01', NULL, '主治医生关系', '2025-03-02 10:48:08', '2025-03-02 10:48:08');
INSERT INTO `doctor_patient` VALUES (47, 9, 29, 'PRIMARY', '2024-06-21', NULL, '主治医生关系', '2025-03-02 10:48:08', '2025-03-02 10:48:08');
INSERT INTO `doctor_patient` VALUES (48, 9, 28, 'PRIMARY', '2024-09-10', NULL, '主治医生关系', '2025-03-02 10:48:08', '2025-03-02 10:48:08');
INSERT INTO `doctor_patient` VALUES (49, 9, 30, 'PRIMARY', '2024-11-18', NULL, '主治医生关系', '2025-03-02 10:48:08', '2025-03-02 10:48:08');
INSERT INTO `doctor_patient` VALUES (50, 9, 27, 'PRIMARY', '2025-02-28', NULL, '主治医生关系', '2025-03-02 10:48:08', '2025-03-02 10:48:08');
INSERT INTO `doctor_patient` VALUES (54, 10, 34, 'PRIMARY', '2024-12-27', NULL, '主治医生关系', '2025-03-02 10:48:08', '2025-03-02 10:48:08');
INSERT INTO `doctor_patient` VALUES (55, 10, 33, 'PRIMARY', '2024-04-19', NULL, '主治医生关系', '2025-03-02 10:48:08', '2025-03-02 10:48:08');
INSERT INTO `doctor_patient` VALUES (56, 10, 32, 'PRIMARY', '2024-05-08', NULL, '主治医生关系', '2025-03-02 10:48:08', '2025-03-02 10:48:08');
INSERT INTO `doctor_patient` VALUES (57, 10, 31, 'PRIMARY', '2024-09-10', NULL, '主治医生关系', '2025-03-02 10:48:08', '2025-03-02 10:48:08');

SET FOREIGN_KEY_CHECKS = 1;
