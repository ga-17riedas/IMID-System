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

 Date: 02/03/2025 14:24:33
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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

SET FOREIGN_KEY_CHECKS = 1;
