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
    role VARCHAR(20) NOT NULL COMMENT '用户角色：ROLE_ADMIN/ROLE_DOCTOR/ROLE_PATIENT',
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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id)
) COMMENT='患者详细信息表';

-- 医学图像表（需要先创建，因为它被其他表引用）
CREATE TABLE IF NOT EXISTS medical_images (
    id VARCHAR(32) PRIMARY KEY COMMENT '图像ID',
    url VARCHAR(255) NOT NULL COMMENT '图像URL',
    upload_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    type VARCHAR(50) COMMENT '图像类型',
    description TEXT COMMENT '图像描述'
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

-- 诊断报告表 (5)
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
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id),
    FOREIGN KEY (medical_image_id) REFERENCES medical_images(id)
);

-- 诊断结果表
CREATE TABLE IF NOT EXISTS diagnosis_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '诊断ID，主键',
    image_id VARCHAR(32) NOT NULL COMMENT '关联的医学图像ID',
    doctor_id BIGINT NOT NULL COMMENT '诊断医生ID',
    diagnosis_text TEXT NOT NULL COMMENT '诊断结果描述',
    diagnosis_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '诊断日期',
    severity_level VARCHAR(20) COMMENT '严重程度：LOW/MEDIUM/HIGH',
    recommendations TEXT COMMENT '治疗建议',
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

-- 创建索引（如果已存在会自动跳过）
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

-- 系统日志表
CREATE TABLE IF NOT EXISTS system_logs (
    id BIGINT AUTO_INCREMENT COMMENT '日志ID，主键',
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '日志记录时间',
    level VARCHAR(20) NOT NULL COMMENT '日志级别：ERROR/WARNING/INFO/DEBUG',
    source VARCHAR(100) COMMENT '日志来源（模块/类名）',
    message TEXT NOT NULL COMMENT '日志内容',
    stack_trace TEXT COMMENT '错误堆栈信息（仅ERROR级别）',
    user_id BIGINT COMMENT '相关用户ID（如果有）',
    ip_address VARCHAR(50) COMMENT '操作IP地址',
    user_agent VARCHAR(255) COMMENT '用户代理信息',
    PRIMARY KEY (id)
) COMMENT='系统日志表，记录系统运行状态和错误信息';

-- 系统日志表索引
CREATE INDEX idx_system_logs_timestamp ON system_logs(timestamp);
CREATE INDEX idx_system_logs_level ON system_logs(level);
CREATE INDEX idx_system_logs_level_timestamp ON system_logs(level, timestamp);