/**
 * 应用主入口文件
 * 初始化Vue实例、加载插件和全局配置
 */

// 导入核心库
import Vue from 'vue'
import App from './App'
import router from './router'
import store from './store'
import request from './utils/request'
import WebSocketClient from '@/utils/websocket'

// 导入UI组件库
import Antd from 'ant-design-vue'
import ElementUI from 'element-ui'

// 导入样式文件
import 'ant-design-vue/dist/antd.less'
import 'element-ui/lib/theme-chalk/index.css'
import './assets/styles/theme.css'

// 导入工具函数
import { initTheme } from './utils/theme'

// 引入自定义样式
import './styles/index.scss'

// 引入权限控制
import './permission'

// 注册UI组件库
Vue.use(Antd)
Vue.use(ElementUI)

// 关闭生产环境提示
Vue.config.productionTip = false

// 将 axios 请求实例挂载到 Vue 原型上
// 这样所有组件都可以通过 this.$http 访问
Vue.prototype.$http = request

// 初始化应用主题
initTheme()

// 创建WebSocket客户端实例
const wsClient = new WebSocketClient({
  url: `${window.location.protocol === 'https:' ? 'wss:' : 'ws:'}//${window.location.host}/ws`,
  debug: process.env.NODE_ENV === 'development',
  reconnectDelay: 2000,
  maxRetries: 10
});

// 注册WebSocket事件处理
wsClient.on('open', () => {
  console.log('WebSocket连接已建立');
});

wsClient.on('message', (data) => {
  // 处理收到的消息
  try {
    // 尝试解析为对象（如果是字符串）
    const message = typeof data === 'string' ? JSON.parse(data) : data;
    
    // 根据消息类型分发处理
    if (message.type === 'SETTING_CHANGED') {
      // 更新应用设置
      store.dispatch('settings/updateSetting', {
        key: message.key,
        value: message.value
      });
    } else if (message.type === 'NOTIFICATION') {
      // 显示通知
      Vue.prototype.$notify({
        title: message.title || '系统通知',
        message: message.message,
        type: message.level || 'info',
        duration: 5000
      });
    }
  } catch (e) {
    console.error('处理WebSocket消息出错:', e);
  }
});

wsClient.on('error', (error) => {
  console.error('WebSocket错误:', error);
});

wsClient.on('close', (event) => {
  console.log('WebSocket连接已关闭');
});

// 将WebSocket客户端实例添加到Vue原型
Vue.prototype.$websocket = wsClient;

// 初始化WebSocket连接
setTimeout(() => {
  // 延迟连接以确保应用已完全加载
  wsClient.connect();
}, 1000);

// 创建Vue实例
const app = new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})

// 监听登录状态变化，连接或断开WebSocket
store.watch(
  state => state.user.token,
  token => {
    if (token) {
      wsClient.connect()
    } else {
      wsClient.disconnect()
    }
  }
)

export default app 