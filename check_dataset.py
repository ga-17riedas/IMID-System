import os
from pathlib import Path
from collections import defaultdict

def count_images(path):
    """统计给定路径下的图片文件数量"""
    image_extensions = {'.jpg', '.jpeg', '.png', '.bmp', '.tif', '.tiff'}
    count = 0
    for file in Path(path).rglob('*'):
        if file.suffix.lower() in image_extensions:
            count += 1
    return count

def check_dataset_structure(base_path="D:/IMID/model_images"):
    base_dir = Path(base_path)
    if not base_dir.exists():
        print(f"目录不存在: {base_dir}")
        return
    
    print(f"\n检查数据集目录结构: {base_dir}\n")
    print("=" * 80)
    
    # 遍历所有目录
    for root, dirs, files in os.walk(base_dir):
        current = Path(root)
        level = len(current.relative_to(base_dir).parts)
        indent = "  " * level
        
        # 打印当前目录
        rel_path = current.relative_to(base_dir)
        if level > 0:  # 不打印根目录
            print(f"{indent}📁 {rel_path}")
            
            # 统计当前目录下的图片数量
            img_count = count_images(current)
            if img_count > 0:
                print(f"{indent}   └── 包含 {img_count} 张图片")
        
        # 如果是空目录，显示提示
        if not dirs and not files:
            print(f"{indent}   └── (空目录)")
    
    print("\n" + "=" * 80)
    
    # 打印总体统计
    print("\n总体统计:")
    print("-" * 40)
    
    # 统计主要数据集目录
    for dataset in ['brain_tumor', 'covid']:
        dataset_path = base_dir / dataset
        if dataset_path.exists():
            total_images = count_images(dataset_path)
            print(f"\n{dataset} 数据集:")
            print(f"总图片数量: {total_images}")
            
            # 检查YOLO格式目录结构
            for split in ['train', 'val', 'test']:
                img_dir = dataset_path / 'images' / split
                label_dir = dataset_path / 'labels' / split
                
                if img_dir.exists():
                    img_count = count_images(img_dir)
                    print(f"- images/{split}: {img_count} 张图片")
                
                if label_dir.exists():
                    label_count = len(list(label_dir.glob('*.txt')))
                    print(f"- labels/{split}: {label_count} 个标签文件")

if __name__ == "__main__":
    check_dataset_structure() 