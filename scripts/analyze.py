import argparse
import torch
import cv2
import json
import sys
import traceback
import os
from ultralytics import YOLO


def analyze_image(model_path, image_path, analysis_type):
    try:
        print(f"Loading model from {model_path}...", file=sys.stderr)

        print(model_path)
        print(image_path)
        print(analysis_type)

        # 确保文件存在
        if not os.path.exists(model_path):
            raise FileNotFoundError(f"Model file not found: {model_path}")
        if not os.path.exists(image_path):
            raise FileNotFoundError(f"Image file not found: {image_path}")

        # 加载模型
        model = YOLO(model_path)
        print(f"Processing image {image_path}...", file=sys.stderr)

        # 进行推理
        results = model(image_path)
        result = results[0]

        # 设置阈值
        threshold = 0.5
        abnormal_found = False
        top_conf = 0.0
        top_class = "unknown"
        boxes_list = []

        # 读取原始图像以绘制边界框
        original_image = cv2.imread(image_path)
        if original_image is None:
            raise ValueError(f"Failed to read image: {image_path}")

        # 获取图像尺寸
        img_height, img_width = original_image.shape[:2]

        # 处理检测结果
        for box in result.boxes:
            conf = float(box.conf.item())
            cls_id = int(box.cls.item())
            cls_name = result.names.get(cls_id, "unknown")

            if conf >= threshold:
                # 如果不是 notumor，就认为是异常
                if cls_name != "notumor":
                    abnormal_found = True

                # 记录最高置信度的结果
                if conf > top_conf:
                    top_conf = conf
                    top_class = cls_name

                # 提取坐标
                xyxy = box.xyxy[0].tolist()
                x1, y1, x2, y2 = xyxy

                # 保存检测框信息
                boxes_list.append({
                    "x1": round(x1, 2),
                    "y1": round(y1, 2),
                    "x2": round(x2, 2),
                    "y2": round(y2, 2),
                    "confidence": round(conf, 4),
                    "label": cls_name
                })

                # 在图像上绘制边界框
                cv2.rectangle(
                    original_image, 
                    (int(x1), int(y1)), 
                    (int(x2), int(y2)), 
                    get_color_for_label(cls_name), 
                    2
                )

                # 添加标签文本
                label = f"{cls_name}: {conf:.2f}"
                text_size = cv2.getTextSize(label, cv2.FONT_HERSHEY_SIMPLEX, 0.5, 2)[0]
                cv2.rectangle(
                    original_image,
                    (int(x1), int(y1 - text_size[1] - 5)),
                    (int(x1 + text_size[0]), int(y1)),
                    get_color_for_label(cls_name),
                    -1
                )
                cv2.putText(
                    original_image,
                    label,
                    (int(x1), int(y1 - 5)),
                    cv2.FONT_HERSHEY_SIMPLEX,
                    0.5,
                    (255, 255, 255),
                    1
                )

        # 生成带标注框的图像文件名
        base_path, ext = os.path.splitext(image_path)
        annotated_image_path = f"{base_path}_annotated{ext}"
        
        # 保存带标注框的图像
        cv2.imwrite(annotated_image_path, original_image)
        print(f"Saved annotated image to {annotated_image_path}", file=sys.stderr)

        # 获取相对路径作为URL
        # 假设图像都存储在uploads目录下
        if "uploads" in annotated_image_path:
            # 修复URL路径，确保不含双斜杠
            path_parts = annotated_image_path.split("uploads")
            if len(path_parts) > 1:
                relative_path = path_parts[1].replace("\\", "/")
                if relative_path.startswith("/"):
                    relative_path = relative_path[1:]  # 去掉开头的斜杠
                annotated_image_url = "/uploads/" + relative_path
            else:
                annotated_image_url = annotated_image_path
        else:
            annotated_image_url = annotated_image_path

        # 构建返回
        detections = {
            "detected": abnormal_found,
            "top_class": top_class,
            "top_confidence": round(top_conf, 4),
            "suggestion": "检测到异常，建议进一步检查" if abnormal_found else "未检测到明显异常",
            "boxes": boxes_list,
            "annotated_image_url": annotated_image_url
        }

        print(json.dumps(detections, ensure_ascii=False), flush=True)
        return 0

    except Exception as e:
        print(f"Error occurred: {str(e)}", file=sys.stderr)
        print(json.dumps({
            "error": str(e),
            "traceback": traceback.format_exc()
        }, ensure_ascii=False), file=sys.stderr)
        return 1


def get_color_for_label(label):
    """根据标签返回颜色"""
    color_map = {
        'glioma': (0, 0, 255),     # 红色 (BGR)
        'meningioma': (0, 255, 0),  # 绿色
        'pituitary': (255, 0, 0),   # 蓝色
        'covid': (255, 0, 255),     # 紫色
        'notumor': (128, 128, 128)  # 灰色
    }
    
    label = label.lower()
    return color_map.get(label, (0, 0, 255))  # 默认红色


if __name__ == "__main__":
    try:
        parser = argparse.ArgumentParser()
        parser.add_argument("--model", required=True)
        parser.add_argument("--image", required=True)
        parser.add_argument("--type", required=True)
        args = parser.parse_args()
        sys.stdout.reconfigure(encoding='utf-8')
        sys.exit(analyze_image(args.model, args.image, args.type))
        # model = "D:/IMID/IMID_model/saved_models/brain_tumor_model.pt"
        # image0 = "D:\IMID_images\model_images/brain_tumor\Training\glioma\Tr-gl_0010.jpg"
        # image1 = "D:\IMID_images\model_images/brain_tumor\Training/notumor\Tr-no_0010.jpg"
        # type = "BRAIN_TUMOR"
        # analyze_image(model, image0, type)
    except Exception as e:
        print(f"Script error: {str(e)}", file=sys.stderr)
        sys.exit(1)
