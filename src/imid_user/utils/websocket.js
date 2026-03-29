import SockJS from 'sockjs-client'
import Stomp from 'webstomp-client'
import { getToken } from '@/utils/auth'
import store from '@/store'

/**
 * WebSocket客户端
 */
class WebSocketClient {
  constructor(options = {}) {
    this.url = options.url || '';
    this.reconnectDelay = options.reconnectDelay || 3000;
    this.maxRetries = options.maxRetries || 5;
    this.retries = 0;
    this.debug = options.debug || false;
    this.autoReconnect = options.autoReconnect !== false;
    this.handlers = {
      message: [],
      open: [],
      close: [],
      error: []
    };
    this.ws = null;
    this.isConnecting = false;
    this.isActive = false;
    this.pingInterval = null;
    this.pingTimeout = null;
  }

  /**
   * 连接到服务器
   * @param {String} url - 可选，覆盖构造函数中设置的URL
   */
  connect(url) {
    if (this.isConnecting) {
      this.log('WebSocket 正在连接中，请勿重复连接');
      return;
    }
    
    // 清理之前的连接
    this.cleanup();
    
    this.isConnecting = true;
    
    try {
      const wsUrl = url || this.url;
      
      if (!wsUrl) {
        throw new Error('WebSocket URL不能为空');
      }
      
      this.log(`正在连接到WebSocket: ${wsUrl}`);
      this.ws = new WebSocket(wsUrl);
      
      // 设置二进制类型为Blob
      this.ws.binaryType = 'arraybuffer';
      
      // 配置事件处理
      this.ws.onopen = this.onOpen.bind(this);
      this.ws.onmessage = this.onMessage.bind(this);
      this.ws.onclose = this.onClose.bind(this);
      this.ws.onerror = this.onError.bind(this);
    } catch (error) {
      this.log('WebSocket连接初始化失败', error);
      this.isConnecting = false;
      this.triggerHandlers('error', error);
      this.reconnect();
    }
  }
  
  /**
   * 关闭连接
   */
  disconnect() {
    this.log('主动关闭WebSocket连接');
    this.autoReconnect = false;
    this.cleanup();
  }
  
  /**
   * 清理连接相关资源
   */
  cleanup() {
    if (this.ws) {
      // 移除所有事件监听
      this.ws.onopen = null;
      this.ws.onmessage = null;
      this.ws.onclose = null;
      this.ws.onerror = null;
      
      // 如果连接仍然打开，则关闭它
      if (this.ws.readyState === WebSocket.OPEN || this.ws.readyState === WebSocket.CONNECTING) {
        try {
          this.ws.close();
        } catch (e) {
          this.log('关闭WebSocket时出错', e);
        }
      }
      
      this.ws = null;
    }
    
    // 清除ping定时器
    if (this.pingInterval) {
      clearInterval(this.pingInterval);
      this.pingInterval = null;
    }
    
    if (this.pingTimeout) {
      clearTimeout(this.pingTimeout);
      this.pingTimeout = null;
    }
    
    this.isActive = false;
    this.isConnecting = false;
  }
  
  /**
   * 发送消息
   * @param {Object|String} data - 要发送的数据
   */
  send(data) {
    if (!this.isActive) {
      this.log('WebSocket未连接，无法发送消息');
      return false;
    }
    
    try {
      let message = data;
      
      // 如果是对象，转换为JSON字符串
      if (typeof data === 'object') {
        message = JSON.stringify(data);
      }
      
      this.ws.send(message);
      return true;
    } catch (error) {
      this.log('发送消息失败', error);
      this.triggerHandlers('error', error);
      return false;
    }
  }
  
