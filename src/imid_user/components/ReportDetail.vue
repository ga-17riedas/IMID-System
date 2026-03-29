<template>
  <div class="report-detail">
    <div class="report-header">
      <h3>基本信息</h3>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="报告编号">
          {{ report.id }}
        </el-descriptions-item>
        <el-descriptions-item label="患者姓名">
          {{ report.patientName }}
        </el-descriptions-item>
        <el-descriptions-item label="检查类型">
          {{ getImageType(report.imageType) }}
        </el-descriptions-item>
        <el-descriptions-item label="主治医师">
          {{ report.doctor ? report.doctor.fullName + ' ' + (report.doctor.professionalTitle || '') : '暂无' }}
        </el-descriptions-item>
        <el-descriptions-item label="科室">
          {{ report.doctor ? report.doctor.department : '暂无' }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ formatDateTime(report.createdAt) }}
        </el-descriptions-item>
      </el-descriptions>
    </div>

    <div class="report-image" v-if="report.imageUrl">
      <h3>医学影像</h3>
      <el-image 
        :src="getImageUrl(report.imageUrl)"
        :preview-src-list="[getImageUrl(report.imageUrl)]"
        fit="contain"
        @error="handleImageError"
      >
        <div slot="error" class="image-slot">
          <i class="el-icon-picture-outline"></i>
          <span>加载失败</span>
        </div>
      </el-image>
    </div>

    <div class="diagnosis-section" v-if="report.diagnosis || report.aiDiagnosis">
      <h3>诊断结果</h3>
      <el-card shadow="never" class="diagnosis-card">
        <div v-if="report.aiDiagnosis" class="ai-diagnosis">
          <h4><i class="el-icon-cpu"></i> AI辅助诊断</h4>
          <p>{{ report.aiDiagnosis }}</p>
          <el-progress 
            v-if="report.confidenceScore"
            :percentage="Math.round(report.confidenceScore * 100)"
            :color="getConfidenceColor(report.confidenceScore * 100)"
            :format="percent => percent + '%'"
            class="confidence-bar"
          ></el-progress>
        </div>
        
        <div v-if="report.diagnosis" class="doctor-diagnosis">
          <h4><i class="el-icon-user"></i> 医生诊断</h4>
          <p>{{ report.diagnosis }}</p>
        </div>
      </el-card>
    </div>

    <div class="treatment-section" v-if="report.treatmentPlan">
      <h3>治疗方案</h3>
      <el-card shadow="never" class="treatment-card">
        <p>{{ report.treatmentPlan }}</p>
      </el-card>
    </div>

    <div class="recommendations-section" v-if="report.recommendations">
      <h3>医生建议</h3>
      <el-card shadow="never" class="recommendations-card">
        <p>{{ report.recommendations }}</p>
      </el-card>
    </div>

    <div class="dialog-footer" style="text-align: right; margin-top: 20px;">
      <el-button type="primary" @click="downloadPdf">下载PDF</el-button>
    </div>
  </div>
</template>

<script>
import { formatDateTime } from '@/utils/format'
import { getImageUrl, handleImageError as imageErrorHandler } from '@/utils/image'

export default {
  name: 'ReportDetail',
  props: {
    report: {
      type: Object,
      required: true
    }
  },
  methods: {
    formatDateTime,
    getImageUrl,
    handleImageError(e) {
      console.error('Image load error:', e)
      imageErrorHandler(e)
    },
    getImageType(type) {
      const types = {
        'BRAIN_TUMOR': '脑肿瘤检查',
        'COVID': '新冠肺炎检查',
        'LUNG': '肺部检查',
        'XRAY': 'X光检查',
        'CT': 'CT扫描',
        'MRI': '核磁共振',
        'OTHER': '其他检查'
      }
      return types[type] || type
    },
    getConfidenceColor(percentage) {
      if (percentage > 80) return '#67C23A'
      if (percentage > 60) return '#E6A23C'
      return '#F56C6C'
    },
    async downloadPdf() {
      try {
        const response = await this.$http.get(`/api/patient/medical/reports/${this.report.id}/pdf`, {
          responseType: 'blob'
        })
        const blob = new Blob([response.data], { type: 'application/pdf' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `诊断报告_${this.report.id}.pdf`
        link.click()
        window.URL.revokeObjectURL(url)
      } catch (error) {
        console.error('下载PDF失败:', error)
        this.$message.error('下载PDF失败')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.report-detail {
  padding: 20px;

  .report-header {
    margin-bottom: 20px;
  }

  h3 {
    margin: 25px 0 15px;
    color: #303133;
    font-weight: 600;
  }

  h4 {
    margin: 0 0 10px;
    color: #606266;
    font-size: 16px;
    
    i {
      margin-right: 5px;
    }
  }

  .report-image {
    margin: 20px 0;
    text-align: center;
    
    .el-image {
      max-width: 100%;
      max-height: 400px;
    }
  }

  .image-slot {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 200px;
    background: #f5f7fa;
    color: #909399;

    i {
      font-size: 28px;
      margin-bottom: 10px;
    }
  }
  
  .diagnosis-card, .treatment-card, .recommendations-card {
    margin-bottom: 15px;
    
    p {
      margin: 10px 0;
      line-height: 1.6;
      color: #606266;
      white-space: pre-line;
    }
  }
  
  .ai-diagnosis {
    margin-bottom: 20px;
    padding-bottom: 15px;
    border-bottom: 1px dashed #ebeef5;
  }
  
  .confidence-bar {
    margin-top: 10px;
  }
}
</style> 