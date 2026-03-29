<template>
  <div class="user-guide">
    <el-container>
      <el-header>
        <div class="header-content">
          <div class="logo">
            <img src="@/assets/logo.png" alt="IMID Logo">
            <span>智能医学影像诊断系统</span>
          </div>
          <div class="nav-buttons">
            <el-button type="primary" @click="$router.push('/login')">登录</el-button>
            <el-button type="success" @click="$router.push('/register')">注册</el-button>
            <el-button type="info" @click="$router.push('/')">返回首页</el-button>
          </div>
        </div>
      </el-header>
      
      <el-main>
        <h1>用户使用手册</h1>
        
        <el-tabs v-model="activeTab">
          <!-- 医生端使用手册 -->
          <el-tab-pane label="医生端使用手册" name="doctor">
            <div class="guide-section">
              <h2>1. 登录系统</h2>
              <p>使用医生账号登录系统，进入医生工作台。</p>
              
              <h2>2. 查看待处理影像</h2>
              <p>在工作台首页可以查看待处理的医学影像列表，包括：</p>
              <ul>
                <li>待分析影像</li>
                <li>待审核报告</li>
                <li>待确认诊断</li>
              </ul>
              
              <h2>3. 影像分析</h2>
              <p>点击待处理影像，进入影像分析页面：</p>
              <ul>
                <li>查看原始影像</li>
                <li>查看AI分析结果</li>
                <li>进行人工诊断</li>
                <li>生成诊断报告</li>
              </ul>
              
              <h2>4. 报告管理</h2>
              <p>在报告管理页面可以：</p>
              <ul>
                <li>查看所有诊断报告</li>
                <li>编辑报告内容</li>
                <li>确认报告</li>
                <li>导出报告</li>
              </ul>
              
              <h2>5. 患者管理</h2>
              <p>在患者管理页面可以：</p>
              <ul>
                <li>查看患者列表</li>
                <li>查看患者病历</li>
                <li>查看患者影像历史</li>
                <li>与患者沟通</li>
              </ul>
              
              <h2>6. 排班管理</h2>
              <p>在排班管理页面可以：</p>
              <ul>
                <li>设置出诊时间</li>
                <li>查看预约情况</li>
                <li>管理预约信息</li>
              </ul>
            </div>
          </el-tab-pane>
          
          <!-- 患者端使用手册 -->
          <el-tab-pane label="患者端使用手册" name="patient">
            <div class="guide-section">
              <h2>1. 登录系统</h2>
              <p>使用患者账号登录系统，进入患者个人中心。</p>
              
              <h2>2. 查看健康档案</h2>
              <p>在个人中心可以查看：</p>
              <ul>
                <li>基本信息</li>
                <li>健康记录</li>
                <li>检查报告</li>
                <li>用药记录</li>
              </ul>
              
              <h2>3. 查看诊断报告</h2>
              <p>在诊断报告页面可以：</p>
              <ul>
                <li>查看所有检查报告</li>
                <li>查看报告详情</li>
                <li>下载报告PDF</li>
                <li>分享报告给其他医生</li>
              </ul>
              
              <h2>4. 预约管理</h2>
              <p>在预约管理页面可以：</p>
              <ul>
                <li>查看可预约医生</li>
                <li>选择预约时间</li>
                <li>查看预约记录</li>
                <li>取消预约</li>
              </ul>
              
              <h2>5. 健康记录</h2>
              <p>在健康记录页面可以：</p>
              <ul>
                <li>记录日常健康数据</li>
                <li>查看健康趋势</li>
                <li>设置健康提醒</li>
                <li>导出健康报告</li>
              </ul>
              
              <h2>6. 在线咨询</h2>
              <p>在咨询页面可以：</p>
              <ul>
                <li>选择咨询医生</li>
                <li>发送文字消息</li>
                <li>上传图片资料</li>
                <li>查看咨询历史</li>
              </ul>
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-main>
    </el-container>
  </div>
</template>

<script>
export default {
  name: 'UserGuide',
  data() {
    return {
      activeTab: 'doctor'
    }
  }
}
</script>

<style scoped>
.user-guide {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
}

.logo img {
  height: 40px;
  margin-right: 10px;
}

.logo span {
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
}

.nav-buttons {
  display: flex;
  gap: 10px;
}

.el-main {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

h1 {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
}

.guide-section {
  background-color: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

h2 {
  color: #409EFF;
  margin: 20px 0 10px;
  font-size: 18px;
}

p {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 15px;
}

ul {
  list-style-type: disc;
  margin-left: 20px;
  margin-bottom: 15px;
}

li {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 5px;
}

.el-tabs {
  margin-top: 20px;
}

.el-tab-pane {
  padding: 20px 0;
}
</style>