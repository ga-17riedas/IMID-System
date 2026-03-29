/**
 * 主题管理工具模块
 * 提供主题切换、存储和应用相关功能
 */

/**
 * 主题类型枚举
 * 定义系统支持的主题类型
 */
export const ThemeType = {
  LIGHT: 'light', // 亮色主题
  DARK: 'dark'    // 暗色主题
};

/**
 * 将主题设置保存到本地存储
 * 
 * @param {string} theme - 要保存的主题类型
 */
export const saveTheme = (theme) => {
  localStorage.setItem('theme', theme);
};

/**
 * 从本地存储获取主题设置
 * 
 * @returns {string} 当前保存的主题类型，默认为亮色主题
 */
export const getTheme = () => {
  return localStorage.getItem('theme') || ThemeType.LIGHT; // 默认为亮色主题
};

/**
 * 切换主题
 * 在亮色和暗色主题之间切换
 * 
 * @returns {string} 切换后的新主题类型
 */
export const toggleTheme = () => {
  const currentTheme = getTheme();
  const newTheme = currentTheme === ThemeType.LIGHT ? ThemeType.DARK : ThemeType.LIGHT;
  saveTheme(newTheme);
  applyTheme(newTheme);
  return newTheme;
};

/**
 * 应用主题到文档
 * 设置文档根元素的data-theme属性和相关CSS类
 * 
 * @param {string} theme - 要应用的主题类型
 */
export const applyTheme = (theme) => {
  // 设置HTML根元素的data-theme属性
  document.documentElement.setAttribute('data-theme', theme);
  
  // 应用主题相关的类
  if (theme === ThemeType.DARK) {
    document.body.classList.add('dark-theme');
    document.body.classList.remove('light-theme');
  } else {
    document.body.classList.add('light-theme');
    document.body.classList.remove('dark-theme');
  }
};

/**
 * 初始化主题
 * 从本地存储加载主题设置并应用
 * 
 * @returns {string} 初始化后应用的主题类型
 */
export const initTheme = () => {
  const savedTheme = getTheme();
  applyTheme(savedTheme);
  return savedTheme;
}; 