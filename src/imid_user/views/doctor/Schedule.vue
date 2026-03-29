<template>
  <div class="schedule">
    <a-card title="排班管理" :bordered="false">
      <!-- 日历视图 -->
      <a-calendar
        :value="selectedDate"
        @select="onSelect"
        @panelChange="onPanelChange"
      >
        <!-- 自定义日期单元格 -->
        <template slot="dateCellRender" slot-scope="value">
          <div class="schedule-cell">
            <template v-if="hasSchedule(value)">
              <a-tag :color="getScheduleColor(value)">
                {{ getScheduleTime(value) }}
              </a-tag>
            </template>
          </div>
        </template>
      </a-calendar>

      <!-- 排班设置对话框 -->
      <a-modal
        v-model="modalVisible"
        title="排班设置"
        @ok="handleModalOk"
        @cancel="handleModalCancel"
      >
        <a-form :form="form">
          <a-form-item label="日期">
            <a-date-picker
              v-decorator="[
                'date',
                {
                  initialValue: selectedDate,
                  rules: [{ required: true, message: '请选择日期' }]
                }
              ]"
              style="width: 100%"
            />
          </a-form-item>
          <a-form-item label="时间段">
            <a-select
              v-decorator="[
                'timeSlot',
                { rules: [{ required: true, message: '请选择时间段' }] }
              ]"
              style="width: 100%"
            >
              <a-select-option value="morning">上午 (8:00-12:00)</a-select-option>
              <a-select-option value="afternoon">下午 (14:00-18:00)</a-select-option>
              <a-select-option value="evening">晚上 (18:00-21:00)</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="可预约人数">
            <a-input-number
              v-decorator="[
                'maxPatients',
                {
                  initialValue: 10,
                  rules: [{ required: true, message: '请设置可预约人数' }]
                }
              ]"
              :min="1"
              :max="50"
              style="width: 100%"
            />
          </a-form-item>
          <a-form-item label="备注">
            <a-textarea
              v-decorator="['notes']"
              :rows="4"
              placeholder="请输入备注信息（选填）"
            />
          </a-form-item>
        </a-form>
      </a-modal>

      <!-- 排班列表 -->
      <a-card title="排班列表" class="mt-4">
        <a-table
          :columns="columns"
          :data-source="scheduleList"
          :loading="loading"
          rowKey="id"
        >
          <template slot="operation" slot-scope="text, record">
            <a-button-group>
              <a-button type="primary" size="small" @click="editSchedule(record)">
                编辑
              </a-button>
              <a-button type="danger" size="small" @click="deleteSchedule(record)">
                删除
              </a-button>
            </a-button-group>
          </template>
        </a-table>
      </a-card>
    </a-card>
  </div>
</template>

<script>
import moment from 'moment'
import { getToken } from '@/utils/auth'

export default {
  name: 'Schedule',
  data() {
    return {
      selectedDate: moment(),
      modalVisible: false,
      loading: false,
      scheduleList: [],
      form: this.$form.createForm(this),
      columns: [
        {
          title: '日期',
          dataIndex: 'date',
          key: 'date',
          render: (text) => moment(text).format('YYYY-MM-DD')
        },
        {
          title: '时间段',
          dataIndex: 'timeSlot',
          key: 'timeSlot',
          render: (text) => {
            const timeSlots = {
              morning: '上午 (8:00-12:00)',
              afternoon: '下午 (14:00-18:00)',
              evening: '晚上 (18:00-21:00)'
            }
            return timeSlots[text] || text
          }
        },
        {
          title: '可预约人数',
          dataIndex: 'maxPatients',
          key: 'maxPatients'
        },
        {
          title: '已预约人数',
          dataIndex: 'bookedPatients',
          key: 'bookedPatients'
        },
        {
          title: '备注',
          dataIndex: 'notes',
          key: 'notes'
        },
        {
          title: '操作',
          key: 'operation',
          scopedSlots: { customRender: 'operation' }
        }
      ]
    }
  },
  methods: {
    onSelect(date) {
      this.selectedDate = date
      this.showModal()
    },
    onPanelChange(date) {
      this.loadSchedules(date)
    },
    showModal() {
      this.modalVisible = true
      this.form.setFieldsValue({
        date: this.selectedDate
      })
    },
    async handleModalOk() {
      this.form.validateFields(async (err, values) => {
        if (err) return
        
        try {
          const scheduleData = {
            date: values.date.format('YYYY-MM-DD'),
            timeSlot: values.timeSlot,
            maxPatients: values.maxPatients,
            notes: values.notes
          }
          
          await this.$http.post('/api/doctor/schedules', scheduleData, {
            headers: {
              'Authorization': `Bearer ${getToken()}`
            }
          })
          this.$message.success('排班设置成功')
          this.modalVisible = false
          this.loadSchedules()
        } catch (error) {
          this.$message.error('排班设置失败：' + error.message)
        }
      })
    },
    handleModalCancel() {
      this.modalVisible = false
      this.form.resetFields()
    },
    async loadSchedules(date = this.selectedDate) {
      this.loading = true
      try {
        const response = await this.$http.get('/api/doctor/schedules', {
          params: {
            year: date.year(),
            month: date.month() + 1
          },
          headers: {
            'Authorization': `Bearer ${getToken()}`
          }
        })
        this.scheduleList = response.data
      } catch (error) {
        console.error('Error loading schedules:', error)
        this.$message.error('获取排班信息失败')
      } finally {
        this.loading = false
      }
    },
    hasSchedule(date) {
      return this.scheduleList.some(schedule => 
        moment(schedule.date).isSame(date, 'day')
      )
    },
    getScheduleColor(date) {
      const schedule = this.scheduleList.find(s => 
        moment(s.date).isSame(date, 'day')
      )
      if (!schedule) return ''
      
      const ratio = schedule.bookedPatients / schedule.maxPatients
      if (ratio >= 0.8) return 'red'
      if (ratio >= 0.5) return 'orange'
      return 'green'
    },
    getScheduleTime(date) {
      const schedule = this.scheduleList.find(s => 
        moment(s.date).isSame(date, 'day')
      )
      if (!schedule) return ''
      
      const timeSlots = {
        morning: '上午',
        afternoon: '下午',
        evening: '晚上'
      }
      return timeSlots[schedule.timeSlot]
    },
    async editSchedule(record) {
      this.selectedDate = moment(record.date)
      this.modalVisible = true
      this.form.setFieldsValue({
        date: moment(record.date),
        timeSlot: record.timeSlot,
        maxPatients: record.maxPatients,
        notes: record.notes
      })
    },
    async deleteSchedule(record) {
      try {
        await this.$confirm({
          title: '确认删除',
          content: '确定要删除这个排班吗？已有的预约将会被取消。',
          okText: '确认',
          cancelText: '取消',
          type: 'warning'
        })
        
        await this.$http.delete(`/api/doctor/schedules/${record.id}`, {
          headers: {
            'Authorization': `Bearer ${getToken()}`
          }
        })
        this.$message.success('删除成功')
        this.loadSchedules()
      } catch (error) {
        if (error.toString() !== 'Cancel') {
          this.$message.error('删除失败：' + error.message)
        }
      }
    }
  },
  mounted() {
    this.loadSchedules()
  }
}
</script>

<style lang="less" scoped>
.schedule {
  .schedule-cell {
    min-height: 20px;
    text-align: center;
  }
  
  .mt-4 {
    margin-top: 24px;
  }
}
</style> 