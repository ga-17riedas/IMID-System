#配置文件

import os
from pathlib import Path

class Config:
    # 基础路径
    BASE_DIR = Path("D:/IMID")  # 使用正斜杠
    
    # 数据集路径
    BRAIN_TUMOR_DATASET = BASE_DIR / "model_images/brain_tumor"
    COVID_DATASET = BASE_DIR / "model_images/covid"
    
    # 模型保存路径
    MODEL_SAVE_DIR = os.path.join(BASE_DIR, "IMID_model", "saved_models")
    
    # 训练参数
    BATCH_SIZE = 16
    EPOCHS = 50
    IMG_SIZE = 640  # YOLOv8默认输入大小
    
    # 类别
    BRAIN_TUMOR_CLASSES = ['glioma', 'meningioma', 'notumor', 'pituitary']
    COVID_CLASSES = ['COVID', 'NonCOVID']
    
    # 训练设置
    LEARNING_RATE = 0.001
    VALIDATION_SPLIT = 0.2