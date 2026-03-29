import request from '@/utils/request'

// 患者管理接口
export function getPatients(params) {
  return request({
    url: '/api/admin/patients',
    method: 'get',
    params
  })
}

export function createPatient(data) {
  return request({
    url: '/api/admin/patients',
    method: 'post',
    data
  })
}

export function updatePatient(id, data) {
  return request({
    url: `/api/admin/patients/${id}`,
    method: 'put',
    data
  })
}

export function deletePatient(id) {
  return request({
    url: `/api/admin/patients/${id}`,
    method: 'delete'
  })
}

export function getPatientHistory(id) {
  return request({
    url: `/api/admin/patients/${id}/history`,
    method: 'get'
  })
}

// 医生管理接口
export function getDoctors(params) {
  return request({
    url: '/api/admin/doctors',
    method: 'get',
    params
  })
}

export function createDoctor(data) {
  return request({
    url: '/api/admin/doctors',
    method: 'post',
    data
  })
}

export function updateDoctor(id, data) {
  return request({
    url: `/api/admin/doctors/${id}`,
    method: 'put',
    data
  })
}

export function deleteDoctor(id) {
  return request({
    url: `/api/admin/doctors/${id}`,
    method: 'delete'
  })
}

// 管理员管理接口
export function getAdmins(params) {
  return request({
    url: '/api/admin/admins',
    method: 'get',
    params
  })
}

export function createAdmin(data) {
  return request({
    url: '/api/admin/admins',
    method: 'post',
    data
  })
}

export function updateAdminStatus(id, data) {
  return request({
    url: `/api/admin/admins/${id}/status`,
    method: 'put',
    data
  })
}

export function resetAdminPassword(id) {
  return request({
    url: `/api/admin/admins/${id}/reset-password`,
    method: 'post'
  })
}

// 获取影像列表
export function getImageList(params) {
  return request({
    url: '/api/admin/medical-images',
    method: 'get',
    params
  })
}

// 删除影像
export function deleteImage(id) {
  return request({
    url: `/api/admin/medical-images/${id}`,
    method: 'delete'
  })
}

// 获取影像详情
export function getImageDetail(id) {
  return request({
    url: `/api/admin/medical-images/${id}`,
    method: 'get'
  })
}

// 获取报告列表
export function getReportList(params) {
  return request({
    url: '/api/admin/medical-reports',
    method: 'get',
    params
  })
}

// 获取报告详情
export function getReportDetail(id) {
  return request({
    url: `/api/admin/medical-reports/${id}`,
    method: 'get'
  })
}

// 更新报告
export function updateReport(id, data) {
  return request({
    url: `/api/admin/medical-reports/${id}`,
    method: 'put',
    data
  })
}

// 导出报告
export function exportReport(id) {
  return request({
    url: `/api/admin/medical-reports/${id}/export`,
    method: 'get',
    responseType: 'blob'
  })
}

// 批量导出报告
export function exportBatchReports(ids) {
  return request({
    url: '/api/admin/medical-reports/batch-export',
    method: 'post',
    data: { ids },
    responseType: 'blob'
  })
}

// 批量删除影像
export function deleteBatchImages(ids) {
  return request({
    url: '/api/admin/medical-images/batch',
    method: 'delete',
    data: { ids }
  })
}

// 获取报告模板列表
export function getTemplates() {
  return request({
    url: '/api/admin/report-templates',
    method: 'get'
  })
}

// 创建报告模板
export function createTemplate(data) {
  return request({
    url: '/api/admin/report-templates',
    method: 'post',
    data
  })
}

// 更新报告模板
export function updateTemplate(id, data) {
  return request({
    url: `/api/admin/report-templates/${id}`,
    method: 'put',
    data
  })
}

// 删除报告模板
export function deleteTemplate(id) {
  return request({
    url: `/api/admin/report-templates/${id}`,
    method: 'delete'
  })
}

// 从admin/system.js重新导出所有函数
export { 
  getSystemLogs, 
  clearSystemLogs, 
  getErrorLogs, 
  getSystemMetrics, 
  getSystemStatistics,
  getPerformanceMetrics,
  getLatestLogs
} from './admin/system'

// 导出其他可能需要的函数
// export { ... } from './admin/backup' 