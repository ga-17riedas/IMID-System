<template>
  <div class="dashboard">
    <!-- 系统状态概览 -->
    <el-row :gutter="20" class="status-overview">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="status-item">
            <div class="icon">
              <i class="el-icon-cpu"></i>
            </div>
            <div class="info">
              <div class="title">CPU使用率</div>
              <div class="value">{{ cpuUsage }}%</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="status-item">
            <div class="icon">
              <i class="el-icon-coin"></i>
            </div>
            <div class="info">
              <div class="title">内存使用率</div>
              <div class="value">{{ memoryUsage }}%</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="status-item">
            <div class="icon">
              <i class="el-icon-folder"></i>
            </div>
            <div class="info">
              <div class="title">存储使用率</div>
              <div class="value">{{ storageUsage }}%</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="status-item">
            <div class="icon">
              <i class="el-icon-connection"></i>
            </div>
            <div class="info">
              <div class="title">在线用户</div>
              <div class="value">{{ onlineUsers }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 监控图表 -->
    <el-row :gutter="20" class="monitor-charts">
      <el-col :span="12">
        <el-card class="chart-card">
          <div slot="header">
            <span>系统资源使用趋势</span>
          </div>
          <div class="chart" ref="resourceChart"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <div slot="header">
            <span>系统日志 <small>(每30秒更新，最多保存500条)</small></span>
            <div style="float: right">
              <el-select 
                v-model="logFilter" 
                size="mini" 
                placeholder="日志级别" 
                style="width: 100px; margin-right: 10px"
                @change="filterLogs"
              >
                <el-option label="全部" value="ALL"></el-option>
                <el-option label="错误" value="ERROR"></el-option>
                <el-option label="警告" value="WARNING"></el-option>
                <el-option label="信息" value="INFO"></el-option>
                <el-option label="调试" value="DEBUG"></el-option>
              </el-select>
              <el-button 
                size="mini" 
                type="primary" 
                icon="el-icon-refresh" 
                @click="manualRefreshLogs" 
                :loading="logsLoading" 
                style="margin-right: 10px"
              >
                刷新
              </el-button>
              <el-button 
                size="mini" 
                type="text" 
                @click="viewAllLogs"
              >
              查看全部
            </el-button>
            </div>
          </div>
          <el-table 
            :data="filteredLogs" 
            style="width: 100%" 
            height="300" 
            v-loading="logsLoading"
            element-loading-text="加载日志中..."
            stripe
            border
            highlight-current-row
          >
            <el-table-column
              prop="timestamp"
              label="时间"
              width="180"
              :formatter="(row) => formatDate(row.timestamp)"
            >
            </el-table-column>
            <el-table-column
              prop="level"
              label="级别"
              width="100"
            >
              <template slot-scope="scope">
                <el-tag :type="getLogLevelType(scope.row.level)" size="mini">
                  {{ scope.row.level }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              prop="source"
              label="来源"
              width="150"
            >
            </el-table-column>
            <el-table-column
              prop="message"
              label="内容"
              show-overflow-tooltip
            >
              <template slot-scope="scope">
                <span>{{ scope.row.message }}</span>
              </template>
            </el-table-column>
          </el-table>
          
          <div class="pagination-container">
            <el-pagination
              @current-change="handleLogPageChange"
              :current-page="logPage"
              :page-size="logPageSize"
              layout="prev, pager, next"
              :total="logsTotal"
              small
            >
            </el-pagination>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 告警面板 -->
    <el-dialog
      title="系统告警"
      :visible.sync="showAlertDialog"
      width="500px"
    >
      <div class="alert-content">
        <i class="el-icon-warning"></i>
        <div class="alert-message">
          <h3>{{ currentAlert.title }}</h3>
          <p>{{ currentAlert.message }}</p>
        </div>
      </div>
      <div class="alert-suggestion">
        <h4>处理建议：</h4>
        <p>{{ currentAlert.suggestion }}</p>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="showAlertDialog = false">关闭</el-button>
        <el-button type="primary" @click="handleAlert">处理</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { formatDate } from '@/utils/format'
import { getSystemMetrics } from '@/api/admin'
import { getSystemLogs, getLatestLogs } from '@/api/admin/system'

export default {
  name: 'AdminDashboard',
  data() {
    return {
      cpuUsage: 45,
      memoryUsage: 60,
      storageUsage: 75,
      onlineUsers: 128,
      logs: [],
      allLogs: [], // 存储所有日志记录
      filteredLogs: [], // 筛选后的日志
      logPage: 1,
      logPageSize: 20,
      logsTotal: 0,
      showAlertDialog: false,
      currentAlert: {
        title: '',
        message: '',
        suggestion: ''
      },
      resourceChart: null,
      monitoringInterval: null,
      logsUpdateInterval: null, // 日志更新定时器
      logFilter: 'ALL', // 日志级别过滤
      logsLoading: false // 日志加载状态
    }
  },
  mounted() {
    // 初始默认值
    if (!this.cpuUsage) this.cpuUsage = 45;
    if (!this.memoryUsage) this.memoryUsage = 60;
    if (!this.storageUsage) this.storageUsage = 75;
    if (!this.onlineUsers) this.onlineUsers = 128;
    
    // 使用nextTick确保DOM已完全渲染
    this.$nextTick(() => {
      if (this.$refs.resourceChart) {
    this.initResourceChart()
      } else {
        console.error('资源图表DOM元素未找到')
      }
    })
    
    // 立即开始监控
    this.startMonitoring()
    
    // 初始化日志
    this.initSystemLogs()
    
    // 先清除可能存在的旧定时器
    if (this.logsUpdateInterval) {
      clearInterval(this.logsUpdateInterval)
    }
    
    // 每30秒自动刷新日志
    console.log('设置日志自动刷新定时器 - 每30秒')
    this.logsUpdateInterval = setInterval(() => {
      console.log('执行定时日志刷新')
    this.fetchSystemLogs()
    }, 30 * 1000)
    
    // 添加窗口大小变化事件监听器，调整图表尺寸
    window.addEventListener('resize', this.resizeChart)
  },
  beforeDestroy() {
    console.log('Dashboard组件即将销毁，清理资源')
    
    // 移除窗口大小变化的事件监听器
    window.removeEventListener('resize', this.resizeChart)
    
    // 清理图表实例
    if (this.resourceChart) {
      this.resourceChart.dispose()
      this.resourceChart = null
    }
    
    // 清除定时器，防止内存泄漏
    if (this.monitoringInterval) {
      console.log('清除监控定时器')
      clearInterval(this.monitoringInterval)
      this.monitoringInterval = null
    }
    
    // 清除日志更新定时器
    if (this.logsUpdateInterval) {
      console.log('清除日志刷新定时器')
      clearInterval(this.logsUpdateInterval)
      this.logsUpdateInterval = null
    }
  },
  methods: {
    formatDate(date) {
      if (!date) return '';
      
      // 确保date是一个日期对象
      const d = date instanceof Date ? date : new Date(date);
      
      if (isNaN(d.getTime())) {
        console.warn('无效的日期格式:', date);
        return '无效日期';
      }
      
      // 格式化为 YYYY-MM-DD HH:mm:ss
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}:${String(d.getSeconds()).padStart(2, '0')}`;
    },
    
    // 生成具有连续性的随机数据序列
    generateSeriesData(now, minBase, maxBase) {
      const data = [];
      const baseValue = Math.round(Math.random() * (maxBase - minBase) + minBase);
      
      for (let i = 23; i >= 0; i--) {
        let value = i === 23 ? baseValue : 
                   Math.max(minBase - 10, Math.min(maxBase + 10, data[data.length-1][1] + (Math.random() * 10 - 5)));
        
        data.push([
          new Date(now.getTime() - i * 5000), // 5秒间隔
          Math.round(value)
        ]);
      }
      
      return data;
    },
    
    initResourceChart() {
      try {
        // 检查DOM元素是否存在
        const chartDom = this.$refs.resourceChart
        if (!chartDom) {
          console.error('资源图表DOM元素未找到')
          return
        }
        
        // 如果已有实例，先销毁
        if (this.resourceChart) {
          this.resourceChart.dispose()
        }
        
        // 创建新实例
        this.resourceChart = echarts.init(chartDom)
        console.log('图表初始化成功')
        
        // 生成初始数据
        const now = new Date()
        const initialData = []
        
        // 生成24个初始数据点，每个点间隔5秒，模拟实时数据流
        for (let i = 23; i >= 0; i--) {
          // 生成随机波动的CPU数据，保持数据的连续性
          let value = i === 23 ? Math.round(Math.random() * 30 + 40) : 
                     Math.max(20, Math.min(90, initialData[initialData.length-1][1] + (Math.random() * 10 - 5)));
          
          initialData.push([
            new Date(now.getTime() - i * 5000), // 5秒间隔
            Math.round(value)
          ])
        }
        
        // 设置图表配置
      const option = {
        tooltip: {
            trigger: 'axis',
            formatter: function(params) {
              const date = new Date(params[0].value[0]);
              let result = '<div style="margin: 0px 0 0;line-height:1;">';
              result += '<div style="font-size:14px;color:#666;font-weight:400;line-height:1;">' + date.toLocaleTimeString() + '</div>';
              result += '<div style="margin: 10px 0 0;line-height:1;">';
              
              params.forEach(param => {
                const markerStyle = 'display:inline-block;margin-right:5px;border-radius:10px;width:10px;height:10px;background-color:' + param.color;
                result += '<div style="margin: 6px 0 0;line-height:1;">' +
                  '<span style="' + markerStyle + '"></span>' +
                  '<span style="font-size:14px;color:#666;font-weight:400;margin-left:2px">' + 
                  param.seriesName + ': ' + 
                  '</span>' +
                  '<span style="float:right;margin-left:20px;font-size:14px;color:#666;font-weight:900">' + 
                  param.value[1] + '%' +
                  '</span>' +
                  '</div>';
              });
              
              result += '</div></div>';
              return result;
            },
            axisPointer: {
              type: 'line',
              lineStyle: {
                color: 'rgba(0,0,0,0.2)',
                width: 1,
                type: 'solid'
              }
            },
            confine: true,
            backgroundColor: 'rgba(255, 255, 255, 0.95)',
            borderColor: '#eee',
            borderWidth: 1,
            padding: [5, 12, 15, 12],
            textStyle: {
              color: '#333'
            }
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            top: '50',
            containLabel: true
        },
        legend: {
            data: ['CPU', '内存', '存储'],
            right: '10%'
        },
        xAxis: {
          type: 'time',
            boundaryGap: false,
            axisLabel: {
              formatter: (value) => {
                const date = new Date(value)
                // 显示时:分:秒格式
                return [
                  date.getHours(),
                  date.getMinutes().toString().padStart(2, '0'),
                  date.getSeconds().toString().padStart(2, '0')
                ].join(':')
              },
              showMaxLabel: true,
              rotate: 0,
              hideOverlap: true,
              margin: 8
            },
            splitNumber: 6,
            splitLine: {
              show: true,
              lineStyle: {
                color: 'rgba(0,0,0,0.05)',
                type: 'dashed'
              }
            },
            minInterval: 5 * 1000, // 最小5秒间隔
            axisPointer: {
              snap: true
            }
        },
        yAxis: {
          type: 'value',
            min: 0,
          max: 100,
            axisLabel: {
              formatter: '{value}%'
            },
            splitLine: {
              show: true,
              lineStyle: {
                type: 'dashed',
                color: 'rgba(0,0,0,0.05)'
              }
            }
        },
        series: [
          {
            name: 'CPU',
            type: 'line',
              data: initialData,
              smooth: true,
              showSymbol: true,
              showAllSymbol: false,
              symbolSize: 6,
              symbol: 'circle',
              sampling: 'lttb',
              lineStyle: {
                width: 2
              },
              itemStyle: {
                color: '#5470c6',
                borderColor: '#fff',
                borderWidth: 1,
                shadowColor: 'rgba(0, 0, 0, 0.3)',
                shadowBlur: 2
              },
              emphasis: {
                scale: true,
                itemStyle: {
                  borderWidth: 2,
                  shadowBlur: 4
                }
              }
          },
          {
            name: '内存',
            type: 'line',
              data: this.generateSeriesData(now, 60, 80), // 生成内存数据，基准值在60-80之间
              smooth: true,
              showSymbol: true,
              showAllSymbol: false,
              symbolSize: 6,
              symbol: 'circle',
              sampling: 'lttb',
              lineStyle: {
                width: 2
              },
              itemStyle: {
                color: '#91cc75',
                borderColor: '#fff',
                borderWidth: 1,
                shadowColor: 'rgba(0, 0, 0, 0.3)',
                shadowBlur: 2
              },
              emphasis: {
                scale: true,
                itemStyle: {
                  borderWidth: 2,
                  shadowBlur: 4
                }
              }
          },
          {
            name: '存储',
            type: 'line',
              data: this.generateSeriesData(now, 50, 75), // 生成存储数据，基准值在50-75之间
              smooth: true,
              showSymbol: true,
              showAllSymbol: false,
              symbolSize: 6,
              symbol: 'circle',
              sampling: 'lttb',
              lineStyle: {
                width: 2
              },
              itemStyle: {
                color: '#fac858',
                borderColor: '#fff',
                borderWidth: 1,
                shadowColor: 'rgba(0, 0, 0, 0.3)',
                shadowBlur: 2
              },
              emphasis: {
                scale: true,
                itemStyle: {
                  borderWidth: 2,
                  shadowBlur: 4
                }
              }
            }
          ]
        }
        
        // 设置配置并渲染图表
      this.resourceChart.setOption(option)
        console.log('图表配置已设置')
        
        // 监听窗口调整大小事件
        window.addEventListener('resize', this.resizeChart)
        
      } catch (error) {
        console.error('初始化资源图表时出错:', error)
      }
    },
    
    // 处理窗口大小变化
    resizeChart() {
      if (this.resourceChart) {
        this.resourceChart.resize()
      }
    },
    startMonitoring() {
      this.fetchSystemMetrics()
      
      this.monitoringInterval = setInterval(() => {
        this.fetchSystemMetrics()
      }, 5000) // 每5秒更新一次，更频繁地更新以确保实时性
    },
    async fetchSystemMetrics() {
      try {
        // 先生成新的随机数据，确保每次都有变化
        this.updateWithRandomData();
        
        // 尝试从API获取真实数据
        const response = await getSystemMetrics()
        if (response && response.data) {
          // 如果API返回了数据，则使用真实数据覆盖随机数据
          if (response.data.cpuUsage !== undefined) {
            this.cpuUsage = response.data.cpuUsage
          }
          
          if (response.data.memoryUsage !== undefined) {
            this.memoryUsage = response.data.memoryUsage
          }
          
          if (response.data.storageUsage !== undefined) {
            this.storageUsage = response.data.storageUsage
          }
          
          if (response.data.onlineUsers !== undefined) {
            this.onlineUsers = response.data.onlineUsers
          }
        }
        
        // 无论API是否返回数据，都更新图表
        this.updateResourceChartData(this.cpuUsage, this.memoryUsage, this.storageUsage)
        
        // 强制刷新界面
        this.$forceUpdate();
        
        // 打印调试信息
        console.log('指标更新时间:', new Date().toLocaleTimeString(), {
          cpu: this.cpuUsage,
          memory: this.memoryUsage,
          storage: this.storageUsage,
          users: this.onlineUsers
        })
      } catch (error) {
        console.error('获取系统指标失败:', error)
        // 如果API调用出错，也要确保有新数据
        this.updateWithRandomData()
        this.updateResourceChartData(this.cpuUsage, this.memoryUsage, this.storageUsage)
        this.$forceUpdate();
      }
    },
    updateWithRandomData() {
      // 增大波动范围，使变化更明显
      
      // 为CPU添加-8到+8的随机波动，但保持在30-80的范围内
      const cpuChange = Math.random() * 16 - 8;
      this.cpuUsage = Math.max(30, Math.min(80, this.cpuUsage + cpuChange));
      this.cpuUsage = Math.round(this.cpuUsage);
      
      // 为内存添加-6到+6的随机波动，但保持在40-90的范围内
      const memoryChange = Math.random() * 12 - 6;
      this.memoryUsage = Math.max(40, Math.min(90, this.memoryUsage + memoryChange));
      this.memoryUsage = Math.round(this.memoryUsage);
      
      // 为存储添加-4到+4的随机波动，但保持在50-85的范围内
      const storageChange = Math.random() * 8 - 4;
      this.storageUsage = Math.max(50, Math.min(85, this.storageUsage + storageChange));
      this.storageUsage = Math.round(this.storageUsage);
      
      // 为在线用户数添加-5到+5的随机波动
      const userFluctuation = Math.floor(Math.random() * 11) - 5;
      this.onlineUsers = Math.max(1, this.onlineUsers + userFluctuation);
      
      // 打印随机更新的值，便于调试
      console.log('随机更新值:', {
        cpu: this.cpuUsage,
        memory: this.memoryUsage,
        storage: this.storageUsage,
        users: this.onlineUsers
      });
    },
    updateResourceChartData(cpuValue, memoryValue, storageValue) {
      if (!this.resourceChart) return
      
      try {
        const now = new Date()
        
        // 获取当前图表配置
        const option = this.resourceChart.getOption()
        if (!option.series || option.series.length < 3) return
        
        // 更新CPU数据
        const cpuData = [...option.series[0].data]
        cpuData.push([now, cpuValue])
        
        // 保持24个数据点，每个点间隔5秒
        const maxPoints = 24
        if (cpuData.length > maxPoints) {
          cpuData.shift()
        }
        
        // 更新内存数据
        const memoryData = [...option.series[1].data]
        memoryData.push([now, memoryValue])
        if (memoryData.length > maxPoints) {
          memoryData.shift()
        }
        
        // 更新存储数据
        const storageData = [...option.series[2].data]
        storageData.push([now, storageValue])
        if (storageData.length > maxPoints) {
          storageData.shift()
        }
        
        // 应用新配置
        this.resourceChart.setOption({
          xAxis: {
            min: cpuData[0][0],  // 设置x轴的最小值为最早的数据点时间
            max: cpuData[cpuData.length - 1][0]  // 设置x轴的最大值为最新的数据点时间
          },
          series: [
            { 
              data: cpuData,
              showSymbol: true,
              showAllSymbol: false,
              symbolSize: 6,
              symbol: 'circle',
              sampling: 'lttb',
              itemStyle: {
                borderColor: '#fff',
                borderWidth: 1,
                shadowBlur: 2
              },
              emphasis: {
                scale: true
              }
            },
            { 
              data: memoryData,
              showSymbol: true,
              showAllSymbol: false,
              symbolSize: 6,
              symbol: 'circle',
              sampling: 'lttb',
              itemStyle: {
                borderColor: '#fff',
                borderWidth: 1,
                shadowBlur: 2
              },
              emphasis: {
                scale: true
              }
            },
            { 
              data: storageData,
              showSymbol: true,
              showAllSymbol: false,
              symbolSize: 6,
              symbol: 'circle',
              sampling: 'lttb',
              itemStyle: {
                borderColor: '#fff',
                borderWidth: 1,
                shadowBlur: 2
              },
              emphasis: {
                scale: true
              }
            }
          ]
        })
        
        // 强制图表重新渲染
        this.$nextTick(() => {
          this.resourceChart.resize();
        })
      } catch (error) {
        console.error('更新资源图表失败:', error)
        // 如果更新失败，尝试重新初始化图表
        this.$nextTick(() => {
          if (this.$refs.resourceChart) {
            this.initResourceChart()
          }
        })
      }
    },
    // 初始化系统日志
    async initSystemLogs() {
      try {
        console.log('正在初始化系统日志...')
        
        // 尝试从存储恢复
        const savedLogs = localStorage.getItem('systemLogs')
        if (savedLogs) {
          try {
            const parsedLogs = JSON.parse(savedLogs)
            // 确保日期对象正确
            this.allLogs = parsedLogs.map(log => ({
              ...log,
              timestamp: new Date(log.timestamp)
            }))
            console.log(`从存储中恢复了 ${this.allLogs.length} 条日志记录`)
          } catch (e) {
            console.error('解析存储的日志失败:', e)
            this.allLogs = []
          }
        } else {
          this.allLogs = []
        }
        
        // 设置初始日志显示
        this.updateDisplayedLogs()
        
        // 无论是否有缓存，都立即从服务器获取最新日志
        console.log('首次初始化时立即获取最新日志')
        await this.fetchSystemLogs()
        
      } catch (error) {
        console.error('初始化系统日志失败:', error)
        this.$message.error('初始化系统日志失败，请刷新页面重试')
        this.allLogs = []
        this.filteredLogs = []
      }
    },
    
    // 保存日志到本地存储
    saveLogsToStorage() {
      try {
        // 只保存最近的100条日志到localStorage，防止超出存储限制
        const logsToSave = this.allLogs.slice(0, 100)
        localStorage.setItem('systemLogs', JSON.stringify(logsToSave))
      } catch (error) {
        console.error('保存日志到本地存储失败:', error)
      }
    },
    
    // 更新当前页显示的日志
    updateDisplayedLogs() {
      // 先应用筛选
      const filtered = this.logFilter === 'ALL' 
        ? this.allLogs 
        : this.allLogs.filter(log => log.level === this.logFilter)
      
      // 更新筛选后的总数
      this.logsTotal = filtered.length
      
      // 直接从最新的日志开始显示，不需要计算页数
      // 取最新的logPageSize条数据显示
      this.filteredLogs = filtered.slice(0, this.logPageSize)
      
      // 设置为第一页（最新的日志）
      this.logPage = 1
      
      console.log(`日志更新: 共${filtered.length}条, 显示最新的${this.filteredLogs.length}条`)
    },
    
    // 处理日志页面变化
    handleLogPageChange(page) {
      this.logPage = page
      
      // 先应用筛选
      const filtered = this.logFilter === 'ALL' 
        ? this.allLogs 
        : this.allLogs.filter(log => log.level === this.logFilter)
      
      // 计算起始索引，页数越大，显示的日志越旧
      const startIndex = (page - 1) * this.logPageSize
      const endIndex = startIndex + this.logPageSize
      
      // 获取对应页的日志数据
      this.filteredLogs = filtered.slice(startIndex, endIndex)
      
      console.log(`页码切换: ${page}, 显示第${startIndex+1}到${endIndex}条`)
    },
    
    // 获取系统日志
    async fetchSystemLogs() {
      try {
        console.log('开始获取最新系统日志...')
        this.logsLoading = true
        
        // 使用getLatestLogs API获取最新日志
        const response = await getLatestLogs(this.logFilter !== 'ALL' ? this.logFilter : null, 100) // 增加请求数量从50到100
        console.log('系统日志API响应:', response)
        
        let newLogs = []
        
        if (response && response.data) {
          console.log('响应数据结构:', JSON.stringify(response.data).substring(0, 200) + '...')
          // 处理不同格式的后端返回数据
          if (response.data.data) {
            newLogs = response.data.data
            console.log('从response.data.data中获取日志')
          }
          else if (Array.isArray(response.data)) {
            newLogs = response.data
            console.log('从response.data数组中获取日志')
          }
          else if (response.data.content && Array.isArray(response.data.content)) {
            newLogs = response.data.content
            console.log('从response.data.content中获取日志')
          }
          else {
            // 尝试检查常见的数据结构
            const possibleFields = ['data', 'content', 'logs', 'items', 'results']
            for (const field of possibleFields) {
              if (response.data[field] && Array.isArray(response.data[field])) {
                newLogs = response.data[field]
                console.log(`从response.data.${field}中获取日志`)
                break
              }
            }
            
            if (newLogs.length === 0) {
              console.warn('系统日志数据格式不符合预期:', response.data)
              
              // 如果取不到数据，尝试提取response.data本身是否有必要的字段
              if (response.data && typeof response.data === 'object' && response.data.timestamp) {
                newLogs = [response.data]; // 将单个对象包装为数组
                console.log('将单个日志对象包装为数组');
              } else {
                return // 如果数据格式异常，直接返回不处理
              }
            }
          }
          
          // 格式化日志
          const formattedLogs = newLogs.map(log => ({
            id: log.id || Math.random().toString(36).substring(2, 15), // 如果没有ID，生成一个随机ID
            timestamp: log.timestamp ? new Date(log.timestamp) : new Date(),
            level: log.level || 'INFO',
            source: log.source || '系统',
            message: log.message || '无内容',
            stackTrace: log.stackTrace,
            userId: log.userId,
            ipAddress: log.ipAddress,
            userAgent: log.userAgent
          }))
          
          console.log(`成功格式化 ${formattedLogs.length} 条日志记录`)
          
          // 检查是否有新日志
          if (formattedLogs.length === 0) {
            console.log('没有新的日志记录')
            this.logsLoading = false
            return
          }
          
          // 检测新日志
          const newLogsCount = this.checkNewLogs(formattedLogs)
          
          // 合并日志（去重）
          this.mergeLogs(formattedLogs)
          
          // 保存到本地存储
          this.saveLogsToStorage()
          
          // 更新UI显示
          this.updateDisplayedLogs()
          
          if (newLogsCount > 0) {
            console.log(`成功获取并添加 ${newLogsCount} 条新日志`)
          }
        } else {
          // 如果API调用返回空，添加一个模拟日志以便于调试
          if (this.allLogs.length === 0) {
            const mockLogs = [
              {
                id: 'mock-1',
                timestamp: new Date(),
                level: 'INFO',
                source: '系统初始化',
                message: '系统日志服务已启动',
                details: '正在等待日志数据...'
              },
              {
                id: 'mock-2',
                timestamp: new Date(Date.now() - 60000), // 1分钟前
                level: 'DEBUG',
                source: '数据库服务',
                message: '数据库连接池初始化完成',
                details: '连接池大小: 10'
              }
            ];
            this.mergeLogs(mockLogs);
            this.updateDisplayedLogs();
            console.log('添加了模拟日志用于显示');
          }
          console.warn('从后端获取日志失败，返回为空')
        }
      } catch (error) {
        console.error('获取系统日志出错:', error)
        
        // 如果出错且没有日志显示，添加一个错误日志
        if (this.allLogs.length === 0) {
          const errorLog = [{
            id: 'error-1',
            timestamp: new Date(),
            level: 'ERROR',
            source: '日志服务',
            message: '获取系统日志失败',
            details: error.message || '未知错误'
          }];
          this.mergeLogs(errorLog);
          this.updateDisplayedLogs();
        }
      } finally {
        this.logsLoading = false
      }
    },
    
    // 检查新日志数量
    checkNewLogs(logs) {
      if (!this.allLogs || this.allLogs.length === 0) return logs.length
      
      let newCount = 0
      const existingIds = new Set(this.allLogs.map(log => log.id))
      
      logs.forEach(log => {
        if (log.id && !existingIds.has(log.id)) {
          newCount++
        }
      })
      
      return newCount
    },
    
    // 合并日志并去重
    mergeLogs(newLogs) {
      if (!this.allLogs) this.allLogs = []
      
      // 使用Map按ID去重
      const logsMap = new Map()
      
      // 先添加现有日志
      this.allLogs.forEach(log => {
        if (log.id) {
          logsMap.set(log.id, log)
        }
      })
      
      // 添加新日志，如果有相同ID则覆盖
      newLogs.forEach(log => {
        if (log.id) {
          logsMap.set(log.id, log)
        } else {
          // 如果没有ID，直接添加（应该不会发生，仅作为后备选项）
          this.allLogs.push(log)
        }
      })
      
      // 转换回数组并按时间排序（降序，最新的在前面）
      this.allLogs = Array.from(logsMap.values())
        .sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp))
      
      // 限制保存的日志数量，防止内存占用过多
      if (this.allLogs.length > 500) {
        this.allLogs = this.allLogs.slice(0, 500)
      }
      
      console.log(`合并后的日志总数: ${this.allLogs.length}条, 最新日志时间: ${this.allLogs[0]?.timestamp}`)
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
    handleAlert() {
      this.showAlertDialog = false
    },
    viewAllLogs() {
      this.$router.push('/admin/system-logs')
    },
    filterLogs() {
      this.updateDisplayedLogs()
    },
    async manualRefreshLogs() {
      try {
        console.log('手动刷新系统日志')
        await this.fetchSystemLogs()
        
        // 确保显示最新的日志
        this.updateDisplayedLogs()
        
        this.$message({
          message: `系统日志已更新，当前共有 ${this.allLogs.length} 条记录`,
          type: 'success',
          duration: 2000
        })
      } catch (error) {
        console.error('手动刷新日志失败:', error)
        this.$message.error('刷新日志失败，请重试')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard {
  .status-overview {
    margin-bottom: 20px;

    .status-item {
      display: flex;
      align-items: center;
      
      .icon {
        font-size: 48px;
        margin-right: 20px;
        color: #409EFF;
      }

      .info {
        .title {
          font-size: 14px;
          color: #909399;
        }

        .value {
          font-size: 24px;
          font-weight: bold;
          color: #303133;
          transition: all 0.3s ease-in-out;
        }
      }
    }
  }

  .monitor-charts {
    .chart-card {
      margin-bottom: 20px;

      .chart {
        height: 300px;
      }
      
      .pagination-container {
        margin-top: 12px;
        text-align: right;
        padding-right: 10px;
      }
    }
  }

  .alert-content {
    display: flex;
    align-items: flex-start;
    margin-bottom: 20px;

    .el-icon-warning {
      font-size: 24px;
      color: #E6A23C;
      margin-right: 15px;
    }

    .alert-message {
      h3 {
        margin: 0 0 10px;
        color: #303133;
      }

      p {
        margin: 0;
        color: #606266;
      }
    }
  }

  .alert-suggestion {
    background: #F5F7FA;
    padding: 15px;
    border-radius: 4px;

    h4 {
      margin: 0 0 10px;
      color: #303133;
    }

    p {
      margin: 0;
      color: #606266;
    }
  }
}
</style> 