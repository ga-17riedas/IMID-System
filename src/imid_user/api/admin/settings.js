import request from '@/utils/request'

/**
 * 获取所有系统设置
 * @returns {Promise}
 */
export function getAllSettings() {
  return request({
    url: '/api/admin/settings',
    method: 'get'
  })
}

/**
 * 按组获取系统设置
 * @returns {Promise}
 */
export function getSettingsByGroups() {
  return request({
    url: '/api/admin/settings/groups',
    method: 'get'
  })
}

/**
 * 获取系统基本设置
 * @returns {Promise}
 */
export function getBasicSettings() {
  return request({
    url: '/api/admin/settings/basic',
    method: 'get'
  })
}

/**
 * 获取系统安全设置
 * @returns {Promise}
 */
export function getSecuritySettings() {
  return request({
    url: '/api/admin/settings/security',
    method: 'get'
  })
}

/**
 * 获取日志设置
 * @returns {Promise}
 */
export function getLogSettings() {
  return request({
    url: '/api/admin/settings/log',
    method: 'get'
  })
}

/**
 * 获取存储设置
 * @returns {Promise}
 */
export function getStorageSettings() {
  return request({
    url: '/api/admin/settings/storage',
    method: 'get'
  })
}

/**
 * 获取备份设置
 * @returns {Promise}
 */
export function getBackupSettings() {
  return request({
    url: '/api/admin/settings/backup',
    method: 'get'
  })
}

/**
 * 获取AI模型设置
 * @returns {Promise}
 */
export function getAISettings() {
  return request({
    url: '/api/admin/settings/ai',
    method: 'get'
  })
}

/**
 * 更新系统设置
 * @param {Array} settings - 设置数组
 * @returns {Promise}
 */
export function updateSettings(settings) {
  return request({
    url: '/api/admin/settings',
    method: 'put',
    data: settings
  })
}

/**
 * 更新单个设置
 * @param {String} key - 设置键
 * @param {Object} setting - 设置对象
 * @returns {Promise}
 */
export function updateSetting(key, setting) {
  return request({
    url: `/api/admin/settings/${key}`,
    method: 'put',
    data: setting
  })
}

/**
 * 上传系统Logo
 * @param {FormData} formData - 包含文件的表单数据
 * @returns {Promise}
 */
export function uploadLogo(formData) {
  return request({
    url: '/api/admin/settings/logo',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 重置系统设置
 * @param {String} group - 设置组，可选
 * @returns {Promise}
 */
export function resetSettings(group) {
  return request({
    url: '/api/admin/settings/reset',
    method: 'post',
    params: group ? { group } : {}
  })
} 