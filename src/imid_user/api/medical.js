/**
 * 医学影像相关API
 * 处理医学影像分析、管理和诊断记录相关的后端请求
 */
import request from '@/utils/request'

/**
 * 发送医学影像分析请求
 * @param {Object} data - 包含imageUrl和analysisType的对象
 * @returns {Promise} - 返回分析结果的Promise
 */
export function analyzeImage(data) {
  return request({
    url: '/api/medical/analyze',
    method: 'post',
    data
  })
}

/**
 * 获取医学影像列表
 * @param {Object} params - 分页和筛选参数
 * @returns {Promise} - 返回影像列表的Promise
 */
export function getModelList(params) {
  return request({
    url: '/api/admin/images',  // 确保这个路径与后端控制器路径匹配
    method: 'get',
    params
  })
}

/**
 * 删除指定ID的医学影像
 * @param {number|string} id - 要删除的影像ID
 * @returns {Promise} - 返回删除操作结果的Promise
 */
export function deleteImage(id) {
  return request({
    url: `/api/admin/images/${id}`,
    method: 'delete'
  })
}

/**
 * 获取患者病史记录
 * @param {number|string} patientId - 患者ID
 * @returns {Promise} - 返回患者病史的Promise
 */
export function getPatientHistory(patientId) {
  return request({
    url: `/api/medical/history/${patientId}`,
    method: 'get'
  })
}

/**
 * 获取诊断记录列表
 * @param {Object} params - 分页和筛选参数
 * @returns {Promise} - 返回诊断记录列表的Promise
 */
export function getDiagnosisRecords(params) {
  return request({
    url: '/api/medical/diagnosis',
    method: 'get',
    params
  })
}

/**
 * 获取医疗数据列表
 * @param {Object} params - 分页和筛选参数
 * @returns {Promise} - 返回医疗数据列表的Promise
 */
export function getMedicalData(params) {
  return request({
    url: '/api/admin/medical-data',
    method: 'get',
    params
  })
}

/**
 * 删除指定ID的医疗数据
 * @param {number|string} id - 要删除的医疗数据ID
 * @returns {Promise} - 返回删除操作结果的Promise
 */
export function deleteMedicalData(id) {
  return request({
    url: `/api/admin/medical-data/${id}`,
    method: 'delete'
  })
}