#数据预处理模块
import cv2
import numpy as np
from pathlib import Path
import os
import sys

# 添加项目根目录到Python路径
project_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.append(project_root)

from configs.config import Config

class DataPreprocessor:
    def __init__(self):
        self.img_size = Config.IMG_SIZE
    
    def preprocess_image(self, image_path):
        """
        预处理单张图像
        :param image_path: 图像路径
        :return: 预处理后的图像
        """
        # 读取图像
        img = cv2.imread(str(image_path))
        if img is None:
            raise ValueError(f"无法读取图像: {image_path}")
        
        # 调整大小
        img = cv2.resize(img, (self.img_size, self.img_size))
        
        # 标准化
        img = img / 255.0
        
        return img
    
    def augment_image(self, image):
        """
        数据增强
        :param image: 输入图像
        :return: 增强后的图像
        """
        # 随机亮度调整
        if np.random.random() > 0.5:
            brightness = np.random.uniform(0.8, 1.2)
            image = cv2.convertScaleAbs(image, alpha=brightness, beta=0)
        
        # 随机对比度调整
        if np.random.random() > 0.5:
            contrast = np.random.uniform(0.8, 1.2)
            mean = np.mean(image)
            image = (image - mean) * contrast + mean
        
        # 随机水平翻转
        if np.random.random() > 0.5:
            image = cv2.flip(image, 1)
        
        # 确保像素值在[0,1]范围内
        image = np.clip(image, 0, 1)
        
        return image

if __name__ == "__main__":
    preprocessor = DataPreprocessor()
    print("开始测试数据预处理器...")
    
    # 创建测试图像
    test_image = np.random.rand(100, 100, 3)  # 创建随机测试图像
    print("创建测试图像成功")
    
    # 测试数据增强
    augmented_image = preprocessor.augment_image(test_image)
    print("数据增强测试成功")
    
    # 打印图像信息
    print(f"原始图像形状: {test_image.shape}")
    print(f"增强后图像形状: {augmented_image.shape}")
    print(f"原始图像值范围: [{test_image.min():.2f}, {test_image.max():.2f}]")
    print(f"增强后图像值范围: [{augmented_image.min():.2f}, {augmented_image.max():.2f}]")
    
    # 测试脑肿瘤图像
    brain_test_path = Path(Config.BRAIN_TUMOR_DATASET) / "Testing" / "glioma" / "1.jpg"
    print(f"尝试访问脑肿瘤测试图像: {brain_test_path}")
    if brain_test_path.exists():
        print("找到脑肿瘤测试图像")
        img = preprocessor.preprocess_image(brain_test_path)
        aug_img = preprocessor.augment_image(img)
        print("脑肿瘤图像预处理成功")
    else:
        print(f"脑肿瘤测试图像不存在: {brain_test_path}")
    
    # 测试COVID图像
    covid_test_path = Path(Config.COVID_DATASET) / "COVID-19" / "test" / "COVID" / "1.png"
    print(f"尝试访问COVID测试图像: {covid_test_path}")
    if covid_test_path.exists():
        print("找到COVID测试图像")
        img = preprocessor.preprocess_image(covid_test_path)
        aug_img = preprocessor.augment_image(img)
        print("COVID图像预处理成功")
    else:
        print(f"COVID测试图像不存在: {covid_test_path}")
    
    # 列出可用的数据集目录
    print("\n可用的目录结构:")
    if Config.BASE_DIR.exists():
        for path in Config.BASE_DIR.rglob("*"):
            if path.is_dir():
                print(f"目录: {path}")
            elif path.is_file():
                print(f"文件: {path}")
    else:
        print(f"基础目录不存在: {Config.BASE_DIR}") 