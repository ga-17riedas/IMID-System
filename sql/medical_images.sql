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

 Date: 02/03/2025 14:24:23
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for medical_images
-- ----------------------------
DROP TABLE IF EXISTS `medical_images`;
CREATE TABLE `medical_images`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '图像ID，主键',
  `patient_id` bigint(20) NOT NULL COMMENT '关联的患者ID',
  `image_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图像存储路径',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图像访问URL',
  `image_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图像类型',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '图像描述',
  `body_part` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '身体部位',
  `ai_diagnosis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'AI诊断结果',
  `confidence_score` decimal(5, 2) NULL DEFAULT NULL COMMENT 'AI诊断置信度',
  `doctor_diagnosis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '医生诊断结果',
  `upload_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '上传时间',
  `doctor_id` bigint(20) NULL DEFAULT NULL COMMENT '上传医生ID',
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `patient_id`(`patient_id`) USING BTREE,
  INDEX `doctor_id`(`doctor_id`) USING BTREE,
  CONSTRAINT `medical_images_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `medical_images_ibfk_2` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '医学图像信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of medical_images
-- ----------------------------
INSERT INTO `medical_images` VALUES (1, 1, '/images/brain_scan_1.jpg', NULL, 'MRI', '脑部MRI扫描', '大脑', '正常', 0.99, '正常', '2025-02-05 10:30:00', 1, '2025-02-05 10:30:00', '2025-03-01 20:07:41');
INSERT INTO `medical_images` VALUES (2, 1, '/images/brain_scan_2.jpg', NULL, 'CT', '脑部CT扫描', '大脑', '脑部肿瘤', 0.99, '脑部肿瘤', '2025-02-10 14:00:00', 1, '2025-02-10 14:00:00', '2025-03-01 20:07:46');
INSERT INTO `medical_images` VALUES (4, 2, '/images/lung_scan_1.jpg', NULL, 'X-Ray', '肺部X光', '肺部', '肺炎', 0.90, '肺炎', '2025-02-08 09:30:00', 2, '2025-02-08 09:30:00', '2025-02-08 09:30:00');
INSERT INTO `medical_images` VALUES (5, 2, '/images/lung_scan_2.jpg', NULL, 'CT', '肺部CT扫描', '肺部', '轻微肺部感染', 0.88, '轻微肺部感染', '2025-02-15 11:00:00', 2, '2025-02-15 11:00:00', '2025-02-15 11:00:00');
INSERT INTO `medical_images` VALUES (6, 1, '/images/brain_scan_3.jpg', NULL, 'CT', '脑部CT扫描', '大脑', '脑出血', 0.92, '脑出血', '2025-02-20 16:00:00', 1, '2025-02-20 16:00:00', '2025-02-20 16:00:00');
INSERT INTO `medical_images` VALUES (7, 2, '/images/lung_scan_3.jpg', NULL, 'MRI', '肺部MRI扫描', '肺部', '肺结节', 0.87, '肺结节', '2025-02-25 13:30:00', 1, '2025-02-25 13:30:00', '2025-02-25 13:30:00');
INSERT INTO `medical_images` VALUES (8, 1, '/uploads/2bef2a99-baca-4c08-ad39-3ebb95b9b6f7.jpg', '/uploads/2bef2a99-baca-4c08-ad39-3ebb95b9b6f7.jpg', 'BRAIN_TUMOR', NULL, NULL, '检测到异常，建议进一步检查', 1.00, NULL, '2025-03-01 14:55:23', 1, '2025-03-01 14:55:23', '2025-03-01 14:55:23');
INSERT INTO `medical_images` VALUES (9, 32, '/uploads/570ff06c-fbc9-4625-931c-c63f18b9de63.jpg', '/uploads/570ff06c-fbc9-4625-931c-c63f18b9de63.jpg', 'BRAIN_TUMOR', NULL, NULL, '检测到异常，建议进一步检查', 1.00, NULL, '2025-03-02 02:50:09', 10, '2025-03-02 02:50:09', '2025-03-02 02:50:09');
INSERT INTO `medical_images` VALUES (10, 1, '/uploads/b5286741-0ac7-40cb-b9b5-ee79d38302d0.jpg', '/uploads/b5286741-0ac7-40cb-b9b5-ee79d38302d0.jpg', 'BRAIN_TUMOR', NULL, NULL, '检测到异常，建议进一步检查', 1.00, NULL, '2025-03-02 05:01:12', 1, '2025-03-02 05:01:12', '2025-03-02 05:01:12');

SET FOREIGN_KEY_CHECKS = 1;
