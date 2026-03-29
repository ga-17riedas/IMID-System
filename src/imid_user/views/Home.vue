<template>
  <div class="home">
    <el-container>
      <el-header>
        <div class="header-content">
          <div class="logo">
            <router-link to="/">
              <img :src="require('../../imid_user/assets/logo.png')" alt="IMID" class="logo-img">
              <span>IMID 医学影像诊断系统</span>
            </router-link>
          </div>
          <div class="nav-links">
            <router-link to="/" class="nav-item">首页</router-link>
            <router-link to="/login" class="nav-item">登录</router-link>
            <router-link to="/register" class="nav-item">注册</router-link>
            <router-link to="/user-guide" class="nav-item">用户使用手册</router-link>
          </div>
        </div>
      </el-header>

      <el-main>
        <div class="welcome-section">
          <div class="welcome-content">
            <h1>欢迎使用 IMID 医学影像诊断系统</h1>
            <p class="subtitle">基于人工智能的医学影像辅助诊断平台</p>
            <div class="action-buttons">
              <el-button type="primary" size="large" class="action-button" @click="$router.push('/login')">
                <i class="el-icon-right"></i> 立即登录
              </el-button>
              <el-button size="large" class="action-button" @click="$router.push('/register')">
                <i class="el-icon-plus"></i> 注册账号
              </el-button>
            </div>
          </div>
          <div class="welcome-image">
            <img :src="require('../../imid_user/assets/medical-ai.png')" alt="AI医疗" />
          </div>
        </div>

        <div class="intro-section">
          <h2>系统简介</h2>
          <div class="divider"></div>
          <p class="intro-text">
            IMID（Intelligent Medical Image Diagnosis）是一个综合医学影像诊断平台，结合了人工智能技术和医学专业知识，旨在辅助医生进行更准确、高效的医学影像诊断。系统基于YOLOV8深度学习模型，能够自动检测和分析医学影像中的异常区域，提供初步诊断建议，大幅提高诊断效率和准确性。该系统支持多角色使用（医生、患者、管理员），提供从影像上传、AI辅助分析到诊断报告生成的全流程服务，是新一代智能医疗解决方案的典范。
          </p>
        </div>

        <div class="tech-stack-section">
          <h2>技术栈</h2>
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="tech-card">
                <h3>前端技术</h3>
                <ul>
                  <li>Vue.js - 渐进式框架</li>
                  <li>Element UI - 组件库</li>
                  <li>Vue Router - 路由管理</li>
                  <li>Axios - HTTP客户端</li>
                  <li>ECharts - 数据可视化</li>
                </ul>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="tech-card">
                <h3>后端技术</h3>
                <ul>
                  <li>Spring Boot - 应用框架</li>
                  <li>Spring Security - 安全框架</li>
                  <li>Spring Data JPA - 数据访问</li>
                  <li>JWT - 用户认证</li>
                  <li>BCrypt - 密码加密</li>
                </ul>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="tech-card">
                <h3>AI模型</h3>
                <ul>
                  <li>YOLOV8 - 目标检测</li>
                  <li>PyTorch - 深度学习</li>
                  <li>OpenCV - 图像处理</li>
                  <li>NumPy - 数据处理</li>
                  <li>Ultralytics - 模型训练</li>
                </ul>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="tech-card">
                <h3>数据存储</h3>
                <ul>
                  <li>MySQL/H2 - 关系型数据库</li>
                  <li>文件系统 - 影像存储</li>
                  <li>缓存机制 - 性能优化</li>
                  <li>数据加密 - 安全保障</li>
                  <li>数据备份 - 容灾方案</li>
                </ul>
              </div>
            </el-col>
          </el-row>
        </div>

        <div class="features-section">
          <el-row :gutter="40">
            <el-col :span="8">
              <div class="feature-card">
                <i class="el-icon-view"></i>
                <h3>AI辅助诊断</h3>
                <p>利用YOLOV8深度学习模型自动检测影像异常，提供专业分析结果</p>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="feature-card">
                <i class="el-icon-document"></i>
                <h3>报告生成管理</h3>
                <p>根据AI分析和医生诊断自动生成标准化报告，支持导出和历史查询</p>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="feature-card">
                <i class="el-icon-user"></i>
                <h3>多角色系统</h3>
                <p>针对医生、患者和管理员设计专属界面和功能，优化使用体验</p>
              </div>
            </el-col>
          </el-row>
        </div>
      </el-main>

      
    </el-container>
  </div>
