<template>
  <div class="report-form">
    <el-form
      ref="form"
      :model="form"
      :rules="rules"
      label-width="120px"
      :disabled="readonly"
    >
      <el-form-item label="患者信息">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="姓名">
            {{ form.patientName }}
          </el-descriptions-item>
          <el-descriptions-item label="ID">
            {{ form.patientId }}
          </el-descriptions-item>
        </el-descriptions>
      </el-form-item>

      <el-form-item label="影像类型">
        {{ form.imageType }}
      </el-form-item>

      <el-form-item label="AI分析结果">
        <el-input
          v-model="form.aiAnalysis"
          type="textarea"
          :rows="4"
          readonly
        />
      </el-form-item>

      <el-form-item label="诊断结果" prop="diagnosis">
        <el-input
          v-model="form.diagnosis"
          type="textarea"
          :rows="4"
          placeholder="请输入诊断结果"
        />
      </el-form-item>

      <el-form-item label="治疗建议" prop="treatment">
        <el-input
          v-model="form.treatment"
          type="textarea"
          :rows="4"
          placeholder="请输入治疗建议"
        />
      </el-form-item>

      <el-form-item label="备注">
        <el-input
          v-model="form.remarks"
          type="textarea"
          :rows="3"
          placeholder="请输入备注信息"
        />
      </el-form-item>

      <el-form-item v-if="!readonly">
        <el-button type="primary" @click="handleSubmit">保存</el-button>
        <el-button @click="handleCancel">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  name: 'ReportForm',
  props: {
    report: {
      type: Object,
      required: true
    },
    readonly: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      form: {
        patientName: '',
        patientId: '',
        imageType: '',
        aiAnalysis: '',
        diagnosis: '',
        treatment: '',
        remarks: ''
      },
      rules: {
        diagnosis: [
          { required: true, message: '请输入诊断结果', trigger: 'blur' }
        ],
        treatment: [
          { required: true, message: '请输入治疗建议', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    report: {
      handler(val) {
        this.form = { ...val }
      },
      immediate: true
    }
  },
  methods: {
    handleSubmit() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.$emit('save', this.form)
        }
      })
    },
    handleCancel() {
      this.$emit('cancel')
    }
  }
}
</script>

<style lang="scss" scoped>
.report-form {
  padding: 20px;
}
</style> 