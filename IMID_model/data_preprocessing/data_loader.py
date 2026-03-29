#数据加载模块
import tensorflow as tf
import numpy as np
from pathlib import Path
import cv2
import os
import sys

# 添加项目根目录到Python路径
project_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.append(project_root)

# 现在导入Config
from configs.config import Config

class DataLoader:
    def __init__(self, dataset_path, dataset_type):
        """
        初始化数据加载器
        :param dataset_path: 数据集路径
        :param dataset_type: 数据集类型 ('brain_tumor' 或 'covid')
        """
        self.dataset_path = Path(dataset_path)
        self.dataset_type = dataset_type
        self.classes = (Config.BRAIN_TUMOR_CLASSES if dataset_type == 'brain_tumor' 
                       else Config.COVID_CLASSES)
    
    def prepare_yolo_dataset(self):
        """准备YOLO格式的数据集"""
        print(f"正在处理数据集: {self.dataset_path}")
        print(f"数据集类型: {self.dataset_type}")
        
        # 验证数据集路径
        if not self.dataset_path.exists():
            print(f"当前工作目录: {os.getcwd()}")
            print(f"所有可用路径: {list(Path(Config.BASE_DIR).glob('**/*'))}")
            raise FileNotFoundError(f"数据集路径不存在: {self.dataset_path}")
        
        # 创建必要的目录结构
        for split in ['train', 'val', 'test']:
            os.makedirs(self.dataset_path / 'images' / split, exist_ok=True)
            os.makedirs(self.dataset_path / 'labels' / split, exist_ok=True)
        
        if self.dataset_type == 'brain_tumor':
            self._prepare_brain_tumor_data()
        else:
            self._prepare_covid_data()
            
    def _prepare_brain_tumor_data(self):
        """准备脑肿瘤数据集"""
        import random
        
        # 收集所有图像路径
        all_images = []
        train_path = self.dataset_path / 'Training'
        test_path = self.dataset_path / 'Testing'
        
        # 收集训练集图像
        for class_idx, class_name in enumerate(self.classes):
            class_path = train_path / class_name
            if class_path.exists():
                images = list(class_path.glob('*.jpg'))
                all_images.extend([(img, class_idx) for img in images])
        
        # 收集测试集图像
        test_images = []
        for class_idx, class_name in enumerate(self.classes):
            class_path = test_path / class_name
            if class_path.exists():
                images = list(class_path.glob('*.jpg'))
                test_images.extend([(img, class_idx) for img in images])
        
        # 手动分割训练集和验证集
        random.seed(42)  # 设置随机种子以确保可重复性
        random.shuffle(all_images)
        
        # 计算分割点
        val_size = int(len(all_images) * 0.2)  # 20%用于验证
        val_images = all_images[:val_size]
        train_images = all_images[val_size:]
        
        print(f"训练集数量: {len(train_images)}")
        print(f"验证集数量: {len(val_images)}")
        print(f"测试集数量: {len(test_images)}")
        
        # 处理训练集
        for img_path, class_idx in train_images:
            self._create_yolo_label(img_path, class_idx, 'train')
        
        # 处理验证集
        for img_path, class_idx in val_images:
            self._create_yolo_label(img_path, class_idx, 'val')
        
        # 处理测试集
        for img_path, class_idx in test_images:
            self._create_yolo_label(img_path, class_idx, 'test')

    def _prepare_covid_data(self):
        """准备COVID数据集"""
        print("开始准备COVID数据集...")
        base_path = self.dataset_path / 'COVID-19'
        
        # 处理训练集
        train_path = base_path / 'train'
        if train_path.exists():
            for class_idx, class_name in enumerate(self.classes):
                class_path = train_path / class_name
                if class_path.exists():
                    print(f"处理训练集: {class_name}")
                    for img_path in class_path.glob('*.*'):
                        if img_path.suffix.lower() in ['.jpg', '.png', '.jpeg']:
                            self._create_yolo_label(img_path, class_idx, 'train')
        
        # 处理验证集
        val_path = base_path / 'val'
        if val_path.exists():
            for class_idx, class_name in enumerate(self.classes):
                class_path = val_path / class_name
                if class_path.exists():
                    print(f"处理验证集: {class_name}")
                    for img_path in class_path.glob('*.*'):
                        if img_path.suffix.lower() in ['.jpg', '.png', '.jpeg']:
                            self._create_yolo_label(img_path, class_idx, 'val')
        
        # 处理测试集
        test_path = base_path / 'test'
        if test_path.exists():
            for class_idx, class_name in enumerate(self.classes):
                class_path = test_path / class_name
                if class_path.exists():
                    print(f"处理测试集: {class_name}")
                    for img_path in class_path.glob('*.*'):
                        if img_path.suffix.lower() in ['.jpg', '.png', '.jpeg']:
                            self._create_yolo_label(img_path, class_idx, 'test')
        
        print("COVID数据集准备完成")
    
    def _create_yolo_label(self, img_path, class_idx, split):
        """
        创建YOLO格式的标签文件
        :param img_path: 图像路径
        :param class_idx: 类别索引
        :param split: 数据集分割（train/val/test）
        """
        try:
            # 使用str()确保路径是字符串格式
            img = cv2.imdecode(np.fromfile(str(img_path), dtype=np.uint8), cv2.IMREAD_COLOR)
            if img is None:
                print(f"无法读取图像: {img_path}")
                return
            
            h, w = img.shape[:2]
            
            # 创建标签文件路径
            label_dir = self.dataset_path / 'labels' / split
            label_path = label_dir / f"{img_path.stem}.txt"
            
            # 将图像复制到正确的位置
            dest_img_path = self.dataset_path / 'images' / split / img_path.name
            if not dest_img_path.exists():
                os.makedirs(dest_img_path.parent, exist_ok=True)
                # 使用cv2.imencode和fromfile来保存包含中文路径的图像
                _, img_encoded = cv2.imencode('.jpg', img)
                img_encoded.tofile(str(dest_img_path))
            
            # 创建YOLO格式的标签
            with open(label_path, 'w') as f:
                f.write(f"{class_idx} 0.5 0.5 1.0 1.0\n")
                
        except Exception as e:
            print(f"处理图像时出错 {img_path}: {str(e)}")

if __name__ == "__main__":
    # 测试脑肿瘤数据集加载器
    brain_loader = DataLoader(Config.BRAIN_TUMOR_DATASET, 'brain_tumor')
    brain_loader.prepare_yolo_dataset()
    
    # 测试COVID数据集加载器
    covid_loader = DataLoader(Config.COVID_DATASET, 'covid')
    covid_loader.prepare_yolo_dataset()