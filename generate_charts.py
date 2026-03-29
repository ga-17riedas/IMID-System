import matplotlib.pyplot as plt
import numpy as np

def create_performance_chart():
    # 数据准备
    users = [50, 100, 200]
    avg_response = [0.5, 0.8, 1.2]
    p95_response = [0.8, 1.2, 1.8]
    max_response = [1.2, 1.8, 2.5]

    plt.figure(figsize=(10, 6))
    plt.plot(users, avg_response, 'b-', label='平均响应时间', marker='o')
    plt.plot(users, p95_response, 'r-', label='95%响应时间', marker='s')
    plt.plot(users, max_response, 'g-', label='最大响应时间', marker='^')

    # 添加警戒线
    plt.axhline(y=1.5, color='r', linestyle='--', label='响应时间警戒线(1.5s)')

    plt.xlabel('并发用户数')
    plt.ylabel('响应时间(秒)')
    plt.title('系统性能测试结果')
    plt.grid(True)
    plt.legend()

    # 保存图表
    plt.savefig('performance_test_results.png', dpi=300, bbox_inches='tight')
    plt.close()

def create_security_chart():
    # 数据准备
    categories = ['访问控制', '身份认证', '数据传输', '注入防护', 'XSS防护']
    scores = [100, 95, 98, 97, 96]

    # 将数据转换为雷达图格式
    angles = np.linspace(0, 2*np.pi, len(categories), endpoint=False)
    scores = np.concatenate((scores, [scores[0]]))  # 闭合图形
    angles = np.concatenate((angles, [angles[0]]))  # 闭合图形

    # 创建图表
    fig, ax = plt.subplots(figsize=(8, 8), subplot_kw=dict(projection='polar'))
    
    # 绘制雷达图
    ax.plot(angles, scores, 'o-', linewidth=2, label='安全得分')
    ax.fill(angles, scores, alpha=0.25)

    # 设置角度刻度
    ax.set_xticks(angles[:-1])
    ax.set_xticklabels(categories)

    # 设置径向刻度
    ax.set_ylim(0, 100)
    ax.set_yticks([20, 40, 60, 80, 100])
    
    # 添加网格和标题
    ax.grid(True)
    plt.title('系统安全性测试结果')

    # 添加图例
    plt.legend(loc='upper right', bbox_to_anchor=(0.1, 0.1))

    # 保存图表
    plt.savefig('security_test_results.png', dpi=300, bbox_inches='tight')
    plt.close()

def main():
    print("正在生成性能测试结果图...")
    create_performance_chart()
    print("性能测试结果图已保存为 'performance_test_results.png'")

    print("正在生成安全性测试结果图...")
    create_security_chart()
    print("安全性测试结果图已保存为 'security_test_results.png'")

if __name__ == "__main__":
    main() 