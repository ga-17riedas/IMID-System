'''移动covid数据集'''
from pathlib import Path
import shutil
from configs.config import Config
from data_preprocessing.data_loader import DataLoader

def process_covid_dataset():
    # 清理现有的covid目录
    covid_dir = Config.COVID_DATASET
    if covid_dir.exists():
        print(f"清理现有的covid目录: {covid_dir}")
        shutil.rmtree(covid_dir)
    
    # 创建必要的目录
    covid_dir.mkdir(parents=True, exist_ok=True)
    
    # 源数据集路径
    src_dir = Config.BASE_DIR / "model_images" / "COVID-19 胸部 CT 图像增强 GAN 数据集" / "COVID-19" / "COVID-19"
    
    print(f"从 {src_dir} 复制数据...")
    
    # 复制数据
    for split in ['train', 'val', 'test']:
        for class_name in Config.COVID_CLASSES:
            src = src_dir / split / class_name
            dst = covid_dir / "COVID-19" / split / class_name
            if src.exists():
                print(f"复制 {src} -> {dst}")
                dst.parent.mkdir(parents=True, exist_ok=True)
                shutil.copytree(src, dst)
    
    print("\n运行数据预处理...")
    
    # 运行数据预处理
    covid_loader = DataLoader(covid_dir, 'covid')
    covid_loader.prepare_yolo_dataset()
    
    print("\n处理完成！")

if __name__ == "__main__":
    process_covid_dataset() 