  /**
   * 开始心跳检测
   */
  startHeartbeat() {
    // 清除现有定时器
    if (this.pingInterval) {
      clearInterval(this.pingInterval);
    }
    
    // 30秒发送一次心跳
    this.pingInterval = setInterval(() => {
      // 如果连接已断开，停止心跳
      if (!this.isActive) {
        clearInterval(this.pingInterval);
        return;
      }
      
      // 发送ping
      this.send({ type: 'ping', timestamp: Date.now() });
      
      // 设置超时检测
      if (this.pingTimeout) {
        clearTimeout(this.pingTimeout);
      }
      
      // 如果15秒内没有收到pong响应，认为连接已断开
      this.pingTimeout = setTimeout(() => {
        this.log('心跳超时，断开连接');
        this.cleanup();
        this.reconnect();
      }, 15000);
    }, 30000);
  }
  
  /**
   * 尝试重新连接
   */
  reconnect() {
    if (!this.autoReconnect || this.retries >= this.maxRetries) {
      if (this.retries >= this.maxRetries) {
        this.log(`已达到最大重试次数 ${this.maxRetries}，停止重连`);
      }
      return;
    }
    
    this.retries++;
    
    const delay = this.reconnectDelay * Math.min(this.retries, 10);
    this.log(`${delay}毫秒后尝试第${this.retries}次重连`);
    
    setTimeout(() => {
      this.connect();
    }, delay);
  }
  
  /**
   * 重置重试计数器
   */
  resetRetryCount() {
    this.retries = 0;
  }
  
  /**
   * WebSocket打开事件处理
   */
  onOpen(event) {
    this.log('WebSocket连接已建立');
    this.isActive = true;
    this.isConnecting = false;
    this.resetRetryCount();
    this.startHeartbeat();
    this.triggerHandlers('open', event);
  }
  
  /**
   * WebSocket消息事件处理
   */
  onMessage(event) {
    let data = event.data;
    
    // 尝试解析JSON
    if (typeof data === 'string') {
      try {
        data = JSON.parse(data);
        
        // 处理pong响应
        if (data.type === 'pong') {
          if (this.pingTimeout) {
            clearTimeout(this.pingTimeout);
            this.pingTimeout = null;
          }
          return;
        }
      } catch (e) {
        // 解析失败，保留原始字符串
      }
    }
    
    this.triggerHandlers('message', data);
  }
  
  /**
   * WebSocket关闭事件处理
   */
  onClose(event) {
    this.log(`WebSocket连接已关闭: Code=${event.code}, Reason=${event.reason}`);
    this.isActive = false;
    this.isConnecting = false;
    this.triggerHandlers('close', event);
    
    // 非正常关闭时才重连
    if (event.code !== 1000 && event.code !== 1001) {
      this.reconnect();
    }
  }
  
  /**
   * WebSocket错误事件处理
   */
  onError(event) {
    this.log('WebSocket错误', event);
    this.triggerHandlers('error', event);
  }
  
  /**
   * 添加事件处理器
   * @param {String} event - 事件名称: 'message', 'open', 'close', 'error'
   * @param {Function} handler - 事件处理函数
   */
  on(event, handler) {
    if (this.handlers[event] && typeof handler === 'function') {
      this.handlers[event].push(handler);
    }
    return this;
  }
  
  /**
   * 移除事件处理器
   * @param {String} event - 事件名称
   * @param {Function} handler - 特定的处理函数，如果不提供则移除所有
   */
  off(event, handler) {
    if (!this.handlers[event]) return this;
    
    if (handler) {
      const index = this.handlers[event].indexOf(handler);
      if (index !== -1) {
        this.handlers[event].splice(index, 1);
      }
    } else {
      this.handlers[event] = [];
    }
    
    return this;
  }
  
  /**
   * 触发指定事件的所有处理器
   * @param {String} event - 事件名称
   * @param {*} data - 传递给处理器的数据
   */
  triggerHandlers(event, data) {
    if (!this.handlers[event]) return;
    
    this.handlers[event].forEach(handler => {
      try {
        handler(data);
      } catch (error) {
        console.error(`WebSocket ${event} 事件处理出错:`, error);
      }
    });
  }
  
  /**
   * 调试日志
   */
  log(...args) {
    if (this.debug) {
      console.log(`[WebSocket] ${new Date().toISOString()}:`, ...args);
    }
  }
}

export default WebSocketClient; 