/**
 * HTTP请求封装模块
 * 基于axios的请求库，统一处理HTTP请求和响应
 */
import axios from 'axios'
import { getToken, removeToken, removeUser, setToken } from '@/utils/auth'
import { message } from 'ant-design-vue'
import router from '@/router'
import { MessageBox, Message } from 'element-ui'
import store from '@/store'

/**
 * 创建axios实例
 * 配置基础URL、超时时间、跨域凭证和默认请求头
 */
const request = axios.create({
  baseURL: process.env.VUE_APP_BASE_API || 'http://localhost:8080',
  timeout: 60000,  // 60秒超时时间
  withCredentials: true,  // 允许跨域请求携带凭证
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
})

/**
 * 请求拦截器
 * 在发送请求前自动添加认证令牌
 */
request.interceptors.request.use(
  config => {
    const token = getToken()
    console.log('Request URL:', config.url)
    console.log('Current token:', token)
    
    if (token) {
      // 确保Bearer token格式正确
      config.headers['Authorization'] = `Bearer ${token}`
      console.log('Setting Authorization header:', config.headers['Authorization'])
    } else {
      console.log('No token found')
    }
    
    // 记录请求日志
    if (process.env.NODE_ENV === 'development') {
      console.log('请求配置:', {
        url: config.url,
        method: config.method,
        params: config.params,
        data: config.data
      });
    }
    
    return config
  },
  error => {
    // 处理请求错误
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

/**
 * 响应拦截器
 * 处理响应数据和统一处理错误码
 * 可以处理令牌刷新和会话过期等场景
 */
request.interceptors.response.use(
  response => {
    // 处理令牌刷新
    const newToken = response.headers['new-token']
    if (newToken) {
      setToken(newToken)
    }
    return response
  },
  error => {
    // 处理响应错误
    let errorMessage = '连接服务器失败';
    
    if (error.code === 'ECONNABORTED') {
      errorMessage = '请求超时，请检查网络连接';
    } else if (error.response) {
      // 服务器返回了错误状态码
      const status = error.response.status;
      
      switch (status) {
        case 400:
          errorMessage = '请求参数错误';
          break;
        case 401:
          errorMessage = '未授权，请重新登录';
          // 可以在此处理登出逻辑
          store.dispatch('user/resetToken').then(() => {
            location.reload()
          });
          break;
        case 403:
          errorMessage = '拒绝访问';
          break;
        case 404:
          errorMessage = '请求的资源不存在';
          break;
        case 500:
          errorMessage = '服务器内部错误';
          break;
        default:
          errorMessage = `请求失败(${status})`;
      }
    } else if (error.request) {
      // 请求已发出，但没有收到响应
      errorMessage = '服务器未响应，请检查网络连接';
    }
    
    // 只在非开发环境或明确不是404错误时显示消息
    // 防止在开发环境频繁弹出错误消息
    if (!(process.env.NODE_ENV === 'development' && error.response && error.response.status === 404)) {
      Message({
        message: errorMessage,
        type: 'error',
        duration: 5 * 1000
      });
    }
    
    console.error('API请求错误:', error);
    
    // 如果请求包含noErrorHandle标记，则不统一处理错误
    if (error.config && error.config.noErrorHandle) {
      return Promise.reject(error);
    }
    
    // 构造一个友好的错误对象返回
    return Promise.reject({
      message: errorMessage,
      status: error.response ? error.response.status : null,
      originalError: error
    });
  }
)

export default request 