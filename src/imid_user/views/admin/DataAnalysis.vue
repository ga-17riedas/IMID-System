<template>
  <div class="data-analysis">
    <el-card class="analysis-card">
      <div slot="header">
        <span>数据分析控制面板</span>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          @change="fetchAnalyticsData"
          style="float: right; margin-top: -5px;"
        />
      </div>
      
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card shadow="hover" class="metric-card">
            <div class="metric-title">总诊断数</div>
            <div class="metric-value">{{ totalDiagnoses }}</div>
          </el-card>
        </el-col>
        
        <el-col :span="12">
          <el-card shadow="hover" class="metric-card">
            <div class="metric-title">AI诊断准确率</div>
            <div class="metric-value">{{ aiAccuracy }}</div>
          </el-card>
        </el-col>
      </el-row>
      
      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="24">
          <el-card shadow="hover" class="chart-card">
            <div slot="header">诊断数量趋势</div>
            <div id="diagnoses-trend-chart" style="height: 300px;"></div>
          </el-card>
        </el-col>
      </el-row>
      
      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="12">
          <el-card shadow="hover" class="chart-card">
            <div slot="header">AI诊断准确率分布</div>
            <div id="accuracy-distribution-chart" style="height: 300px;"></div>
          </el-card>
        </el-col>
        
        <el-col :span="12">
          <el-card shadow="hover" class="chart-card">
            <div slot="header">疾病类型分布</div>
            <div id="disease-distribution-chart" style="height: 300px;"></div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script>
import * as echarts from 'echarts';
import { getAnalyticsData } from '@/api/analytics';
import { formatDate } from '@/utils/date';

