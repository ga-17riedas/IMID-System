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

 Date: 02/03/2025 14:22:09
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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

SET FOREIGN_KEY_CHECKS = 1;
