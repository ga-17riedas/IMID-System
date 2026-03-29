-- 创建健康记录表
CREATE TABLE health_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    record_type VARCHAR(20) NOT NULL COMMENT '记录类型：BLOOD_PRESSURE/BLOOD_SUGAR/HEART_RATE',
    value1 DOUBLE COMMENT '主要值（收缩压/血糖/心率）',
    value2 DOUBLE COMMENT '次要值（舒张压）',
    unit VARCHAR(10) COMMENT '单位',
    measure_time DATETIME NOT NULL COMMENT '测量时间',
    notes VARCHAR(255) COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    CONSTRAINT fk_health_records_patient 
        FOREIGN KEY (patient_id) 
        REFERENCES patients (id),
        
    INDEX idx_patient_type_time (patient_id, record_type, measure_time),
    INDEX idx_measure_time (measure_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者健康记录表';

-- 添加一些示例数据
INSERT INTO health_records (patient_id, record_type, value1, value2, unit, measure_time, notes)
SELECT 
    p.id,
    'BLOOD_PRESSURE',
    FLOOR(110 + RAND() * 30),  -- 随机血压收缩压 110-140
    FLOOR(70 + RAND() * 20),   -- 随机血压舒张压 70-90
    'mmHg',
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY),
    '日常测量'
FROM patients p
WHERE p.id <= 5
LIMIT 10;

INSERT INTO health_records (patient_id, record_type, value1, unit, measure_time, notes)
SELECT 
    p.id,
    'BLOOD_SUGAR',
    4.5 + RAND() * 2.5,  -- 随机血糖值 4.5-7.0
    'mmol/L',
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY),
    '空腹测量'
FROM patients p
WHERE p.id <= 5
LIMIT 10;

INSERT INTO health_records (patient_id, record_type, value1, unit, measure_time, notes)
SELECT 
    p.id,
    'HEART_RATE',
    FLOOR(60 + RAND() * 40),  -- 随机心率 60-100
    '次/分',
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY),
    '静息心率'
FROM patients p
WHERE p.id <= 5
LIMIT 10; 