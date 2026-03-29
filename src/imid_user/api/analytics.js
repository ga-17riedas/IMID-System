import request from '@/utils/request'

export function getAnalyticsData(startDate, endDate) {
  return request({
    url: '/api/admin/analytics',
    method: 'get',
    params: {
      startDate,
      endDate
    }
  })
} 