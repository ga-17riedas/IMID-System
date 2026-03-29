/**
 * 认证相关API模块
 * 处理用户登录、注册、登出和获取用户信息
 */
import request from '@/utils/request'

/**
 * 用户登录
 * 
 * @param {Object} data - 登录参数对象
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @param {string} data.userType - 用户类型（DOCTOR/PATIENT/ADMIN）
 * @returns {Promise} - 返回包含token和用户信息的Promise
 */
export function login(data) {
  return request({
    url: '/api/auth/login',
    method: 'post',
    data
  })
}

/**
 * 用户注册
 * 
 * @param {Object} data - 注册参数对象
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @param {string} data.email - 邮箱
 * @param {string} data.fullName - 姓名
 * @param {string} data.userType - 用户类型
 * @returns {Promise} - 返回注册结果的Promise
 */
export function register(data) {
  return request({
    url: '/api/auth/register',
    method: 'post',
    data
  })
}

/**
 * 用户登出
 * 发送登出请求，清除服务器端会话
 * 
 * @returns {Promise} - 返回登出结果的Promise
 */
export function logout() {
  return request({
    url: '/api/auth/logout',
    method: 'post'
  })
}

/**
 * 获取当前用户信息
 * 
 * @returns {Promise} - 返回用户信息的Promise
 */
export function getInfo() {
  return request({
    url: '/api/auth/info',
    method: 'get'
  })
} 