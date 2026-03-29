<template>
  <div class="patient-detail">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="基本信息" name="basic">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="姓名">{{ patientInfo.fullName }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ patientInfo.gender }}</el-descriptions-item>
          <el-descriptions-item label="年龄">{{ patientInfo.age }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ patientInfo.phone }}</el-descriptions-item>
          <el-descriptions-item label="电子邮箱">{{ patientInfo.email }}</el-descriptions-item>
          <el-descriptions-item label="紧急联系人">{{ patientInfo.emergencyContact }}</el-descriptions-item>
          <el-descriptions-item label="紧急联系电话">{{ patientInfo.emergencyPhone }}</el-descriptions-item>
        </el-descriptions>
      </el-tab-pane>

      <el-tab-pane label="病史记录" name="history">
        <div v-loading="loading">
          <el-timeline v-if="medicalHistory.length > 0">
            <el-timeline-item
              v-for="record in medicalHistory"
              :key="record.id"
              :timestamp="formatDateTime(record.visitDate)"
              placement="top"
            >
              <el-card>
                <h4>就诊记录</h4>
                <div class="history-content">
                  <p><strong>主治医生：</strong>{{ record.doctorName }} ({{ record.department }})</p>
                  <p class="diagnosis-title"><strong>诊断结果：</strong></p>
                  <div class="diagnosis-content">{{ record.diagnosis }}</div>
                  <p class="treatment-title"><strong>治疗方案：</strong></p>
                  <div class="treatment-content">{{ record.treatment }}</div>
                  <p v-if="record.notes" class="notes-title"><strong>备注：</strong></p>
                  <div v-if="record.notes" class="notes-content">{{ record.notes }}</div>
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
          <div v-else class="empty-text">
            暂无病史记录
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="诊断记录" name="diagnosis">
        <el-table :data="diagnosisRecords" style="width: 100%">
          <el-table-column prop="createdAt" label="诊断日期" width="180">
            <template slot-scope="scope">
              {{ formatDate(scope.row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column prop="diagnosis" label="诊断结果" />
          <el-table-column prop="doctor.fullName" label="医生" width="120" />
          <el-table-column label="操作" width="120">
            <template slot-scope="scope">
              <el-button 
                type="text" 
                size="small" 
                @click="viewDiagnosisDetail(scope.row)"
              >
                查看详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { formatDate } from '@/utils/format'

export default {
  name: 'PatientDetail',
  
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    patientId: {
      type: [Number, String],
      required: true
    }
  },

  data() {
    return {
      activeTab: 'basic',
      patientInfo: {},
      medicalHistory: [],
      diagnosisRecords: [],
      loading: false,
    }
  },

  watch: {
    visible(val) {
      if (val) {
        this.fetchPatientInfo()
      }
    }
  },

  methods: {
    formatDate,
    
    handleClose() {
      this.$emit('update:visible', false)
    },

    async fetchPatientInfo() {
      try {
        const [basicInfo, history, diagnosis] = await Promise.all([
          this.$http.get(`/api/doctor/patients/${this.patientId}`),
          this.$http.get(`/api/doctor/patients/${this.patientId}/history`),
          this.$http.get(`/api/doctor/patients/${this.patientId}/diagnosis`)
        ])
        
        this.patientInfo = basicInfo.data
        this.medicalHistory = history.data
        this.diagnosisRecords = diagnosis.data
      } catch (error) {
        this.$message.error('获取患者信息失败')
        console.error(error)
      }
    },

    viewDiagnosisDetail(record) {
      // TODO: 实现查看诊断详情的功能
      this.$message.info('功能开发中')
    }
  }
}
</script>

<style lang="scss" scoped>
.patient-detail {
  padding: 20px;

  .empty-text {
    text-align: center;
    color: #909399;
    padding: 30px 0;
  }

  .history-content {
    margin: 10px 0;
    
    p {
      margin: 8px 0;
      line-height: 1.6;
    }

    .diagnosis-title,
    .treatment-title,
    .notes-title {
      margin-top: 15px;
      color: #303133;
    }

    .diagnosis-content,
    .treatment-content,
    .notes-content {
      margin: 8px 0;
      padding: 10px;
      background-color: #f8f9fa;
      border-radius: 4px;
      line-height: 1.8;
      color: #606266;
      white-space: pre-wrap;
    }
  }

  .el-timeline {
    padding: 20px;
    
    .el-card {
      h4 {
        margin: 0 0 10px;
        color: #303133;
        font-size: 16px;
      }
    }
  }
}
</style> 