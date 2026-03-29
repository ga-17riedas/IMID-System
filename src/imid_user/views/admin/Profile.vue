<template>
  <div class="admin-profile">
    <el-card shadow="hover" class="profile-card">
      <div slot="header" class="card-header">
        <span>个人信息</span>
        <el-button 
          size="mini" 
          type="primary" 
          icon="el-icon-edit"
          @click="editMode = !editMode">
          {{ editMode ? '取消编辑' : '编辑信息' }}
        </el-button>
      </div>
      
      <el-form 
        :model="userProfile" 
        :rules="rules" 
        ref="profileForm" 
        label-width="100px"
        :disabled="!editMode">
        
        <div class="avatar-container">
          <el-avatar :size="100" :src="userProfile.avatar || defaultAvatar"></el-avatar>
          <div v-if="editMode" class="avatar-upload">
            <el-button type="text" @click="triggerUpload">更换头像</el-button>
            <input 
              ref="fileInput" 
              type="file" 
              accept="image/*" 
              style="display: none" 
              @change="handleAvatarChange" 
            />
          </div>
        </div>
        
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userProfile.username"></el-input>
        </el-form-item>
        
        <el-form-item label="真实姓名" prop="fullName">
          <el-input v-model="userProfile.fullName"></el-input>
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userProfile.email"></el-input>
        </el-form-item>
        
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="userProfile.phone"></el-input>
        </el-form-item>
        
        <el-form-item label="角色">
          <el-tag type="success">管理员</el-tag>
        </el-form-item>
        
        <el-form-item label="上次登录">
          <span>{{ userProfile.lastLogin }}</span>
        </el-form-item>
        
        <el-divider></el-divider>
        
        <el-form-item label="修改密码" v-if="editMode">
          <el-button type="text" @click="showPasswordDialog = true">修改密码</el-button>
        </el-form-item>
        
        <el-form-item v-if="editMode">
          <el-button type="primary" @click="saveProfile">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 修改密码对话框 -->
    <el-dialog
      title="修改密码"
      :visible.sync="showPasswordDialog"
      width="500px">
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordForm" label-width="100px">
        <el-form-item label="当前密码" prop="currentPassword">
          <el-input type="password" v-model="passwordForm.currentPassword" show-password></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input type="password" v-model="passwordForm.newPassword" show-password></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input type="password" v-model="passwordForm.confirmPassword" show-password></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="showPasswordDialog = false">取消</el-button>
        <el-button type="primary" @click="changePassword">确认修改</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'AdminProfile',
  data() {
    // 密码确认验证
    const validatePass = (rule, value, callback) => {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    };
    
    return {
      editMode: false,
      showPasswordDialog: false,
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      userProfile: {
        username: 'admin',
        fullName: '管理员',
        email: 'admin@imid.com',
        phone: '13800000000',
        avatar: '',
        lastLogin: '2025-03-02 03:44'
      },
      passwordForm: {
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        fullName: [
          { required: true, message: '请输入真实姓名', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱地址', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号码', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ]
      },
      passwordRules: {
        currentPassword: [
          { required: true, message: '请输入当前密码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入密码', trigger: 'blur' },
          { validator: validatePass, trigger: 'blur' }
        ]
      }
    };
  },
  mounted() {
    this.fetchUserProfile();
  },
  methods: {
    fetchUserProfile() {
      // 从API获取用户资料
      // 这里先使用模拟数据
      // TODO: 调用后端API获取用户信息
      console.log('获取用户资料');
    },
    triggerUpload() {
      this.$refs.fileInput.click();
    },
    handleAvatarChange(e) {
      const file = e.target.files[0];
      if (!file) return;
      
      // 检查文件类型
      if (!file.type.includes('image/')) {
        this.$message.error('请上传图片文件');
        return;
      }
      
      // 创建预览
      const reader = new FileReader();
      reader.onload = (e) => {
        this.userProfile.avatar = e.target.result;
      };
      reader.readAsDataURL(file);
      
      // TODO: 实际上传逻辑
    },
    saveProfile() {
      this.$refs.profileForm.validate(valid => {
        if (valid) {
          // TODO: 调用API保存用户资料
          this.$message.success('个人信息更新成功');
          this.editMode = false;
        } else {
          this.$message.error('请正确填写所有必填字段');
          return false;
        }
      });
    },
    changePassword() {
      this.$refs.passwordForm.validate(valid => {
        if (valid) {
          // TODO: 调用API修改密码
          this.$message.success('密码修改成功');
          this.showPasswordDialog = false;
          this.passwordForm = {
            currentPassword: '',
            newPassword: '',
            confirmPassword: ''
          };
        } else {
          this.$message.error('请正确填写所有密码字段');
          return false;
        }
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.admin-profile {
  padding: 20px;
  
  .profile-card {
    max-width: 800px;
    margin: 0 auto;
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    
    .avatar-container {
      display: flex;
      flex-direction: column;
      align-items: center;
      margin-bottom: 20px;
      
      .avatar-upload {
        margin-top: 10px;
      }
    }
  }
  
  .el-divider {
    margin: 24px 0;
  }
}
</style> 