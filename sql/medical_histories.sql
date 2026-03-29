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

 Date: 02/03/2025 14:24:18
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for medical_histories
-- ----------------------------
DROP TABLE IF EXISTS `medical_histories`;
CREATE TABLE `medical_histories`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `patient_id` bigint(20) NOT NULL,
  `doctor_id` bigint(20) NOT NULL,
  `date` date NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `diagnosis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `treatment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `visit_date` datetime(6) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_medical_histories_patient`(`patient_id`) USING BTREE,
  INDEX `idx_medical_histories_doctor`(`doctor_id`) USING BTREE,
  CONSTRAINT `medical_histories_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `medical_histories_ibfk_2` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of medical_histories
-- ----------------------------
INSERT INTO `medical_histories` VALUES (1, 1, 1, '2025-02-04', '首次就诊，常规检查', '2025-02-19 00:22:10', '2025-02-20 08:36:53', NULL, NULL, NULL);
INSERT INTO `medical_histories` VALUES (2, 2, 2, '2025-02-18', '复查，情况稳定', '2025-02-19 00:22:10', '2025-02-19 00:22:10', NULL, NULL, NULL);
INSERT INTO `medical_histories` VALUES (3, 1, 1, '2025-02-17', 'MRI检查，结果正常', '2025-02-19 00:22:10', '2025-02-19 00:22:10', NULL, NULL, NULL);
INSERT INTO `medical_histories` VALUES (4, 1, 2, '2025-02-12', '111', '2025-02-20 07:48:16', '2025-02-20 07:51:13', NULL, NULL, NULL);
INSERT INTO `medical_histories` VALUES (5, 1, 2, '2025-02-20', '111', '2025-02-20 07:51:27', '2025-02-20 07:51:27', NULL, NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
