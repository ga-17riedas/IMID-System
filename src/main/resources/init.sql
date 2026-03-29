-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS imid_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE imid_db;

-- 用户表 (1)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT COMMENT '用户ID，主键',
    username VARCHAR(50) NOT NULL COMMENT '用户名，唯一',
    password VARCHAR(100) NOT NULL COMMENT '密码，加密存储',
    email VARCHAR(100) COMMENT '电子邮箱',
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_PATIENT' COMMENT '用户角色：ROLE_ADMIN/ROLE_DOCTOR/ROLE_PATIENT',
    enabled BOOLEAN DEFAULT TRUE COMMENT '账号是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) COMMENT='用户基本信息表';

-- 医生表 (2)
CREATE TABLE IF NOT EXISTS doctors (
    id BIGINT AUTO_INCREMENT COMMENT '医生ID，主键',
    user_id BIGINT NOT NULL COMMENT '关联的用户ID',
    full_name VARCHAR(100) COMMENT '医生姓名',
    professional_title VARCHAR(50) COMMENT '职称',
    department VARCHAR(50) COMMENT '所属科室',
    specialty VARCHAR(200) COMMENT '专业特长',
    phone VARCHAR(20) COMMENT '联系电话',
    hospital VARCHAR(100) COMMENT '所属医院',
    license_number VARCHAR(50) UNIQUE COMMENT '执业证号',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id)
) COMMENT='医生详细信息表';

-- 患者表 (3)
CREATE TABLE IF NOT EXISTS patients (
    id BIGINT AUTO_INCREMENT COMMENT '患者ID，主键',
    user_id BIGINT NOT NULL COMMENT '关联的用户ID',
    full_name VARCHAR(100) NOT NULL COMMENT '患者姓名',
    gender VARCHAR(10) COMMENT '性别',
    birth_date DATE COMMENT '出生日期',
    phone VARCHAR(20) COMMENT '联系电话',
    address TEXT COMMENT '居住地址',
    medical_history TEXT COMMENT '病史记录',
    age INT COMMENT '年龄',
    emergency_contact VARCHAR(100) COMMENT '紧急联系人姓名',
    emergency_phone VARCHAR(20) COMMENT '紧急联系人电话',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id)
) COMMENT='患者详细信息表';

-- 健康记录表 (4)
CREATE TABLE IF NOT EXISTS health_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    record_type VARCHAR(50) NOT NULL COMMENT '记录类型',
    value1 DECIMAL(10,2) NOT NULL COMMENT '测量值1',
    value2 DECIMAL(10,2) COMMENT '测量值2',
    unit VARCHAR(20) COMMENT '单位',
    measure_time DATETIME NOT NULL COMMENT '测量时间',
    notes TEXT COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    INDEX idx_measure_time (measure_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者健康记录表';

-- 医学影像表 (5)
CREATE TABLE IF NOT EXISTS medical_images (
    id BIGINT AUTO_INCREMENT COMMENT '图像ID，主键',
    patient_id BIGINT NOT NULL COMMENT '关联的患者ID',
    image_path VARCHAR(255) NOT NULL COMMENT '图像存储路径',
    image_url VARCHAR(255) COMMENT '图像访问URL',
    image_type VARCHAR(50) COMMENT '图像类型',
    description TEXT COMMENT '图像描述',
    body_part VARCHAR(50) COMMENT '身体部位',
    ai_diagnosis TEXT COMMENT 'AI诊断结果',
    confidence_score DECIMAL(5,2) COMMENT 'AI诊断置信度',
    doctor_diagnosis TEXT COMMENT '医生诊断结果',
    upload_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    doctor_id BIGINT COMMENT '上传医生ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id)
) COMMENT='医学图像信息表';

-- 病史记录表
CREATE TABLE IF NOT EXISTS medical_histories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    date DATE NOT NULL,
    description TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id)
);

-- 诊断报告表 (6)
CREATE TABLE IF NOT EXISTS diagnosis_reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    medical_image_id BIGINT,
    diagnosis TEXT NOT NULL,
    treatment_plan TEXT,
    recommendations TEXT,
    read_status BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    expire_date DATETIME COMMENT '过期时间',
    is_expired BOOLEAN DEFAULT FALSE COMMENT '是否已过期',
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id),
    FOREIGN KEY (medical_image_id) REFERENCES medical_images(id)
);

-- 删除旧表（如果存在）
DROP TABLE IF EXISTS diagnosis_results;

