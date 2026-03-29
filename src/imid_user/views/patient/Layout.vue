<template>
  <el-container class="app-container">
    <el-aside width="200px">
      <div class="logo">IMID System</div>
      <el-menu
        :default-active="$route.path"
        class="el-menu-vertical"
        background-color="#304156"
        text-color="#fff"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item index="/patient/home">
          <i class="el-icon-s-home"></i>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/patient/reports">
          <i class="el-icon-document"></i>
          <span>诊断记录</span>
        </el-menu-item>
        <el-menu-item index="/patient/health">
          <i class="el-icon-s-data"></i>
          <span>健康管理</span>
        </el-menu-item>
        <el-menu-item index="/patient/profile">
          <i class="el-icon-user"></i>
          <span>个人信息</span>
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
          <span>欢迎，{{ fullName }}</span>
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
import { removeToken, removeUser } from '@/utils/auth'
import { toggleTheme, getTheme } from '@/utils/theme'

export default {
  name: 'PatientLayout',
  data() {
    return {
      fullName: '',
      currentTheme: getTheme()
    }
  },
  created() {
    this.fetchUserInfo()
  },
  methods: {
    async fetchUserInfo() {
      try {
        const response = await this.$http.get('/api/patient/profile')
        this.fullName = response.data.fullName
      } catch (error) {
        console.error('获取用户信息失败:', error)
      }
    },
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

<style lang="scss" scoped>
.app-container {
  height: 100vh;
}

.el-aside {
  background-color: #304156;
  color: #fff;
  display: flex;
  flex-direction: column;

  .logo {
    height: 60px;
    line-height: 60px;
    text-align: center;
    font-size: 20px;
    font-weight: bold;
    color: #fff;
  }

  .el-menu {
    border-right: none;
    flex-grow: 1;
  }
}

.el-header {
  background-color: #fff;
  color: #333;
  line-height: 60px;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  justify-content: flex-end;
  padding: 0 20px;

  .header-right {
    display: flex;
    align-items: center;
    gap: 20px;

    span {
      color: #606266;
    }

    .el-button {
      color: #409EFF;
      
      &:hover {
        color: #66b1ff;
      }
    }
  }
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}

.el-menu-item {
  i {
    margin-right: 10px;
    font-size: 18px;
  }
}

.theme-toggle {
  padding: 14px 20px;
  color: #fff;
  display: flex;
  align-items: center;
  cursor: pointer;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  
  i {
    margin-right: 10px;
    font-size: 18px;
  }
  
  &:hover {
    background-color: var(--menu-hover-bg);
  }
}
</style> 