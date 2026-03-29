# 智能医疗影像辅助诊断系统（IMID）

## 📌 项目简介

本项目是一个基于深度学习的智能医疗影像辅助诊断系统（IMID），旨在帮助医生提高医学影像诊断的效率与准确性。系统采用前后端分离架构，前端使用 Vue.js + Element UI，后端使用 SpringBoot，AI 分析模块基于 YOLOv8 模型，支持多种医学影像（CT、X光等）的智能识别与异常区域检测。

## 🧩 主要功能

- 🔐 用户登录 / 注册（JWT 认证）
- 🖼️ 医学影像上传、存储、管理
- 🤖 AI 辅助诊断（基于 YOLOv8）
- 📄 诊断报告生成与导出（PDF）
- 📊 患者健康记录管理
- 📅 医生日程安排
- 🧠 模型管理（多版本支持）
- 📈 系统日志与监控

## 🛠️ 技术栈

| 模块     | 技术 |
|----------|------|
| 前端     | Vue.js, Element UI, Vue Router |
| 后端     | SpringBoot, Spring Security, JWT |
| 数据库   | MySQL |
| AI 模型  | YOLOv8, PyTorch, OpenCV |
| 部署     | Docker（可选） |

## 🚀 快速开始

### 环境要求

- JDK 11+
- Node.js 16+
- MySQL 8.0+
- Python 3.8+（用于模型推理）

### 后端启动

```bash
cd backend
mvn clean install
mvn spring-boot:run