</template>

<script>
export default {
  name: 'Home',
  data() {
    return {}
  },
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
}

.el-header {
  background-color: #fff;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
  position: fixed;
  width: 100%;
  z-index: 100;
  top: 0;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  padding: 0 20px;
}

.logo {
  font-size: 24px;
  font-weight: bold;
}

.logo a {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: #409EFF;
}

.logo-img {
  height: 40px;
  margin-right: 10px;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 20px;
}

.nav-item {
  color: #606266;
  text-decoration: none;
  font-size: 16px;
  padding: 8px 12px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.nav-item:hover {
  color: #409EFF;
  background-color: rgba(64, 158, 255, 0.1);
}

.el-main {
  padding-top: 80px;
  min-height: calc(100vh - 120px);
}

.welcome-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 1200px;
  margin: 0 auto;
  padding: 80px 20px;
}

.welcome-content {
  flex: 1;
  text-align: left;
  padding-right: 60px;
}

.welcome-image {
  flex: 1;
}

.welcome-image img {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  box-shadow: 0 20px 40px rgba(0,0,0,0.1);
}

.welcome-section h1 {
  font-size: 48px;
  color: #303133;
  margin-bottom: 20px;
  line-height: 1.2;
}

.subtitle {
  font-size: 24px;
  color: #606266;
  margin-bottom: 40px;
  line-height: 1.4;
}

.action-buttons {
  display: flex;
  gap: 20px;
}

.action-button {
  padding: 15px 30px;
  font-size: 16px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.action-button i {
  margin-right: 8px;
}

.intro-section {
  max-width: 1200px;
  margin: 60px auto;
  padding: 40px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.05);
}

.divider {
  width: 60px;
  height: 4px;
  background: #409EFF;
  margin: 20px auto;
  border-radius: 2px;
}

.intro-section h2 {
  font-size: 28px;
  color: #303133;
  margin-bottom: 20px;
  text-align: center;
}

.intro-text {
  color: #606266;
  line-height: 1.8;
  text-align: justify;
  padding: 0 20px;
}

.tech-stack-section {
  max-width: 1200px;
  margin: 60px auto;
  padding: 40px 20px;
}

.tech-stack-section h2 {
  font-size: 28px;
  color: #303133;
  margin-bottom: 30px;
  text-align: center;
}

.tech-card {
  background: #fff;
  padding: 30px;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.05);
  height: 100%;
  transition: transform 0.3s ease;
}

.tech-card:hover {
  transform: translateY(-5px);
}

.tech-card h3 {
  color: #409EFF;
  margin-bottom: 20px;
  font-size: 20px;
}

.tech-card ul {
  list-style: none;
  padding: 0;
}

.tech-card li {
  padding: 8px 0;
  color: #606266;
  border-bottom: 1px solid #f0f2f5;
}

.tech-card li:last-child {
  border-bottom: none;
}

.features-section {
  padding: 40px 20px;
}

.feature-card {
  text-align: center;
  padding: 40px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.05);
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-card i {
  font-size: 48px;
  color: #409EFF;
  margin-bottom: 20px;
}

.feature-card h3 {
  font-size: 20px;
  color: #303133;
  margin-bottom: 15px;
}

.feature-card p {
  color: #606266;
  line-height: 1.6;
}

.el-footer {
  text-align: center;
  padding: 20px;
  color: #909399;
  background: transparent;
}
</style> 