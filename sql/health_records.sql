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

 Date: 02/03/2025 14:24:14
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for health_records
-- ----------------------------
DROP TABLE IF EXISTS `health_records`;
CREATE TABLE `health_records`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `patient_id` bigint(20) NOT NULL COMMENT '患者ID',
  `record_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '记录类型',
  `value1` decimal(10, 2) NOT NULL COMMENT '测量值1',
  `value2` decimal(10, 2) NULL DEFAULT NULL COMMENT '测量值2',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '单位',
  `measure_time` datetime(0) NOT NULL COMMENT '测量时间',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '备注',
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `blood_sugar` double NULL DEFAULT NULL,
  `diastolic_pressure` double NULL DEFAULT NULL,
  `heart_rate` double NULL DEFAULT NULL,
  `medications` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `record_time` datetime(6) NULL DEFAULT NULL,
  `symptoms` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `systolic_pressure` double NULL DEFAULT NULL,
  `temperature` double NULL DEFAULT NULL,
  `weight` double NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `patient_id`(`patient_id`) USING BTREE,
  INDEX `idx_measure_time`(`measure_time`) USING BTREE,
  CONSTRAINT `health_records_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '患者健康记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of health_records
-- ----------------------------
INSERT INTO `health_records` VALUES (2, 2, '血糖', 5.60, NULL, 'mmol/L', '2025-02-19 00:22:09', '空腹血糖', '2025-02-19 00:22:09', '2025-02-19 00:22:09', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `health_records` VALUES (4, 1, 'WEIGHT', 1.00, 1.00, 'kg', '2025-02-18 18:19:33', '', '2025-02-18 18:19:33', '2025-02-18 18:19:33', 1, 1, 1, '', '2025-02-18 18:19:33.319691', '', 1, 37.5, 1);
INSERT INTO `health_records` VALUES (5, 1, 'WEIGHT', 1.00, 2.00, 'kg', '2025-02-20 08:38:25', '', '2025-02-20 08:38:25', '2025-02-20 08:38:25', 1, 2, 2, '', '2025-02-20 08:38:24.860920', '', 2, 37.5, 1);
INSERT INTO `health_records` VALUES (6, 1, 'WEIGHT', 130.00, 2.00, 'kg', '2025-02-22 08:05:40', '', '2025-02-22 08:05:40', '2025-02-22 08:05:40', 2, 2, 3, '', '2025-02-22 08:05:40.110128', '', 4, 36.5, 130);
INSERT INTO `health_records` VALUES (7, 10, 'WEIGHT', 150.00, 22.00, 'kg', '2025-03-02 02:50:50', '', '2025-03-02 02:50:50', '2025-03-02 02:50:50', 24, 22, 142, '', '2025-03-02 02:50:49.688308', '', 23, 37.5, 150);

SET FOREIGN_KEY_CHECKS = 1;
