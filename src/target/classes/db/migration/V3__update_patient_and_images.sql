-- 更新患者表结构
ALTER TABLE patients 
ADD COLUMN age INT COMMENT '年龄',
ADD COLUMN emergency_contact VARCHAR(100) COMMENT '紧急联系人姓名',
ADD COLUMN emergency_phone VARCHAR(20) COMMENT '紧急联系人电话';

-- 创建触发器更新年龄
DELIMITER //
CREATE TRIGGER update_patient_age_insert 
BEFORE INSERT ON patients
FOR EACH ROW
BEGIN
    SET NEW.age = TIMESTAMPDIFF(YEAR, NEW.birth_date, CURDATE());
END//

CREATE TRIGGER update_patient_age_update
BEFORE UPDATE ON patients
FOR EACH ROW
BEGIN
    IF NEW.birth_date != OLD.birth_date OR NEW.age IS NULL THEN
        SET NEW.age = TIMESTAMPDIFF(YEAR, NEW.birth_date, CURDATE());
    END IF;
END//
DELIMITER ;

-- 初始化现有患者的年龄
UPDATE patients 
SET age = TIMESTAMPDIFF(YEAR, birth_date, CURDATE())
WHERE age IS NULL;

-- 更新医学影像表结构
ALTER TABLE medical_images 
ADD COLUMN body_part VARCHAR(50) COMMENT '身体部位',
ADD COLUMN ai_diagnosis TEXT COMMENT 'AI诊断结果',
ADD COLUMN confidence_score DECIMAL(5,2) COMMENT 'AI诊断置信度',
ADD COLUMN doctor_diagnosis TEXT COMMENT '医生诊断结果';

-- 修改用户表的角色字段
ALTER TABLE users 
MODIFY COLUMN role VARCHAR(20) NOT NULL DEFAULT 'ROLE_PATIENT' 
COMMENT '用户角色：ROLE_ADMIN/ROLE_DOCTOR/ROLE_PATIENT';

-- 更新现有患者数据
UPDATE patients SET 
    emergency_contact = CONCAT(full_name, '的家属'),
    emergency_phone = CONCAT('139', LPAD(id, 8, '0'))
WHERE emergency_contact IS NULL;

-- 更新现有医学影像数据
UPDATE medical_images SET 
    body_part = CASE 
        WHEN image_type = 'MRI' THEN '大脑'
        WHEN image_type = 'CT' THEN '胸部'
        ELSE '未指定'
    END,
    ai_diagnosis = '待AI分析',
    confidence_score = 0.00,
    doctor_diagnosis = '待医生诊断'
WHERE body_part IS NULL;

-- 更新用户角色格式
UPDATE users 
SET role = CONCAT('ROLE_', UPPER(REPLACE(role, 'ROLE_', '')))
WHERE role NOT LIKE 'ROLE_%'; 