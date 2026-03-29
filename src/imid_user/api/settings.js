import request from '@/utils/request'

/**
 * 获取系统设置
 * @returns {Promise} 返回系统设置数据
 */
export function getSystemSettings() {
  console.log('获取系统设置API调用')
  return request({
    url: '/api/admin/settings',
    method: 'get',
    params: { _t: new Date().getTime() } // 添加时间戳防止缓存
  }).catch(error => {
    console.error('获取系统设置失败:', error)
    // 返回默认设置以防止UI崩溃
    return {
      data: {
        systemName: 'IMID 医学影像诊断系统',
        systemDescription: '智能医学影像诊断辅助平台',
        logoUrl: '',
        maintenanceMode: false,
        theme: 'light',
        passwordStrength: 'medium',
        logLevel: 'INFO',
        storageType: 'local'
      }
    }
  })
}

/**
 * 更新系统设置
 * @param {Array|Object} data 系统设置数据，可能是数组或对象
 * @returns {Promise} 返回响应
 */
export function updateSystemSettings(data) {
  console.log('更新系统设置API调用:', data)
  
  // 确保数据是数组格式，这是后端API期望的格式
  let settingsData = data;
  
  // 如果是对象格式，将其转换为数组
  if (data && typeof data === 'object' && !Array.isArray(data)) {
    settingsData = Object.entries(data).map(([key, value]) => ({
      key,
      value: typeof value === 'object' ? JSON.stringify(value) : String(value),
      description: `${key} setting`,
      group: 'SYSTEM'
    }));
  }
  
  return request({
    url: '/api/admin/settings',
    method: 'put',
    data: settingsData,
    // 增加错误处理
    validateStatus: function (status) {
      return status < 500; // 接受所有非服务器错误的状态码
    }
  }).then(response => {
    // 检查状态码
    if (response.status >= 200 && response.status < 300) {
      return response;
    } else {
      // 提取错误消息
      const errorMessage = response.data?.message || '请求失败，请检查输入参数';
      const error = new Error(errorMessage);
      error.response = response;
      throw error;
    }
  }).catch(error => {
    // 特殊处理400错误，尝试提供更有用的信息
    if (error.response?.status === 400) {
      console.error('设置更新格式错误，服务器返回:', error.response.data);
      // 重新抛出带有更友好消息的错误
      throw new Error('设置格式不正确，请检查输入数据');
    }
    throw error;
  });
}

/**
 * 获取用户设置
 * @returns {Promise} 返回响应
 */
export function getUserSettings() {
  return request({
    url: '/api/settings/user',
    method: 'get'
  })
}

/**
 * 更新用户设置
 * @param {Object} data 用户设置数据
 * @returns {Promise} 返回响应
 */
export function updateUserSettings(data) {
  return request({
    url: '/api/admin/settings/user',
    method: 'put',
    data
  })
}

/**
 * 重置系统设置
 * @returns {Promise} 返回响应
 */
export function resetSystemSettings() {
  return request({
    url: '/api/admin/settings/reset',
    method: 'post'
  })
}

/**
 * 上传系统Logo
 * @param {File} file Logo文件
 * @returns {Promise} 返回上传结果
 */
export function uploadSystemLogo(file) {
  const formData = new FormData()
  formData.append('file', file)
  
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
 * 获取系统主题
 * @returns {Promise} 返回主题设置
 */
export function getSystemTheme() {
  return request({
    url: '/api/admin/settings/theme',
    method: 'get'
  }).catch(() => {
    // 如果请求失败，返回默认主题
    return { data: { theme: 'light' } }
  })
}

/**
 * 更新系统主题
 * @param {String} theme 主题名称
 * @returns {Promise} 返回响应
 */
export function updateSystemTheme(theme) {
  return request({
    url: '/api/admin/settings/theme',
    method: 'put',
    data: { theme }
  })
} 