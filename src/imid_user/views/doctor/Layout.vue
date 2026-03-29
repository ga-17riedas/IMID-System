<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <el-menu
        :router="true"
        class="el-menu-vertical"
        :default-active="$route.path"
        background-color="#304156"
        text-color="#fff"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/doctor/home">
          <i class="el-icon-s-home"></i>
          <span>首页</span>
        </el-menu-item>
        
        <el-menu-item index="/doctor/image-analysis">
          <i class="el-icon-picture-outline"></i>
          <span>医学影像分析</span>
        </el-menu-item>

        <el-menu-item index="/doctor/patients">
          <i class="el-icon-user-solid"></i>
          <span>患者管理</span>
        </el-menu-item>

        <el-menu-item index="/doctor/schedule">
          <i class="el-icon-date"></i>
          <span>日程安排</span>
        </el-menu-item>

        <el-menu-item index="/doctor/profile">
          <i class="el-icon-setting"></i>
          <span>个人信息</span>
        </el-menu-item>

        <el-menu-item index="/doctor/reports">
          <i class="el-icon-document"></i>
          <span>报告管理</span>
        </el-menu-item>
      </el-menu>
      
      <!-- 主题切换按钮 -->
      <div class="theme-toggle" @click="switchTheme">
        <i :class="['theme-icon', currentTheme === 'dark' ? 'el-icon-sunny' : 'el-icon-moon']"></i>
        <span>{{ currentTheme === 'dark' ? '切换到白天模式' : '切换到夜间模式' }}</span>
      </div>
    </el-aside>
    
    <el-container>
      <el-header>
        <div class="header-right">
          <span>欢迎，{{ doctorName }}</span>
          <el-button type="text" @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>
      
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { removeToken, removeUser, getUser } from '@/utils/auth'
import { toggleTheme, getTheme } from '@/utils/theme'
import { getDoctorProfile } from '@/api/doctor'

export default {
  name: 'DoctorLayout',
  
  data() {
    return {
      doctorName: '',
      currentTheme: getTheme()
    }
  },

  async created() {
    try {
      // 从后端API获取医生完整信息
      const response = await getDoctorProfile()
      if (response.data) {
        this.doctorName = response.data.fullName
      }
    } catch (error) {
      console.error('获取医生信息失败:', error)
      // 如果API调用失败，回退到使用本地存储的用户信息
      const user = getUser()
      if (user) {
        this.doctorName = user.username
      }
    }
  },

  methods: {
    handleLogout() {
      removeToken()
      removeUser()
      this.$router.push('/login')
    },
    
    switchTheme() {
      this.currentTheme = toggleTheme()
    }
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.el-aside {
  background-color: #304156;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.el-menu {
  border-right: none;
  flex-grow: 1;
}

.el-header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 0 20px;
  height: 60px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}

.el-menu-item {
  display: flex;
  align-items: center;
}

.el-menu-item i {
  margin-right: 10px;
  font-size: 18px;
}

.theme-toggle {
  padding: 14px 20px;
  color: #fff;
  display: flex;
  align-items: center;
  cursor: pointer;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.theme-toggle i {
  margin-right: 10px;
  font-size: 18px;
}

.theme-toggle:hover {
  background-color: var(--menu-hover-bg);
}
</style> 