export default {
  data() {
    return {
      dateRange: [new Date(new Date().setMonth(new Date().getMonth() - 1)), new Date()],
      totalDiagnoses: 0,
      diagnosesTrend: 0,
      DaiAccuracy: '0%',
      accuracyTrend: 0,
      charts: {
        diagnosesTrend: null,
        accuracyDistribution: null,
        diseaseDistribution: null
      },
      analyticsData: null
    };
  },
  
  mounted() {
    this.fetchAnalyticsData();
    window.addEventListener('resize', this.resizeCharts);
  },
  
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeCharts);
    Object.values(this.charts).forEach(chart => {
      if (chart) chart.dispose();
    });
  },
  
  methods: {
    async fetchAnalyticsData() {
      try {
        const startDate = formatDate(this.dateRange[0]);
        const endDate = formatDate(this.dateRange[1]);
        
        console.log('请求分析数据，日期范围:', startDate, '至', endDate);
        const response = await getAnalyticsData(startDate, endDate);
        
        // 从响应中提取实际数据
        this.analyticsData = response.data || {};
        console.log('提取的分析数据:', JSON.stringify(this.analyticsData));
        
        this.totalDiagnoses = this.analyticsData.totalDiagnoses || 0;
        this.diagnosesTrend = this.analyticsData.diagnosesTrend || 0;
        this.aiAccuracy = this.analyticsData.aiAccuracy || '0%';
        this.accuracyTrend = this.analyticsData.accuracyTrend || 0;
        
        this.$nextTick(() => {
          console.log('初始化图表...');
          this.initCharts();
        });
      } catch (error) {
        console.error('获取分析数据失败:', error);
        this.$message.error('获取分析数据失败: ' + error.message);
      }
    },
    
    initCharts() {
      console.log('开始初始化所有图表');
      this.initDiagnosesTrendChart();
      this.initAccuracyDistributionChart();
      this.initDiseaseDistributionChart();
      
      // 强制重新渲染所有图表
      this.$nextTick(() => {
        console.log('强制重新渲染所有图表');
        this.resizeCharts();
      });
    },
    
    initDiagnosesTrendChart() {
      const chartDom = document.getElementById('diagnoses-trend-chart');
      if (!chartDom) {
        console.error('找不到趋势图表DOM元素');
        return;
      }
      
      console.log('初始化诊断趋势图表');
      if (this.charts.diagnosesTrend) {
        this.charts.diagnosesTrend.dispose();
      }
      this.charts.diagnosesTrend = echarts.init(chartDom);
      
      const option = {
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['总诊断数', 'AI辅助诊断数']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: this.analyticsData.timeLabels || []
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '总诊断数',
            type: 'line',
            data: this.analyticsData.totalDiagnosesData || [],
            smooth: true,
            lineStyle: {
              width: 3
            }
          },
          {
            name: 'AI辅助诊断数',
            type: 'line',
            data: this.analyticsData.aiDiagnosesData || [],
            smooth: true,
            lineStyle: {
              width: 3
            }
          }
        ]
      };
      
      console.log('诊断趋势图表配置:', JSON.stringify(option));
      this.charts.diagnosesTrend.setOption(option);
    },
    
    initAccuracyDistributionChart() {
      const chartDom = document.getElementById('accuracy-distribution-chart');
      if (!chartDom) {
        console.error('找不到准确率分布图表DOM元素');
        return;
      }
      
      console.log('初始化准确率分布图表');
      if (this.charts.accuracyDistribution) {
        this.charts.accuracyDistribution.dispose();
      }
      this.charts.accuracyDistribution = echarts.init(chartDom);
      
      const accuracyData = this.analyticsData.accuracyDistribution || {};
      console.log('准确率分布数据:', JSON.stringify(accuracyData));
      
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 10,
          data: ['90-100%', '80-90%', '70-80%', '60-70%', '<60%']
        },
        series: [
          {
            name: 'AI诊断准确率',
            type: 'pie',
            radius: ['50%', '70%'],
            avoidLabelOverlap: false,
            label: {
              show: true,
              position: 'outside',
              formatter: '{b}: {c}'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '18',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: true
            },
            data: [
              { value: accuracyData['90-100'] || 0, name: '90-100%' },
              { value: accuracyData['80-90'] || 0, name: '80-90%' },
              { value: accuracyData['70-80'] || 0, name: '70-80%' },
              { value: accuracyData['60-70'] || 0, name: '60-70%' },
              { value: accuracyData['<60'] || 0, name: '<60%' }
            ]
          }
        ]
      };
      
      console.log('准确率分布图表配置:', JSON.stringify(option));
      this.charts.accuracyDistribution.setOption(option);
    },
    
    initDiseaseDistributionChart() {
      const chartDom = document.getElementById('disease-distribution-chart');
      if (!chartDom) {
        console.error('找不到疾病分布图表DOM元素');
        return;
      }
      
      console.log('初始化疾病分布图表');
      if (this.charts.diseaseDistribution) {
        this.charts.diseaseDistribution.dispose();
      }
      this.charts.diseaseDistribution = echarts.init(chartDom);
      
      const diseaseData = this.analyticsData.diseaseDistribution || {};
      console.log('疾病分布数据:', JSON.stringify(diseaseData));
      
      const data = Object.entries(diseaseData).map(([name, value]) => ({ name, value }));
      
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 10,
          data: data.map(item => item.name)
        },
        series: [
          {
            name: '疾病类型',
            type: 'pie',
            radius: '55%',
            center: ['50%', '60%'],
            data: data,
            label: {
              show: true,
              formatter: '{b}: {c}'
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      };
      
      console.log('疾病分布图表配置:', JSON.stringify(option));
      this.charts.diseaseDistribution.setOption(option);
    },
    
    resizeCharts() {
      Object.values(this.charts).forEach(chart => {
        if (chart) chart.resize();
      });
    }
  }
};
</script>

<style scoped>
.data-analysis {
  padding: 20px;
}

.analysis-card {
  margin-bottom: 20px;
}

.metric-card {
  height: 120px;
  text-align: center;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.metric-title {
  font-size: 16px;
  color: #606266;
  margin-bottom: 10px;
}

.metric-value {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 10px;
}

.chart-card {
  margin-bottom: 20px;
}
</style> 