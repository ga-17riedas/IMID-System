<template>
  <div class="admin-settings">
    <el-card shadow="hover" class="settings-card">
      <div slot="header" class="card-header">
        <span>系统设置</span>
      </div>
      
      <el-form :model="settingsForm" ref="settingsForm" label-width="160px">
        <!-- 系统基本设置 -->
        <h3 class="settings-section-title">
          <i class="el-icon-setting"></i> 基本设置
        </h3>
        
        <el-form-item label="系统名称">
          <el-input v-model="settingsForm.systemName"></el-input>
        </el-form-item>
        
        <el-form-item label="系统描述">
          <el-input type="textarea" v-model="settingsForm.systemDescription" :rows="2"></el-input>
        </el-form-item>
        
        <el-form-item label="网站Logo">
          <el-upload
            class="avatar-uploader"
            action="#"
            :http-request="uploadLogo"
            :show-file-list="false"
            :before-upload="beforeLogoUpload">
            <img v-if="settingsForm.logoUrl" :src="settingsForm.logoUrl" class="logo-image">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
          <div class="form-tip">推荐尺寸: 200x60px, 格式: PNG, JPG</div>
        </el-form-item>
        
        <el-form-item label="系统维护模式">
          <el-switch
            v-model="settingsForm.maintenanceMode"
            active-text="开启"
            inactive-text="关闭">
          </el-switch>
          <div class="form-tip">启用维护模式将阻止普通用户访问系统</div>
        </el-form-item>
        
        <!-- 安全设置 -->
        <h3 class="settings-section-title">
          <i class="el-icon-lock"></i> 安全设置
        </h3>
        
        <el-form-item label="密码强度要求">
          <el-select v-model="settingsForm.passwordStrength" style="width: 100%">
            <el-option label="低 (6位数字或字母)" value="low"></el-option>
            <el-option label="中 (8位，包含数字和字母)" value="medium"></el-option>
            <el-option label="高 (10位，包含数字、大小写字母和特殊字符)" value="high"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="登录失败锁定">
          <el-input-number v-model="settingsForm.loginAttempts" :min="3" :max="10"></el-input-number>
          <span class="input-suffix">次失败后锁定账户</span>
        </el-form-item>
        
        <el-form-item label="锁定时长">
          <el-input-number v-model="settingsForm.lockDuration" :min="5" :max="60"></el-input-number>
          <span class="input-suffix">分钟</span>
        </el-form-item>
        
        <el-form-item label="会话超时时间">
          <el-input-number v-model="settingsForm.sessionTimeout" :min="10" :max="120"></el-input-number>
          <span class="input-suffix">分钟</span>
        </el-form-item>
        
        <el-form-item label="双因素认证">
          <el-switch
            v-model="settingsForm.twoFactorAuth"
            active-text="必选"
            inactive-text="可选">
          </el-switch>
        </el-form-item>
        
        <!-- 系统日志设置 -->
        <h3 class="settings-section-title">
          <i class="el-icon-document"></i> 日志设置
        </h3>
        
        <el-form-item label="日志保留时间">
          <el-input-number v-model="settingsForm.logRetention" :min="7" :max="365"></el-input-number>
          <span class="input-suffix">天</span>
        </el-form-item>
        
        <el-form-item label="日志级别">
          <el-select v-model="settingsForm.logLevel" style="width: 100%">
            <el-option label="DEBUG - 所有日志" value="DEBUG"></el-option>
            <el-option label="INFO - 信息、警告和错误" value="INFO"></el-option>
            <el-option label="WARNING - 仅警告和错误" value="WARNING"></el-option>
            <el-option label="ERROR - 仅错误信息" value="ERROR"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="记录用户操作">
          <el-switch v-model="settingsForm.logUserActions"></el-switch>
          <div class="form-tip">记录用户的重要操作（登录、修改数据等）</div>
        </el-form-item>
        
        <!-- 存储设置 -->
        <h3 class="settings-section-title">
          <i class="el-icon-folder"></i> 存储设置
        </h3>
        
        <el-form-item label="存储方式">
          <el-radio-group v-model="settingsForm.storageType">
            <el-radio label="local">本地存储</el-radio>
            <el-radio label="cloud">云存储</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="最大上传大小">
          <el-input-number v-model="settingsForm.maxUploadSize" :min="1" :max="100"></el-input-number>
          <span class="input-suffix">MB</span>
        </el-form-item>
        
        <el-form-item label="允许的文件类型">
          <el-select v-model="settingsForm.allowedFileTypes" multiple style="width: 100%">
            <el-option label="图片 (JPG, PNG, GIF)" value="image"></el-option>
            <el-option label="文档 (PDF, DOC, DOCX)" value="document"></el-option>
            <el-option label="视频 (MP4, AVI)" value="video"></el-option>
            <el-option label="其他 (ZIP, RAR)" value="archive"></el-option>
          </el-select>
        </el-form-item>
        
        <!-- 备份设置 -->
        <h3 class="settings-section-title">
          <i class="el-icon-copy-document"></i> 备份设置
        </h3>
        
        <el-form-item label="自动备份">
          <el-switch v-model="settingsForm.autoBackup"></el-switch>
        </el-form-item>
        
        <el-form-item label="备份频率" v-if="settingsForm.autoBackup">
          <el-select v-model="settingsForm.backupFrequency" style="width: 100%">
            <el-option label="每天" value="daily"></el-option>
            <el-option label="每周" value="weekly"></el-option>
            <el-option label="每月" value="monthly"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="保留备份数量" v-if="settingsForm.autoBackup">
          <el-input-number v-model="settingsForm.backupRetention" :min="1" :max="30"></el-input-number>
          <span class="input-suffix">个</span>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="saveSettings">保存设置</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { getSystemSettings, updateSystemSettings, resetSystemSettings, uploadSystemLogo } from '@/api/settings'

