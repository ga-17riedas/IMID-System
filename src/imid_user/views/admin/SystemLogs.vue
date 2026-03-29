<template>
  <div class="system-logs-container">
    <el-card>
      <div slot="header" class="page-header">
        <span>系统日志管理</span>
        <div class="header-actions">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            :picker-options="pickerOptions"
            @change="handleDateChange"
          />
          <el-select v-model="logLevel" placeholder="日志级别" @change="handleLevelChange">
            <el-option label="全部" value="" />
            <el-option label="错误" value="ERROR" />
            <el-option label="警告" value="WARNING" />
            <el-option label="信息" value="INFO" />
            <el-option label="调试" value="DEBUG" />
          </el-select>
          <el-button type="primary" @click="fetchLogs">查询</el-button>
          <el-button type="danger" @click="showClearDialog = true">清除日志</el-button>
          <el-switch
            v-model="autoRefresh"
            active-text="自动刷新"
            inactive-text="手动刷新"
            @change="handleAutoRefreshChange"
          ></el-switch>
          <span v-if="autoRefresh" class="refresh-timer">{{ countdown }}秒后刷新</span>
        </div>
      </div>
      
      <el-table
        :data="logs"
        style="width: 100%"
        v-loading="loading"
        border
      >
        <el-table-column prop="timestamp" label="时间" width="180">
          <template slot-scope="scope">
            {{ formatDate(scope.row.timestamp) }}
          </template>
        </el-table-column>
        <el-table-column prop="level" label="级别" width="100">
          <template slot-scope="scope">
            <el-tag :type="getLogLevelType(scope.row.level)">
              {{ scope.row.level }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="source" label="来源" width="150"></el-table-column>
        <el-table-column prop="message" label="内容"></el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="page"
          :page-sizes="[10, 20, 50, 100, 500]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
        >
        </el-pagination>
      </div>
    </el-card>
    
    <!-- 清除日志确认对话框 -->
    <el-dialog
      title="清除日志"
      :visible.sync="showClearDialog"
      width="500px"
    >
      <div>
        <p>确定要清除以下范围的日志吗？</p>
        <p v-if="dateRange && dateRange.length === 2">
          时间范围：{{ formatDate(dateRange[0]) }} 至 {{ formatDate(dateRange[1]) }}
        </p>
        <p v-else>
          时间范围：全部
        </p>
        <p>
          日志级别：{{ logLevel ? logLevel : '全部' }}
        </p>
        <div class="warning-text">
          <i class="el-icon-warning"></i>
          <span>此操作不可逆，请谨慎操作！</span>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="showClearDialog = false">取消</el-button>
        <el-button type="danger" @click="clearLogs">确定清除</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { formatDate } from '@/utils/format'
import { getSystemLogs, clearSystemLogs, getLatestLogs } from '@/api/admin'

export default {
  name: 'SystemLogs',
  data() {
    return {
      logs: [],
      loading: false,
      page: 1,
      pageSize: 50, // 默认每页显示50条
      total: 0,
      dateRange: null,
      logLevel: '',
      showClearDialog: false,
      autoRefresh: true, // 默认开启自动刷新
      refreshTimer: null, // 定时器
      countdown: 30, // 倒计时秒数
      countdownTimer: null, // 倒计时定时器
      pickerOptions: {
        shortcuts: [
          {
            text: '最近一天',
            onClick(picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24)
              picker.$emit('pick', [start, end])
            }
          },
          {
            text: '最近一周',
            onClick(picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
              picker.$emit('pick', [start, end])
            }
          },
          {
            text: '最近一个月',
            onClick(picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
              picker.$emit('pick', [start, end])
            }
          }
        ]
      }
    }
  },
  created() {
    console.log('系统日志组件初始化')
    // 初始加载日志
    this.fetchLogs()
    
    // 启动自动刷新
    if (this.autoRefresh) {
      console.log('初始化时启动自动刷新')
      this.startAutoRefresh()
    }
  },
  beforeDestroy() {
    // 在组件销毁前，清除所有定时器
    this.stopAutoRefresh()
  },
  methods: {
    formatDate,
    async fetchLogs() {
      this.loading = true
      console.log('开始获取日志，模式:', this.autoRefresh ? '自动刷新' : '普通查询', '页码:', this.page)
      
      try {
        // 如果是自动刷新模式且在第一页，使用最新日志API
        if (this.autoRefresh && this.page === 1 && !this.dateRange) {
          console.log('使用最新日志API获取数据')
          await this.fetchLatestLogs()
        } else {
          // 普通分页查询
          console.log('使用普通分页查询获取数据')
          const params = {
            page: this.page - 1,
            size: this.pageSize,
            sort: 'timestamp,desc' // 按时间戳倒序排序，最新的日志显示在前面
          }
          
          if (this.logLevel) {
            params.level = this.logLevel
          }
          
          if (this.dateRange && this.dateRange.length === 2) {
            params.startDate = this.dateRange[0].toISOString()
            params.endDate = this.dateRange[1].toISOString()
          }
          
          console.log('查询参数:', params)
          const response = await getSystemLogs(params)
          console.log('查询响应:', response)
          
          if (response && response.data) {
            this.processLogResponse(response)
          } else {
            this.logs = []
            this.total = 0
            console.warn('未获取到日志数据')
          }
        }
        
        // 每次成功加载后重置倒计时
        if (this.autoRefresh) {
          console.log('加载日志成功，重置倒计时')
          this.resetCountdown()
        }
      } catch (error) {
        console.error('获取系统日志失败:', error)
        this.$message.error('获取系统日志失败')
        this.logs = []
        this.total = 0
      } finally {
        this.loading = false
      }
    },
    
    // 获取最新的日志
    async fetchLatestLogs() {
      try {
        const response = await getLatestLogs(this.logLevel, this.pageSize)
        
        if (response && response.data) {
          // 添加控制台输出，帮助调试
          console.log('Latest logs response structure:', JSON.stringify(response.data))
          
          // 检查各种可能的响应结构
          if (response.data.data && Array.isArray(response.data.data)) {
            this.logs = response.data.data.map(log => ({
              ...log,
              timestamp: new Date(log.timestamp)
            }))
            this.total = response.data.data.length
          } else if (Array.isArray(response.data)) {
            this.logs = response.data.map(log => ({
              ...log,
              timestamp: new Date(log.timestamp)
            }))
            this.total = response.data.length
          } else if (typeof response.data === 'object' && response.data !== null) {
            // 尝试检查常见的响应格式
            const possibleDataFields = ['data', 'content', 'logs', 'items', 'results']
            for (const field of possibleDataFields) {
              if (response.data[field] && Array.isArray(response.data[field])) {
                this.logs = response.data[field].map(log => ({
                  ...log,
                  timestamp: new Date(log.timestamp)
                }))
                this.total = response.data[field].length
                break
              }
            }
          } else {
            this.logs = []
            this.total = 0
            console.warn('最新日志数据格式不符合预期', response.data)
          }
        } else {
          this.logs = []
          this.total = 0
          console.warn('获取最新日志失败，响应数据为空')
        }
      } catch (error) {
        console.error('获取最新日志失败:', error)
        throw error
      }
    },
    
    // 处理日志响应数据
    processLogResponse(response) {
      // 添加控制台输出，帮助调试
      console.log('Process log response structure:', JSON.stringify(response.data))
      
      // 检查各种可能的数据结构
      if (response.data.data && response.data.data.content && Array.isArray(response.data.data.content)) {
        this.logs = response.data.data.content.map(log => ({
          ...log,
          timestamp: new Date(log.timestamp)
        }))
        this.total = response.data.data.totalElements || response.data.data.content.length
      } else if (response.data.content && Array.isArray(response.data.content)) {
        this.logs = response.data.content.map(log => ({
          ...log,
          timestamp: new Date(log.timestamp)
        }))
        this.total = response.data.totalElements || response.data.content.length
      } else if (Array.isArray(response.data)) {
        this.logs = response.data.map(log => ({
          ...log,
          timestamp: new Date(log.timestamp)
        }))
        this.total = response.data.length
      } else if (response.data.data && Array.isArray(response.data.data)) {
        this.logs = response.data.data.map(log => ({
          ...log,
          timestamp: new Date(log.timestamp)
        }))
        this.total = response.data.data.length
      } else {
        // 尝试检查常见的响应格式
        const possibleDataFields = ['data', 'content', 'logs', 'items', 'results']
        let dataFound = false
        
        for (const field of possibleDataFields) {
          if (response.data[field] && Array.isArray(response.data[field])) {
            this.logs = response.data[field].map(log => ({
              ...log,
              timestamp: new Date(log.timestamp)
            }))
            this.total = response.data[field].length
            dataFound = true
            break
          }
        }
        
        if (!dataFound) {
          this.logs = []
          this.total = 0
          console.warn('系统日志数据格式不符合预期', response.data)
        }
      }
    },
    getLogLevelType(level) {
      const types = {
        ERROR: 'danger',
        WARNING: 'warning',
        INFO: 'info',
        DEBUG: 'success'
      }
      return types[level] || 'info'
    },
    handleDateChange() {
      // 日期变化时不自动查询，等用户点击查询按钮
      if (this.autoRefresh) {
        // 当用户设置了日期筛选时，关闭自动刷新
        this.autoRefresh = false
        this.stopAutoRefresh()
        this.$message.info('已设置日期筛选，自动刷新已关闭')
      }
    },
    handleLevelChange() {
      // 级别变化时不自动查询，等用户点击查询按钮
    },
    handleSizeChange(size) {
      this.pageSize = size
      this.fetchLogs()
      
      // 如果用户选择查看500条数据，提示可能会影响性能
      if (size === 500) {
        this.$message.warning('显示大量数据可能会影响页面性能')
      }
    },
    handleCurrentChange(page) {
      this.page = page
      this.fetchLogs()
      
      // 如果用户翻页，关闭自动刷新
      if (page > 1 && this.autoRefresh) {
        this.autoRefresh = false
        this.stopAutoRefresh()
        this.$message.info('正在查看历史数据，自动刷新已关闭')
      }
    },
    async clearLogs() {
      try {
        let startDate = null
        let endDate = null
        
        if (this.dateRange && this.dateRange.length === 2) {
          startDate = this.dateRange[0].toISOString()
          endDate = this.dateRange[1].toISOString()
        }
        
        await clearSystemLogs({
          level: this.logLevel || null,
          startDate,
          endDate
        })
        
        this.$message.success('日志清除成功')
        this.showClearDialog = false
        this.fetchLogs() // 重新加载日志
      } catch (error) {
        console.error('清除日志失败:', error)
        this.$message.error('清除日志失败')
      }
    },
    // 开始自动刷新
    startAutoRefresh() {
      // 确保先停止之前的定时器，避免重复设置
      this.stopAutoRefresh()

      // 调试日志
      console.log('启动自动刷新机制')
      
      // 设置30秒刷新一次
      this.refreshTimer = setInterval(() => {
        if (this.autoRefresh && !this.loading && this.page === 1) {
          console.log('执行自动刷新')
          this.fetchLogs()
        } else {
          console.log('跳过自动刷新', {
            autoRefresh: this.autoRefresh,
            loading: this.loading,
            page: this.page
          })
        }
      }, 30000)
      
      // 倒计时
      this.resetCountdown()
    },
    
    // 停止自动刷新
    stopAutoRefresh() {
      if (this.refreshTimer) {
        clearInterval(this.refreshTimer)
        this.refreshTimer = null
        console.log('停止自动刷新定时器')
      }
      
      if (this.countdownTimer) {
        clearInterval(this.countdownTimer)
        this.countdownTimer = null
        console.log('停止倒计时定时器')
      }
    },
    
    // 重置倒计时
    resetCountdown() {
      // 清除之前的倒计时
      if (this.countdownTimer) {
        clearInterval(this.countdownTimer)
      }
      
      console.log('重置倒计时')
      
      // 重置倒计时
      this.countdown = 30
      
      // 启动新的倒计时
      this.countdownTimer = setInterval(() => {
        if (this.countdown > 0) {
          this.countdown--
        } else {
          // 倒计时结束时重置为30秒
          this.countdown = 30
          
          // 如果在自动刷新模式并且不在加载状态，主动触发一次刷新
          if (this.autoRefresh && !this.loading && this.page === 1) {
            console.log('倒计时结束，执行刷新')
            this.fetchLogs()
          }
        }
      }, 1000)
    },
    
    // 处理自动刷新开关变化
    handleAutoRefreshChange(value) {
      console.log('自动刷新设置变更为:', value)
      
      if (value) {
        // 打开自动刷新
        this.startAutoRefresh()
        
        // 回到第一页并立即刷新
        if (this.page !== 1) {
          this.page = 1
        }
        this.fetchLogs()
      } else {
        // 关闭自动刷新
        this.stopAutoRefresh()
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.system-logs-container {
  padding: 20px;
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .header-actions {
      display: flex;
      gap: 10px;
      align-items: center;
      
      .refresh-timer {
        font-size: 12px;
        color: #909399;
        margin-left: 5px;
      }
    }
  }
  
  .pagination-container {
    margin-top: 20px;
    text-align: right;
  }
  
  .warning-text {
    margin-top: 15px;
    color: #E6A23C;
    display: flex;
    align-items: center;
    
    .el-icon-warning {
      margin-right: 10px;
      font-size: 18px;
    }
  }
}
</style> 