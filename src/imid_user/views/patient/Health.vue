<template>
  <div class="health-management">
    <el-tabs v-model="activeTab" type="border-card">
      <!-- 健康记录标签页 -->
      <el-tab-pane label="健康记录" name="records">
        <el-card>
          <div slot="header" class="clearfix">
            <span>健康记录</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="showAddRecordDialog">
              添加记录
            </el-button>
          </div>

          <el-table :data="healthRecords" style="width: 100%">
            <el-table-column prop="recordTime" label="记录时间" width="180">
              <template #default="scope">
                {{ formatDateTime(scope.row.recordTime) }}
              </template>
            </el-table-column>
            <el-table-column label="血压" width="150">
              <template #default="scope">
                {{ scope.row.systolicPressure }}/{{ scope.row.diastolicPressure }} mmHg
              </template>
            </el-table-column>
            <el-table-column prop="heartRate" label="心率" width="120">
              <template #default="scope">
                {{ scope.row.heartRate }} 次/分
              </template>
            </el-table-column>
            <el-table-column prop="bloodSugar" label="血糖" width="120">
              <template #default="scope">
                {{ scope.row.bloodSugar }} mmol/L
              </template>
            </el-table-column>
            <el-table-column prop="temperature" label="体温" width="120">
              <template #default="scope">
                {{ scope.row.temperature }} ℃
              </template>
            </el-table-column>
            <el-table-column prop="weight" label="体重" width="120">
              <template #default="scope">
                {{ scope.row.weight }} kg
              </template>
            </el-table-column>
            <el-table-column prop="symptoms" label="症状" />
            <el-table-column prop="medications" label="用药" />
            <el-table-column prop="notes" label="备注" />
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="scope">
                <el-button type="text" @click="editRecord(scope.row)">编辑</el-button>
                <el-button type="text" class="delete-btn" @click="deleteRecord(scope.row)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <!-- 健康统计标签页 -->
      <el-tab-pane label="健康统计" name="stats">
        <el-row :gutter="20">
          <!-- 血压图表 -->
          <el-col :span="12">
            <el-card class="chart-card">
              <div slot="header">血压趋势</div>
              <div class="chart-container">
                <div ref="bloodPressureChart" style="width: 100%; height: 300px"></div>
              </div>
            </el-card>
          </el-col>

          <!-- 心率图表 -->
          <el-col :span="12">
            <el-card class="chart-card">
              <div slot="header">心率趋势</div>
              <div class="chart-container">
                <div ref="heartRateChart" style="width: 100%; height: 300px"></div>
              </div>
            </el-card>
          </el-col>

          <!-- 血糖图表 -->
          <el-col :span="12">
            <el-card class="chart-card">
              <div slot="header">血糖趋势</div>
              <div class="chart-container">
                <div ref="bloodSugarChart" style="width: 100%; height: 300px"></div>
              </div>
            </el-card>
          </el-col>

          <!-- 体温图表 -->
          <el-col :span="12">
            <el-card class="chart-card">
              <div slot="header">体温趋势</div>
              <div class="chart-container">
                <div ref="temperatureChart" style="width: 100%; height: 300px"></div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>
    </el-tabs>

    <!-- 添加/编辑记录对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="50%"
    >
      <el-form ref="recordForm" :model="recordForm" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="收缩压" prop="systolicPressure">
              <el-input-number
                v-model="recordForm.systolicPressure"
                :min="0"
                :max="300"
                controls-position="right"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="舒张压" prop="diastolicPressure">
              <el-input-number
                v-model="recordForm.diastolicPressure"
                :min="0"
                :max="200"
                controls-position="right"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="心率" prop="heartRate">
              <el-input-number
                v-model="recordForm.heartRate"
                :min="0"
                :max="200"
                controls-position="right"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="血糖" prop="bloodSugar">
              <el-input-number
                v-model="recordForm.bloodSugar"
                :min="0"
                :max="30"
                :precision="1"
                controls-position="right"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="体温" prop="temperature">
              <el-input-number
                v-model="recordForm.temperature"
                :min="35"
                :max="42"
                :precision="1"
                controls-position="right"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="体重" prop="weight">
              <el-input-number
                v-model="recordForm.weight"
                :min="0"
                :max="200"
                :precision="1"
                controls-position="right"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="症状" prop="symptoms">
          <el-input
            type="textarea"
            v-model="recordForm.symptoms"
            :rows="2"
            placeholder="请描述您的症状"
          />
        </el-form-item>

        <el-form-item label="用药情况" prop="medications">
          <el-input
            type="textarea"
            v-model="recordForm.medications"
            :rows="2"
            placeholder="请记录用药情况"
          />
        </el-form-item>

        <el-form-item label="备注" prop="notes">
          <el-input
            type="textarea"
            v-model="recordForm.notes"
            :rows="2"
            placeholder="其他需要记录的信息"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRecord">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import request from '@/utils/request'
