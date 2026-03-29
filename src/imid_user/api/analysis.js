import request from '@/utils/request'

// 获取统计数据
export function getStatistics(params) {
  return request({
    url: '/api/admin/analysis/statistics',
    method: 'get',
    params
  })
}

// 获取图表数据
export function getChartData(params) {
  return request({
    url: '/api/admin/analysis/charts',
    method: 'get',
    params
  })
} 