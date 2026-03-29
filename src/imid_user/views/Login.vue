<template>
  <div class="login-container">
    <!-- 返回首页按钮 -->
    <el-button 
      type="text" 
      icon="el-icon-back" 
      class="back-home" 
      @click="goHome"
    >
      返回首页
    </el-button>
    
    <!-- 登录卡片 -->
    <el-card class="login-card">
      <div slot="header" class="card-header">
        <h2>用户登录</h2>
      </div>
      
      <!-- 登录表单 -->
      <el-form :model="loginForm" :rules="rules" ref="loginForm" label-width="80px">
        <!-- 用户类型选择 -->
        <el-form-item label="用户类型" prop="role">
          <el-select v-model="loginForm.role" placeholder="请选择登录角色" style="width: 100%">
            <el-option label="医生" value="DOCTOR"></el-option>
            <el-option label="患者" value="PATIENT"></el-option>
            <el-option label="管理员" value="ADMIN"></el-option>
          </el-select>
        </el-form-item>
        
        <!-- 用户名输入 -->
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        
        <!-- 密码输入 -->
        <el-form-item label="密码" prop="password">
          <el-input type="password" v-model="loginForm.password" placeholder="请输入密码"></el-input>
        </el-form-item>
        
        <!-- 操作按钮 -->
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading">登录</el-button>
          <el-button @click="resetForm('loginForm')">重置</el-button>
        </el-form-item>
        
        <!-- 注册链接 -->
        <div class="register-link">
          还没有账号？<router-link :to="{ path: '/register', query: { type: loginForm.role }}">立即注册</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request'
import { setToken, setUser, removeToken, removeUser, getToken, getUser } from '@/utils/auth'

/**
 * 登录组件
 * 处理用户登录逻辑，支持不同角色的登录
 */
export default {
  name: 'Login',
  data() {
    return {
      // 登录状态控制
      loading: false,
      // 登录表单数据
      loginForm: {
        role: 'DOCTOR', // 默认选择医生角色
        username: '',
        password: ''
      },
      // 表单验证规则
      rules: {
        role: [
          { required: true, message: '请选择登录角色', trigger: 'change' }
        ],
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    /**
     * 处理登录请求
     * 验证表单 -> 发送登录请求 -> 保存用户信息 -> 跳转到相应页面
     */
    async handleLogin() {
      try {
        this.$refs.loginForm.validate(async (valid) => {
          if (valid) {
            this.loading = true
            try {
              // 发送登录请求
              const response = await this.$http.post('/api/auth/login', {
                username: this.loginForm.username,
                password: this.loginForm.password,
                userType: this.loginForm.role
              })

              // 处理登录成功响应
              if (response.data && response.data.token) {
                const { token, user } = response.data
                // 保存令牌和用户信息到本地存储
                setToken(token)
                setUser(user)

                // 根据角色重定向到相应的首页
                let redirectPath = '/'
                const role = user.role

                if (role === 'ROLE_DOCTOR') {
                  redirectPath = '/doctor/home'
                } else if (role === 'ROLE_PATIENT') {
                  redirectPath = '/patient/home'
                } else if (role === 'ROLE_ADMIN') {
                  redirectPath = '/admin/dashboard'
                }

                // 使用 replace 而不是 push 来避免导航守卫的重定向问题
                await this.$router.replace(redirectPath)
              }
            } catch (error) {
              console.error('Login error:', error)
              this.$message.error(error.response?.data?.message || '登录失败，请重试')
            } finally {
              this.loading = false
            }
          }
        })
      } catch (error) {
        console.error('Form validation error:', error)
        this.loading = false
      }
    },
    
    /**
     * 重置登录表单
     * @param {string} formName - 表单引用名称
     */
    resetForm(formName) {
      this.$refs[formName].resetFields()
    },
    
    /**
     * 返回首页
     * 处理路由导航，避免重复导航错误
     */
    goHome() {
      this.$router.push('/').catch(err => {
        if (err.name !== 'NavigationDuplicated') {
          throw err
        }
      })
    }
  },
  created() {
    // 从路由查询参数获取用户类型
    const type = this.$route.query.type
    if (type) {
      this.loginForm.role = type
    }
  }
}
</script>

<style scoped>
/* 登录容器样式 */
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: url('../assets/medical-ai.png') no-repeat center center;
  background-size: cover;
  position: relative;
}

/* 登录卡片样式 */
.login-card {
  width: 400px;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 卡片标题样式 */
.card-header {
  text-align: center;
}

.card-header h2 {
  margin: 0;
  color: #3c8dbc;
  font-weight: 500;
}

/* 注册链接样式 */
.register-link {
  text-align: center;
  margin-top: 20px;
}

.register-link a {
  color: #409EFF;
  text-decoration: none;
  font-weight: bold;
}

/* 下拉框样式 */
.el-select {
  width: 100%;
}

/* 返回首页按钮样式 */
.back-home {
  position: absolute;
  top: 20px;
  left: 20px;
  font-size: 16px;
  background-color: rgba(255, 255, 255, 0.8);
  padding: 8px 12px;
  border-radius: 4px;
}
</style> 