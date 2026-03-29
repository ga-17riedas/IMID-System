-- 设置外键检查为0，允许插入数据
SET FOREIGN_KEY_CHECKS = 0;

-- 插入或更新医学图像数据
REPLACE INTO medical_images (id, patient_id, image_path, image_url, image_type, description, body_part, ai_diagnosis, confidence_score, doctor_diagnosis, upload_time, doctor_id, created_at, updated_at) VALUES
(1, 1, '/images/brain_scan_1.jpg', NULL, 'MRI', '脑部MRI扫描', '大脑', '正常', 0.95, '正常', '2025-02-05 10:30:00', 1, '2025-02-05 10:30:00', '2025-02-05 10:30:00'),
(2, 1, '/images/brain_scan_2.jpg', NULL, 'CT', '脑部CT扫描', '大脑', '脑部肿瘤', 0.85, '脑部肿瘤', '2025-02-10 14:00:00', 1, '2025-02-10 14:00:00', '2025-02-10 14:00:00'),
(4, 2, '/images/lung_scan_1.jpg', NULL, 'X-Ray', '肺部X光', '肺部', '肺炎', 0.90, '肺炎', '2025-02-08 09:30:00', 2, '2025-02-08 09:30:00', '2025-02-08 09:30:00'),
(5, 2, '/images/lung_scan_2.jpg', NULL, 'CT', '肺部CT扫描', '肺部', '轻微肺部感染', 0.88, '轻微肺部感染', '2025-02-15 11:00:00', 2, '2025-02-15 11:00:00', '2025-02-15 11:00:00'),
(6, 1, '/images/brain_scan_3.jpg', NULL, 'CT', '脑部CT扫描', '大脑', '脑出血', 0.92, '脑出血', '2025-02-20 16:00:00', 1, '2025-02-20 16:00:00', '2025-02-20 16:00:00'),
(7, 2, '/images/lung_scan_3.jpg', NULL, 'MRI', '肺部MRI扫描', '肺部', '肺结节', 0.87, '肺结节', '2025-02-25 13:30:00', 1, '2025-02-25 13:30:00', '2025-02-25 13:30:00');

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1; 