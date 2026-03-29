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

 Date: 02/03/2025 14:29:04
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for schedules
-- ----------------------------
DROP TABLE IF EXISTS `schedules`;
CREATE TABLE `schedules`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `booked_patients` int(11) NULL DEFAULT NULL,
  `created_at` datetime(6) NULL DEFAULT NULL,
  `date` date NOT NULL,
  `max_patients` int(11) NOT NULL,
  `notes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `time_slot` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `doctor_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKfpyatautb52nts46e1y1y4nvg`(`doctor_id`) USING BTREE,
  CONSTRAINT `FKfpyatautb52nts46e1y1y4nvg` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of schedules
-- ----------------------------
INSERT INTO `schedules` VALUES (1, 0, '2025-02-19 17:31:32.235635', '2025-01-28', 10, NULL, 'morning', '2025-02-19 17:31:32.235635', 1);

SET FOREIGN_KEY_CHECKS = 1;