-- 创建新的诊断结果表
CREATE TABLE diagnosis_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    image_id VARCHAR(32) NOT NULL COMMENT '图像ID',
    image_url VARCHAR(255) COMMENT '图像URL',
    analysis_type VARCHAR(50) COMMENT '分析类型',
    diagnosis TEXT COMMENT '诊断结果',
    confidence DOUBLE COMMENT '置信度',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='诊断结果信息表';

-- 预约表
CREATE TABLE IF NOT EXISTS appointments (
    id BIGINT AUTO_INCREMENT COMMENT '预约ID，主键',
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    doctor_id BIGINT NOT NULL COMMENT '医生ID',
    appointment_time TIMESTAMP NOT NULL COMMENT '预约时间',
    status VARCHAR(20) NOT NULL COMMENT '预约状态：PENDING/CONFIRMED/CANCELLED',
    description TEXT COMMENT '预约说明',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id)
) COMMENT='预约信息表';

-- 医生-患者关联表
CREATE TABLE IF NOT EXISTS doctor_patient (
    id BIGINT AUTO_INCREMENT COMMENT '关联ID，主键',
    doctor_id BIGINT NOT NULL COMMENT '医生ID',
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    relationship_type VARCHAR(50) COMMENT '关系类型：PRIMARY/CONSULTING',
    start_date DATE NOT NULL COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    notes TEXT COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id),
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    UNIQUE KEY uk_doctor_patient (doctor_id, patient_id)
) COMMENT='医生-患者关联表';

-- 病历记录表
CREATE TABLE IF NOT EXISTS medical_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    patient_id BIGINT NOT NULL,
    visit_date DATETIME NOT NULL COMMENT '就诊日期',
    hospital VARCHAR(100) NOT NULL COMMENT '就诊医院',
    department VARCHAR(50) NOT NULL COMMENT '科室',
    doctor_name VARCHAR(50) NOT NULL COMMENT '医生姓名',
    diagnosis TEXT NOT NULL COMMENT '诊断结果',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (patient_id) REFERENCES patients(id)
);

-- 创建索引
CREATE INDEX idx_user_username ON users(username);
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_doctor_department ON doctors(department);
CREATE INDEX idx_doctor_title ON doctors(professional_title);
CREATE INDEX idx_patient_name ON patients(full_name);
CREATE INDEX idx_appointment_time ON appointments(appointment_time);
CREATE INDEX idx_appointment_status ON appointments(status);
CREATE INDEX idx_doctor_patient_doctor ON doctor_patient(doctor_id);
CREATE INDEX idx_doctor_patient_patient ON doctor_patient(patient_id);
CREATE INDEX idx_medical_histories_patient ON medical_histories(patient_id);
CREATE INDEX idx_medical_histories_doctor ON medical_histories(doctor_id);
CREATE INDEX idx_diagnosis_reports_patient ON diagnosis_reports(patient_id);
CREATE INDEX idx_diagnosis_reports_doctor ON diagnosis_reports(doctor_id);

-- 清空现有数据（可选，注意顺序）
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE doctor_patient;
TRUNCATE TABLE appointments;
TRUNCATE TABLE diagnosis_results;
TRUNCATE TABLE diagnosis_reports;
TRUNCATE TABLE medical_images;
TRUNCATE TABLE medical_records;
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

-- 5. 插入病历记录测试数据
INSERT INTO medical_records (
    patient_id, 
    visit_date, 
    hospital, 
    department, 
    doctor_name, 
    diagnosis, 
    created_at
) VALUES 
((SELECT id FROM patients WHERE full_name = '张病人'), '2024-02-01 09:00:00', '第一人民医院', '内科', '张医生', '感冒', NOW()),
((SELECT id FROM patients WHERE full_name = '张病人'), '2024-01-15 14:30:00', '第二人民医院', '骨科', '李医生', '扭伤', NOW()),
((SELECT id FROM patients WHERE full_name = '张病人'), '2023-12-20 11:00:00', '中心医院', '眼科', '王医生', '近视', NOW());

-- 更新密码为明文"123456"的加密值
UPDATE users 
SET password = '$2a$10$Ko5RrGPVERg2rTN5c.iPYOfzu4SK1xHadyerqNvazq.rb4LfOcmTm'
WHERE username IN ('admin', 'doctor1', 'doctor2', 'doctor3', 'patient1', 'patient2', 'patient3');

-- 删除旧表（如果存在）
DROP TABLE IF EXISTS diagnosis_results;

