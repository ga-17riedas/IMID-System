<template>
  <div class="doctor-home">
    <!-- 欢迎卡片 -->
    <el-card class="welcome-card" shadow="hover">
      <div class="welcome-content">
        <div class="avatar">
          <el-avatar :size="80" icon="el-icon-user-solid"></el-avatar>
        </div>
        <div class="welcome-text">
          <h2>欢迎回来, {{ doctorInfo.fullName }}</h2>
          <p>{{ doctorInfo.hospital }} | {{ doctorInfo.department }} | {{ doctorInfo.professionalTitle }}</p>
        </div>
      </div>
    </el-card>

    <!-- 统计数据卡片 -->
    <el-row :gutter="24" class="stat-row">
      <el-col :span="8">
        <el-card class="stat-card">
          <div style="color: #409EFF; font-size: 18px; font-weight: bold; margin-bottom: 15px; border-bottom: 1px solid #EBEEF5; padding-bottom: 10px;">
            影像分析
          </div>
          <div style="display: flex; align-items: center; margin-bottom: 15px;">
            <div style="width: 60px; height: 60px; background-color: #ecf5ff; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin-right: 15px;">
              <i class="el-icon-picture-outline" style="font-size: 30px; color: #409EFF;"></i>
            </div>
            <div>
              <div style="font-size: 30px; font-weight: bold; margin-bottom: 5px;">{{ stats.pendingImages || 0 }}</div>
              <div style="color: #606266; font-size: 14px;">待分析影像</div>
            </div>
          </div>
          <div style="text-align: right;">
            <el-button type="primary" size="small" @click="$router.push('/doctor/image-analysis')">前往分析 <i class="el-icon-arrow-right"></i></el-button>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="stat-card">
          <div style="color: #409EFF; font-size: 18px; font-weight: bold; margin-bottom: 15px; border-bottom: 1px solid #EBEEF5; padding-bottom: 10px;">
            患者管理
          </div>
          <div style="display: flex; align-items: center; margin-bottom: 15px;">
            <div style="width: 60px; height: 60px; background-color: #ecf5ff; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin-right: 15px;">
              <i class="el-icon-user" style="font-size: 30px; color: #409EFF;"></i>
            </div>
            <div>
              <div style="font-size: 30px; font-weight: bold; margin-bottom: 5px;">{{ stats.todayPatients || 0 }}</div>
              <div style="color: #606266; font-size: 14px;">今日患者</div>
            </div>
          </div>
          <div style="text-align: right;">
            <el-button type="primary" size="small" @click="$router.push('/doctor/patients')">查看患者 <i class="el-icon-arrow-right"></i></el-button>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="stat-card">
          <div style="color: #409EFF; font-size: 18px; font-weight: bold; margin-bottom: 15px; border-bottom: 1px solid #EBEEF5; padding-bottom: 10px;">
            报告管理
          </div>
          <div style="display: flex; align-items: center; margin-bottom: 15px;">
            <div style="width: 60px; height: 60px; background-color: #ecf5ff; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin-right: 15px;">
              <i class="el-icon-document" style="font-size: 30px; color: #409EFF;"></i>
            </div>
            <div>
              <div style="font-size: 30px; font-weight: bold; margin-bottom: 5px;">{{ stats.totalReports || 0 }}</div>
              <div style="color: #606266; font-size: 14px;">诊断报告</div>
            </div>
          </div>
          <div style="text-align: right;">
            <el-button type="primary" size="small" @click="$router.push('/doctor/reports')">查看报告 <i class="el-icon-arrow-right"></i></el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 系统信息卡片 -->
    <el-row :gutter="24" class="info-row">
      <el-col :span="12">
        <el-card class="info-card" shadow="hover">
          <div class="card-header">
            <span>系统介绍</span>
          </div>
          <div class="info-content">
            <p class="info-title">IMID 医学影像诊断系统</p>
            <p class="info-desc">
              基于深度学习和大语言模型的智能医疗影像诊断辅助系统(IMID)是一款专为医疗影像诊断领域设计的智能辅助平台。
              系统采用YOLOv8深度学习模型算法，旨在提升医疗影像诊断的准确性和效率。
            </p>
            <div class="feature-list">
              <div class="feature-item">
                <div class="feature-icon">
                  <i class="el-icon-check"></i>
                </div>
                <span>智能影像分析</span>
              </div>
              <div class="feature-item">
                <div class="feature-icon">
                  <i class="el-icon-check"></i>
                </div>
                <span>辅助诊断建议</span>
              </div>
              <div class="feature-item">
                <div class="feature-icon">
                  <i class="el-icon-check"></i>
                </div>
                <span>患者数据管理</span>
              </div>
              <div class="feature-item">
                <div class="feature-icon">
                  <i class="el-icon-check"></i>
                </div>
                <span>报告自动生成</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="info-card" shadow="hover">
          <div class="card-header">
            <span>快捷操作</span>
          </div>
          <div class="quick-actions">
            <el-button type="primary" plain icon="el-icon-plus" @click="$router.push('/doctor/image-analysis')">
              新建分析
            </el-button>
            <el-button type="primary" plain icon="el-icon-user" @click="$router.push('/doctor/patients')">
              患者管理
            </el-button>
            <el-button type="primary" plain icon="el-icon-document" @click="$router.push('/doctor/reports')">
              报告管理
            </el-button>
            <el-button type="primary" plain icon="el-icon-user" @click="$router.push('/doctor/profile')">
              个人信息
            </el-button>
          </div>
          <div class="system-info">
            <div class="info-item">
              <span class="label">系统版本</span>
              <span class="value">V1.0.0</span>
            </div>
            <div class="info-item">
              <span class="label">更新日期</span>
              <span class="value">2025-02-15</span>
            </div>
            <div class="info-item">
              <span class="label">技术支持</span>
              <span class="value">孙茂深</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getHomeStats, getDoctorProfile } from '@/api/doctor'

