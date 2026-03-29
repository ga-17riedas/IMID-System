<template>
  <div class="image-analysis">
    <el-card>
      <div slot="header">
        <span>医学影像分析</span>
      </div>
      
      <el-form ref="form" :model="form" label-width="120px">
        <!-- 步骤导航 -->
        <el-steps :active="currentStep" finish-status="success" simple style="margin-bottom: 20px;">
          <el-step title="选择患者"></el-step>
          <el-step title="上传图像"></el-step>
          <el-step title="分析结果"></el-step>
        </el-steps>

        <!-- 步骤1：选择患者 -->
        <div v-show="currentStep === 0">
          <el-form-item label="选择患者" prop="patientId" required>
            <el-select v-model="form.patientId" placeholder="请选择患者" style="width: 100%;">
              <el-option
                v-for="patient in patients"
                :key="patient.id"
                :label="patient.fullName"
                :value="patient.id">
              </el-option>
            </el-select>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="nextStep" :disabled="!form.patientId">下一步</el-button>
          </el-form-item>
        </div>

        <!-- 步骤2：上传图像 -->
        <div v-show="currentStep === 1">
          <el-form-item label="分析类型" prop="analysisType">
            <el-radio-group v-model="form.analysisType">
              <el-radio label="BRAIN_TUMOR">脑肿瘤诊断</el-radio>
              <el-radio label="COVID">新冠肺炎诊断</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="上传图片">
            <el-upload
              class="image-uploader"
              :action="`${baseUrl}/api/medical/upload`"
              :headers="uploadHeaders"
              :show-file-list="false"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              :before-upload="beforeUpload">
              <img v-if="imageUrl" :src="getFullImageUrl(imageUrl)" class="uploaded-image">
              <i v-else class="el-icon-plus image-uploader-icon"></i>
            </el-upload>
            <div class="upload-tip">支持jpg、png格式，文件大小不超过10MB</div>
          </el-form-item>

          <el-form-item>
            <el-button @click="prevStep">上一步</el-button>
            <el-button type="primary" @click="analyze" :loading="analyzing" :disabled="!imageUrl">
              开始分析
            </el-button>
            <el-button @click="resetForm">重置</el-button>
          </el-form-item>
        </div>

        <!-- 步骤3：分析结果 -->
        <div v-show="currentStep === 2 && result">
          <div class="analysis-report">
            <div class="result-content">
              <div class="result-images">
                <div class="original-image">
                  <h4>原始图像</h4>
                  <img :src="getFullImageUrl(imageUrl)" class="result-image"/>
                </div>
                <div class="analyzed-image">
                  <h4>分析结果图像</h4>
                  <div class="image-container">
                    <img :src="getFullImageUrl(result.analyzedImageUrl || imageUrl)" class="result-image" ref="resultImage" @load="drawBoundingBoxes"/>
                    <canvas v-if="!result.analyzedImageUrl && result.boxes && result.boxes.length > 0" ref="boundingBoxCanvas" class="bounding-box-canvas"></canvas>
                  </div>
                </div>
              </div>
              
              <div class="result-details">
                <el-descriptions title="诊断详情" :column="2" border>
                  <el-descriptions-item label="患者姓名">
                    {{ selectedPatientName }}
                  </el-descriptions-item>
                  <el-descriptions-item label="诊断类型">
                    {{ form.analysisType === 'BRAIN_TUMOR' ? '脑肿瘤诊断' : '新冠肺炎诊断' }}
                  </el-descriptions-item>
                  <el-descriptions-item label="检测结果">
                    {{ result.detected ? '发现异常' : '未发现异常' }}
                  </el-descriptions-item>
                  <el-descriptions-item label="置信度">
                    {{ (result.confidence * 100).toFixed(2) }}%
                  </el-descriptions-item>
                  <el-descriptions-item label="分析时间" :span="2">
                    {{ result.analysisTime }}
                  </el-descriptions-item>
                </el-descriptions>

                <div class="diagnosis-suggestion" v-if="result.suggestion">
                  <h4>诊断建议</h4>
                  <p>{{ result.suggestion }}</p>
                </div>
                
                <div v-if="result.boxes && result.boxes.length > 0" class="lesion-boxes">
                  <h4>检测到的病灶</h4>
                  <el-table :data="result.boxes" stripe style="width: 100%">
                    <el-table-column prop="label" label="类型"></el-table-column>
                    <el-table-column prop="confidence" label="置信度">
                      <template slot-scope="scope">
                        {{ (scope.row.confidence * 100).toFixed(2) }}%
                      </template>
                    </el-table-column>
                    <el-table-column label="位置信息">
                      <template slot-scope="scope">
                        x1: {{ scope.row.x1.toFixed(0) }}, y1: {{ scope.row.y1.toFixed(0) }}, 
                        x2: {{ scope.row.x2.toFixed(0) }}, y2: {{ scope.row.y2.toFixed(0) }}
                      </template>
                    </el-table-column>
                  </el-table>
                </div>
              </div>
            </div>

            <!-- 报告生成表单 -->
            <el-form ref="reportForm" :model="reportForm" :rules="reportRules" label-width="100px" class="report-form">
              <el-form-item label="诊断结果" prop="diagnosis">
                <el-input
                  type="textarea"
                  v-model="reportForm.diagnosis"
                  :rows="3"
                  placeholder="请输入诊断结果">
                </el-input>
              </el-form-item>

              <el-form-item label="治疗方案" prop="treatmentPlan">
                <el-input
                  type="textarea"
                  v-model="reportForm.treatmentPlan"
                  :rows="3"
                  placeholder="请输入治疗方案">
                </el-input>
              </el-form-item>

              <el-form-item label="医生建议" prop="recommendations">
                <el-input
                  type="textarea"
                  v-model="reportForm.recommendations"
                  :rows="3"
                  placeholder="请输入医生建议">
                </el-input>
              </el-form-item>

              <el-form-item>
                <el-button @click="prevStep">上一步</el-button>
                <el-button type="primary" @click="saveReport">保存报告</el-button>
                <el-button @click="resetAll">完成并重置</el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { analyzeImage } from '@/api/medical'
