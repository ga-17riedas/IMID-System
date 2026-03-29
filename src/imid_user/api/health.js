/**
 * 健康记录相关API模块
 * 处理患者健康记录的获取、创建、更新和删除
 */
import request from '@/utils/request'

/**
 * 获取患者健康记录列表
 * 
 * @returns {Promise} - 返回患者所有健康记录的Promise
 */
export function getHealthRecords() {
  return request({
    url: '/api/patient/health/records',
    method: 'get'
  })
}

/**
 * 获取患者健康统计数据
 * 返回计算统计后的健康指标数据，如平均血压、心率趋势等
 * 
 * @returns {Promise} - 返回健康统计数据的Promise
 */
export function getHealthStats() {
  return request({
    url: '/api/patient/health/stats',
    method: 'get'
  })
}

/**
 * 创建新的健康记录
 * 
 * @param {Object} data - 健康记录数据
 * @param {string} data.recordType - 记录类型（如血压、心率、血糖等）
 * @param {Object} data.values - 记录值对象
 * @param {string} data.recordDate - 记录日期
 * @returns {Promise} - 返回创建结果的Promise
 */
export function createHealthRecord(data) {
  return request({
    url: '/api/patient/health/records',
    method: 'post',
    data
  })
}

/**
 * 更新已有的健康记录
 * 
 * @param {string|number} id - 健康记录ID
 * @param {Object} data - 更新的健康记录数据
 * @returns {Promise} - 返回更新结果的Promise
 */
export function updateHealthRecord(id, data) {
  return request({
    url: `/api/patient/health/records/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除健康记录
 * 
 * @param {string|number} id - 要删除的健康记录ID
 * @returns {Promise} - 返回删除结果的Promise
 */
export function deleteHealthRecord(id) {
  return request({
    url: `/api/patient/health/records/${id}`,
    method: 'delete'
  })
} 