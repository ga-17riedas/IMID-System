import torch
import json
import sys
import os
from torch.utils.mobile_optimizer import optimize_for_mobile
from torch.serialization import add_safe_globals

def format_percentage(value):
    """将数值格式化为百分比字符串"""
    if value is None or not isinstance(value, (int, float)) or torch.isnan(torch.tensor(value)):
        return "-"  # 改为返回"-"而不是"0.00%"
    return f"{value * 100:.2f}%"

def analyze_model(model_path):
    try:
        # 添加DetectionModel到安全全局变量列表
        from ultralytics.nn.tasks import DetectionModel
        add_safe_globals([DetectionModel])
        
        # 加载模型，设置weights_only=False因为我们信任这个模型
        model_data = torch.load(model_path, map_location='cpu', weights_only=False)
        
        # 获取模型基本信息
        result = {
            'modelSize': os.path.getsize(model_path),  # 保持原始字节大小
            'framework': 'PyTorch',
            'modelType': 'Unknown',
            'modelSizeFormatted': None  # 添加格式化的大小作为单独的字段
        }

        # 格式化显示大小
        size_bytes = result['modelSize']
        if size_bytes < 1024:
            result['modelSizeFormatted'] = f"{size_bytes} B"
        elif size_bytes < 1024 * 1024:
            result['modelSizeFormatted'] = f"{size_bytes/1024:.2f} KB"
        else:
            result['modelSizeFormatted'] = f"{size_bytes/(1024*1024):.2f} MB"

        # 检查加载的对象类型并相应处理
        if isinstance(model_data, dict):
            # 如果是字典，尝试从字典中获取信息
            try:
                param_count = sum(p.numel() for p in model_data['model'].parameters()) if 'model' in model_data else 0
            except:
                param_count = 0
                
            # 尝试从字典中获取性能指标
            metrics = model_data.get('metrics', {})
            if not isinstance(metrics, dict):
                metrics = {}
                
            result.update({
                'parameterCount': param_count,
                'modelType': model_data.get('model_type', type(model_data.get('model', '')).__name__),
                'accuracy': format_percentage(metrics.get('accuracy')),
                'precision': format_percentage(metrics.get('precision')),
                'recall': format_percentage(metrics.get('recall')),
                'f1Score': format_percentage(metrics.get('f1_score'))
            })
            
            # 获取类别信息
            if 'classes' in model_data:
                result['classes'] = model_data['classes']
            elif 'names' in model_data:
                result['classes'] = model_data['names']
                
        else:
            # 如果是模型对象，使用原来的逻辑
            try:
                param_count = sum(p.numel() for p in model_data.parameters())
            except:
                param_count = 0
                
            result.update({
                'parameterCount': param_count,
                'modelType': model_data.__class__.__name__,
                'accuracy': "-",
                'precision': "-",
                'recall': "-",
                'f1Score': "-"
            })
            
            # 获取模型性能指标
            if hasattr(model_data, 'metrics'):
                metrics = model_data.metrics
                if isinstance(metrics, dict):
                    result.update({
                        'accuracy': format_percentage(metrics.get('accuracy')),
                        'precision': format_percentage(metrics.get('precision')),
                        'recall': format_percentage(metrics.get('recall')),
                        'f1Score': format_percentage(metrics.get('f1_score'))
                    })
            
            # 获取类别信息
            if hasattr(model_data, 'names'):
                result['classes'] = model_data.names
        
        # 输出JSON结果
        print(json.dumps(result))
        return 0
        
    except Exception as e:
        print(f"Error analyzing model: {str(e)}", file=sys.stderr)
        return 1

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python analyze_model.py <model_path>", file=sys.stderr)
        sys.exit(1)
    
    sys.exit(analyze_model(sys.argv[1])) 