export default {
  name: 'AdminSettings',
  data() {
    return {
      settingsForm: {
        // 基本设置
        systemName: 'IMID 医学影像诊断系统',
        systemDescription: '智能医学影像诊断辅助平台',
        logoUrl: '',
        maintenanceMode: false,
        
        // 安全设置
        passwordStrength: 'medium',
        loginAttempts: 5,
        lockDuration: 30,
        sessionTimeout: 30,
        twoFactorAuth: false,
        
        // 日志设置
        logRetention: 90,
        logLevel: 'INFO',
        logUserActions: true,
        
        // 存储设置
        storageType: 'local',
        maxUploadSize: 10,
        allowedFileTypes: ['image', 'document'],
        
        // 备份设置
        autoBackup: true,
        backupFrequency: 'daily',
        backupRetention: 7
      },
      originalSettings: null
    };
  },
  created() {
    this.fetchSettings();
  },
  methods: {
    fetchSettings() {
      // 从API获取实际系统设置
      getSystemSettings().then(response => {
        if (response && response.data) {
          // 合并返回的设置到表单
          this.settingsForm = { ...this.settingsForm, ...response.data };
          // 保存一份原始设置用于重置
          this.originalSettings = JSON.parse(JSON.stringify(this.settingsForm));
          console.log('成功获取系统设置');
        }
      }).catch(error => {
        console.error('获取系统设置失败:', error);
        this.$message.error('获取系统设置失败，将使用默认设置');
        // 保存一份当前设置用于重置
        this.originalSettings = JSON.parse(JSON.stringify(this.settingsForm));
      });
    },
    beforeLogoUpload(file) {
      const isImage = file.type.startsWith('image/');
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isImage) {
        this.$message.error('上传Logo只能是图片格式!');
        return false;
      }
      if (!isLt2M) {
        this.$message.error('上传Logo大小不能超过 2MB!');
        return false;
      }
      return true;
    },
    uploadLogo(options) {
      const file = options.file;
      // 创建文件预览
      const reader = new FileReader();
      reader.onload = (e) => {
        this.settingsForm.logoUrl = e.target.result;
      };
      reader.readAsDataURL(file);
      
      // 调用API上传Logo
      const loading = this.$loading({
        lock: true,
        text: '上传Logo中...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      });
      
      uploadSystemLogo(file).then(response => {
        if (response && response.data) {
          // 设置返回的Logo URL
          this.settingsForm.logoUrl = response.data;
          this.$message.success('Logo上传成功');
        }
      }).catch(error => {
        console.error('上传Logo失败:', error);
        this.$message.error('上传Logo失败，请重试');
      }).finally(() => {
        loading.close();
      });
    },
    saveSettings() {
      // 显示加载中状态
      const loading = this.$loading({
        lock: true,
        text: '保存设置中...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      });
      
      // 构造符合后端格式的设置数据
      // 将对象格式转换成后端期望的列表格式
      const settingsArray = Object.entries(this.settingsForm).map(([key, value]) => {
        return {
          key: key,
          value: typeof value === 'object' ? JSON.stringify(value) : String(value),
          description: `${key} 设置`,
          group: this.getSettingGroup(key)
        };
      });
      
      console.log('转换后的设置数据:', settingsArray);
      
      // 调用API保存系统设置
      updateSystemSettings(settingsArray).then(response => {
        loading.close();
        
        // 更新原始设置
        this.originalSettings = JSON.parse(JSON.stringify(this.settingsForm));
        
        // 触发全局事件，使其他组件可以更新
        this.$store.dispatch('settings/updateSetting', { 
          key: 'systemName', 
          value: this.settingsForm.systemName 
        });
        
        // 如果有主题设置，应用它
        if (this.settingsForm.theme) {
          document.body.className = `theme-${this.settingsForm.theme}`;
          this.$store.dispatch('settings/updateSetting', { 
            key: 'theme', 
            value: this.settingsForm.theme 
          });
        }
        
        // 显示成功消息
        this.$message({
          message: '系统设置保存成功，部分设置可能需要刷新页面才能生效',
          type: 'success',
          duration: 5000
        });
        
        // 询问用户是否要刷新页面
        this.$confirm('某些设置需要刷新页面才能完全生效，是否立即刷新?', '提示', {
          confirmButtonText: '立即刷新',
          cancelButtonText: '稍后刷新',
          type: 'info'
        }).then(() => {
          // 刷新页面
          location.reload();
        }).catch(() => {
          // 用户选择不刷新
        });
      }).catch(error => {
        loading.close();
        console.error('保存系统设置失败:', error);
        this.$message.error('保存系统设置失败: ' + (error.message || '未知错误'));
      });
    },
    
    // 根据设置键名确定其分组
    getSettingGroup(key) {
      const groupMappings = {
        // 基本设置
        systemName: 'BASIC',
        systemDescription: 'BASIC',
        logoUrl: 'BASIC',
        maintenanceMode: 'BASIC',
        theme: 'APPEARANCE',
        
        // 安全设置
        passwordStrength: 'SECURITY',
        loginAttempts: 'SECURITY',
        lockDuration: 'SECURITY',
        sessionTimeout: 'SECURITY',
        twoFactorAuth: 'SECURITY',
        
        // 日志设置
        logRetention: 'LOGGING',
        logLevel: 'LOGGING',
        logUserActions: 'LOGGING',
        
        // 存储设置
        storageType: 'STORAGE',
        maxUploadSize: 'STORAGE',
        allowedFileTypes: 'STORAGE',
        
        // 备份设置
        autoBackup: 'BACKUP',
        backupFrequency: 'BACKUP',
        backupRetention: 'BACKUP'
      };
      
      return groupMappings[key] || 'OTHER';
    },
    resetForm() {
      this.$confirm('确定要重置所有设置吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        if (this.originalSettings) {
          this.settingsForm = JSON.parse(JSON.stringify(this.originalSettings));
          this.$message({
            message: '设置已重置为上次保存的值',
            type: 'info'
          });
        } else {
          // 如果没有原始设置，则调用API重置为系统默认值
          resetSystemSettings().then(() => {
            this.fetchSettings(); // 重新获取默认设置
            this.$message({
              message: '设置已重置为系统默认值',
              type: 'success'
            });
          }).catch(error => {
            console.error('重置设置失败:', error);
            this.$message.error('重置设置失败: ' + (error.message || '未知错误'));
          });
        }
      }).catch(() => {
        // 用户取消重置
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.admin-settings {
  padding: 20px;
  
  .settings-card {
    margin: 0 auto;
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
  
  .settings-section-title {
    margin: 20px 0 15px 0;
    padding-bottom: 10px;
    border-bottom: 1px solid #eee;
    color: #409EFF;
  }
  
  .form-tip {
    font-size: 12px;
    color: #909399;
    margin-top: 5px;
  }
  
  .input-suffix {
    margin-left: 10px;
    color: #606266;
  }
  
  .avatar-uploader {
    .el-upload {
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      
      &:hover {
        border-color: #409EFF;
      }
    }
    
    .avatar-uploader-icon {
      font-size: 28px;
      color: #8c939d;
      width: 200px;
      height: 60px;
      line-height: 60px;
      text-align: center;
    }
    
    .logo-image {
      width: 200px;
      height: 60px;
      display: block;
    }
  }
}
</style> 