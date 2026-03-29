import os
import sys
from ultralytics import YOLO
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from configs.config import Config

"""
def train_brain_tumor_model():
    # 训练脑肿瘤检测模型
    try:
        # 检查预训练模型是否存在
        pretrained_model_path = os.path.join(Config.MODEL_SAVE_DIR, 'yolov8n.pt')
        
        if not os.path.exists(pretrained_model_path):
            print(f"预训练模型不存在: {pretrained_model_path}")
            print("请从以下地址手动下载预训练模型：")
            print("https://github.com/ultralytics/assets/releases/download/v8.3.0/yolov8n.pt")
            print(f"并将其放置在: {pretrained_model_path}")
            return
        
        # 初始化YOLOv8模型
        model = YOLO(pretrained_model_path)
        
        # 检查数据集配置文件
        data_yaml_path = os.path.join(Config.BRAIN_TUMOR_DATASET, 'data.yaml')
        if not os.path.exists(data_yaml_path):
            print(f"数据集配置文件不存在: {data_yaml_path}")
            create_data_yaml(Config.BRAIN_TUMOR_DATASET, Config.BRAIN_TUMOR_CLASSES)
        
        # 训练脑肿瘤检测模型
        try:
            results = model.train(
                data=data_yaml_path,
                epochs=Config.EPOCHS,
                imgsz=Config.IMG_SIZE,
                batch=Config.BATCH_SIZE,
                name='brain_tumor_model'
            )
        except Exception as e:
            print(f"训练过程中出现错误: {str(e)}")
            print("但模型可能已经成功保存")
        
        # 保存模型
        try:
            model.save(os.path.join(Config.MODEL_SAVE_DIR, 'brain_tumor_model.pt'))
            print("模型已成功保存")
        except Exception as e:
            print(f"保存模型时出现错误: {str(e)}")
            # 尝试直接复制最后一个检查点
            last_checkpoint = os.path.join('runs', 'detect', 'brain_tumor_model', 'weights', 'best.pt')
            if os.path.exists(last_checkpoint):
                import shutil
                shutil.copy2(last_checkpoint, os.path.join(Config.MODEL_SAVE_DIR, 'brain_tumor_model.pt'))
                print("已复制最佳模型检查点作为最终模型")
            
    except Exception as e:
        print(f"训练过程中出现未处理的错误: {str(e)}")
"""

def train_covid_model():
    # 检查预训练模型是否存在
    pretrained_model_path = os.path.join(Config.MODEL_SAVE_DIR, 'yolov8n.pt')
    
    if not os.path.exists(pretrained_model_path):
        print("请先下载预训练模型")
        return
    
    # 初始化YOLOv8模型
    model = YOLO(pretrained_model_path)
    
    # 检查数据集配置文件
    data_yaml_path = os.path.join(Config.COVID_DATASET, 'data.yaml')
    if not os.path.exists(data_yaml_path):
        print(f"数据集配置文件不存在: {data_yaml_path}")
        create_covid_data_yaml()
    
    # 训练COVID-19检测模型
    results = model.train(
        data=data_yaml_path,
        epochs=Config.EPOCHS,
        imgsz=Config.IMG_SIZE,
        batch=Config.BATCH_SIZE,
        name='covid_model'
    )
    
    # 保存模型
    model.save(os.path.join(Config.MODEL_SAVE_DIR, 'covid_model.pt'))

def create_data_yaml(dataset_path, classes):
    """创建YOLO格式的数据集配置文件"""
    yaml_content = f"""
# Train/val/test sets as 1) dir: path/to/imgs, 2) file: path/to/imgs.txt, or 3) list: [path/to/imgs1, path/to/imgs2, ..]
path: {dataset_path}  # dataset root dir
train: images/train  # train images (relative to 'path')
val: images/val  # val images (relative to 'path')
test: images/test  # test images (relative to 'path')

# Classes
names:
  {dict(enumerate(classes))}  # class names
"""
    
    yaml_path = os.path.join(dataset_path, 'data.yaml')
    with open(yaml_path, 'w', encoding='utf-8') as f:
        f.write(yaml_content)
    print(f"创建数据集配置文件: {yaml_path}")

def create_covid_data_yaml():
    yaml_content = f"""
path: {Config.COVID_DATASET}  # dataset root dir
train: images/train  # train images
val: images/val  # val images
test: images/test  # test images

# Classes
names:
  0: COVID
  1: NonCOVID
"""
    
    yaml_path = os.path.join(Config.COVID_DATASET, 'data.yaml')
    with open(yaml_path, 'w', encoding='utf-8') as f:
        f.write(yaml_content)
    print(f"创建COVID数据集配置文件: {yaml_path}")

if __name__ == '__main__':
    # 创建模型保存目录
    os.makedirs(Config.MODEL_SAVE_DIR, exist_ok=True)
    
    # 只训练COVID模型
    print("开始训练COVID-19检测模型...")
    train_covid_model()