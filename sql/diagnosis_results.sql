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

 Date: 02/03/2025 14:21:57
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for diagnosis_results
-- ----------------------------
DROP TABLE IF EXISTS `diagnosis_results`;
CREATE TABLE `diagnosis_results`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `image_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图像ID',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图像URL',
  `analysis_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分析类型',
  `diagnosis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '诊断结果',
  `confidence` double NULL DEFAULT NULL COMMENT '置信度',
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '诊断结果信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of diagnosis_results
-- ----------------------------
INSERT INTO `diagnosis_results` VALUES (1, '/uploads/bb6c5535-c098-4cd3-846e-66183ff01feb.jpg', NULL, 'BRAIN_TUMOR', '检测到异常，建议进一步检查', 0.9982689619064331, '2025-03-01 12:05:19', '2025-03-01 20:05:18');
INSERT INTO `diagnosis_results` VALUES (2, '/uploads/2bef2a99-baca-4c08-ad39-3ebb95b9b6f7.jpg', NULL, 'BRAIN_TUMOR', '检测到异常，建议进一步检查', 0.9981757402420044, '2025-03-01 14:55:22', '2025-03-01 22:55:21');
INSERT INTO `diagnosis_results` VALUES (3, '/uploads/570ff06c-fbc9-4625-931c-c63f18b9de63.jpg', NULL, 'BRAIN_TUMOR', '检测到异常，建议进一步检查', 0.9979640245437622, '2025-03-02 02:50:01', '2025-03-02 10:50:01');
INSERT INTO `diagnosis_results` VALUES (4, '/uploads/b5286741-0ac7-40cb-b9b5-ee79d38302d0.jpg', NULL, 'BRAIN_TUMOR', '检测到异常，建议进一步检查', 0.9980695843696594, '2025-03-02 05:00:29', '2025-03-02 13:00:29');

SET FOREIGN_KEY_CHECKS = 1;