import request from '@/utils/request'
import { getToken } from '@/utils/auth'
import { getImageUrl, handleImageError } from '@/utils/image'

/**
 * 医学影像分析组件
 * 提供医学影像上传、AI分析和诊断报告生成功能
 */
export default {
  name: 'ImageAnalysis',
  data() {
    return {
      // 基础配置
      baseUrl: process.env.VUE_APP_BASE_API,
      // 图像数据
      imageUrl: '',
      // 表单数据
      form: {
        analysisType: 'BRAIN_TUMOR', // 默认分析类型：脑肿瘤
        patientId: ''
      },
      // 状态控制
      analyzing: false,
      // 患者列表
      patients: [],
      // 报告表单
      reportForm: {
        patientId: '',
        diagnosis: '',
        treatmentPlan: '',
        recommendations: ''
      },
      // 报告表单验证规则
      reportRules: {
        diagnosis: [
          { required: true, message: '请输入诊断结果', trigger: 'blur' }
        ],
        treatmentPlan: [
          { required: true, message: '请输入治疗方案', trigger: 'blur' }
        ],
        recommendations: [
          { required: true, message: '请输入医生建议', trigger: 'blur' }
        ]
      },
      // 分析结果
      result: {
        detected: false,
        confidence: 0,
        suggestion: '',
        analyzedImageUrl: '',
        analysisTime: null
      },
      // 步骤控制
      currentStep: 0,
      // 选中的患者姓名
      selectedPatientName: ''
    }
  },
  computed: {
    /**
     * 生成上传所需的请求头
     * 包含Bearer认证令牌
     */
    uploadHeaders() {
      return {
        'Authorization': 'Bearer ' + getToken()
      }
    }
  },
  methods: {
    /**
     * 图像上传前的验证
     * 检查文件类型和大小是否符合要求
     * @param {File} file - 待上传的文件对象
     * @returns {boolean} 是否通过验证
     */
    beforeUpload(file) {
      const isJPGOrPNG = file.type === 'image/jpeg' || file.type === 'image/png'
      const isLt10M = file.size / 1024 / 1024 < 10

      if (!isJPGOrPNG) {
        this.$message.error('只能上传 JPG/PNG 格式的图片!')
        return false
      }
      if (!isLt10M) {
        this.$message.error('图片大小不能超过 10MB!')
        return false
      }
      return true
    },
    
    /**
     * 获取完整的图片URL
     * @param {string} url - 相对URL
     * @returns {string} 完整的图片URL
     */
    getFullImageUrl(url) {
      return getImageUrl(url);
    },

    /**
     * 处理图片上传成功事件
     * @param {Object} response - 上传响应
     */
    handleUploadSuccess(response) {
      console.log('Upload success:', response);
      if (response.url) {
        this.imageUrl = response.url;
        this.$message.success('上传成功');
      } else {
        this.$message.error('上传失败：返回数据格式错误');
        console.error('Invalid response:', response);
      }
    },

    handleUploadError(err, file) {
      console.error('Upload error:', err);
      let errorMessage = '上传失败';
      if (err.response) {
        errorMessage += ': ' + (err.response.data?.message || err.response.statusText);
      } else if (err.message) {
        errorMessage += ': ' + err.message;
      }
      this.$message.error(errorMessage);
    },

    /**
     * 开始分析医学影像
     * 调用API进行AI辅助诊断
     */
    async analyze() {
      if (!this.imageUrl) {
        this.$message.warning('请先上传图片');
        return;
      }

      this.analyzing = true;
      try {
        // 调用分析API
        const response = await analyzeImage({
          imageUrl: this.imageUrl,
          analysisType: this.form.analysisType
        });
        this.result = response.data;
        this.$message.success('分析完成');

        // 自动预填充诊断建议到报告中
        this.reportForm.diagnosis = this.result.suggestion || '';
        
        // 设置报告表单中的患者ID
        this.reportForm.patientId = this.form.patientId;
        
        // 获取选定患者的姓名
        this.updateSelectedPatientName();
        
        // 进入下一步
        this.currentStep = 2;
        
        // 等待DOM更新后绘制边界框
        this.$nextTick(() => {
          if (this.$refs.resultImage && this.$refs.resultImage.complete) {
            this.drawBoundingBoxes();
          }
        });
      } catch (error) {
        console.error('Analysis failed:', error);
        this.$message.error('分析失败：' + (error.response?.data?.message || error.message));
      } finally {
        this.analyzing = false;
      }
    },
    
    resetForm() {
      this.form.analysisType = 'BRAIN_TUMOR';
      this.imageUrl = '';
      this.result = {
        detected: false,
        confidence: 0,
        suggestion: '',
        analyzedImageUrl: '',
        analysisTime: null
      };
    },
    
    async loadPatients() {
      try {
        const response = await this.$http.get('/api/doctor/patients')
        this.patients = response.data
      } catch (error) {
        this.$message.error('获取患者列表失败')
        console.error(error)
      }
    },
    
    updateSelectedPatientName() {
      const selectedPatient = this.patients.find(p => p.id === this.form.patientId);
      this.selectedPatientName = selectedPatient ? selectedPatient.fullName : '';
    },
    
    nextStep() {
      if (this.currentStep === 0) {
        // 第一步到第二步
        if (!this.form.patientId) {
          this.$message.warning('请先选择患者');
          return;
        }
        this.updateSelectedPatientName();
      }
      this.currentStep++;
    },
    
    prevStep() {
      this.currentStep--;
    },
    
    resetAll() {
      this.resetForm();
      this.reportForm = {
        patientId: this.form.patientId,
        diagnosis: '',
        treatmentPlan: '',
        recommendations: ''
      };
      this.currentStep = 0;
    },
    
    async saveReport() {
      try {
        await this.$refs.reportForm.validate()

        const formData = new FormData()
        formData.append('patientId', this.form.patientId)
        formData.append('diagnosis', this.reportForm.diagnosis || '')
        formData.append('treatmentPlan', this.reportForm.treatmentPlan || '')
        formData.append('recommendations', this.reportForm.recommendations || '')
        
        // 图片处理
        if (this.imageFile) {
          formData.append('image', this.imageFile)
        }
        if (this.imageUrl) {
          formData.append('imageUrl', this.imageUrl)
        }

        // AI 分析结果
        if (this.result) {
          formData.append('aiDiagnosis', this.result.suggestion || '')
          formData.append('confidenceScore', this.result.confidence || 0)
          formData.append('imageType', this.form.analysisType || '')
        }

        const response = await this.$http.post('/api/medical/diagnosis-reports', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
        
        this.$message.success('保存成功')
        this.$router.push('/doctor/reports')
      } catch (error) {
        console.error('保存失败:', error)
        this.$message.error('保存失败，请重试')
      }
    },
    
    drawBoundingBoxes() {
      // 如果已经有标注图像URL，则不需要在前端绘制边界框
      if (this.result.analyzedImageUrl) return;
      
      const resultImage = this.$refs.resultImage;
      const boundingBoxCanvas = this.$refs.boundingBoxCanvas;

      if (!resultImage || !boundingBoxCanvas || !this.result.boxes || this.result.boxes.length === 0) return;

      // 设置Canvas尺寸与图像显示尺寸一致
      boundingBoxCanvas.width = resultImage.clientWidth;
      boundingBoxCanvas.height = resultImage.clientHeight;

      const ctx = boundingBoxCanvas.getContext('2d');
      ctx.clearRect(0, 0, boundingBoxCanvas.width, boundingBoxCanvas.height);

      // 计算原始图像与显示图像的缩放比例
      const scaleX = boundingBoxCanvas.width / resultImage.naturalWidth;
      const scaleY = boundingBoxCanvas.height / resultImage.naturalHeight;

      // 绘制每个边界框
      this.result.boxes.forEach((box) => {
        // 根据缩放比例计算坐标
        const x1 = box.x1 * scaleX;
        const y1 = box.y1 * scaleY;
        const width = (box.x2 - box.x1) * scaleX;
        const height = (box.y2 - box.y1) * scaleY;

        // 绘制边界框
        ctx.beginPath();
        ctx.rect(x1, y1, width, height);
        ctx.strokeStyle = this.getLesionColor(box.label);
        ctx.lineWidth = 2;
        ctx.stroke();

        // 添加标签文本
        ctx.font = '12px Arial';
        ctx.fillStyle = this.getLesionColor(box.label);
        ctx.fillText(
          `${box.label} (${(box.confidence * 100).toFixed(0)}%)`, 
          x1, 
          y1 > 20 ? y1 - 5 : y1 + 20
        );
      });
    },

    // 根据病灶类型获取不同颜色
    getLesionColor(label) {
      const colorMap = {
        'glioma': '#FF0000',    // 胶质瘤 - 红色
        'meningioma': '#00FF00', // 脑膜瘤 - 绿色
        'pituitary': '#0000FF',  // 垂体瘤 - 蓝色
        'covid': '#FF00FF',     // 新冠 - 紫色
        'notumor': '#AAAAAA'    // 无肿瘤 - 灰色
      };
      
      return colorMap[label.toLowerCase()] || '#FF0000';
    },
  },
  created() {
    this.loadPatients()
  }
}
</script>

<style scoped>
.image-analysis {
  padding: 20px;
}

.image-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 300px;
  margin-bottom: 15px;
}

.image-uploader:hover {
  border-color: #409EFF;
}

.image-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 300px;
  height: 200px;
  line-height: 200px;
  text-align: center;
}

.uploaded-image {
  width: 300px;
  height: 200px;
  display: block;
  object-fit: contain;
}

.upload-tip {
  font-size: 12px;
  color: #606266;
  margin-top: 7px;
}

.result-images {
  display: flex;
  justify-content: space-between;
  margin: 20px 0;
}

.original-image,
.analyzed-image {
  flex: 1;
  margin: 0 10px;
  text-align: center;
}

.result-image {
  max-width: 100%;
  max-height: 400px;
  object-fit: contain;
}

.result-details {
  margin: 20px 0;
}

.diagnosis-suggestion {
  margin: 20px 0;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.action-buttons {
  margin-top: 20px;
  text-align: center;
}

.analysis-report {
  margin-top: 20px;
  padding: 20px;
  border-top: 1px solid #eee;
}

.result-content {
  margin: 20px 0;
}

.report-form {
  max-width: 800px;
  margin: 20px auto;
}

.image-container {
  position: relative;
}

.bounding-box-canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.lesion-boxes {
  margin-top: 20px;
}
</style> 