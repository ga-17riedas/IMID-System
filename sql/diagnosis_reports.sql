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

 Date: 02/03/2025 14:21:52
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for diagnosis_reports
-- ----------------------------
DROP TABLE IF EXISTS `diagnosis_reports`;
CREATE TABLE `diagnosis_reports`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `patient_id` bigint(20) NOT NULL,
  `doctor_id` bigint(20) NOT NULL,
  `medical_image_id` bigint(20) NULL DEFAULT NULL,
  `diagnosis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '诊断结果',
  `treatment_plan` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '治疗方案',
  `recommendations` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '医生建议',
  `read_status` tinyint(1) NULL DEFAULT 0,
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `expire_date` datetime(0) NULL DEFAULT NULL COMMENT '过期时间',
  `is_expired` tinyint(1) NULL DEFAULT 0 COMMENT '是否已过期',
  `expired` bit(1) NOT NULL,
  `treatment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `medical_image_id`(`medical_image_id`) USING BTREE,
  INDEX `idx_diagnosis_reports_patient`(`patient_id`) USING BTREE,
  INDEX `idx_diagnosis_reports_doctor`(`doctor_id`) USING BTREE,
  CONSTRAINT `diagnosis_reports_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `diagnosis_reports_ibfk_2` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `diagnosis_reports_ibfk_3` FOREIGN KEY (`medical_image_id`) REFERENCES `medical_images` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of diagnosis_reports
-- ----------------------------
INSERT INTO `diagnosis_reports` VALUES (1, 1, 1, 1, '正常', '无需治疗', '定期复查', 1, '2025-02-05 11:30:00', '2025-02-05 11:30:00', '2025-05-05 11:30:00', 0, b'0', '无需治疗');
INSERT INTO `diagnosis_reports` VALUES (2, 1, 1, 2, '脑部肿瘤', '手术切除', '术后定期复查', 1, '2025-02-10 15:00:00', '2025-02-10 15:00:00', '2025-05-10 15:00:00', 0, b'0', '手术切除肿瘤');
INSERT INTO `diagnosis_reports` VALUES (3, 1, 1, 6, '脑出血', '药物治疗', '卧床休息，定期复查', 1, '2025-02-20 17:00:00', '2025-02-20 17:00:00', '2025-05-20 17:00:00', 0, b'0', '药物治疗');
INSERT INTO `diagnosis_reports` VALUES (4, 1, 2, 1, '正常', '无需治疗', '保持健康生活习惯', 1, '2025-02-06 09:30:00', '2025-02-06 09:30:00', '2025-05-06 09:30:00', 0, b'0', '无需治疗');
INSERT INTO `diagnosis_reports` VALUES (5, 2, 2, 4, '肺炎', '抗生素治疗', '卧床休息，多喝水', 1, '2025-02-08 10:30:00', '2025-02-08 10:30:00', '2025-05-08 10:30:00', 0, b'0', '抗生素治疗');
INSERT INTO `diagnosis_reports` VALUES (6, 2, 2, 5, '轻微肺部感染', '抗生素治疗', '多休息，避免剧烈运动', 1, '2025-02-15 12:00:00', '2025-02-15 12:00:00', '2025-05-15 12:00:00', 0, b'0', '抗生素治疗');
INSERT INTO `diagnosis_reports` VALUES (7, 2, 1, 7, '肺结节', '定期观察', '三个月后复查', 1, '2025-02-25 14:30:00', '2025-02-25 14:30:00', '2025-05-25 14:30:00', 0, b'0', '定期观察');
INSERT INTO `diagnosis_reports` VALUES (8, 1, 1, 2, '脑部肿瘤', '手术切除', '术后定期复查', 1, '2025-02-12 10:00:00', '2025-02-12 10:00:00', '2025-05-12 10:00:00', 0, b'0', '手术切除肿瘤');
INSERT INTO `diagnosis_reports` VALUES (9, 2, 2, 5, '肺炎', '抗生素治疗', '卧床休息，多喝水', 1, '2025-02-18 14:00:00', '2025-02-18 14:00:00', '2025-05-18 14:00:00', 0, b'0', '抗生素治疗');
INSERT INTO `diagnosis_reports` VALUES (10, 1, 1, 1, '正常', '无需治疗', '保持健康生活习惯', 1, '2025-02-22 09:00:00', '2025-02-22 09:00:00', '2025-05-22 09:00:00', 0, b'0', '无需治疗');
INSERT INTO `diagnosis_reports` VALUES (11, 1, 2, 6, '脑出血', '药物治疗', '卧床休息，定期复查', 1, '2025-02-24 11:00:00', '2025-02-24 11:00:00', '2025-05-24 11:00:00', 0, b'0', '药物治疗');
INSERT INTO `diagnosis_reports` VALUES (12, 2, 1, 4, '肺炎', '抗生素治疗', '卧床休息，多喝水', 1, '2025-02-26 13:00:00', '2025-02-26 13:00:00', '2025-05-26 13:00:00', 0, b'0', '抗生素治疗');
INSERT INTO `diagnosis_reports` VALUES (13, 2, 2, 7, '肺结节', '定期观察', '三个月后复查', 1, '2025-02-28 15:00:00', '2025-02-28 15:00:00', '2025-05-28 15:00:00', 0, b'0', '定期观察');
INSERT INTO `diagnosis_reports` VALUES (14, 1, 1, 1, '正常', '无需治疗', '定期复查', 1, '2025-03-01 10:00:00', '2025-03-01 10:00:00', '2025-06-01 10:00:00', 0, b'0', '无需治疗');
INSERT INTO `diagnosis_reports` VALUES (15, 2, 2, 4, '肺炎', '抗生素治疗', '卧床休息，多喝水', 1, '2025-03-01 11:00:00', '2025-03-01 11:00:00', '2025-06-01 11:00:00', 0, b'0', '抗生素治疗');
INSERT INTO `diagnosis_reports` VALUES (16, 1, 1, 8, '检测到异常，建议进一步检查', '111111111', '11111111111111', 0, '2025-03-01 14:55:23', '2025-03-01 14:55:23', NULL, 0, b'0', NULL);
INSERT INTO `diagnosis_reports` VALUES (17, 32, 10, 9, '检测到异常，建议进一步检查', '22222222222', '22222222222222222222', 0, '2025-03-02 02:50:09', '2025-03-02 02:50:09', NULL, 0, b'0', NULL);
INSERT INTO `diagnosis_reports` VALUES (18, 1, 1, 10, '检测到异常，建议进一步检查', '切除', '手术后静养', 0, '2025-03-02 05:01:12', '2025-03-02 05:01:12', NULL, 0, b'0', NULL);

SET FOREIGN_KEY_CHECKS = 1;
