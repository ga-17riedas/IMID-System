/**
 * 日期格式化工具模块
 * 提供日期和日期时间的格式化功能
 */

/**
 * 将日期格式化为YYYY-MM-DD格式
 * 
 * @param {Date|string} date - 要格式化的日期对象或日期字符串
 * @returns {string} 格式化后的日期字符串，格式为YYYY-MM-DD
 */
export function formatDate(date) {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

/**
 * 将日期格式化为YYYY-MM-DD HH:MM格式
 * 
 * @param {Date|string} date - 要格式化的日期对象或日期字符串
 * @returns {string} 格式化后的日期时间字符串，格式为YYYY-MM-DD HH:MM
 */
export function formatDateTime(date) {
  if (!date) return ''
  const d = new Date(date)
  return `${formatDate(d)} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
} 