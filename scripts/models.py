import torch
from ultralytics import YOLO

def load_yolo_model(model_path):
    """
    加载预训练的YOLOv8模型
    """
    try:
        # 直接使用YOLO加载模型
        model = YOLO(model_path)
        return model
        
    except Exception as e:
        print(f"Error loading model: {str(e)}")
        raise Exception(f"Failed to load model from {model_path}: {str(e)}") 