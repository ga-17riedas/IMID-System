/**
 * 图像处理工具模块
 * 提供图像URL处理和图像加载错误处理功能
 */

/**
 * 获取完整的图像URL
 * 将相对路径转换为完整的URL，处理不同的URL格式
 * 
 * @param {string} url - 原始图像URL，可以是相对路径或完整URL
 * @returns {string} 处理后的完整图像URL
 */
export function getImageUrl(url) {
  if (!url) {
    console.warn('Empty image URL provided');
    return '';
  }

  console.log('Processing image URL:', url);
  
  // 如果是完整的URL，直接返回
  if (url.startsWith('http://') || url.startsWith('https://')) {
    console.log('Full URL detected, returning as is:', url);
    return url;
  }

  // 获取基础API URL
  const baseUrl = process.env.VUE_APP_BASE_API || 'http://localhost:8080';
  console.log('Base API URL:', baseUrl);

  // 处理相对路径
  let fullUrl;
  if (url.startsWith('/')) {
    // 如果路径以/开头，直接拼接到baseUrl后面
    fullUrl = baseUrl + url;
  } else {
    // 否则添加/再拼接
    fullUrl = baseUrl + '/' + url;
  }

  console.log('Generated full image URL:', fullUrl);
  return fullUrl;
}

/**
 * 处理图像加载错误
 * 当图像无法加载时显示一个备用图像
 * 
 * @param {Event} e - 图像错误事件对象
 */
export function handleImageError(e) {
  console.error('Image loading error:', e);
  if (e.target && e.target.src) {
    console.error('Failed image URL:', e.target.src);
  }
  // 替换为默认的错误图像
  e.target.src = require('@/assets/image-error.png');
} 