#预测模块

import os
import sys
# 添加项目根目录到Python路径
project_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.append(project_root)

import numpy as np
import cv2
from configs.config import Config
from pathlib import Path
from ultralytics import YOLO


class MedicalImagePredictor:
    def __init__(self):
        self.brain_tumor_model = YOLO(os.path.join(Config.MODEL_SAVE_DIR, 'brain_tumor_model.pt'))
        self.covid_model = YOLO(os.path.join(Config.MODEL_SAVE_DIR, 'covid_model.pt'))
        
    def preprocess_image(self, image_path):
        """预处理图像"""
        img = cv2.imread(str(image_path))
        if img is None:
            raise ValueError(f"无法读取图像: {image_path}")
        img = cv2.resize(img, (Config.IMG_SIZE, Config.IMG_SIZE))  # 使用Config.IMG_SIZE替代IMAGE_SIZE
        img = img / 255.0
        return np.expand_dims(img, axis=0)
    
    def predict_brain_tumor(self, image_path):
        """预测脑肿瘤图像"""
        try:
            results = self.brain_tumor_model.predict(image_path)
            return self._process_results(results, Config.BRAIN_TUMOR_CLASSES)
        except Exception as e:
            print(f"预测脑肿瘤图像时出错: {str(e)}")
            return []
    
    def predict_covid(self, image_path):
        """预测COVID-19胸部CT图像"""
        try:
            results = self.covid_model.predict(image_path)
            return self._process_results(results, Config.COVID_CLASSES)
        except Exception as e:
            print(f"预测COVID图像时出错: {str(e)}")
            return []
    
    def _process_results(self, results, classes):
        """处理预测结果"""
        result = results[0]
        predictions = []
        
        for box in result.boxes:
            class_id = int(box.cls[0])
            confidence = float(box.conf[0])
            class_name = classes[class_id]
            bbox = box.xyxy[0].tolist()  # 获取边界框坐标
            predictions.append({
                'class': class_name,
                'confidence': confidence,
                'bbox': bbox  # [x1, y1, x2, y2]
            })
        
        return predictions

def main():
    predictor = MedicalImagePredictor()
    
    # 测试脑肿瘤预测
    brain_test_path = Path(Config.BRAIN_TUMOR_DATASET) / "images" / "test" / "Te-gl_0010.jpg"
    if brain_test_path.exists():
        print(f"正在预测脑肿瘤图像: {brain_test_path}")
        results = predictor.predict_brain_tumor(brain_test_path)
        print("脑肿瘤预测结果:", results)
    else:
        print(f"脑肿瘤测试图像不存在: {brain_test_path}")
    
    # 测试COVID-19预测
    covid_test_path = Path(Config.COVID_DATASET) / "images" / "test" / "1%0.jpg"
    if covid_test_path.exists():
        print(f"正在预测COVID图像: {covid_test_path}")
        results = predictor.predict_covid(covid_test_path)
        print("COVID-19预测结果:", results)
    else:
        print(f"COVID测试图像不存在: {covid_test_path}")

if __name__ == "__main__":
    main()