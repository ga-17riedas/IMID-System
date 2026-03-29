<template>
  <div class="patients-container">
    <el-card class="box-card">
      <!-- 顶部搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchQuery"
          placeholder="搜索患者姓名/联系电话"
          prefix-icon="el-icon-search"
          @input="handleSearch"
          clearable
          style="width: 300px; margin-bottom: 20px;">
        </el-input>
      </div>

      <!-- 患者列表 -->
      <el-table
        v-loading="loading"
        :data="patients"
        border
        highlight-current-row
        style="width: 100%">
        <el-table-column
          prop="fullName"
          label="姓名"
          width="120">
        </el-table-column>
        <el-table-column
          prop="gender"
          label="性别"
          width="80">
        </el-table-column>
        <el-table-column
          prop="age"
          label="年龄"
          width="80">
        </el-table-column>
        <el-table-column
          prop="phone"
          label="联系电话"
          width="150">
        </el-table-column>
        <el-table-column
          prop="lastVisit"
          label="最近就诊"
          width="150">
        </el-table-column>
        <el-table-column
          prop="imageCount"
          label="影像数"
          width="100">
        </el-table-column>
        <el-table-column
          prop="reportCount"
          label="报告数"
          width="100">
        </el-table-column>
        <el-table-column
          label="操作"
          fixed="right"
          width="200">
          <template slot-scope="scope">
            <el-button
              size="mini"
              @click="handleViewDetails(scope.row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 患者详情对话框 -->
    <el-dialog
      title="患者详情"
      :visible.sync="detailsDialogVisible"
      width="70%">
      <el-tabs v-model="detailsActiveTab">
        <el-tab-pane label="基本信息" name="basic">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="姓名">{{ currentPatient?.fullName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ currentPatient?.gender || '-' }}</el-descriptions-item>
            <el-descriptions-item label="年龄">{{ currentPatient?.age || '-' }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ currentPatient?.phone || '-' }}</el-descriptions-item>
            <el-descriptions-item label="电子邮箱">{{ currentPatient?.email || '-' }}</el-descriptions-item>
            <el-descriptions-item label="影像数量">{{ currentPatient?.imageCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="报告数量">{{ currentPatient?.reportCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="最近就诊">{{ currentPatient?.lastVisit || '-' }}</el-descriptions-item>
            <el-descriptions-item label="紧急联系人">{{ currentPatient.emergencyContact }}</el-descriptions-item>
            <el-descriptions-item label="紧急联系电话">{{ currentPatient.emergencyPhone }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
        
        <el-tab-pane label="病史记录" name="history">
          <el-button 
            type="primary" 
            size="small" 
            @click="addMedicalHistory"
            style="margin-bottom: 15px">
            添加病史记录
          </el-button>
          <el-timeline>
            <el-timeline-item
              v-for="record in medicalHistory"
              :key="record.id"
              placement="top"
              :timestamp="formatDateTime(record.visitDate)"
              type="primary">
              <el-card>
                <div class="history-content">
                  <h4>{{ record.diagnosis }}</h4>
                  <p>{{ record.description }}</p>
                  <p>治疗方案：{{ record.treatment }}</p>
                  <p>主治医生：{{ record.doctorName }}</p>
                </div>
                <div class="history-actions">
                  <el-button 
                    type="text" 
                    size="small"
                    @click="editMedicalHistory(record)">
                    编辑
                  </el-button>
                  <el-button 
                    type="text" 
                    size="small"
                    class="danger"
                    @click="deleteMedicalHistory(record.id)">
                    删除
                  </el-button>
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-tab-pane>
        
        <el-tab-pane label="诊断记录" name="diagnosis">
          <div v-loading="loading">
            <el-table 
              :data="diagnosisReports" 
              border 
              style="width: 100%">
              <el-table-column 
                prop="createdAt" 
                label="诊断日期" 
                width="180" />
              <el-table-column 
                prop="diagnosis" 
                label="诊断结果" 
                show-overflow-tooltip />
              <el-table-column 
                prop="treatmentPlan" 
                label="治疗方案" 
                show-overflow-tooltip />
              <el-table-column 
                prop="recommendations" 
                label="医嘱建议"
                show-overflow-tooltip />
              <el-table-column 
                label="操作" 
                width="150">
                <template slot-scope="scope">
                  <el-button 
                    type="text" 
                    @click="viewReport(scope.row)">查看</el-button>
                </template>
              </el-table-column>
            </el-table>
            <div v-if="diagnosisReports.length === 0" class="empty-text">
              暂无诊断记录
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>

    <!-- 编辑患者信息对话框 -->
    <el-dialog
      title="编辑患者信息"
      :visible.sync="editDialogVisible"
      width="50%"
      :close-on-click-modal="false">
      <el-form
        ref="editForm"
        :model="editForm"
        :rules="rules"
        label-width="100px">
        <el-form-item label="姓名" prop="fullName">
          <el-input v-model="editForm.fullName"></el-input>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="editForm.gender" style="width: 100%">
            <el-option label="男" value="男"></el-option>
            <el-option label="女" value="女"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="editForm.phone"></el-input>
        </el-form-item>
        <el-form-item label="电子邮箱" prop="email">
          <el-input v-model="editForm.email"></el-input>
        </el-form-item>
        <el-form-item label="紧急联系人" prop="emergencyContact">
          <el-input v-model="editForm.emergencyContact"></el-input>
        </el-form-item>
        <el-form-item label="紧急电话" prop="emergencyPhone">
          <el-input v-model="editForm.emergencyPhone"></el-input>
        </el-form-item>
        <el-form-item label="病史记录" prop="medicalHistory">
          <el-input
            type="textarea"
            :rows="4"
            v-model="editForm.medicalHistory">
          </el-input>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">确定</el-button>
      </span>
    </el-dialog>

    <!-- 病史记录编辑对话框 -->
    <el-dialog
      :title="historyForm.id ? '编辑病史记录' : '添加病史记录'"
      :visible.sync="historyDialogVisible"
      width="50%">
      <el-form :model="historyForm" :rules="historyRules" ref="historyForm" label-width="100px">
        <el-form-item label="日期" prop="date">
          <el-date-picker
            v-model="historyForm.date"
            type="date"
            placeholder="选择日期"
            style="width: 100%">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            type="textarea"
            :rows="4"
            v-model="historyForm.description"
            placeholder="请输入病史描述">
          </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="historyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitMedicalHistory">确定</el-button>
      </div>
    </el-dialog>

    <!-- 诊断报告编辑对话框 -->
    <el-dialog
      title="诊断报告"
      :visible.sync="reportDialogVisible"
      width="60%">
      <el-form :model="reportForm" :rules="reportRules" ref="reportForm" label-width="100px">
        <el-form-item label="诊断结果" prop="diagnosis">
          <el-input
            type="textarea"
            :rows="3"
            v-model="reportForm.diagnosis"
            placeholder="请输入诊断结果">
          </el-input>
        </el-form-item>
        <el-form-item label="治疗方案" prop="treatmentPlan">
          <el-input
            type="textarea"
            :rows="3"
            v-model="reportForm.treatmentPlan"
            placeholder="请输入治疗方案">
          </el-input>
        </el-form-item>
        <el-form-item label="医嘱建议" prop="recommendations">
          <el-input
            type="textarea"
            :rows="3"
            v-model="reportForm.recommendations"
            placeholder="请输入医嘱建议">
          </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="reportDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveReport">确定</el-button>
      </div>
    </el-dialog>

    <!-- 报告详情对话框 -->
    <el-dialog
      title="诊断报告详情"
      :visible.sync="reportDetailVisible"
      width="60%">
      <div v-if="currentReport" class="report-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="诊断日期">
            {{ currentReport.createdAt }}
          </el-descriptions-item>
          <el-descriptions-item label="诊断结果">
            {{ currentReport.diagnosis }}
          </el-descriptions-item>
          <el-descriptions-item label="治疗方案">
            {{ currentReport.treatmentPlan }}
          </el-descriptions-item>
          <el-descriptions-item label="医嘱建议">
            {{ currentReport.recommendations }}
          </el-descriptions-item>
        </el-descriptions>
        
        <div class="report-actions" style="margin-top: 20px; text-align: right;">
          <el-button 
            type="primary" 
            @click="handleExportReport(currentReport.id)">
            导出报告
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'
import { getToken } from '@/utils/auth'
import { getPatients, getPatientHistory, getPatientDiagnosis, exportReport } from '@/api/doctor'
import { formatDateTime } from '@/utils/format'

export default {
  name: 'Patients',
  data() {
    return {
      loading: false,
      searchQuery: '',
      patients: [],
      currentPatient: {
        fullName: '',
        gender: '',
        age: '',
        phone: '',
        email: '',
        imageCount: 0,
        reportCount: 0,
        lastVisit: '',
        emergencyContact: '',
        emergencyPhone: ''
      },
      medicalHistory: [],
      diagnosisReports: [],
      detailsDialogVisible: false,
      editDialogVisible: false,
      detailsActiveTab: 'basic',
      historyDialogVisible: false,
      reportDialogVisible: false,
      editForm: {
        id: '',
        fullName: '',
        gender: '',
        age: '',
        phone: '',
        email: '',
        emergencyContact: '',
        emergencyPhone: ''
      },
      historyForm: {
        id: null,
        date: '',
        description: ''
      },
      reportForm: {
        id: null,
        diagnosis: '',
        treatmentPlan: '',
        recommendations: ''
      },
      rules: {
        fullName: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ],
        email: [
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ]
      },
      historyRules: {
        date: [
          { required: true, message: '请选择日期', trigger: 'change' }
        ],
        description: [
          { required: true, message: '请输入病史描述', trigger: 'blur' }
        ]
      },
      reportRules: {
        diagnosis: [
          { required: true, message: '请输入诊断结果', trigger: 'blur' }
        ],
        treatmentPlan: [
          { required: true, message: '请输入治疗方案', trigger: 'blur' }
        ]
      },
      dialogVisible: false,
      activeTab: 'basic',
      historyLoading: false,
      patientInfo: {},
      reportDetailVisible: false,
      currentReport: null,
      saveLoading: false
    }
  },
  created() {
    this.fetchPatients()
  },
  methods: {
    formatDateTime,
    async fetchPatients() {
      try {
        this.loading = true
        const response = await getPatients()
        this.patients = response.data
      } catch (error) {
        console.error('获取患者列表失败:', error)
        this.$message.error('获取患者列表失败')
      } finally {
        this.loading = false
      }
    },
    async handleSearch() {
      if (!this.searchQuery) {
        // 如果搜索框为空，获取所有患者
        await this.fetchPatients()
        return
      }
      
      try {
        this.loading = true
        const query = this.searchQuery.trim()
        
        // 使用API进行搜索，根据姓名和电话号码
        const response = await request({
          url: '/api/doctor/patients/search',
          method: 'get',
          params: { query }
        })
        
        if (response && response.data) {
          this.patients = response.data
        } else {
          this.patients = []
        }
        
        // 如果API请求出错或者不可用，可以进行本地过滤
        if (!response || !response.data) {
          // 本地过滤
          this.patients = this.patients.filter(patient => {
            const nameMatch = patient.fullName && patient.fullName.toLowerCase().includes(query.toLowerCase())
            const phoneMatch = patient.phone && patient.phone.includes(query)
            return nameMatch || phoneMatch
          })
        }
        
        if (this.patients.length === 0) {
          this.$message.info('未找到匹配的患者')
        }
      } catch (error) {
        console.error('搜索患者失败:', error)
        this.$message.error('搜索失败，请检查网络连接')
        
        // 如果API搜索失败，尝试本地过滤
        if (this.patients && this.patients.length > 0) {
          const query = this.searchQuery.trim().toLowerCase()
          this.patients = this.patients.filter(patient => {
            const nameMatch = patient.fullName && patient.fullName.toLowerCase().includes(query)
            const phoneMatch = patient.phone && patient.phone.includes(query)
            return nameMatch || phoneMatch
          })
        }
      } finally {
        this.loading = false
      }
    },
    async handleViewDetails(patient) {
      this.currentPatient = patient
      this.detailsDialogVisible = true
      await Promise.all([
        this.fetchPatientHistory(patient.id),
        this.fetchPatientDiagnosis(patient.id)
      ])
    },
    async fetchPatientHistory(patientId) {
      try {
        this.historyLoading = true
        const response = await getPatientHistory(patientId)
        this.medicalHistory = response.data
      } catch (error) {
        console.error('获取病史记录失败:', error)
        this.$message.error('获取病史记录失败')
      } finally {
        this.historyLoading = false
      }
    },
    async fetchPatientDiagnosis(patientId) {
      try {
        const response = await getPatientDiagnosis(patientId)
        this.diagnosisReports = response.data.map(report => ({
          ...report,
          createdAt: this.formatDateTime(report.createdAt)
        }))
      } catch (error) {
        console.error('获取诊断记录失败:', error)
        this.$message.error('获取诊断记录失败')
      }
    },
    handleEditPatient(patient) {
      this.editForm = { ...patient }
      this.editDialogVisible = true
    },
    async submitEdit() {
      try {
        await this.$refs.editForm.validate()
        await request({
          url: `/api/doctors/patients/${this.editForm.id}`,
          method: 'put',
          data: this.editForm
        })
        this.$message.success('更新成功')
        this.editDialogVisible = false
        await this.fetchPatients()
      } catch (error) {
        this.$message.error('更新失败')
        console.error(error)
      }
    },
    async viewReport(report) {
      this.currentReport = report
      this.reportDetailVisible = true
    },
    handleViewPatient(/* patient */) {
      // 实现查看患者详情的逻辑
    },
    async addMedicalHistory() {
      this.historyForm = {
        id: null,
        date: new Date(),
        description: ''
      }
      this.historyDialogVisible = true
    },
    async editMedicalHistory(record) {
      this.historyForm = { ...record }
      this.historyDialogVisible = true
    },
    async deleteMedicalHistory(id) {
      try {
        await this.$confirm('确认删除该病史记录?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await request({
          url: `/api/doctors/patients/${this.currentPatient.id}/history/${id}`,
          method: 'delete'
        })
        this.medicalHistory = this.medicalHistory.filter(record => record.id !== id)
        this.$message.success('删除成功')
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败')
        }
      }
    },
    async submitMedicalHistory() {
      try {
        await this.$refs.historyForm.validate()
        const url = this.historyForm.id
          ? `/api/doctors/patients/${this.currentPatient.id}/history/${this.historyForm.id}`
          : `/api/doctors/patients/${this.currentPatient.id}/history`
        
        const method = this.historyForm.id ? 'put' : 'post'
        await request({
          url: url,
          method: method,
          data: this.historyForm
        })
        
        this.$message.success('保存成功')
        this.historyDialogVisible = false
        await this.fetchPatientHistory(this.currentPatient.id)
      } catch (error) {
        this.$message.error('保存失败')
      }
    },
    async editReport(report) {
      this.reportForm = { ...report }
      this.reportDialogVisible = true
    },
    async handleSaveReport() {
      this.$refs.reportForm.validate(async valid => {
        if (valid) {
          try {
            this.saveLoading = true;
            
            // 构建请求数据
            const reportData = {
              patientId: this.currentPatient.id,
              diagnosis: this.reportForm.diagnosis,
              treatmentPlan: this.reportForm.treatmentPlan,
              recommendations: this.reportForm.recommendations
            };

            // 修改请求路径为正确的后端接口
            await this.$http.post('/api/doctor/reports', reportData);
            
            this.$message.success('诊断报告保存成功');
            this.reportDialogVisible = false;
            this.resetReportForm();
            await this.fetchPatientList(); // 刷新列表
          } catch (error) {
            console.error('保存诊断报告失败:', error);
            this.$message.error(error.response?.data?.message || '保存失败，请重试');
          } finally {
            this.saveLoading = false;
          }
        }
      });
    },
    // 重置表单
    resetReportForm() {
      if (this.$refs.reportForm) {
        this.$refs.reportForm.resetFields();
      }
      this.reportForm = {
        diagnosis: '',
        treatmentPlan: '',
        recommendations: '',
        imageUrl: '',
        aiDiagnosis: '',
        confidenceScore: null,
        imageType: ''
      };
    },
    async handleExportReport(reportId) {
      try {
        await exportReport(reportId)
        this.$message.success('报告导出成功')
      } catch (error) {
        console.error('导出报告失败:', error)
        this.$message.error('导出报告失败')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.patients-container {
  padding: 20px;

  .box-card {
    margin-bottom: 20px;
  }

  .search-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
  }

  .el-table {
    margin-top: 20px;
  }

  .dialog-footer {
    text-align: right;
  }

  .history-actions {
    margin-bottom: 20px;
  }

  .history-item {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
  }

  .history-content {
    flex: 1;
    margin-right: 20px;
  }

  .el-timeline-item {
    .el-timeline-item__timestamp {
      font-size: 14px;
      color: #409EFF;
    }
  }

  .history-content {
    h4 {
      margin: 0 0 10px;
      color: #303133;
    }
    p {
      margin: 5px 0;
      color: #606266;
    }
  }

  .history-actions {
    margin-top: 10px;
    text-align: right;
    
    .danger {
      color: #F56C6C;
    }
  }

  .empty-text {
    text-align: center;
    color: #909399;
    padding: 30px 0;
  }

  .report-detail {
    .el-descriptions-item__label {
      width: 120px;
      background-color: #f5f7fa;
    }
    
    .el-descriptions-item__content {
      padding: 12px 15px;
      line-height: 1.6;
    }
  }
}
</style> 