-- 创建新的诊断结果表
CREATE TABLE diagnosis_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    image_id VARCHAR(32) NOT NULL COMMENT '图像ID',
    image_url VARCHAR(255) COMMENT '图像URL',
    analysis_type VARCHAR(50) COMMENT '分析类型',
    diagnosis TEXT COMMENT '诊断结果',
    confidence DOUBLE COMMENT '置信度',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='诊断结果信息表';

-- 插入医学图像数据
INSERT INTO medical_images (patient_id, image_path, image_type, description, doctor_id) VALUES
((SELECT id FROM patients WHERE full_name = '张病人'), 
 '/images/xray_chest_001.jpg', 
 'X光', 
 '胸部X光片',
 (SELECT id FROM doctors WHERE full_name = '张医生')),
((SELECT id FROM patients WHERE full_name = '李病人'), 
 '/images/ct_brain_001.jpg', 
 'CT', 
 '头部CT扫描',
 (SELECT id FROM doctors WHERE full_name = '李医生')),
((SELECT id FROM patients WHERE full_name = '张病人'), 
 '/images/mri_knee_001.jpg', 
 'MRI', 
 '膝关节核磁共振',
 (SELECT id FROM doctors WHERE full_name = '张医生'));

-- 插入诊断报告数据
INSERT INTO diagnosis_reports (patient_id, doctor_id, medical_image_id, diagnosis, treatment_plan, recommendations) VALUES
((SELECT id FROM patients WHERE full_name = '张病人'),
 (SELECT id FROM doctors WHERE full_name = '张医生'),
 (SELECT id FROM medical_images WHERE image_path = '/images/xray_chest_001.jpg'),
 '胸部X光显示正常',
 '无需特殊治疗',
 '建议定期复查'),
((SELECT id FROM patients WHERE full_name = '李病人'),
 (SELECT id FROM doctors WHERE full_name = '李医生'),
 (SELECT id FROM medical_images WHERE image_path = '/images/ct_brain_001.jpg'),
 '未见异常',
 '继续观察',
 '如有不适及时就医'),
((SELECT id FROM patients WHERE full_name = '张病人'),
 (SELECT id FROM doctors WHERE full_name = '张医生'),
 (SELECT id FROM medical_images WHERE image_path = '/images/mri_knee_001.jpg'),
 '轻微关节炎症状',
 '建议物理治疗',
 '避免剧烈运动，保持适度活动');

-- 插入预约数据
INSERT INTO appointments (patient_id, doctor_id, appointment_time, status, description) VALUES
((SELECT id FROM patients WHERE full_name = '张病人'),
 (SELECT id FROM doctors WHERE full_name = '张医生'),
 DATE_ADD(NOW(), INTERVAL 1 DAY),
 'CONFIRMED',
 '常规检查'),
((SELECT id FROM patients WHERE full_name = '李病人'),
 (SELECT id FROM doctors WHERE full_name = '李医生'),
 DATE_ADD(NOW(), INTERVAL 2 DAY),
 'PENDING',
 'CT复查'),
((SELECT id FROM patients WHERE full_name = '张病人'),
 (SELECT id FROM doctors WHERE full_name = '张医生'),
 DATE_ADD(NOW(), INTERVAL 3 DAY),
 'CONFIRMED',
 'MRI检查');

-- 插入健康记录数据
INSERT INTO health_records (patient_id, record_type, value1, value2, unit, measure_time, notes) VALUES
((SELECT id FROM patients WHERE full_name = '张病人'),
 '血压',
 120,
 80,
 'mmHg',
 NOW(),
 '正常范围'),
((SELECT id FROM patients WHERE full_name = '李病人'),
 '血糖',
 5.6,
 NULL,
 'mmol/L',
 NOW(),
 '空腹血糖'),
((SELECT id FROM patients WHERE full_name = '张病人'),
 '体温',
 36.5,
 NULL,
 '℃',
 NOW(),
 '正常体温');

-- 插入病史记录数据
INSERT INTO medical_histories (patient_id, doctor_id, date, description) VALUES
((SELECT id FROM patients WHERE full_name = '张病人'),
 (SELECT id FROM doctors WHERE full_name = '张医生'),
 CURDATE(),
 '首次就诊，常规检查'),
((SELECT id FROM patients WHERE full_name = '李病人'),
 (SELECT id FROM doctors WHERE full_name = '李医生'),
 DATE_SUB(CURDATE(), INTERVAL 1 DAY),
 '复查，情况稳定'),
((SELECT id FROM patients WHERE full_name = '张病人'),
 (SELECT id FROM doctors WHERE full_name = '张医生'),
 DATE_SUB(CURDATE(), INTERVAL 2 DAY),
 'MRI检查，结果正常'); 