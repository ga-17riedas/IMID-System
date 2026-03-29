/**
 * 患者相关API模块
 * 处理患者健康数据和记录的获取和管理
 */
import request from '@/utils/request'

/**
 * 获取患者健康数据概览
 * 返回包含最新血压、心率、血糖等健康指标的数据
 * 
 * @returns {Promise} - 返回健康数据概览的Promise
 */
export function getHealthOverview() {
  return request({
    url: '/api/patient/home/overview',
    method: 'get'
  })
}

/**
 * 获取患者最近的健康记录
 * 返回患者近期录入的健康数据记录列表
 * 
 * @returns {Promise} - 返回最近健康记录的Promise
 */
export function getRecentRecords() {
  return request({
    url: '/api/patient/home/recent-records',
    method: 'get'
  })
} 