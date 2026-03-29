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

 Date: 02/03/2025 14:19:56
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ai_models
-- ----------------------------
DROP TABLE IF EXISTS `ai_models`;
CREATE TABLE `ai_models`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `version` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `model_size` bigint(20) NULL DEFAULT NULL,
  `accuracy` double(10, 4) NULL DEFAULT NULL,
  `precision_val` double(10, 4) NULL DEFAULT NULL,
  `recall` double(10, 4) NULL DEFAULT NULL,
  `f1_score` double(10, 4) NULL DEFAULT NULL,
  `active` tinyint(1) NULL DEFAULT 0,
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `accuracy_formatted` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `f1_score_formatted` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `model_size_formatted` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `parameter_count` bigint(20) NULL DEFAULT NULL,
  `precision_formatted` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `recall_formatted` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_model_type_version`(`type`, `version`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_models
-- ----------------------------
INSERT INTO `ai_models` VALUES (1, '脑肿瘤检测', 'BRAIN_TUMOR', '1.0.0', '', 'D:\\IMID\\models\\b0e8a9dd-282d-4e2d-8a01-3d426b5e1c47.pt', 6262610, 0.9967, 0.9936, 0.9940, 0.9875, 1, '2025-02-28 04:12:54', '99.67%', '98.75%', '6262610', NULL, '99.36%', '99.4%');
INSERT INTO `ai_models` VALUES (2, '新冠肺炎检测', 'COVID', '1.0.0', '', 'D:\\IMID\\models\\c7ca05ff-d158-4624-8a1d-4e9ba9ecec13.pt', 6270096, 0.9834, 0.9828, 0.9964, 0.9876, 1, '2025-02-28 11:42:58', '98.34%', '98.76%', '5.98 MB', 3011238, '98.28%', '99.64%');

-- ----------------------------
-- Table structure for appointments
-- ----------------------------
DROP TABLE IF EXISTS `appointments`;
CREATE TABLE `appointments`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '预约ID，主键',
  `patient_id` bigint(20) NOT NULL COMMENT '患者ID',
  `doctor_id` bigint(20) NOT NULL COMMENT '医生ID',
  `appointment_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '预约时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '预约状态：PENDING/CONFIRMED/CANCELLED',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '预约说明',
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `patient_id`(`patient_id`) USING BTREE,
  INDEX `doctor_id`(`doctor_id`) USING BTREE,
  INDEX `idx_appointment_time`(`appointment_time`) USING BTREE,
  INDEX `idx_appointment_status`(`status`) USING BTREE,
  CONSTRAINT `appointments_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `appointments_ibfk_2` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '预约信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of appointments
-- ----------------------------
INSERT INTO `appointments` VALUES (1, 1, 1, '2025-03-02 19:44:17', 'PENDING', '常规复查预约', '2025-03-01 19:44:17', '2025-03-01 19:44:17');

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

-- ----------------------------
-- Table structure for doctors
-- ----------------------------
DROP TABLE IF EXISTS `doctors`;
CREATE TABLE `doctors`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '医生ID，主键',
  `user_id` bigint(20) NOT NULL COMMENT '关联的用户ID',
  `full_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '医生姓名',
  `professional_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '职称',
  `department` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '所属科室',
  `specialty` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '专业特长',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `hospital` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '所属医院',
  `license_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '执业证号',
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `age` int(11) NULL DEFAULT NULL,
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `gender` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `practice_years` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `license_number`(`license_number`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `idx_doctor_department`(`department`) USING BTREE,
  INDEX `idx_doctor_title`(`professional_title`) USING BTREE,
  CONSTRAINT `doctors_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '医生详细信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of doctors
-- ----------------------------
INSERT INTO `doctors` VALUES (1, 2, '张医生', '主任医师', '放射科', '影像诊断', '13811111111', '第一人民医院', 'LIC2024001', '2025-03-01 19:44:17', '2025-03-01 19:44:17', NULL, NULL, NULL, NULL);
INSERT INTO `doctors` VALUES (2, 3, '李医生', '副主任医师', '放射科', '影像诊断', '13822222222', '第一人民医院', 'LIC2024002', '2025-03-01 19:44:17', '2025-03-01 19:44:17', NULL, NULL, NULL, NULL);
INSERT INTO `doctors` VALUES (3, 8, '王医生', '主治医师', '内科', '心血管疾病', '13833333333', '第二人民医院', 'LIC2024003', '2025-03-02 10:48:08', '2025-03-02 10:48:08', 35, NULL, '男', 10);
INSERT INTO `doctors` VALUES (4, 9, '赵医生', '副主任医师', '外科', '普外科手术', '13844444444', '第一人民医院', 'LIC2024004', '2025-03-02 10:48:08', '2025-03-02 10:48:08', 42, NULL, '女', 15);
INSERT INTO `doctors` VALUES (5, 10, '刘医生', '主任医师', '神经内科', '神经系统疾病', '13855555555', '中心医院', 'LIC2024005', '2025-03-02 10:48:08', '2025-03-02 10:48:08', 48, NULL, '男', 20);
INSERT INTO `doctors` VALUES (6, 11, '陈医生', '主治医师', '妇产科', '产科疾病', '13866666666', '妇幼保健院', 'LIC2024006', '2025-03-02 10:48:08', '2025-03-02 10:48:08', 37, NULL, '女', 12);
INSERT INTO `doctors` VALUES (7, 12, '孙医生', '住院医师', '儿科', '儿童常见疾病', '13877777777', '儿童医院', 'LIC2024007', '2025-03-02 10:48:08', '2025-03-02 10:48:08', 29, NULL, '女', 5);
INSERT INTO `doctors` VALUES (8, 13, '钱医生', '主任医师', '肿瘤科', '肿瘤治疗', '13888888888', '肿瘤医院', 'LIC2024008', '2025-03-02 10:48:08', '2025-03-02 10:48:08', 52, NULL, '男', 25);
INSERT INTO `doctors` VALUES (9, 14, '周医生', '副主任医师', '骨科', '脊柱外科', '13899999999', '骨科医院', 'LIC2024009', '2025-03-02 10:48:08', '2025-03-02 10:48:08', 45, NULL, '男', 18);
INSERT INTO `doctors` VALUES (10, 15, '吴医生', '主治医师', '皮肤科', '皮肤过敏', '13800000000', '皮肤病医院', 'LIC2024010', '2025-03-02 10:48:08', '2025-03-02 10:48:08', 39, NULL, '女', 14);

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

-- ----------------------------
-- Table structure for medical_records
-- ----------------------------
DROP TABLE IF EXISTS `medical_records`;
CREATE TABLE `medical_records`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `patient_id` bigint(20) NOT NULL,
  `visit_date` datetime(0) NOT NULL COMMENT '就诊日期',
  `hospital` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '就诊医院',
  `department` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '科室',
  `doctor_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '医生姓名',
  `diagnosis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '诊断结果',
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `patient_id`(`patient_id`) USING BTREE,
  CONSTRAINT `medical_records_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of medical_records
-- ----------------------------
INSERT INTO `medical_records` VALUES (1, 1, '2024-02-01 09:00:00', '第一人民医院', '内科', '张医生', '感冒', '2025-02-19 00:22:09');
INSERT INTO `medical_records` VALUES (2, 1, '2024-01-15 14:30:00', '第二人民医院', '骨科', '李医生', '扭伤', '2025-02-19 00:22:09');
INSERT INTO `medical_records` VALUES (3, 1, '2023-12-20 11:00:00', '中心医院', '眼科', '王医生', '近视', '2025-02-19 00:22:09');

-- ----------------------------
-- Table structure for patients
-- ----------------------------
DROP TABLE IF EXISTS `patients`;
CREATE TABLE `patients`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '患者ID，主键',
  `user_id` bigint(20) NOT NULL COMMENT '关联的用户ID',
  `full_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '患者姓名',
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '性别',
  `birth_date` date NULL DEFAULT NULL COMMENT '出生日期',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `address` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '居住地址',
  `medical_history` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '病史记录',
  `age` int(11) NULL DEFAULT NULL COMMENT '年龄',
  `emergency_contact` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '紧急联系人姓名',
  `emergency_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '紧急联系人电话',
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `patient_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `idx_patient_name`(`full_name`) USING BTREE,
  CONSTRAINT `patients_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 35 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '患者详细信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of patients
-- ----------------------------
INSERT INTO `patients` VALUES (1, 5, '张病人', '男', '1988-01-01', '13877777777', '北京市海淀区', '无特殊病史', 35, '张家属', '13900000001', '2025-03-01 19:44:17', '2025-03-01 19:44:17', NULL);
INSERT INTO `patients` VALUES (2, 6, '李病人', '女', '1980-06-15', '13888888888', '北京市朝阳区', '高血压病史', 43, '李家属', '13900000002', '2025-03-01 19:44:17', '2025-03-01 19:44:17', NULL);
INSERT INTO `patients` VALUES (3, 16, '张三', '男', '1990-05-15', '13911111111', '北京市朝阳区', '无特殊病史', 33, '张家人', '13900000003', '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL);
INSERT INTO `patients` VALUES (4, 17, '李四', '女', '1985-12-10', '13922222222', '北京市海淀区', '慢性胃炎', 38, '李家人', '13900000004', '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL);
INSERT INTO `patients` VALUES (5, 18, '王五', '男', '1978-08-20', '13933333333', '上海市浦东新区', '高血压、糖尿病', 45, '王家人', '13900000005', '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL);
INSERT INTO `patients` VALUES (6, 19, '赵六', '女', '1995-03-25', '13944444444', '上海市静安区', '无特殊病史', 28, '赵家人', '13900000006', '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL);
INSERT INTO `patients` VALUES (7, 20, '钱七', '男', '1982-11-30', '13955555555', '广州市天河区', '过敏性鼻炎', 41, '钱家人', '13900000007', '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL);
INSERT INTO `patients` VALUES (8, 21, '孙八', '女', '1998-07-05', '13966666666', '广州市越秀区', '无特殊病史', 25, '孙家人', '13900000008', '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL);
INSERT INTO `patients` VALUES (9, 22, '周九', '男', '1972-04-18', '13977777777', '深圳市南山区', '高血压', 51, '周家人', '13900000009', '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL);
INSERT INTO `patients` VALUES (10, 23, '吴十', '女', '1992-09-22', '13988888888', '深圳市福田区', '无特殊病史', 31, '吴家人', '13900000010', '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL);
INSERT INTO `patients` VALUES (11, 24, '郑十一', '男', '1980-02-14', '13999999999', '成都市武侯区', '胆囊炎', 43, '郑家人', '13900000011', '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL);
INSERT INTO `patients` VALUES (12, 25, '王十二', '女', '1993-06-08', '15011111111', '成都市锦江区', '无特殊病史', 30, '王家人', '13900000012', '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL);
INSERT INTO `patients` VALUES (13, 26, '李十三', '男', '1975-10-12', '15022222222', '重庆市渝北区', '冠心病', 48, '李家人', '13900000013', '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL);
INSERT INTO `patients` VALUES (14, 27, '赵十四', '女', '1996-01-16', '15033333333', '重庆市渝中区', '无特殊病史', 27, '赵家人', '13900000014', '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL);
INSERT INTO `patients` VALUES (15, 28, '钱十五', '男', '1983-05-20', '15044444444', '武汉市洪山区', '慢性支气管炎', 40, '钱家人', '13900000015', '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL);
INSERT INTO `patients` VALUES (16, 29, '孙十六', '女', '1991-09-24', '15055555555', '武汉市江汉区', '无特殊病史', 32, '孙家人', '13900000016', '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL);
INSERT INTO `patients` VALUES (17, 30, '周十七', '男', '1970-03-28', '15066666666', '南京市鼓楼区', '高尿酸血症', 53, '周家人', '13900000017', '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL);
INSERT INTO `patients` VALUES (18, 31, '吴十八', '女', '1994-08-02', '15077777777', '南京市秦淮区', '无特殊病史', 29, '吴家人', '13900000018', '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL);
INSERT INTO `patients` VALUES (19, 32, '郑十九', '男', '1981-12-06', '15088888888', '杭州市西湖区', '胃溃疡', 42, '郑家人', '13900000019', '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL);
INSERT INTO `patients` VALUES (20, 33, '王二十', '女', '1997-04-10', '15099999999', '杭州市上城区', '无特殊病史', 26, '王家人', '13900000020', '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL);
INSERT INTO `patients` VALUES (21, 34, '李二一', '男', '1974-08-14', '15100000000', '苏州市姑苏区', '高血脂', 49, '李家人', '13900000021', '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL);
INSERT INTO `patients` VALUES (22, 35, '赵二二', '女', '1990-12-18', '15111111111', '苏州市吴中区', '无特殊病史', 33, '赵家人', '13900000022', '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL);
INSERT INTO `patients` VALUES (23, 36, '钱二三', '男', '1982-04-22', '15122222222', '天津市和平区', '轻度脂肪肝', 41, '钱家人', '13900000023', '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL);
INSERT INTO `patients` VALUES (24, 37, '孙二四', '女', '1995-08-26', '15133333333', '天津市河西区', '无特殊病史', 28, '孙家人', '13900000024', '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL);
INSERT INTO `patients` VALUES (25, 38, '周二五', '男', '1971-12-30', '15144444444', '西安市雁塔区', '2型糖尿病', 52, '周家人', '13900000025', '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL);
INSERT INTO `patients` VALUES (26, 39, '吴二六', '女', '1993-05-04', '15155555555', '西安市碑林区', '无特殊病史', 30, '吴家人', '13900000026', '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL);
INSERT INTO `patients` VALUES (27, 40, '郑二七', '男', '1980-09-08', '15166666666', '郑州市金水区', '前列腺炎', 43, '郑家人', '13900000027', '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL);
INSERT INTO `patients` VALUES (28, 41, '王二八', '女', '1996-01-12', '15177777777', '郑州市中原区', '无特殊病史', 27, '王家人', '13900000028', '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL);
INSERT INTO `patients` VALUES (29, 42, '李二九', '男', '1973-05-16', '15188888888', '济南市历下区', '高血压', 50, '李家人', '13900000029', '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL);
INSERT INTO `patients` VALUES (30, 43, '赵三十', '女', '1992-09-20', '15199999999', '济南市槐荫区', '无特殊病史', 31, '赵家人', '13900000030', '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL);
INSERT INTO `patients` VALUES (31, 44, '钱三一', '男', '1984-01-24', '15200000000', '长沙市岳麓区', '慢性胃炎', 39, '钱家人', '13900000031', '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL);
INSERT INTO `patients` VALUES (32, 45, '孙三二', '女', '1997-05-28', '15211111111', '长沙市芙蓉区', '无特殊病史', 26, '孙家人', '13900000032', '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL);
INSERT INTO `patients` VALUES (33, 46, '周三三', '男', '1976-10-02', '15222222222', '青岛市市南区', '颈椎病', 47, '周家人', '13900000033', '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL);
INSERT INTO `patients` VALUES (34, 47, '吴三四', '女', '1991-02-06', '15233333333', '青岛市市北区', '无特殊病史', 32, '吴家人', '13900000034', '2025-03-02 10:48:08', '2025-03-02 10:48:08', NULL);

-- ----------------------------
-- Table structure for profiles
-- ----------------------------
DROP TABLE IF EXISTS `profiles`;
CREATE TABLE `profiles`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `user_id` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_4ixsj6aqve5pxrbw2u0oyk8bb`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

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
