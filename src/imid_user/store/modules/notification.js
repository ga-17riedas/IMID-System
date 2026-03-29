import { Message, Notification } from 'element-ui'

const state = {
  notifications: [],
  unreadCount: 0
}

const mutations = {
  ADD_NOTIFICATION: (state, notification) => {
    state.notifications.unshift(notification)
    if (!notification.read) {
      state.unreadCount++
    }
  },
  MARK_AS_READ: (state, id) => {
    const index = state.notifications.findIndex(n => n.id === id)
    if (index !== -1 && !state.notifications[index].read) {
      state.notifications[index].read = true
      state.unreadCount = Math.max(0, state.unreadCount - 1)
    }
  },
  MARK_ALL_READ: (state) => {
    state.notifications.forEach(n => {
      n.read = true
    })
    state.unreadCount = 0
  },
  CLEAR_NOTIFICATIONS: (state) => {
    state.notifications = []
    state.unreadCount = 0
  }
}

const actions = {
  /**
   * 显示通知
   * @param {Object} context
   * @param {Object} options 配置项
   * @param {String} options.message 消息内容
   * @param {String} options.type 消息类型，info/success/warning/error
   * @param {String} options.title 标题，可选
   * @param {Boolean} options.showInNotificationCenter 是否在通知中心显示，默认true
   */
  showNotification({ commit }, options) {
    const { message, type = 'info', title = '系统通知', showInNotificationCenter = true } = options
    
    // 显示Element UI通知
    Notification({
      title,
      message,
      type,
      duration: 4500
    })
    
    // 如果需要添加到通知中心
    if (showInNotificationCenter) {
      const notification = {
        id: Date.now(),
        type,
        title,
        message,
        time: new Date(),
        read: false
      }
      
      commit('ADD_NOTIFICATION', notification)
    }
  },
  
  /**
   * 显示消息提示
   * @param {Object} context
   * @param {Object} options 配置项
   * @param {String} options.message 消息内容
   * @param {String} options.type 消息类型，info/success/warning/error
   */
  showMessage({ commit }, options) {
    const { message, type = 'info' } = options
    
    Message({
      message,
      type,
      duration: 3000
    })
  },
  
  /**
   * 标记通知为已读
   * @param {Object} context
   * @param {Number} id 通知ID
   */
  markAsRead({ commit }, id) {
    commit('MARK_AS_READ', id)
  },
  
  /**
   * 标记所有通知为已读
   * @param {Object} context
   */
  markAllRead({ commit }) {
    commit('MARK_ALL_READ')
  },
  
  /**
   * 清空所有通知
   * @param {Object} context
   */
  clearNotifications({ commit }) {
    commit('CLEAR_NOTIFICATIONS')
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
} 