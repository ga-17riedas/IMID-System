-- 清空现有数据（可选，注意顺序）
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE doctor_patient;
TRUNCATE TABLE appointments;
TRUNCATE TABLE diagnosis_results;
TRUNCATE TABLE diagnosis_reports;
TRUNCATE TABLE medical_images;
TRUNCATE TABLE patients;
TRUNCATE TABLE doctors;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. 插入用户数据
INSERT INTO users (username, password, email, role, enabled) VALUES
('admin', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'admin@imid.com', 'ROLE_ADMIN', true),
('doctor1', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'doctor1@hospital.com', 'ROLE_DOCTOR', true),
('doctor2', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'doctor2@hospital.com', 'ROLE_DOCTOR', true),
('doctor3', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'doctor3@hospital.com', 'ROLE_DOCTOR', true),
('patient1', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient1@example.com', 'ROLE_PATIENT', true),
('patient2', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient2@example.com', 'ROLE_PATIENT', true),
('patient3', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient3@example.com', 'ROLE_PATIENT', true);

-- 2. 插入医生信息
INSERT INTO doctors (user_id, full_name, professional_title, department, specialty, phone, hospital, license_number) VALUES
((SELECT id FROM users WHERE username = 'doctor1'), 
 '张医生', 
 '主任医师', 
 '放射科', 
 '影像诊断', 
 '13811111111',
 '第一人民医院',
 'LIC2024001'
),
((SELECT id FROM users WHERE username = 'doctor2'), 
 '李医生', 
 '副主任医师', 
 '放射科', 
 '影像诊断', 
 '13822222222',
 '第一人民医院',
 'LIC2024002'
);

-- 3. 插入患者信息
INSERT INTO patients (user_id, full_name, gender, birth_date, phone, address, medical_history, age, emergency_contact, emergency_phone) VALUES
((SELECT id FROM users WHERE username = 'patient1'), 
 '张病人', 
 '男', 
 '1988-01-01', 
 '13877777777', 
 '北京市海淀区', 
 '无特殊病史',
 35,
 '张家属',
 '13900000001'
),
((SELECT id FROM users WHERE username = 'patient2'), 
 '李病人', 
 '女', 
 '1980-06-15', 
 '13888888888', 
 '北京市朝阳区', 
 '高血压病史',
 43,
 '李家属',
 '13900000002'
);

-- 4. 插入医生-患者关联数据
INSERT INTO doctor_patient (doctor_id, patient_id, relationship_type, start_date, notes) 
SELECT 
    d.id,
    p.id,
    'PRIMARY',
    CURDATE(),
    '初始主治医生关系'
FROM doctors d
JOIN patients p ON p.full_name IN ('张病人', '李病人')
WHERE d.full_name = '张医生';

INSERT INTO doctor_patient (doctor_id, patient_id, relationship_type, start_date, notes)
SELECT 
    d.id,
    p.id,
    'CONSULTING',
    CURDATE(),
    '会诊医生关系'
FROM doctors d
JOIN patients p ON p.full_name = '张病人'
WHERE d.full_name = '李医生';

-- 5. 插入医学影像
INSERT INTO medical_images (
    patient_id, 
    doctor_id, 
    image_path, 
    image_type, 
    description,
    body_part,
    ai_diagnosis,
    confidence_score,
    doctor_diagnosis
) 
SELECT 
    p.id,
    d.id,
    '/images/test1.jpg',
    'MRI',
    '大脑MRI扫描图像',
    '大脑',
    '疑似脑部肿瘤',
    0.85,
    '需要进一步检查确认'
FROM patients p
JOIN doctors d ON d.full_name = '张医生'
WHERE p.full_name = '张病人'
LIMIT 1;

-- 6. 插入诊断报告
INSERT INTO diagnosis_reports (patient_id, doctor_id, medical_image_id, diagnosis, treatment_plan, recommendations)
SELECT 
    p.id,
    d.id,
    mi.id,
    '初步诊断结果',
    '建议治疗方案',
    '注意事项说明'
FROM patients p
JOIN doctors d ON d.full_name = '张医生'
JOIN medical_images mi ON mi.patient_id = p.id
WHERE p.full_name = '张病人'
LIMIT 1;

-- 7. 插入诊断结果
INSERT INTO diagnosis_results (image_id, doctor_id, diagnosis_text, severity_level, recommendations)
SELECT 
    mi.id,
    d.id,
    '详细诊断说明',
    'MEDIUM',
    '建议定期复查'
FROM medical_images mi
JOIN doctors d ON d.full_name = '张医生'
WHERE mi.doctor_id = d.id
LIMIT 1;

-- 8. 插入预约信息
INSERT INTO appointments (patient_id, doctor_id, appointment_time, status, description)
SELECT 
    p.id,
    d.id,
    NOW() + INTERVAL 1 DAY,
    'PENDING',
    '常规复查预约'
FROM patients p
JOIN doctors d ON d.full_name = '张医生'
WHERE p.full_name = '张病人'
LIMIT 1;

-- 添加测试数据
INSERT INTO medical_images (patient_id, image_url, image_type, created_at) 
VALUES 
(1, '/uploads/test1.jpg', 'BRAIN_TUMOR', NOW()),
(1, '/uploads/test2.jpg', 'COVID', NOW());

-- 系统日志基础数据
INSERT INTO system_logs (timestamp, level, source, message, stack_trace, user_id, ip_address, user_agent, details) VALUES
(NOW() - INTERVAL 1 DAY, 'INFO', 'SystemStartup', '系统启动成功', NULL, NULL, '127.0.0.1', 'Server/1.0', '系统版本: 1.0.0'),
(NOW() - INTERVAL 23 HOUR, 'INFO', 'UserService', '用户登录成功', NULL, 1, '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', '管理员登录'),
(NOW() - INTERVAL 22 HOUR, 'INFO', 'UserService', '用户登录成功', NULL, 2, '192.168.1.101', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', '医生登录'),
(NOW() - INTERVAL 21 HOUR, 'WARNING', 'SecurityService', '检测到多次失败的登录尝试', NULL, NULL, '192.168.1.102', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', '5次失败尝试'),
(NOW() - INTERVAL 20 HOUR, 'ERROR', 'DatabaseService', '数据库连接失败', 'java.sql.SQLException: Connection refused', NULL, '127.0.0.1', 'Server/1.0', '尝试重新连接'),
(NOW() - INTERVAL 19 HOUR, 'INFO', 'DatabaseService', '数据库连接恢复', NULL, NULL, '127.0.0.1', 'Server/1.0', '自动恢复成功'),
(NOW() - INTERVAL 18 HOUR, 'INFO', 'UserService', '用户登录成功', NULL, 3, '192.168.1.103', 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_7_1)', '患者登录'),
(NOW() - INTERVAL 17 HOUR, 'INFO', 'MedicalService', '新增诊断报告', NULL, 2, '192.168.1.101', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', '报告ID: 1001'),
(NOW() - INTERVAL 16 HOUR, 'DEBUG', 'ImageProcessingService', '图像处理完成', NULL, 2, '192.168.1.101', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', '处理时间: 2.3秒'),
(NOW() - INTERVAL 15 HOUR, 'INFO', 'NotificationService', '发送通知成功', NULL, NULL, '127.0.0.1', 'Server/1.0', '发送给用户ID: 3'),
(NOW() - INTERVAL 14 HOUR, 'WARNING', 'StorageService', '存储空间不足', NULL, NULL, '127.0.0.1', 'Server/1.0', '剩余空间: 15%'),
(NOW() - INTERVAL 13 HOUR, 'INFO', 'UserService', '用户登录成功', NULL, 1, '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', '管理员登录'),
(NOW() - INTERVAL 12 HOUR, 'INFO', 'AdminService', '系统配置更新', NULL, 1, '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', '更新缓存设置'),
(NOW() - INTERVAL 11 HOUR, 'ERROR', 'EmailService', '邮件发送失败', 'javax.mail.MessagingException: Connection timeout', NULL, '127.0.0.1', 'Server/1.0', '收件人: patient@example.com'),
(NOW() - INTERVAL 10 HOUR, 'INFO', 'BackupService', '系统备份开始', NULL, NULL, '127.0.0.1', 'Server/1.0', '完整备份'),
(NOW() - INTERVAL 9 HOUR, 'INFO', 'BackupService', '系统备份完成', NULL, NULL, '127.0.0.1', 'Server/1.0', '备份大小: 1.2GB'),
(NOW() - INTERVAL 8 HOUR, 'INFO', 'UserService', '用户登录成功', NULL, 4, '192.168.1.104', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', '医生登录'),
(NOW() - INTERVAL 7 HOUR, 'DEBUG', 'AIService', 'AI模型加载成功', NULL, NULL, '127.0.0.1', 'Server/1.0', '模型版本: 2.3.0'),
(NOW() - INTERVAL 6 HOUR, 'INFO', 'MedicalService', '新增诊断报告', NULL, 4, '192.168.1.104', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', '报告ID: 1002'),
(NOW() - INTERVAL 5 HOUR, 'WARNING', 'PerformanceMonitor', 'CPU使用率过高', NULL, NULL, '127.0.0.1', 'Server/1.0', '使用率: 85%'),
(NOW() - INTERVAL 4 HOUR, 'INFO', 'SchedulerService', '定时任务执行成功', NULL, NULL, '127.0.0.1', 'Server/1.0', '任务: 清理临时文件'),
(NOW() - INTERVAL 3 HOUR, 'ERROR', 'AIService', 'AI诊断失败', 'java.lang.OutOfMemoryError: Java heap space', NULL, '127.0.0.1', 'Server/1.0', '图像ID: 5001'),
(NOW() - INTERVAL 2 HOUR, 'INFO', 'SystemService', '系统资源释放', NULL, NULL, '127.0.0.1', 'Server/1.0', '释放内存: 500MB'),
(NOW() - INTERVAL 1 HOUR, 'INFO', 'UserService', '用户登录成功', NULL, 5, '192.168.1.105', 'Mozilla/5.0 (iPad; CPU OS 14_7_1)', '患者登录'),
(NOW() - INTERVAL 50 MINUTE, 'INFO', 'UserService', '密码重置请求', NULL, NULL, '192.168.1.106', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', '用户邮箱: reset@example.com'),
(NOW() - INTERVAL 40 MINUTE, 'DEBUG', 'CacheService', '缓存刷新完成', NULL, NULL, '127.0.0.1', 'Server/1.0', '刷新项目: 用户数据'),
(NOW() - INTERVAL 30 MINUTE, 'INFO', 'UserService', '用户登出', NULL, 1, '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', '管理员登出'),
(NOW() - INTERVAL 20 MINUTE, 'WARNING', 'SecurityService', '检测到可疑访问', NULL, NULL, '192.168.1.110', 'Mozilla/5.0 (compatible; Googlebot/2.1)', '访问受限资源'),
(NOW() - INTERVAL 10 MINUTE, 'INFO', 'UserService', '用户登录成功', NULL, 1, '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', '管理员登录'),
(NOW() - INTERVAL 5 MINUTE, 'INFO', 'SystemMonitor', '系统状态正常', NULL, NULL, '127.0.0.1', 'Server/1.0', 'CPU: 45%, 内存: 60%, 存储: 75%');

-- 添加更多用户数据（8名医生，32名患者，医生:患者=1:4）

-- 新增医生用户数据
INSERT INTO users (username, password, email, role, enabled) VALUES
('doctor4', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'doctor4@hospital.com', 'ROLE_DOCTOR', true),
('doctor5', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'doctor5@hospital.com', 'ROLE_DOCTOR', true),
('doctor6', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'doctor6@hospital.com', 'ROLE_DOCTOR', true),
('doctor7', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'doctor7@hospital.com', 'ROLE_DOCTOR', true),
('doctor8', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'doctor8@hospital.com', 'ROLE_DOCTOR', true),
('doctor9', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'doctor9@hospital.com', 'ROLE_DOCTOR', true),
('doctor10', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'doctor10@hospital.com', 'ROLE_DOCTOR', true),
('doctor11', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'doctor11@hospital.com', 'ROLE_DOCTOR', true);

-- 新增患者用户数据
INSERT INTO users (username, password, email, role, enabled) VALUES
('patient4', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient4@example.com', 'ROLE_PATIENT', true),
('patient5', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient5@example.com', 'ROLE_PATIENT', true),
('patient6', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient6@example.com', 'ROLE_PATIENT', true),
('patient7', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient7@example.com', 'ROLE_PATIENT', true),
('patient8', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient8@example.com', 'ROLE_PATIENT', true),
('patient9', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient9@example.com', 'ROLE_PATIENT', true),
('patient10', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient10@example.com', 'ROLE_PATIENT', true),
('patient11', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient11@example.com', 'ROLE_PATIENT', true),
('patient12', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient12@example.com', 'ROLE_PATIENT', true),
('patient13', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient13@example.com', 'ROLE_PATIENT', true),
('patient14', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient14@example.com', 'ROLE_PATIENT', true),
('patient15', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient15@example.com', 'ROLE_PATIENT', true),
('patient16', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient16@example.com', 'ROLE_PATIENT', true),
('patient17', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient17@example.com', 'ROLE_PATIENT', true),
('patient18', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient18@example.com', 'ROLE_PATIENT', true),
('patient19', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient19@example.com', 'ROLE_PATIENT', true),
('patient20', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient20@example.com', 'ROLE_PATIENT', true),
('patient21', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient21@example.com', 'ROLE_PATIENT', true),
('patient22', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient22@example.com', 'ROLE_PATIENT', true),
('patient23', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient23@example.com', 'ROLE_PATIENT', true),
('patient24', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient24@example.com', 'ROLE_PATIENT', true),
('patient25', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient25@example.com', 'ROLE_PATIENT', true),
('patient26', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient26@example.com', 'ROLE_PATIENT', true),
('patient27', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient27@example.com', 'ROLE_PATIENT', true),
('patient28', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient28@example.com', 'ROLE_PATIENT', true),
('patient29', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient29@example.com', 'ROLE_PATIENT', true),
('patient30', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient30@example.com', 'ROLE_PATIENT', true),
('patient31', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient31@example.com', 'ROLE_PATIENT', true),
('patient32', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient32@example.com', 'ROLE_PATIENT', true),
('patient33', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient33@example.com', 'ROLE_PATIENT', true),
('patient34', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient34@example.com', 'ROLE_PATIENT', true),
('patient35', '$2a$10$aftVFrmsreAR7i2gN.bHVeFmzqgelvRaoPp8nGC5nQWCTmeEVqC3y', 'patient35@example.com', 'ROLE_PATIENT', true);

-- 新增医生详细信息
INSERT INTO doctors (user_id, full_name, professional_title, department, specialty, phone, hospital, license_number, gender, age, practice_years) VALUES
((SELECT id FROM users WHERE username = 'doctor4'), '王医生', '主治医师', '内科', '心血管疾病', '13833333333', '第二人民医院', 'LIC2024003', '男', 35, 10),
((SELECT id FROM users WHERE username = 'doctor5'), '赵医生', '副主任医师', '外科', '普外科手术', '13844444444', '第一人民医院', 'LIC2024004', '女', 42, 15),
((SELECT id FROM users WHERE username = 'doctor6'), '刘医生', '主任医师', '神经内科', '神经系统疾病', '13855555555', '中心医院', 'LIC2024005', '男', 48, 20),
((SELECT id FROM users WHERE username = 'doctor7'), '陈医生', '主治医师', '妇产科', '产科疾病', '13866666666', '妇幼保健院', 'LIC2024006', '女', 37, 12),
((SELECT id FROM users WHERE username = 'doctor8'), '孙医生', '住院医师', '儿科', '儿童常见疾病', '13877777777', '儿童医院', 'LIC2024007', '女', 29, 5),
((SELECT id FROM users WHERE username = 'doctor9'), '钱医生', '主任医师', '肿瘤科', '肿瘤治疗', '13888888888', '肿瘤医院', 'LIC2024008', '男', 52, 25),
((SELECT id FROM users WHERE username = 'doctor10'), '周医生', '副主任医师', '骨科', '脊柱外科', '13899999999', '骨科医院', 'LIC2024009', '男', 45, 18),
((SELECT id FROM users WHERE username = 'doctor11'), '吴医生', '主治医师', '皮肤科', '皮肤过敏', '13800000000', '皮肤病医院', 'LIC2024010', '女', 39, 14);

-- 新增患者详细信息
INSERT INTO patients (user_id, full_name, gender, birth_date, phone, address, medical_history, age, emergency_contact, emergency_phone) VALUES
((SELECT id FROM users WHERE username = 'patient4'), '张三', '男', '1990-05-15', '13911111111', '北京市朝阳区', '无特殊病史', 33, '张家人', '13900000003'),
((SELECT id FROM users WHERE username = 'patient5'), '李四', '女', '1985-12-10', '13922222222', '北京市海淀区', '慢性胃炎', 38, '李家人', '13900000004'),
((SELECT id FROM users WHERE username = 'patient6'), '王五', '男', '1978-08-20', '13933333333', '上海市浦东新区', '高血压、糖尿病', 45, '王家人', '13900000005'),
((SELECT id FROM users WHERE username = 'patient7'), '赵六', '女', '1995-03-25', '13944444444', '上海市静安区', '无特殊病史', 28, '赵家人', '13900000006'),
((SELECT id FROM users WHERE username = 'patient8'), '钱七', '男', '1982-11-30', '13955555555', '广州市天河区', '过敏性鼻炎', 41, '钱家人', '13900000007'),
((SELECT id FROM users WHERE username = 'patient9'), '孙八', '女', '1998-07-05', '13966666666', '广州市越秀区', '无特殊病史', 25, '孙家人', '13900000008'),
((SELECT id FROM users WHERE username = 'patient10'), '周九', '男', '1972-04-18', '13977777777', '深圳市南山区', '高血压', 51, '周家人', '13900000009'),
((SELECT id FROM users WHERE username = 'patient11'), '吴十', '女', '1992-09-22', '13988888888', '深圳市福田区', '无特殊病史', 31, '吴家人', '13900000010'),
((SELECT id FROM users WHERE username = 'patient12'), '郑十一', '男', '1980-02-14', '13999999999', '成都市武侯区', '胆囊炎', 43, '郑家人', '13900000011'),
((SELECT id FROM users WHERE username = 'patient13'), '王十二', '女', '1993-06-08', '15011111111', '成都市锦江区', '无特殊病史', 30, '王家人', '13900000012'),
((SELECT id FROM users WHERE username = 'patient14'), '李十三', '男', '1975-10-12', '15022222222', '重庆市渝北区', '冠心病', 48, '李家人', '13900000013'),
((SELECT id FROM users WHERE username = 'patient15'), '赵十四', '女', '1996-01-16', '15033333333', '重庆市渝中区', '无特殊病史', 27, '赵家人', '13900000014'),
((SELECT id FROM users WHERE username = 'patient16'), '钱十五', '男', '1983-05-20', '15044444444', '武汉市洪山区', '慢性支气管炎', 40, '钱家人', '13900000015'),
((SELECT id FROM users WHERE username = 'patient17'), '孙十六', '女', '1991-09-24', '15055555555', '武汉市江汉区', '无特殊病史', 32, '孙家人', '13900000016'),
((SELECT id FROM users WHERE username = 'patient18'), '周十七', '男', '1970-03-28', '15066666666', '南京市鼓楼区', '高尿酸血症', 53, '周家人', '13900000017'),
((SELECT id FROM users WHERE username = 'patient19'), '吴十八', '女', '1994-08-02', '15077777777', '南京市秦淮区', '无特殊病史', 29, '吴家人', '13900000018'),
((SELECT id FROM users WHERE username = 'patient20'), '郑十九', '男', '1981-12-06', '15088888888', '杭州市西湖区', '胃溃疡', 42, '郑家人', '13900000019'),
((SELECT id FROM users WHERE username = 'patient21'), '王二十', '女', '1997-04-10', '15099999999', '杭州市上城区', '无特殊病史', 26, '王家人', '13900000020'),
((SELECT id FROM users WHERE username = 'patient22'), '李二一', '男', '1974-08-14', '15100000000', '苏州市姑苏区', '高血脂', 49, '李家人', '13900000021'),
((SELECT id FROM users WHERE username = 'patient23'), '赵二二', '女', '1990-12-18', '15111111111', '苏州市吴中区', '无特殊病史', 33, '赵家人', '13900000022'),
((SELECT id FROM users WHERE username = 'patient24'), '钱二三', '男', '1982-04-22', '15122222222', '天津市和平区', '轻度脂肪肝', 41, '钱家人', '13900000023'),
((SELECT id FROM users WHERE username = 'patient25'), '孙二四', '女', '1995-08-26', '15133333333', '天津市河西区', '无特殊病史', 28, '孙家人', '13900000024'),
((SELECT id FROM users WHERE username = 'patient26'), '周二五', '男', '1971-12-30', '15144444444', '西安市雁塔区', '2型糖尿病', 52, '周家人', '13900000025'),
((SELECT id FROM users WHERE username = 'patient27'), '吴二六', '女', '1993-05-04', '15155555555', '西安市碑林区', '无特殊病史', 30, '吴家人', '13900000026'),
((SELECT id FROM users WHERE username = 'patient28'), '郑二七', '男', '1980-09-08', '15166666666', '郑州市金水区', '前列腺炎', 43, '郑家人', '13900000027'),
((SELECT id FROM users WHERE username = 'patient29'), '王二八', '女', '1996-01-12', '15177777777', '郑州市中原区', '无特殊病史', 27, '王家人', '13900000028'),
((SELECT id FROM users WHERE username = 'patient30'), '李二九', '男', '1973-05-16', '15188888888', '济南市历下区', '高血压', 50, '李家人', '13900000029'),
((SELECT id FROM users WHERE username = 'patient31'), '赵三十', '女', '1992-09-20', '15199999999', '济南市槐荫区', '无特殊病史', 31, '赵家人', '13900000030'),
((SELECT id FROM users WHERE username = 'patient32'), '钱三一', '男', '1984-01-24', '15200000000', '长沙市岳麓区', '慢性胃炎', 39, '钱家人', '13900000031'),
((SELECT id FROM users WHERE username = 'patient33'), '孙三二', '女', '1997-05-28', '15211111111', '长沙市芙蓉区', '无特殊病史', 26, '孙家人', '13900000032'),
((SELECT id FROM users WHERE username = 'patient34'), '周三三', '男', '1976-10-02', '15222222222', '青岛市市南区', '颈椎病', 47, '周家人', '13900000033'),
((SELECT id FROM users WHERE username = 'patient35'), '吴三四', '女', '1991-02-06', '15233333333', '青岛市市北区', '无特殊病史', 32, '吴家人', '13900000034');

-- 创建医生-患者关联关系（为每个医生分配4个患者）
INSERT INTO doctor_patient (doctor_id, patient_id, relationship_type, start_date, notes)
SELECT
    (SELECT id FROM doctors WHERE full_name = '王医生'),
    p.id,
    'PRIMARY',
    CURDATE() - INTERVAL FLOOR(RAND() * 365) DAY,
    '主治医生关系'
FROM patients p
WHERE p.full_name IN ('张三', '李四', '王五', '赵六')
LIMIT 4;

INSERT INTO doctor_patient (doctor_id, patient_id, relationship_type, start_date, notes)
SELECT
    (SELECT id FROM doctors WHERE full_name = '赵医生'),
    p.id,
    'PRIMARY',
    CURDATE() - INTERVAL FLOOR(RAND() * 365) DAY,
    '主治医生关系'
FROM patients p
WHERE p.full_name IN ('钱七', '孙八', '周九', '吴十')
LIMIT 4;

INSERT INTO doctor_patient (doctor_id, patient_id, relationship_type, start_date, notes)
SELECT
    (SELECT id FROM doctors WHERE full_name = '刘医生'),
    p.id,
    'PRIMARY',
    CURDATE() - INTERVAL FLOOR(RAND() * 365) DAY,
    '主治医生关系'
FROM patients p
WHERE p.full_name IN ('郑十一', '王十二', '李十三', '赵十四')
LIMIT 4;

INSERT INTO doctor_patient (doctor_id, patient_id, relationship_type, start_date, notes)
SELECT
    (SELECT id FROM doctors WHERE full_name = '陈医生'),
    p.id,
    'PRIMARY',
    CURDATE() - INTERVAL FLOOR(RAND() * 365) DAY,
    '主治医生关系'
FROM patients p
WHERE p.full_name IN ('钱十五', '孙十六', '周十七', '吴十八')
LIMIT 4;

INSERT INTO doctor_patient (doctor_id, patient_id, relationship_type, start_date, notes)
SELECT
    (SELECT id FROM doctors WHERE full_name = '孙医生'),
    p.id,
    'PRIMARY',
    CURDATE() - INTERVAL FLOOR(RAND() * 365) DAY,
    '主治医生关系'
FROM patients p
WHERE p.full_name IN ('郑十九', '王二十', '李二一', '赵二二')
LIMIT 4;

INSERT INTO doctor_patient (doctor_id, patient_id, relationship_type, start_date, notes)
SELECT
    (SELECT id FROM doctors WHERE full_name = '钱医生'),
    p.id,
    'PRIMARY',
    CURDATE() - INTERVAL FLOOR(RAND() * 365) DAY,
    '主治医生关系'
FROM patients p
WHERE p.full_name IN ('钱二三', '孙二四', '周二五', '吴二六')
LIMIT 4;

INSERT INTO doctor_patient (doctor_id, patient_id, relationship_type, start_date, notes)
SELECT
    (SELECT id FROM doctors WHERE full_name = '周医生'),
    p.id,
    'PRIMARY',
    CURDATE() - INTERVAL FLOOR(RAND() * 365) DAY,
    '主治医生关系'
FROM patients p
WHERE p.full_name IN ('郑二七', '王二八', '李二九', '赵三十')
LIMIT 4;

INSERT INTO doctor_patient (doctor_id, patient_id, relationship_type, start_date, notes)
SELECT
    (SELECT id FROM doctors WHERE full_name = '吴医生'),
    p.id,
    'PRIMARY',
    CURDATE() - INTERVAL FLOOR(RAND() * 365) DAY,
    '主治医生关系'
FROM patients p
WHERE p.full_name IN ('钱三一', '孙三二', '周三三', '吴三四')
LIMIT 4;

-- 添加更多系统日志数据
INSERT INTO system_logs (timestamp, level, source, message, stack_trace, user_id, ip_address, user_agent, details) VALUES
(NOW() - INTERVAL 4 MINUTE, 'INFO', 'UserService', '新用户注册', NULL, NULL, '192.168.1.120', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', '用户名: newuser1'),
(NOW() - INTERVAL 3 MINUTE, 'DEBUG', 'AuthService', '验证码发送', NULL, NULL, '192.168.1.121', 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_7_1)', '手机号: 139****1234'),
(NOW() - INTERVAL 2 MINUTE, 'WARNING', 'FileService', '上传文件过大', NULL, 2, '192.168.1.122', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', '文件大小: 15MB, 限制: 10MB'),
(NOW() - INTERVAL 1 MINUTE, 'INFO', 'PatientService', '患者信息更新', NULL, 5, '192.168.1.123', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', '患者ID: 1'),
(NOW(), 'INFO', 'AdminService', '系统设置更新', NULL, 1, '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', '更新项: 安全设置');