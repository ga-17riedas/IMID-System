import os
from pathlib import Path
import shutil
from configs.config import Config

def setup_project():
    """
    初始化项目结构
    """
    print("开始初始化项目结构...")
    
    # 创建基本目录结构
    directories = [
        # 数据集目录
        Config.BRAIN_TUMOR_DATASET,
        Config.COVID_DATASET,
        # 模型保存目录
        Path(Config.MODEL_SAVE_DIR),
        # YOLO数据集目录结构
        Config.BRAIN_TUMOR_DATASET / 'images' / 'train',
        Config.BRAIN_TUMOR_DATASET / 'images' / 'val',
        Config.BRAIN_TUMOR_DATASET / 'images' / 'test',
        Config.BRAIN_TUMOR_DATASET / 'labels' / 'train',
        Config.BRAIN_TUMOR_DATASET / 'labels' / 'val',
        Config.BRAIN_TUMOR_DATASET / 'labels' / 'test',
        Config.COVID_DATASET / 'images' / 'train',
        Config.COVID_DATASET / 'images' / 'val',
        Config.COVID_DATASET / 'images' / 'test',
        Config.COVID_DATASET / 'labels' / 'train',
        Config.COVID_DATASET / 'labels' / 'val',
        Config.COVID_DATASET / 'labels' / 'test',
    ]
    
    for directory in directories:
        directory.mkdir(parents=True, exist_ok=True)
        print(f"创建目录: {directory}")
    
    # 创建原始数据集目录结构
    brain_tumor_structure = [
        'Training/glioma',
        'Training/meningioma',
        'Training/notumor',
        'Training/pituitary',
        'Testing/glioma',
        'Testing/meningioma',
        'Testing/notumor',
        'Testing/pituitary'
    ]
    
    covid_structure = [
        'COVID-19/train/COVID',
        'COVID-19/train/NonCOVID',
        'COVID-19/val/COVID',
        'COVID-19/val/NonCOVID',
        'COVID-19/test/COVID',
        'COVID-19/test/NonCOVID'
    ]
    
    # 创建脑肿瘤数据集目录
    for path in brain_tumor_structure:
        full_path = Config.BRAIN_TUMOR_DATASET / path
        full_path.mkdir(parents=True, exist_ok=True)
        print(f"创建目录: {full_path}")
    
    # 创建COVID数据集目录
    for path in covid_structure:
        full_path = Config.COVID_DATASET / path
        full_path.mkdir(parents=True, exist_ok=True)
        print(f"创建目录: {full_path}")
    
    print("\n项目结构初始化完成！")
    print("\n请将数据集文件放置在以下位置：")
    print(f"脑肿瘤数据集: {Config.BRAIN_TUMOR_DATASET}")
    print(f"COVID数据集: {Config.COVID_DATASET}")

def move_datasets():
    """移动原始数据集到正确的位置"""
    print("开始移动数据集...")
    
    # 原始数据集路径
    original_brain_path = Config.BASE_DIR / "model_images" / "脑肿瘤 MRI 数据集"
    original_covid_path = Config.BASE_DIR / "model_images" / "COVID-19 胸部 CT 图像增强 GAN 数据集" / "COVID-19" / "COVID-19"
    
    # 移动脑肿瘤数据集
    if original_brain_path.exists():
        print("移动脑肿瘤数据集...")
        for split in ['Training', 'Testing']:
            for class_name in Config.BRAIN_TUMOR_CLASSES:
                src = original_brain_path / split / class_name
                dst = Config.BRAIN_TUMOR_DATASET / split / class_name
                if src.exists():
                    print(f"复制 {src} -> {dst}")
                    dst.parent.mkdir(parents=True, exist_ok=True)
                    if dst.exists():
                        import shutil
                        shutil.rmtree(dst)
                    shutil.copytree(src, dst)
    
    # 移动COVID数据集
    if original_covid_path.exists():
        print("\n移动COVID数据集...")
        for split in ['train', 'val', 'test']:
            for class_name in Config.COVID_CLASSES:
                src = original_covid_path / split / class_name
                dst = Config.COVID_DATASET / "COVID-19" / split / class_name
                if src.exists():
                    print(f"复制 {src} -> {dst}")
                    dst.parent.mkdir(parents=True, exist_ok=True)
                    if dst.exists():
                        import shutil
                        shutil.rmtree(dst)
                    shutil.copytree(src, dst)
    
    print("\n数据集移动完成！")
    print("\n现在运行数据预处理...")
    
    # 运行数据预处理
    from data_preprocessing.data_loader import DataLoader
    
    # 处理脑肿瘤数据集
    brain_loader = DataLoader(Config.BRAIN_TUMOR_DATASET, 'brain_tumor')
    brain_loader.prepare_yolo_dataset()
    
    # 处理COVID数据集
    covid_loader = DataLoader(Config.COVID_DATASET, 'covid')
    covid_loader.prepare_yolo_dataset()
    
    print("\n数据预处理完成！")

if __name__ == "__main__":
    # 清理并重新创建目录结构
    setup_project()
    # 移动数据集并进行预处理
    move_datasets()