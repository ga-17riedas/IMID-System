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
        
        <!-- 基本信息 -->
        <h3>基本信息</h3>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="fullName">
              <el-input v-model="form.fullName"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年龄" prop="age">
              <el-input-number v-model="form.age" :min="0" :max="150"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="form.gender" placeholder="请选择性别">
                <el-option label="男" value="male"></el-option>
                <el-option label="女" value="female"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号码" prop="phone">
              <el-input v-model="form.phone"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email"></el-input>
        </el-form-item>

        <!-- 紧急联系人信息 -->
        <h3>紧急联系人</h3>
        <el-form-item label="联系人姓名" prop="emergencyContact">
          <el-input v-model="form.emergencyContact"></el-input>
        </el-form-item>

        <el-form-item label="联系人电话" prop="emergencyPhone">
          <el-input v-model="form.emergencyPhone"></el-input>
        </el-form-item>

        <!-- 按钮组 -->
        <el-form-item v-if="isEditing">
          <el-button type="primary" @click="submitForm('form')">保存</el-button>
          <el-button @click="cancelEdit">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 病历记录 -->
    <!--
    <el-card class="medical-records">
      <div slot="header">
        <span>病历记录</span>
      </div>
      
      <el-table
        :data="medicalRecords"
        style="width: 100%"
        v-loading="loading">
        <el-table-column
          prop="visitDate"
          label="就诊日期"
          :formatter="formatDate"
          width="180">
        </el-table-column>
        <el-table-column
          prop="hospital"
          label="就诊医院"
          width="200">
        </el-table-column>
        <el-table-column
          prop="department"
          label="科室">
        </el-table-column>
        <el-table-column
          prop="doctorName"
          label="主治医生"
          width="120">
        </el-table-column>
        <el-table-column
          prop="diagnosis"
          label="诊断结果"
          show-overflow-tooltip>
        </el-table-column>
        <el-table-column
          label="操作"
          width="120">
          <template slot-scope="scope">
            <el-button
              type="text"
              size="small"
              @click="viewDetails(scope.row)">
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      title="病历详情"
      :visible.sync="dialogVisible"
      width="60%">
      <div v-if="currentRecord">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="就诊日期">
            {{ formatDate(null, currentRecord.visitDate) }}
          </el-descriptions-item>
          <el-descriptions-item label="就诊医院">
            {{ currentRecord.hospital }}
          </el-descriptions-item>
          <el-descriptions-item label="科室">
            {{ currentRecord.department }}
          </el-descriptions-item>
          <el-descriptions-item label="主治医生">
            {{ currentRecord.doctorName }}
          </el-descriptions-item>
          <el-descriptions-item label="诊断结果" :span="2">
            {{ currentRecord.diagnosis }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
    -->
  </div>
</template>

<script>
import { formatDate } from '@/utils/date'

export default {
  name: 'PatientProfile',
  data() {
    return {
      isEditing: false,
      form: {
        fullName: '',
        age: null,
        gender: '',
        phone: '',
        email: '',
        emergencyContact: '',
        emergencyPhone: ''
      },
      medicalRecords: [],
      loading: false,
      dialogVisible: false,
      currentRecord: null,
      rules: {
        fullName: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        age: [
          { required: true, message: '请输入年龄', trigger: 'blur' }
        ],
        gender: [
          { required: true, message: '请选择性别', trigger: 'change' }
        ],
        phone: [
          { required: true, message: '请输入手机号码', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ],
        email: [
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    formatDate(row, date) {
      return formatDate(date || row.visitDate)
    },
    async fetchProfile() {
      try {
        const response = await this.$http.get('/api/patient/profile')
        if (response.data) {
          this.form = {
            ...this.form,
            ...response.data
          }
        }
      } catch (error) {
        console.error('获取个人信息失败:', error)
        this.$message.error('获取个人信息失败')
      }
    },
    async fetchMedicalRecords() {
      this.loading = true
      try {
        const response = await this.$http.get('/api/patient/medical-records')
        this.medicalRecords = response.data
      } catch (error) {
        console.error('获取病历记录失败:', error)
        this.$message.error('获取病历记录失败')
      } finally {
        this.loading = false
      }
    },
    handleEdit() {
      if (this.isEditing) {
        this.submitForm('form')
      } else {
        this.isEditing = true
      }
    },
    async submitForm(formName) {
      this.$refs[formName].validate(async (valid) => {
        if (valid) {
          try {
            await this.$http.put('/api/patient/profile', this.form)
            this.$message.success('保存成功')
            this.isEditing = false
            this.fetchProfile()
          } catch (error) {
            console.error('保存失败:', error)
            this.$message.error('保存失败')
          }
        }
      })
    },
    cancelEdit() {
      this.isEditing = false
      this.fetchProfile()
    },
    viewDetails(record) {
      this.currentRecord = record
      this.dialogVisible = true
    }
  },
  created() {
    this.fetchProfile()
    this.fetchMedicalRecords()
  }
}
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.profile-card, .medical-records {
  margin-bottom: 20px;
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