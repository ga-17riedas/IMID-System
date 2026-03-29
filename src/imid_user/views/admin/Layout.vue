<template>
  <el-container class="admin-layout">
    <!-- 顶部导航栏 -->
    <el-header height="60px">
      <div class="header-left">
        <div class="logo">IMID Admin</div>
        <el-tag :type="systemStatus ? 'success' : 'danger'" size="small">
          {{ systemStatus ? '系统正常' : '系统异常' }}
        </el-tag>
      </div>
      <div class="header-right">
        <span class="time">{{ currentTime }}</span>
        <el-dropdown @command="handleCommand">
          <span class="admin-info">
            {{ adminName }}
            <i class="el-icon-arrow-down"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="profile">个人信息</el-dropdown-item>
            <el-dropdown-item command="settings">系统设置</el-dropdown-item>
            <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </el-header>

    <el-container>
      <!-- 左侧导航菜单 -->
      <el-aside width="200px">
        <el-menu
          :default-active="$route.path"
          class="admin-menu"
          :collapse="isCollapse"
          background-color="#304156"
          text-color="#fff"
          active-text-color="#409EFF"
          router
        >
          <el-menu-item index="/admin/dashboard">
            <i class="el-icon-monitor"></i>
            <span slot="title">系统监控</span>
          </el-menu-item>

          <el-submenu index="/admin/users">
            <template slot="title">
              <i class="el-icon-user"></i>
              <span>用户管理</span>
            </template>
            <el-menu-item index="/admin/users/doctors">医生管理</el-menu-item>
            <el-menu-item index="/admin/users/patients">患者管理</el-menu-item>
            <el-menu-item index="/admin/users/admins">管理员管理</el-menu-item>
          </el-submenu>

          <el-menu-item index="/admin/medical-data">
            <i class="el-icon-picture"></i>
            <span slot="title">影像数据管理</span>
          </el-menu-item>

          <el-menu-item index="/admin/model">
            <i class="el-icon-cpu"></i>
            <span slot="title">模型管理</span>
          </el-menu-item>

          <el-menu-item index="/admin/analysis">
            <i class="el-icon-data-analysis"></i>
            <span slot="title">数据分析</span>
          </el-menu-item>
        </el-menu>
        
        <!-- 主题切换按钮 -->
        <div class="theme-toggle" @click="switchTheme">
          <i :class="['theme-icon', currentTheme === 'dark' ? 'el-icon-sunny' : 'el-icon-moon']"></i>
          <span>{{ currentTheme === 'dark' ? '切换到白天模式' : '切换到夜间模式' }}</span>
        </div>
      </el-aside>

      <!-- 主内容区 -->
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { toggleTheme, getTheme } from '@/utils/theme'

export default {
  name: 'AdminLayout',
  data() {
    return {
      systemStatus: true,
      currentTime: '',
      adminName: 'Admin',
      isCollapse: false,
      timer: null,
      currentTheme: getTheme()
    }
  },
  computed: {
    activeMenu() {
      return this.$route.path
    }
  },
  created() {
    this.updateTime()
    this.timer = setInterval(this.updateTime, 1000)
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  methods: {
    updateTime() {
      this.currentTime = new Date().toLocaleString()
    },
    handleCommand(command) {
      switch (command) {
        case 'profile':
          this.$router.push('/admin/profile')
          break
        case 'settings':
          this.$router.push('/admin/settings')
          break
        case 'logout':
          this.handleLogout()
          break
      }
    },
    async handleLogout() {
      try {
        // TODO: 调用登出接口
        this.$router.push('/login')
      } catch (error) {
        this.$message.error('登出失败')
      }
    },
    switchTheme() {
      this.currentTheme = toggleTheme()
    }
  }
}
</script>

<style lang="scss" scoped>
.admin-layout {
  height: 100vh;

  .el-header {
    background-color: #fff;
    box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 20px;

    .header-left {
      display: flex;
      align-items: center;
      gap: 20px;

      .logo {
        font-size: 20px;
        font-weight: bold;
        color: #304156;
      }
    }

    .header-right {
      display: flex;
      align-items: center;
      gap: 20px;

      .time {
        color: #606266;
      }

      .admin-info {
        cursor: pointer;
        color: #606266;

        &:hover {
          color: #409EFF;
        }
      }
    }
  }

  .el-aside {
    background-color: #304156;
    display: flex;
    flex-direction: column;
    
    .admin-menu {
      height: 100%;
      border-right: none;
      flex-grow: 1;

      &:not(.el-menu--collapse) {
        width: 200px;
      }
    }
  }

  .el-main {
    background-color: #f0f2f5;
    padding: 20px;
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