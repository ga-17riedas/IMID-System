/**
 * 日期工具模块
 * 提供日期格式化和处理功能
 */

/**
 * 将日期格式化为后端兼容的格式
 * 使用格式：YYYY/M/D HH:MM:SS
 * 
 * @param {Date|string} date - 要格式化的日期对象或日期字符串
 * @returns {string} 格式化后的日期字符串
 */
export function formatDate(date) {
  if (!date) return '';
  
  // 如果输入是字符串，将其转换为Date对象
  if (typeof date === 'string') {
    date = new Date(date);
  }
  
  // 提取日期各个部分
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');
  
  // 使用与后端匹配的格式 yyyy/M/d HH:mm:ss
  return `${year}/${date.getMonth() + 1}/${date.getDate()} ${hours}:${minutes}:${seconds}`;
} 