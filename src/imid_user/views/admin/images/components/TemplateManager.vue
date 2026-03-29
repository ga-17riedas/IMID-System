<template>
  <div class="template-manager">
    <div class="template-list">
      <el-button type="primary" @click="handleAddTemplate">新增模板</el-button>
      <el-table
        :data="templates"
        style="width: 100%"
      >
        <el-table-column prop="name" label="模板名称" />
        <el-table-column prop="type" label="适用类型" />
        <el-table-column prop="createTime" label="创建时间">
          <template slot-scope="{row}">
            {{ row.createTime | parseTime('{y}-{m}-{d}') }}
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="{row}">
            <el-button
              size="mini"
              type="primary"
              @click="handleEditTemplate(row)"
            >
              编辑
            </el-button>
            <el-button
              size="mini"
              type="danger"
              @click="handleDeleteTemplate(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog
      :title="dialogType === 'add' ? '新增模板' : '编辑模板'"
      :visible.sync="dialogVisible"
    >
      <el-form
        ref="form"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="模板名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="适用类型" prop="type">
          <el-select v-model="form.type">
            <el-option
              v-for="item in typeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="诊断模板" prop="diagnosisTemplate">
          <el-input
            v-model="form.diagnosisTemplate"
            type="textarea"
            :rows="4"
          />
        </el-form-item>
        <el-form-item label="治疗模板" prop="treatmentTemplate">
          <el-input
            v-model="form.treatmentTemplate"
            type="textarea"
            :rows="4"
          />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getTemplates, createTemplate, updateTemplate, deleteTemplate } from '@/api/admin'

export default {
  name: 'TemplateManager',
  data() {
    return {
      templates: [],
      dialogVisible: false,
      dialogType: 'add',
      form: {
        name: '',
        type: '',
        diagnosisTemplate: '',
        treatmentTemplate: ''
      },
      rules: {
        name: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
        type: [{ required: true, message: '请选择适用类型', trigger: 'change' }],
        diagnosisTemplate: [{ required: true, message: '请输入诊断模板', trigger: 'blur' }],
        treatmentTemplate: [{ required: true, message: '请输入治疗模板', trigger: 'blur' }]
      },
      typeOptions: [
        { label: 'CT', value: 'CT' },
        { label: 'MRI', value: 'MRI' },
        { label: 'X光', value: 'X-RAY' }
      ]
    }
  },
  created() {
    this.getTemplates()
  },
  methods: {
    async getTemplates() {
      try {
        const { data } = await getTemplates()
        this.templates = data
      } catch (error) {
        console.error('获取模板列表失败:', error)
        this.$message.error('获取模板列表失败')
      }
    },
    handleAddTemplate() {
      this.dialogType = 'add'
      this.form = {
        name: '',
        type: '',
        diagnosisTemplate: '',
        treatmentTemplate: ''
      }
      this.dialogVisible = true
    },
    handleEditTemplate(row) {
      this.dialogType = 'edit'
      this.form = { ...row }
      this.dialogVisible = true
    },
    async handleDeleteTemplate(row) {
      try {
        await this.$confirm('确认删除该模板?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await deleteTemplate(row.id)
        this.$message.success('删除成功')
        this.getTemplates()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除模板失败:', error)
          this.$message.error('删除模板失败')
        }
      }
    },
    async handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          try {
            if (this.dialogType === 'add') {
              await createTemplate(this.form)
            } else {
              await updateTemplate(this.form.id, this.form)
            }
            this.$message.success('保存成功')
            this.dialogVisible = false
            this.getTemplates()
          } catch (error) {
            console.error('保存模板失败:', error)
            this.$message.error('保存模板失败')
          }
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.template-manager {
  padding: 20px;
  
  .template-list {
    margin-bottom: 20px;
  }
}
</style> 