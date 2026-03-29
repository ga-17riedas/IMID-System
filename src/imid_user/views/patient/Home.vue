<template>
  <div class="patient-home">
    <!-- 欢迎区域 -->
    <el-row :gutter="20" class="welcome-section">
      <el-col :span="24">
        <el-card class="welcome-card">
          <div class="welcome-content">
            <div class="welcome-left">
              <el-avatar :size="64" :src="userAvatar"></el-avatar>
              <div class="welcome-text">
                <h2>欢迎回来{{ patientName }}</h2>
                <p>今天是 {{ currentDate }}，{{ greeting }}</p>
              </div>
            </div>
            <div class="welcome-right">
              <el-button type="primary" @click="goToHealthRecord">记录健康数据</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 主要内容区域 -->
    <el-row :gutter="20" class="main-content">
      <!-- 左侧：健康记录概览 -->
      <el-col :span="8">
        <el-card class="health-overview">
          <div slot="header" class="clearfix">
            <span>最新健康数据</span>
            <el-button style="float: right" type="text" @click="goToHealthRecord">
              查看更多
            </el-button>
          </div>
          <div class="health-data-list">
            <div v-for="item in latestHealthData" :key="item.type" class="health-data-item">
              <i :class="item.icon"></i>
              <div class="data-info">
                <span class="label">{{ item.label }}</span>
                <span class="value">{{ item.value }}</span>
                <span class="time">{{ item.time }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 中间：最近诊断记录 -->
      <el-col :span="8">
        <el-card class="diagnosis-records">
          <div slot="header" class="clearfix">
            <span>最近诊断记录</span>
            <div class="reminder-tip">
              <span class="emoticon">(｡◕ˇ∀ˇ◕)</span>
              <span class="tip-text">您的诊断报告已出炉</span>
            </div>
            <el-button style="float: right" type="text" @click="goToDiagnosis">
              前往查看
            </el-button>
          </div>
          <div v-if="diagnosisRecords.length > 0">
            <div v-for="record in diagnosisRecords" :key="record.id" class="diagnosis-item">
              <div class="diagnosis-time">
                <i class="el-icon-time"></i>
                {{ formatDate(record.createdAt) }}
              </div>
              <div class="diagnosis-doctor">
                <i class="el-icon-user"></i>
                {{ record.doctor.fullName }}医生
              </div>
              <div class="diagnosis-dept">
                <i class="el-icon-office-building"></i>
                {{ record.doctor.department }}
              </div>
              <div class="diagnosis-status">
                {{ record.readStatus ? '已查看' : '新报告' }}
              </div>
              <div class="diagnosis-type">
                <i class="el-icon-document"></i>
                {{ record.diagnosis }}
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无诊断记录"></el-empty>
        </el-card>
      </el-col>

      <!-- 右侧：系统介绍 -->
      <el-col :span="8">
        <el-card class="system-intro">
          <div slot="header" class="clearfix">
            <span>系统介绍</span>
          </div>
          <div class="intro-content">
            <h4>IMID 医学影像诊断系统</h4>
            <p>专业的医学影像诊断报告查询与管理平台：</p>
            <ul>
              <li>医学影像报告实时查询</li>
              <li>专业医生诊断意见</li>
              <li>个性化治疗建议</li>
              <li>基础健康数据记录</li>
            </ul>
            <div class="feature-highlight">
              <i class="el-icon-film"></i>
              <span>支持多种医学影像格式：CT、MRI、超声等</span>
            </div>
            <el-button type="text" @click="showSystemGuide">
              了解更多功能 <i class="el-icon-arrow-right"></i>
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getHealthOverview, getRecentRecords } from '@/api/patient'
import { getDiagnosisRecords } from '@/api/diagnosis'
import { formatDate, formatDateTime } from '@/utils/format'

/**
 * 患者首页组件
 * 提供患者的个人信息、健康数据概览和最近诊断记录
 */
export default {
  name: 'PatientHome',
  data() {
    return {
      // 患者基本信息
      patientName: '',
      userAvatar: '',
      // 系统信息
      currentDate: formatDate(new Date()),
      // 健康数据和诊断记录
      latestHealthData: [], // 最新健康数据
      diagnosisRecords: []  // 诊断记录列表
    }
  },
  computed: {
    /**
     * 根据当前时间生成问候语
     * @returns {string} 问候语
     */
    greeting() {
      const hour = new Date().getHours()
      if (hour < 12) return '早上好'
      if (hour < 18) return '下午好'
      return '晚上好'
    }
  },
  created() {
    // 组件创建时获取数据
    this.fetchData()
  },
  methods: {
    // 日期格式化方法
    formatDate,
    formatDateTime,
    
    /**
     * 获取患者首页所需的数据
     * 包括健康概览和诊断记录
     */
    async fetchData() {
      try {
        // 并行请求健康数据和诊断数据
        const [healthData, diagnosisData] = await Promise.all([
          getHealthOverview(),
          getDiagnosisRecords({ 
            page: 1, 
            size: 3,
            sort: 'createdAt,desc'
          })
        ])
        // 处理健康数据
        this.processHealthData(healthData.data)
        
        // 处理诊断记录数据
        if (diagnosisData.data && diagnosisData.data.content) {
          this.diagnosisRecords = diagnosisData.data.content.map(record => ({
            ...record,
            readStatus: record.readStatus || false
          }))
        } else {
          this.diagnosisRecords = []
        }
      } catch (error) {
        console.error('获取数据失败:', error)
        this.$message.error('获取数据失败')
      }
    },
    
    /**
     * 处理健康数据
     * 将API返回的健康数据转换为UI显示格式
     * @param {Object} data - 健康数据对象
     */
    processHealthData(data) {
      this.latestHealthData = [
        {
          type: 'bloodPressure',
          label: '血压',
          value: data.lastBloodPressure ? 
            `${data.lastBloodPressure.systolic}/${data.lastBloodPressure.diastolic} mmHg` : 
            '暂无数据',
          time: data.lastBloodPressure?.date ? formatDateTime(data.lastBloodPressure.date) : '',
          icon: 'el-icon-warning'
        },
        {
          type: 'heartRate',
          label: '心率',
          value: data.lastHeartRate ? `${data.lastHeartRate.value} 次/分` : '暂无数据',
          time: data.lastHeartRate?.date ? formatDateTime(data.lastHeartRate.date) : '',
          icon: 'el-icon-timer'
        },
        {
          type: 'bloodSugar',
          label: '血糖',
          value: data.lastBloodSugar ? `${data.lastBloodSugar.value} mmol/L` : '暂无数据',
          time: data.lastBloodSugar?.date ? formatDateTime(data.lastBloodSugar.date) : '',
          icon: 'el-icon-sugar'
        }
      ]
    },
    
    /**
     * 导航到健康记录页面
     */
    goToHealthRecord() {
      this.$router.push('/patient/health')
    },
    
    /**
     * 导航到诊断报告页面
     */
    goToDiagnosis() {
      this.$router.push('/patient/reports')
    },
    
    /**
     * 显示系统使用指南
     */
    showSystemGuide() {
      // 实现系统使用指南的展示逻辑
    }
  }
}
</script>

