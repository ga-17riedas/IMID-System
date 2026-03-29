/**
 * 诊断相关API模块
 * 处理诊断记录的获取和管理功能
 */
import request from '@/utils/request'

/**
 * 获取诊断记录列表
 * 
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码（从1开始）
 * @param {number} params.size - 每页记录数
 * @param {string} [params.sort] - 排序字段和方向
 * @returns {Promise} - 返回诊断记录列表的Promise
 */
export function getDiagnosisRecords(params) {
  return request({
    url: '/api/patient/reports',
    method: 'get',
    params: {
      page: params.page - 1, // Spring Data JPA 的页码从0开始
      size: params.size,
      sort: 'createdAt,desc' // 按创建时间降序排序
    }
  })
}

/**
 * 获取诊断记录详情
 * 
 * @param {string|number} id - 诊断记录ID
 * @returns {Promise} - 返回诊断记录详情的Promise
 */
export function getDiagnosisDetail(id) {
  return request({
    url: `/api/patient/reports/${id}`,
    method: 'get'
  }).catch(error => {
    console.error('API Error:', error)
    throw error
  })
}

/**
 * 获取最近的诊断记录
 * 
 * @param {Object} params - 查询参数
 * @param {number} [params.limit=3] - 获取记录数量限制
 * @returns {Promise} - 返回最近诊断记录的Promise
 */
export function getRecentDiagnosis(params) {
  return request({
    url: '/api/patient/reports/recent',
    method: 'get',
    params
  })
} 