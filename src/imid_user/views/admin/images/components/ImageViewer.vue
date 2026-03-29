<template>
  <div class="image-viewer">
    <el-row :gutter="20">
      <!-- 左侧影像展示区 -->
      <el-col :span="12">
        <el-card class="image-card" shadow="never">
          <div slot="header">
            <span>医学影像</span>
          </div>
          <div class="image-container">
            <el-image
              :src="getImageUrl(imageInfo.imageUrl)"
              :preview-src-list="[getImageUrl(imageInfo.imageUrl)]"
              fit="contain"
              @error="handleImageError"
            >
              <div slot="error" class="image-slot">
                <i class="el-icon-picture-outline"></i>
                <span>加载失败</span>
              </div>
            </el-image>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧信息展示区 -->
      <el-col :span="12">
        <el-card shadow="never">
          <div slot="header">
            <span>诊断信息</span>
          </div>
          
          <el-descriptions :column="1" border>
            <el-descriptions-item label="患者姓名">
              {{ imageInfo.patientName }}
            </el-descriptions-item>
            <el-descriptions-item label="影像类型">
              {{ imageInfo.imageType }}
            </el-descriptions-item>
            <el-descriptions-item label="上传时间">
              {{ formatDateTime(imageInfo.createdAt) }}
            </el-descriptions-item>
            <el-descriptions-item label="AI诊断结果">
              {{ imageInfo.aiDiagnosis }}
              <template v-if="imageInfo.confidenceScore">
                <el-progress 
                  :percentage="Math.round(imageInfo.confidenceScore * 100)"
                  :color="getConfidenceColor"
                />
              </template>
            </el-descriptions-item>
            <el-descriptions-item label="医生诊断">
              {{ imageInfo.doctorDiagnosis || '暂无' }}
            </el-descriptions-item>
            <el-descriptions-item label="主治医生">
              {{ imageInfo.doctorName || '暂无' }}
            </el-descriptions-item>
            <el-descriptions-item label="治疗方案">
              {{ imageInfo.treatmentPlan || '暂无' }}
            </el-descriptions-item>
            <el-descriptions-item label="医生建议">
              {{ imageInfo.recommendations || '暂无' }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { formatDateTime } from '@/utils/format'
import { getImageUrl, handleImageError } from '@/utils/image'

export default {
  name: 'ImageViewer',
  
  props: {
    imageInfo: {
      type: Object,
      required: true
    }
  },

  methods: {
    formatDateTime,
    getImageUrl,
    handleImageError,

    getConfidenceColor(percentage) {
      if (percentage > 80) return '#67C23A'
      if (percentage > 60) return '#E6A23C'
      return '#F56C6C'
    }
  }
}
</script>

<style lang="scss" scoped>
.image-viewer {
  padding: 20px;

  .image-container {
    width: 100%;
    height: 400px;
    display: flex;
    justify-content: center;
    align-items: center;
    background: #f5f7fa;

    .el-image {
      max-width: 100%;
      max-height: 100%;
    }
  }

  .image-slot {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
    color: #909399;

    i {
      font-size: 28px;
      margin-bottom: 10px;
    }
  }

  ::v-deep .el-descriptions-item__label {
    width: 120px;
    background-color: #fafafa;
  }

  ::v-deep .el-descriptions-item__content {
    padding: 12px 15px;
  }

  .el-progress {
    margin: 8px 0;
  }
}
</style> 