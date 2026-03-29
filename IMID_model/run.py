import os
import sys

# 添加项目根目录到Python路径
project_root = os.path.dirname(os.path.abspath(__file__))
sys.path.append(project_root)

from data_preprocessing.data_loader import DataLoader
from data_preprocessing.data_preprocessor import DataPreprocessor
from configs.config import Config

def main():
    # 首先运行数据加载器
    print("开始准备数据集...")
    
    # 准备脑肿瘤数据集
    brain_loader = DataLoader(Config.BRAIN_TUMOR_DATASET, 'brain_tumor')
    brain_loader.prepare_yolo_dataset()
    print("脑肿瘤数据集准备完成")
    
    # 准备COVID数据集
    covid_loader = DataLoader(Config.COVID_DATASET, 'covid')
    covid_loader.prepare_yolo_dataset()
    print("COVID数据集准备完成")
    
    print("所有数据集准备完成！")

if __name__ == "__main__":
    main() 