/**
 * 医生首页组件
 * 提供医生工作的入口页面，展示统计数据和快捷入口
 */
export default {
  name: 'DoctorHome',
  data() {
    return {
      // 医生个人信息
      doctorInfo: {},
      // 统计数据
      stats: {
        pendingImages: 0, // 待分析影像数量
        todayPatients: 0, // 今日患者数量
        totalReports: 0   // 总诊断报告数
      }
    }
  },
  created() {
    // 组件创建时获取医生信息和统计数据
    this.fetchData()
  },
  methods: {
    /**
     * 获取医生首页所需的数据
     * 包括医生个人信息和统计数据
     */
    async fetchData() {
      try {
        // 获取医生信息
        const profileRes = await getDoctorProfile()
        this.doctorInfo = profileRes.data

        // 获取统计数据
        const statsRes = await getHomeStats()
        this.stats = statsRes.data
      } catch (error) {
        console.error('获取数据失败:', error)
        this.$message.error('获取数据失败')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.doctor-home {
  padding: 24px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);

  .el-card {
    border-radius: 12px;
    overflow: hidden;
    border: none;
  }

  .welcome-card {
    margin-bottom: 24px;
    box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
    transition: all 0.3s;
    
    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 4px 20px rgba(0,0,0,0.1);
    }

    .welcome-content {
      display: flex;
      align-items: center;
      gap: 24px;
      padding: 20px;

      .avatar {
        background-color: rgba(64, 158, 255, 0.1);
        border-radius: 50%;
        padding: 10px;
        
        .el-avatar {
          background-color: #409EFF;
          font-size: 40px;
        }
      }

      .welcome-text {
        h2 {
          margin: 0 0 12px 0;
          font-size: 28px;
          color: #303133;
          font-weight: 600;
        }

        p {
          margin: 0;
          color: #606266;
          font-size: 16px;
        }
      }
    }
  }

  .stat-row {
    margin-bottom: 24px;

    .stat-card {
      height: 200px;
      margin-bottom: 20px;
      border-radius: 8px;
      transition: all 0.3s;
      position: relative;
      display: flex;
      flex-direction: column;
      padding: 0;
      overflow: hidden;
      box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05) !important;

      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 4px 20px rgba(0,0,0,0.1) !important;
      }

      .stat-title {
        font-size: 18px;
        font-weight: 600;
        color: #409EFF;
        padding: 15px;
        border-bottom: 1px solid rgba(64, 158, 255, 0.2);
      }
      
      .stat-body {
        display: flex;
        flex: 1;
        align-items: center;
        padding: 15px;
        
        .stat-icon {
          font-size: 42px;
          color: #409EFF;
          margin-right: 20px;
          background-color: rgba(64, 158, 255, 0.1);
          border-radius: 50%;
          width: 70px;
          height: 70px;
          display: flex;
          align-items: center;
          justify-content: center;
        }

        .stat-info {
          flex: 1;
          
          .stat-value {
            font-size: 36px;
            font-weight: 600;
            color: #303133;
            line-height: 1;
            margin-bottom: 8px;
          }

          .stat-label {
            color: #606266;
            font-size: 14px;
          }
        }
      }

      .stat-footer {
        padding: 0 15px 15px 15px;
        text-align: right;
        
        .el-button {
          border-radius: 20px;
          box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
          
          i {
            margin-left: 4px;
          }
        }
      }
    }
  }

  .info-row {
    .info-card {
      border-radius: 8px;
      height: 360px;
      transition: all 0.3s;
      overflow: auto;
      padding: 0;

      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 4px 20px rgba(0,0,0,0.1);
      }

      .card-header {
        font-size: 18px;
        font-weight: 600;
        color: #409EFF;
        padding: 16px 20px;
        border-bottom: 1px solid rgba(64, 158, 255, 0.2);
      }

      .info-content {
        padding: 20px;

        .info-title {
          font-size: 20px;
          font-weight: 600;
          color: #303133;
          margin-bottom: 16px;
        }

        .info-desc {
          color: #606266;
          line-height: 1.8;
          margin-bottom: 24px;
        }

        .feature-list {
          display: grid;
          grid-template-columns: repeat(2, 1fr);
          gap: 16px;

          .feature-item {
            display: flex;
            align-items: center;
            gap: 10px;
            
            .feature-icon {
              background-color: rgba(64, 158, 255, 0.1);
              color: #409EFF;
              border-radius: 50%;
              width: 32px;
              height: 32px;
              display: flex;
              align-items: center;
              justify-content: center;
              
              i {
                font-size: 16px;
              }
            }
            
            span {
              color: #606266;
              font-size: 14px;
            }
          }
        }
      }

      .quick-actions {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 16px;
        margin: 20px;

        .el-button {
          width: 100%;
          height: 40px;
          border-radius: 8px;
          transition: all 0.3s;
          
          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 2px 12px rgba(64, 158, 255, 0.2);
          }
        }
      }

      .system-info {
        border-top: 1px solid rgba(64, 158, 255, 0.1);
        padding: 20px;
        margin: 0 20px;

        .info-item {
          display: flex;
          justify-content: space-between;
          margin-bottom: 12px;
          color: #606266;

          .label {
            color: #909399;
          }

          &:last-child {
            margin-bottom: 0;
          }
        }
      }
    }
  }
}
</style> 