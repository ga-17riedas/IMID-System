import request from '@/utils/request'

/**
 * 获取系统日志列表
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getSystemLogs(params) {
  // 添加时间戳防止缓存
  const timestamp = new Date().getTime();
  return request({
    url: '/api/system/logs',
    method: 'get',
    params: { ...params, _t: timestamp }
  })
}

/**
 * 获取最近系统日志
 * @param {Number} limit - 数量限制
 * @returns {Promise}
 */
export function getRecentLogs(limit = 10) {
  const timestamp = new Date().getTime();
  return request({
    url: '/api/system/logs/recent',
    method: 'get',
    params: { limit, _t: timestamp }
  })
}

/**
 * 获取错误日志
 * @returns {Promise}
 */
export function getErrorLogs() {
  const timestamp = new Date().getTime();
  return request({
    url: '/api/system/logs/errors',
    method: 'get',
    params: { _t: timestamp }
  })
}

/**
 * 清除系统日志
 * @param {Object} data - 清除参数，包含level、startDate、endDate
 * @returns {Promise}
 */
export function clearSystemLogs(data) {
  return request({
    url: '/api/system/logs/clear',
    method: 'post',
    data
  })
}

/**
 * 添加日志
 * @param {Object} data - 日志数据
 * @returns {Promise}
 */
export function addSystemLog(data) {
  return request({
    url: '/api/system/logs',
    method: 'post',
    data
  })
}

/**
 * 获取系统指标
 * @returns {Promise}
 */
export function getSystemMetrics() {
  const timestamp = new Date().getTime();
  return request({
    url: '/api/admin/system/metrics',
    method: 'get',
    params: { _t: timestamp }
  })
}

/**
 * 获取系统统计数据
 * @returns {Promise}
 */
export function getSystemStatistics() {
  const timestamp = new Date().getTime();
  return request({
    url: '/api/admin/system/statistics',
    method: 'get',
    params: { _t: timestamp }
  })
}

/**
 * 获取性能指标
 * @returns {Promise}
 */
export function getPerformanceMetrics() {
  const timestamp = new Date().getTime();
  return request({
    url: '/api/admin/system/performance',
    method: 'get',
    params: { _t: timestamp }
  })
}

/**
 * 获取最新的系统日志
 * @param {String} level - 日志级别（可选）
 * @param {Number} limit - 数量限制，默认500条
 * @returns {Promise}
 */
export function getLatestLogs(level, limit = 500) {
  const timestamp = new Date().getTime();
  const params = { _t: timestamp };
  
  if (level) {
    params.level = level;
  }
  
  if (limit) {
    params.limit = limit;
  }
  
  // 请求之前记录日志
  console.log('请求最新日志API，参数:', params);
  
  return request({
    url: '/api/system/logs/latest',
    method: 'get',
    params,
    // 增加错误处理和超时设置
    timeout: 10000, // 10秒超时
    // 添加错误处理钩子
    validateStatus: function (status) {
      // 返回true表示不抛出HTTP错误，我们会在响应拦截器中处理
      return true;
    }
  }).then(response => {
    if (response.status >= 200 && response.status < 300) {
      return response;
    }
    
    // 如果服务器返回错误，包装为模拟数据以防止UI崩溃
    console.warn(`获取最新日志失败，HTTP状态码: ${response.status}`);
    
    // 返回一个模拟的成功响应，包含一条错误日志
    return {
      data: {
        data: [{
          id: `error-${Date.now()}`,
          timestamp: new Date(),
          level: 'ERROR',
          source: '系统日志服务',
          message: `获取日志失败，HTTP错误: ${response.status}`,
          details: response.statusText || '请检查服务器连接'
        }]
      }
    };
  }).catch(error => {
    console.error('获取最新日志异常:', error);
    
    // 返回一个模拟的成功响应，包含一条错误日志
    return {
      data: {
        data: [{
          id: `exception-${Date.now()}`,
          timestamp: new Date(),
          level: 'ERROR',
          source: '系统日志客户端',
          message: '系统日志获取失败，请检查网络连接',
          details: error.message || '未知错误'
        }]
      }
    };
  });
}

