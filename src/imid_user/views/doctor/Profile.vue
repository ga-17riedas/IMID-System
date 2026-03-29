<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <div slot="header">
        <span>个人信息</span>
        <el-button 
          style="float: right; padding: 3px 0" 
          type="text"
          @click="handleEdit">
          {{ isEditing ? '保存' : '编辑' }}
        </el-button>
      </div>

      <el-form 
        ref="form" 
        :model="form" 
        :rules="rules"
        label-width="100px"
        :disabled="!isEditing">
        
        <!-- 基本信息部分 -->
        <h3>基本信息</h3>
        <el-form-item label="姓名" prop="fullName">
          <el-input v-model="form.fullName"></el-input>
        </el-form-item>
        
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="form.phone"></el-input>
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email"></el-input>
        </el-form-item>

        <!-- 专业信息部分 -->
        <h3>专业信息</h3>
        <el-form-item label="职称" prop="professionalTitle">
          <el-select v-model="form.professionalTitle" placeholder="请选择职称">
            <el-option label="主任医师" value="主任医师"></el-option>
            <el-option label="副主任医师" value="副主任医师"></el-option>
            <el-option label="主治医师" value="主治医师"></el-option>
            <el-option label="住院医师" value="住院医师"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="所属医院" prop="hospital">
          <el-input v-model="form.hospital"></el-input>
        </el-form-item>

        <el-form-item label="科室" prop="department">
          <el-input v-model="form.department"></el-input>
        </el-form-item>

        <el-form-item label="执业证号" prop="licenseNumber">
          <el-input v-model="form.licenseNumber"></el-input>
        </el-form-item>

        <!-- 专业特长 -->
        <el-form-item label="专业特长">
          <el-input
            type="textarea"
            :rows="4"
            placeholder="请输入专业特长"
            v-model="form.specialty">
          </el-input>
        </el-form-item>

        <!-- 按钮组 -->
        <el-form-item v-if="isEditing">
          <el-button type="primary" @click="submitForm('form')">保存</el-button>
          <el-button @click="cancelEdit">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request'  // 确保正确导入
import { getToken } from '@/utils/auth'  // 添加这行导入

export default {
  name: 'DoctorProfile',
  data() {
    return {
      isEditing: false,
      form: {
        fullName: '',
        phone: '',
        email: '',
        professionalTitle: '',
        hospital: '',
        department: '',
        licenseNumber: '',
        specialty: ''
      },
      // 表单验证规则
      rules: {
        fullName: [
          { required: true, message: '请输入姓名', trigger: 'blur' },
          { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号码', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱地址', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        professionalTitle: [
          { required: true, message: '请选择职称', trigger: 'change' }
        ],
        hospital: [
          { required: true, message: '请输入所属医院', trigger: 'blur' }
        ],
        department: [
          { required: true, message: '请输入科室', trigger: 'blur' }
        ],
        licenseNumber: [
          { required: true, message: '请输入执业证号', trigger: 'blur' }
        ]
      },
      // 保存原始数据用于取消编辑
      originalForm: null,
      profile: null,
      loading: false
    }
  },
  created() {
    this.fetchDoctorProfile()
  },
  methods: {
    async fetchDoctorProfile() {
      try {
        const response = await this.$http.get('/api/doctor/profile', {
          headers: {
            'Authorization': `Bearer ${getToken()}`  // 使用导入的 getToken
          }
        });
        if (response.data) {
          this.form = response.data;
          this.originalForm = JSON.parse(JSON.stringify(response.data));
        }
      } catch (error) {
        console.error('获取个人信息失败:', error);
        this.$message.error('获取个人信息失败: ' + (error.response?.data?.message || error.message));
      }
    },
    async updateProfile() {
      try {
        this.loading = true;
        await this.$http.put('/api/doctor/profile', this.form, {
          headers: {
            'Authorization': `Bearer ${getToken()}`  // 这里也要加上
          }
        });
        this.$message.success('个人信息更新成功');
        await this.fetchDoctorProfile();
      } catch (error) {
        console.error('更新个人信息失败:', error);
        this.$message.error(error.response?.data?.message || '更新个人信息失败');
      } finally {
        this.loading = false;
      }
    },
    // 处理编辑按钮点击
    async handleEdit() {
      if (this.isEditing) {
        await this.updateProfile();
      } else {
        this.isEditing = true;
      }
    },
    // 提交表单
    submitForm(formName) {
      this.$refs[formName].validate(async (valid) => {
        if (valid) {
          try {
            await this.updateProfile();
            this.isEditing = false;
          } catch (error) {
            console.error('保存失败:', error);
            this.$message.error('保存失败');
          }
        } else {
          return false;
        }
      });
    },
    // 取消编辑
    cancelEdit() {
      this.form = JSON.parse(JSON.stringify(this.originalForm))
      this.isEditing = false
    }
  }
}
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.profile-card {
  max-width: 800px;
  margin: 0 auto;
}

h3 {
  margin: 20px 0;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
  color: #409EFF;
}

.el-form-item {
  margin-bottom: 22px;
}

.el-select {
  width: 100%;
}
</style> 