import { formatDate, formatDateTime } from '@/utils/format'
import { getHealthRecords, getHealthStats, createHealthRecord, updateHealthRecord, deleteHealthRecord } from '@/api/health'

export default {
  name: 'Health',
  data() {
    return {
      activeTab: 'records',
      healthRecords: [],
      healthStats: {},
      charts: {},
      dialogVisible: false,
      dialogType: 'add',
      recordForm: this.getInitialRecordForm(),
      rules: {
        systolicPressure: [{ required: true, message: '请输入收缩压', trigger: 'blur' }],
        diastolicPressure: [{ required: true, message: '请输入舒张压', trigger: 'blur' }],
        heartRate: [{ required: true, message: '请输入心率', trigger: 'blur' }],
        bloodSugar: [{ required: true, message: '请输入血糖', trigger: 'blur' }],
        temperature: [{ required: true, message: '请输入体温', trigger: 'blur' }],
        weight: [{ required: true, message: '请输入体重', trigger: 'blur' }]
      }
    }
  },
  computed: {
    dialogTitle() {
      return this.dialogType === 'add' ? '添加健康记录' : '编辑健康记录'
    }
  },
  watch: {
    activeTab(newVal) {
      if (newVal === 'stats') {
        this.$nextTick(() => {
          this.initCharts()
          this.fetchStats()
        })
      }
    }
  },
  mounted() {
    this.fetchRecords()
    if (this.activeTab === 'stats') {
      this.$nextTick(() => {
        this.initCharts()
        this.fetchStats()
      })
    }
  },
  beforeDestroy() {
    // 销毁图表实例
    Object.values(this.charts).forEach(chart => {
      if (chart) {
        chart.dispose()
      }
    })
  },
  methods: {
    formatDate,
    formatDateTime,
    initCharts() {
      // 初始化血压图表
      this.charts.bloodPressure = echarts.init(this.$refs.bloodPressureChart)
      // 初始化心率图表
      this.charts.heartRate = echarts.init(this.$refs.heartRateChart)
      // 初始化血糖图表
      this.charts.bloodSugar = echarts.init(this.$refs.bloodSugarChart)
      // 初始化体温图表
      this.charts.temperature = echarts.init(this.$refs.temperatureChart)
    },
    async fetchStats() {
      try {
        const { data } = await getHealthStats()
        this.healthStats = data
        this.updateCharts()
      } catch (error) {
        console.error('获取健康统计数据失败:', error)
        this.$message.error('获取健康统计数据失败')
      }
    },
    updateCharts() {
      // 血压图表
      if (this.charts.bloodPressure) {
        this.charts.bloodPressure.setOption({
          title: { text: '血压趋势' },
          tooltip: { 
            trigger: 'axis',
            formatter: function(params) {
              return `${params[0].axisValue}<br/>
                     收缩压: ${params[0].data} mmHg<br/>
                     舒张压: ${params[1].data} mmHg`
            }
          },
          legend: {
            data: ['收缩压', '舒张压']
          },
          xAxis: {
            type: 'category',
            data: this.healthStats.bloodPressureStats?.map(item => formatDate(item.date)) || []
          },
          yAxis: { 
            type: 'value',
            name: 'mmHg',
            min: function(value) {
              return Math.floor(value.min - 10);
            }
          },
          series: [
            {
              name: '收缩压',
              type: 'line',
              data: this.healthStats.bloodPressureStats?.map(item => item.value) || [],
              symbol: 'circle',
              symbolSize: 6
            },
            {
              name: '舒张压',
              type: 'line',
              data: this.healthStats.bloodPressureStats?.map(item => item.secondaryValue) || [],
              symbol: 'circle',
              symbolSize: 6
            }
          ]
        })
      }

      // 心率图表
      if (this.charts.heartRate) {
        this.charts.heartRate.setOption({
          title: { text: '心率趋势' },
          tooltip: { trigger: 'axis' },
          xAxis: {
            type: 'category',
            data: this.healthStats.heartRateStats?.map(item => formatDate(item.date)) || []
          },
          yAxis: { 
            type: 'value',
            name: '次/分',
            min: function(value) {
              return Math.floor(value.min - 5);
            }
          },
          series: [{
            name: '心率',
            type: 'line',
            data: this.healthStats.heartRateStats?.map(item => item.value) || [],
            symbol: 'circle',
            symbolSize: 6
          }]
        })
      }

      // 血糖图表
      if (this.charts.bloodSugar) {
        this.charts.bloodSugar.setOption({
          title: { text: '血糖趋势' },
          tooltip: { trigger: 'axis' },
          xAxis: {
            type: 'category',
            data: this.healthStats.bloodSugarStats?.map(item => item.date) || []
          },
          yAxis: { 
            type: 'value',
            name: 'mmol/L'
          },
          series: [{
            name: '血糖',
            type: 'line',
            data: this.healthStats.bloodSugarStats?.map(item => item.value) || []
          }]
        })
      }

      // 体温图表
      if (this.charts.temperature) {
        this.charts.temperature.setOption({
          title: { text: '体温趋势' },
          tooltip: { trigger: 'axis' },
          xAxis: {
            type: 'category',
            data: this.healthStats.temperatureStats?.map(item => item.date) || []
          },
          yAxis: { 
            type: 'value',
            name: '℃'
          },
          series: [{
            name: '体温',
            type: 'line',
            data: this.healthStats.temperatureStats?.map(item => item.value) || []
          }]
        })
      }
    },
    showAddRecordDialog() {
      this.dialogType = 'add'
      this.recordForm = this.getInitialRecordForm()
      this.dialogVisible = true
    },
    editRecord(record) {
      this.dialogType = 'edit'
      this.recordForm = { ...record }
      this.dialogVisible = true
    },
    async submitRecord() {
      try {
        await this.$refs.recordForm.validate()
        if (this.dialogType === 'add') {
          await createHealthRecord(this.recordForm)
          this.$message.success('添加记录成功')
        } else {
          await updateHealthRecord(this.recordForm.id, this.recordForm)
          this.$message.success('更新记录成功')
        }
        this.dialogVisible = false
        await this.fetchRecords()
        await this.fetchStats()
      } catch (error) {
        console.error('提交记录失败:', error)
        this.$message.error('提交记录失败')
      }
    },
    async deleteRecord(record) {
      try {
        await this.$confirm('确定要删除这条记录吗？', '提示', {
          type: 'warning'
        })
        await deleteHealthRecord(record.id)
        this.$message.success('删除成功')
        this.fetchRecords()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除记录失败:', error)
          this.$message.error('删除记录失败')
        }
      }
    },
    getInitialRecordForm() {
      return {
        systolicPressure: null,
        diastolicPressure: null,
        heartRate: null,
        bloodSugar: null,
        temperature: 36.5,
        weight: null,
        symptoms: '',
        medications: '',
        notes: ''
      }
    },
    async fetchRecords() {
      try {
        const { data } = await getHealthRecords()
        this.healthRecords = data
      } catch (error) {
        console.error('获取健康记录失败:', error)
        this.$message.error('获取健康记录失败')
      }
    }
  }
}
</script>

<style scoped>
.health-management {
  padding: 20px;
}
.chart-card {
  margin-bottom: 20px;
}
.chart-container {
  margin: 20px 0;
}
.delete-btn {
  color: #F56C6C;
}
.el-input-number {
  width: 100%;
}
</style> 