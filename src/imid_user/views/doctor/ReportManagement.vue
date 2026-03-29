<template>
  <div class="report-management">
    <el-card>
      <div slot="header" class="header">
        <span>报告管理</span>
        <el-radio-group v-model="reportType" size="small">
          <el-radio-button label="all">全部报告</el-radio-button>
          <el-radio-button label="analyzed">已分析</el-radio-button>
          <el-radio-button label="unanalyzed">待分析</el-radio-button>
        </el-radio-group>
      </div>

      <el-table
        v-loading="loading"
        :data="filteredReports"
        border
        style="width: 100%">
        <el-table-column
          prop="id"
          label="报告ID"
          width="80">
        </el-table-column>
        
        <el-table-column
          prop="patient.fullName"
          label="患者姓名"
          width="120">
          <template slot-scope="scope">
            {{ scope.row.patient?.fullName || '-' }}
          </template>
        </el-table-column>

        <el-table-column
          label="检查类型"
          width="120">
          <template slot-scope="scope">
            {{ scope.row.medicalImage?.imageType || '-' }}
          </template>
        </el-table-column>

        <el-table-column
          label="诊断结果"
          show-overflow-tooltip>
          <template slot-scope="scope">
            {{ scope.row.diagnosis || '-' }}
          </template>
        </el-table-column>

        <el-table-column
          label="创建时间"
          width="180">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>

        <el-table-column
          label="操作"
          width="200">
          <template slot-scope="scope">
            <el-button
              size="mini"
              @click="viewReport(scope.row)">查看</el-button>
            <el-button
              size="mini"
              type="primary"
              @click="editReport(scope.row)"
              v-if="!scope.row.diagnosis">分析</el-button>
            <el-button
              size="mini"
              type="success"
              @click="exportReport(scope.row)"
              v-if="scope.row.diagnosis">导出</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20, 50]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
        </el-pagination>
      </div>
    </el-card>

    <!-- 报告详情对话框 -->
    <el-dialog
      title="报告详情"
      :visible.sync="dialogVisible"
      width="70%">
      <div v-if="currentReport" class="report-detail">
        <h3>基本信息</h3>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="患者姓名">{{ currentReport.patient?.fullName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ currentReport.patient?.gender || '-' }}</el-descriptions-item>
          <el-descriptions-item label="年龄">{{ currentReport.patient?.age || '-' }}</el-descriptions-item>
          <el-descriptions-item label="检查类型">
            {{ getImageType(currentReport.medicalImage?.imageType) || '-' }}
          </el-descriptions-item>
        </el-descriptions>
        
        <h3>医学影像</h3>
        <div class="image-container" v-if="currentReport.medicalImage?.imageUrl">
          <img 
            :src="getFullImageUrl(currentReport.medicalImage.imageUrl)" 
            @error="handleImageError"
            alt="医学影像"
          >
        </div>
        <div v-else class="image-slot">
          暂无医学影像
        </div>
        
        <h3>AI 分析结果</h3>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="AI诊断">
            {{ currentReport.medicalImage?.aiDiagnosis || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="置信度">
            {{ currentReport.medicalImage?.confidenceScore ? 
              (currentReport.medicalImage.confidenceScore * 100).toFixed(2) + '%' : '-' }}
          </el-descriptions-item>
        </el-descriptions>

        <h3>医生诊断</h3>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="诊断结果">{{ currentReport.diagnosis || '-' }}</el-descriptions-item>
          <el-descriptions-item label="治疗方案">{{ currentReport.treatmentPlan || '-' }}</el-descriptions-item>
          <el-descriptions-item label="医生建议">{{ currentReport.recommendations || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { formatDate } from '@/utils/date'
import { getImageUrl, handleImageError } from '@/utils/image'

export default {
  name: 'ReportManagement',
  data() {
    return {
      loading: false,
      reports: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      reportType: 'all',
      dialogVisible: false,
      currentReport: null,
      baseUrl: process.env.VUE_APP_BASE_API || ''
    }
  },
  computed: {
    filteredReports() {
      if (this.reportType === 'all') return this.reports
      
      return this.reports.filter(report => {
        if (this.reportType === 'analyzed') {
          return report.diagnosis && report.medicalImage?.aiDiagnosis
        } else {
          return !report.diagnosis || !report.medicalImage?.aiDiagnosis
        }
      })
    }
  },
  methods: {
    formatDate,
    async fetchReports() {
      this.loading = true
      try {
        const response = await this.$http.get('/api/medical/diagnosis-reports', {
          params: {
            page: this.currentPage - 1,
            size: this.pageSize
          }
        })
        this.reports = response.data.content
        this.total = response.data.totalElements
      } catch (error) {
        console.error('获取报告列表失败:', error)
        this.$message.error('获取报告列表失败')
      } finally {
        this.loading = false
      }
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.fetchReports()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.fetchReports()
    },
    async viewReport(report) {
      try {
        const response = await this.$http.get(`/api/medical/diagnosis-reports/${report.id}`)
        this.currentReport = response.data
        this.dialogVisible = true
      } catch (error) {
        console.error('获取报告详情失败:', error)
        this.$message.error('获取报告详情失败')
      }
    },
    editReport(report) {
      this.$router.push(`/doctor/image-analysis?reportId=${report.id}`)
    },
    async exportReport(report) {
      try {
        const loading = this.$loading({
          lock: true,
          text: '正在生成报告...',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        })

        const response = await this.$http.post(`/api/medical/diagnosis-reports/${report.id}/export`, null, {
          responseType: 'blob'
        })
        
        const contentType = response.headers['content-type']
        if (contentType && contentType.includes('application/json')) {
          const reader = new FileReader()
          reader.onload = () => {
            const error = JSON.parse(reader.result)
            this.$message.error(error.message || '导出失败')
          }
          reader.readAsText(response.data)
          loading.close()
          return
        }
        
        const url = window.URL.createObjectURL(new Blob([response.data]))
        const link = document.createElement('a')
        link.href = url
        link.setAttribute('download', `medical_report_${report.id}.pdf`)
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
        
        loading.close()
        this.$message.success('报告导出成功')
      } catch (error) {
        console.error('导出失败:', error)
        this.$message.error('导出失败，请重试')
      }
    },
    getFullImageUrl(url) {
      return getImageUrl(url)
    },
    handleImageError(e) {
      handleImageError(e)
    },
    getImageType(type) {
      const types = {
        'BRAIN_TUMOR': '脑肿瘤诊断',
        'COVID': '新冠肺炎诊断',
        'OTHER': '其他检查'
      }
      return types[type] || type
    }
  },
  created() {
    this.fetchReports()
  }
}
</script>

<style scoped>
.report-management {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.report-detail {
  padding: 20px;
}

.report-detail h3 {
  margin: 20px 0 10px;
  color: #303133;
}

.report-detail p {
  margin: 10px 0;
  line-height: 1.6;
  color: #606266;
}

.image-container {
  margin: 20px 0;
  text-align: center;
}

.image-container img {
  max-width: 100%;
  max-height: 400px;
  object-fit: contain;
}

.image-slot {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  background: #f5f7fa;
  color: #909399;
}
</style> 