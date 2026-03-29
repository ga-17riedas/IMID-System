/**
 * 医生相关API模块
 * 处理医生首页数据、患者管理、日程安排和诊断报告相关功能
 */
import request from '@/utils/request'

/**
 * 获取医生首页统计数据
 * 
 * @returns {Promise} - 返回包含待分析影像数、今日患者数和报告总数的Promise
 */
export function getHomeStats() {
  return request({
    url: '/api/doctor/home/stats',
    method: 'get'
  })
}

/**
 * 获取医生的患者列表
 * 
 * @returns {Promise} - 返回医生关联的患者列表的Promise
 */
export function getPatients() {
  return request({
    url: '/api/doctor/patients',
    method: 'get'
  })
}

/**
 * 获取医生日程安排
 * 
 * @param {Object} params - 查询参数
 * @param {string} params.startDate - 开始日期
 * @param {string} params.endDate - 结束日期
 * @returns {Promise} - 返回日程安排列表的Promise
 */
export function getSchedules(params) {
  return request({
    url: '/api/doctor/schedules',
    method: 'get',
    params
  })
}

/**
 * 获取医生个人资料
 * 
 * @returns {Promise} - 返回医生个人资料的Promise
 */
export function getDoctorProfile() {
  return request({
    url: '/api/doctor/profile',
    method: 'get'
  })
}

/**
 * 获取患者病史记录
 * 
 * @param {string|number} patientId - 患者ID
 * @returns {Promise} - 返回患者病史记录的Promise
 */
export function getPatientHistory(patientId) {
  return request({
    url: `/api/doctors/patients/${patientId}/history`,
    method: 'get'
  })
}

/**
 * 获取患者诊断报告列表
 * 
 * @param {string|number} patientId - 患者ID
 * @returns {Promise} - 返回患者诊断报告列表的Promise
 */
export function getPatientReports(patientId) {
  return request({
    url: `/api/doctor/patients/${patientId}/reports`,
    method: 'get'
  })
}

/**
 * 导出诊断报告
 * 将报告导出为PDF文件并自动下载
 * 
 * @param {string|number} reportId - 报告ID
 * @returns {Promise} - 处理下载的Promise
 */
export function exportReport(reportId) {
  return request({
    url: `/api/doctor/reports/${reportId}/export`,
    method: 'get',
    responseType: 'blob'
  }).then(response => {
    // 创建blob链接
    const blob = new Blob([response.data], { type: 'application/pdf' })
    const url = window.URL.createObjectURL(blob)
    
    // 创建一个临时链接并点击它来下载文件
    const link = document.createElement('a')
    link.href = url
    link.download = 'report.pdf'
    link.click()
    
    // 清理
    window.URL.revokeObjectURL(url)
  })
}

/**
 * 更新患者信息
 * 
 * @param {string|number} patientId - 患者ID
 * @param {Object} data - 更新的患者信息
 * @returns {Promise} - 返回更新结果的Promise
 */
export function updatePatient(patientId, data) {
  return request({
    url: `/api/doctors/patients/${patientId}`,
    method: 'put',
    data
  })
}

/**
 * 更新患者病史记录
 * 
 * @param {string|number} patientId - 患者ID
 * @param {string|number} historyId - 病史记录ID
 * @param {Object} data - 更新的病史记录数据
 * @returns {Promise} - 返回更新结果的Promise
 */
export function updateMedicalHistory(patientId, historyId, data) {
  return request({
    url: `/api/doctors/patients/${patientId}/history/${historyId}`,
    method: 'put',
    data
  })
}

/**
 * 创建患者病史记录
 * 
 * @param {string|number} patientId - 患者ID
 * @param {Object} data - 新病史记录数据
 * @returns {Promise} - 返回创建结果的Promise
 */
export function createMedicalHistory(patientId, data) {
  return request({
    url: `/api/doctors/patients/${patientId}/history`,
    method: 'post',
    data
  })
}

/**
 * 获取患者诊断记录
 * 
 * @param {string|number} patientId - 患者ID
 * @returns {Promise} - 返回患者诊断记录的Promise
 */
export function getPatientDiagnosis(patientId) {
  return request({
    url: `/api/doctors/patients/${patientId}/diagnosis`,
    method: 'get'
  })
} 