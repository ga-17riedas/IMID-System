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
        results = model(image_path)
        result = results[0]

        # 构建结果
        detections = {
            "detected": len(result.boxes) > 0,
            "confidence": float(result.boxes[0].conf[0]) if len(result.boxes) > 0 else 0.0,
            "suggestion": "检测到异常，建议进一步检查" if len(result.boxes) > 0 else "未检测到明显异常"
        }

        print("2213")
        print(result)
        print("3321")

        # 确保输出是有效的JSON，并立即刷新
        print(json.dumps(detections), flush=True)
        sys.stdout.flush()  # 额外确保输出被刷新
        return 0

    except Exception as e:
        print(f"Error occurred: {str(e)}", file=sys.stderr)
        print(json.dumps({
            "error": str(e),
            "traceback": traceback.format_exc()
        }), file=sys.stderr)
        return 1


if __name__ == "__main__":
    try:
        # parser = argparse.ArgumentParser()
        # parser.add_argument("--model", required=True)
        # parser.add_argument("--image", required=True)
        # parser.add_argument("--type", required=True)
        # args = parser.parse_args()
        #
        # sys.exit(analyze_image(args.model, args.image, args.type))
        model = "D:/IMID/IMID_model/saved_models/brain_tumor_model.pt"
        image = "D:\IMID_images\model_images/brain_tumor\Training\glioma\Tr-gl_0010.jpg"
        type = "BRAIN_TUMOR"
        analyze_image(model, image, type)
    except Exception as e:
        print(f"Script error: {str(e)}", file=sys.stderr)
        sys.exit(1)
