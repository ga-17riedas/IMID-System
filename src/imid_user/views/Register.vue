<template>
  <div class="register-container">
    <el-button 
      type="text" 
      icon="el-icon-back" 
      class="back-home" 
      @click="goHome"
    >
      返回首页
    </el-button>
    
    <el-card class="register-card">
      <div slot="header" class="card-header">
        <h2>用户注册</h2>
      </div>
      
      <el-form :model="registerForm" :rules="rules" ref="registerForm" label-width="100px">
        <el-form-item label="用户类型" prop="userType">
          <el-select v-model="registerForm.userType" placeholder="请选择用户类型" @change="handleTypeChange">
            <el-option label="医生" value="DOCTOR"></el-option>
            <el-option label="患者" value="PATIENT"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input type="password" v-model="registerForm.password" placeholder="请输入密码"></el-input>
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input type="password" v-model="registerForm.confirmPassword" placeholder="请再次输入密码"></el-input>
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="registerForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="registerForm.phone" placeholder="请输入手机号码"></el-input>
        </el-form-item>
        
        <el-form-item label="真实姓名" prop="fullName">
          <el-input v-model="registerForm.fullName" placeholder="请输入真实姓名"></el-input>
        </el-form-item>

        <!-- 医生特有字段 -->
        <template v-if="registerForm.userType === 'DOCTOR'">
          <el-form-item label="职称" prop="professionalTitle">
            <el-input v-model="registerForm.professionalTitle" placeholder="请输入职称"></el-input>
          </el-form-item>
          
          <el-form-item label="所属医院" prop="hospital">
            <el-input v-model="registerForm.hospital" placeholder="请输入所属医院"></el-input>
          </el-form-item>
          
          <el-form-item label="科室" prop="department">
            <el-input v-model="registerForm.department" placeholder="请输入科室"></el-input>
          </el-form-item>
          
          <el-form-item label="执业证号" prop="licenseNumber">
            <el-input v-model="registerForm.licenseNumber" placeholder="请输入执业证号"></el-input>
          </el-form-item>
        </template>

        <!-- 患者特有字段 -->
        <template v-if="registerForm.userType === 'PATIENT'">
          <el-form-item label="年龄" prop="age">
            <el-input-number v-model="registerForm.age" :min="0" :max="150"></el-input-number>
          </el-form-item>
          
          <el-form-item label="性别" prop="gender">
            <el-radio-group v-model="registerForm.gender">
              <el-radio label="male">男</el-radio>
              <el-radio label="female">女</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item label="紧急联系人" prop="emergencyContact">
            <el-input v-model="registerForm.emergencyContact" placeholder="请输入紧急联系人"></el-input>
          </el-form-item>
          
          <el-form-item label="紧急电话" prop="emergencyPhone">
            <el-input v-model="registerForm.emergencyPhone" placeholder="请输入紧急联系电话"></el-input>
          </el-form-item>
        </template>

        <el-form-item>
          <el-button type="primary" @click="submitForm('registerForm')" :loading="loading">注册</el-button>
          <el-button @click="resetForm('registerForm')">重置</el-button>
        </el-form-item>
        
        <div class="login-link">
          已有账号？<router-link :to="{ path: '/login', query: { type: registerForm.userType }}">立即登录</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'Register',
  data() {
    // 自定义验证规则
    const validatePass2 = (rule, value, callback) => {
      if (value !== this.registerForm.password) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }
    
    return {
      loading: false,
      registerForm: {
        userType: '',
        username: '',
        password: '',
        confirmPassword: '',
        email: '',
        phone: '',
        fullName: '',
        // 医生字段
        professionalTitle: '',
        hospital: '',
        department: '',
        licenseNumber: '',
        // 患者字段
        age: null,
        gender: '',
        emergencyContact: '',
        emergencyPhone: ''
      },
      rules: {
        userType: [
          { required: true, message: '请选择用户类型', trigger: 'change' }
        ],
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入密码', trigger: 'blur' },
          { validator: validatePass2, trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱地址', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号码', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ],
        fullName: [
          { required: true, message: '请输入真实姓名', trigger: 'blur' }
        ],
        emergencyPhone: [
          { pattern: /^(|1[3-9]\d{9})$/, message: '紧急联系人电话格式不正确（请留空或输入正确手机号）', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    handleTypeChange(value) {
      // 切换用户类型时重置表单
      this.$refs.registerForm.resetFields()
      this.registerForm.userType = value
    },
    async submitForm(formName) {
      this.$refs[formName].validate(async (valid) => {
        if (valid) {
          this.loading = true
          try {
            // 构建注册请求数据
            const registerData = {
              username: this.registerForm.username,
              password: this.registerForm.password,
              confirmPassword: this.registerForm.confirmPassword,
              email: this.registerForm.email,
              phone: this.registerForm.phone,
              fullName: this.registerForm.fullName,
              role: this.registerForm.userType,
              
              // 医生特有信息
              professionalTitle: this.registerForm.userType === 'DOCTOR' ? this.registerForm.professionalTitle : null,
              hospital: this.registerForm.userType === 'DOCTOR' ? this.registerForm.hospital : null,
              department: this.registerForm.userType === 'DOCTOR' ? this.registerForm.department : null,
              licenseNumber: this.registerForm.userType === 'DOCTOR' ? this.registerForm.licenseNumber : null,
              
              // 患者特有信息
              age: this.registerForm.userType === 'PATIENT' ? Number(this.registerForm.age) : null,
              gender: this.registerForm.userType === 'PATIENT' ? this.registerForm.gender : null,
              emergencyContact: this.registerForm.userType === 'PATIENT' ? this.registerForm.emergencyContact || '' : '',
              emergencyPhone: this.registerForm.userType === 'PATIENT' ? this.registerForm.emergencyPhone || '' : ''
            }

            console.log('Registration request data:', registerData)

            const response = await this.$http.post('/api/auth/register', registerData)
            console.log('Registration response:', response)

            this.$message.success('注册成功')
            this.$router.push({
              path: '/login',
              query: { type: this.registerForm.userType }
            })
          } catch (error) {
            console.error('Registration error details:', {
              status: error.response?.status,
              data: error.response?.data,
              message: error.response?.data?.message,
              error: error.response?.data?.error,
              url: error.config?.url,
              requestData: JSON.parse(error.config?.data || '{}')
            })
            const errorMsg = error.response?.data?.message || 
                            error.response?.data?.error || 
                            '注册失败，请重试'
            this.$message.error(errorMsg)
          } finally {
            this.loading = false
          }
        }
      })
    },
    resetForm(formName) {
      this.$refs[formName].resetFields()
    },
    goHome() {
      this.$router.push('/').catch(err => {
        if (err.name !== 'NavigationDuplicated') {
          throw err
        }
      })
    }
  },
  created() {
    // 从URL获取用户类型
    const type = this.$route.query.type
    if (type) {
      this.registerForm.userType = type
    }
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 30px 0;
  background: url('../assets/medical-ai.png') no-repeat center center;
  background-size: cover;
  position: relative;
}

.register-card {
  width: 500px;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  margin-bottom: 20px;
}

.card-header {
  text-align: center;
}

.card-header h2 {
  margin: 0;
  color: #3c8dbc;
  font-weight: 500;
}

.login-link {
  text-align: center;
  margin-top: 20px;
}

.login-link a {
  color: #409EFF;
  text-decoration: none;
  font-weight: bold;
}

.back-home {
  position: absolute;
  top: 20px;
  left: 20px;
  font-size: 16px;
  background-color: rgba(255, 255, 255, 0.8);
  padding: 8px 12px;
  border-radius: 4px;
  z-index: 10;
}

.el-select {
  width: 100%;
}

.el-input-number {
  width: 100%;
}
</style> 