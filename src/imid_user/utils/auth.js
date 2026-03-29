import Cookies from 'js-cookie'

// Token密钥名称
const TokenKey = 'imid_token'
// 用户信息密钥
const UserKey = 'imid_user_info'

/**
 * 获取Token
 * @returns {String} 认证令牌
 */
export function getToken() {
  return Cookies.get(TokenKey)
}

/**
 * 设置Token
 * @param {String} token 认证令牌
 */
export function setToken(token) {
  return Cookies.set(TokenKey, token)
}

/**
 * 移除Token
 */
export function removeToken() {
  return Cookies.remove(TokenKey)
}

export function getAuthHeaders() {
  const token = getToken()
  return token ? { Authorization: `Bearer ${token}` } : {}
}

export function getUser() {
  const userStr = localStorage.getItem(UserKey)
  return userStr ? JSON.parse(userStr) : null
}

export function setUser(user) {
  return localStorage.setItem(UserKey, JSON.stringify(user))
}

export function removeUser() {
  return localStorage.removeItem(UserKey)
} 