<style scoped>
.patient-home {
  padding: 20px;
}

.welcome-section {
  margin-bottom: 20px;
}

.welcome-card {
  background: linear-gradient(to right, #1989fa, #5cadff);
  color: white;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.welcome-text h2 {
  margin: 0;
  font-size: 24px;
}

.welcome-text p {
  margin: 5px 0 0;
  opacity: 0.8;
}

.main-content {
  margin-bottom: 20px;
}

.health-data-list {
  .health-data-item {
    display: flex;
    align-items: center;
    padding: 12px 0;
    border-bottom: 1px solid #EBEEF5;

    &:last-child {
      border-bottom: none;
    }

    i {
      font-size: 24px;
      color: #409EFF;
      margin-right: 12px;
    }

    .data-info {
      flex: 1;
      display: flex;
      flex-direction: column;

      .label {
        color: #606266;
        font-size: 14px;
      }

      .value {
        font-size: 18px;
        font-weight: bold;
        color: #303133;
        margin: 4px 0;
      }

      .time {
        font-size: 12px;
        color: #909399;
      }
    }
  }
}

.diagnosis-item {
  padding: 12px 0;
  border-bottom: 1px solid #EBEEF5;
  position: relative;
  transition: all 0.3s;
  padding: 15px;
  margin: 8px 0;
  border-radius: 4px;
  background: #fafafa;
  cursor: pointer;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    background: #f0f7ff;
    transform: translateX(5px);
    box-shadow: 0 2px 12px 0 rgba(0,0,0,.05);
  }

  .diagnosis-time {
    font-size: 14px;
    color: #409EFF;
    margin-bottom: 8px;
    
    i {
      margin-right: 5px;
    }
  }

  .diagnosis-doctor {
    font-size: 16px;
    color: #303133;
    margin: 8px 0;
    font-weight: 500;
    
    i {
      color: #67C23A;
      margin-right: 5px;
    }
  }

  .diagnosis-dept {
    font-size: 12px;
    color: #909399;
    display: flex;
    align-items: center;
    
    i {
      margin-right: 5px;
    }
  }

  .diagnosis-status {
    position: absolute;
    top: 15px;
    right: 15px;
    font-size: 12px;
    padding: 2px 8px;
    border-radius: 10px;
    background: #ecf5ff;
    color: #409EFF;
  }

  .diagnosis-type {
    font-size: 14px;
    color: #606266;
    margin-top: 8px;
    display: flex;
    align-items: center;
    
    i {
      color: #909399;
      margin-right: 5px;
    }
  }
}

.intro-content {
  h4 {
    margin-top: 0;
    color: #303133;
    font-size: 18px;
  }

  p {
    color: #606266;
    margin: 12px 0;
  }

  ul {
    padding-left: 20px;
    margin: 16px 0;
    color: #606266;
  }

  li {
    margin: 10px 0;
    position: relative;
    
    &::before {
      content: "•";
      color: #409EFF;
      font-weight: bold;
      position: absolute;
      left: -15px;
    }
  }

  .feature-highlight {
    margin: 20px 0;
    padding: 12px;
    background-color: #ecf5ff;
    border-radius: 4px;
    display: flex;
    align-items: center;
    
    i {
      color: #409EFF;
      font-size: 20px;
      margin-right: 10px;
    }
    
    span {
      color: #409EFF;
      font-size: 14px;
    }
  }
}

.diagnosis-records {
  position: relative;

  .reminder-tip {
    position: absolute;
    top: 10px;
    right: 80px;
    background: #ecf5ff;
    padding: 8px 12px;
    border-radius: 6px;
    font-size: 13px;
    color: #409EFF;
    display: flex;
    align-items: center;
    gap: 8px;
    z-index: 1;
    
    &::after {
      content: '';
      position: absolute;
      top: 50%;
      right: -6px;
      width: 0;
      height: 0;
      border-top: 6px solid transparent;
      border-bottom: 6px solid transparent;
      border-left: 6px solid #ecf5ff;
      transform: translateY(-50%);
    }
    
    .emoticon {
      font-size: 15px;
      font-family: "Microsoft YaHei", "PingFang SC", sans-serif;
    }
    
    .tip-text {
      white-space: nowrap;
      line-height: 1.2;
    }
  }
}
</style>