import { getSystemSettings } from '@/api/settings'

const state = {
  systemLogo: '/logo.png',
  systemName: '医学影像诊断系统',
  theme: 'light'
}

const mutations = {
  SET_SYSTEM_LOGO: (state, logo) => {
    state.systemLogo = logo
  },
  SET_SYSTEM_NAME: (state, name) => {
    state.systemName = name
  },
  SET_THEME: (state, theme) => {
    state.theme = theme
  },
  UPDATE_SETTING: (state, { key, value }) => {
    if (Object.prototype.hasOwnProperty.call(state, key)) {
      state[key] = value
    }
  }
}

const actions = {
  // 获取系统设置
  getSettings({ commit }) {
    return new Promise((resolve, reject) => {
      getSystemSettings()
        .then(response => {
          const { data } = response
          
          if (data.systemLogo) {
            commit('SET_SYSTEM_LOGO', data.systemLogo)
          }
          
          if (data.systemName) {
            commit('SET_SYSTEM_NAME', data.systemName)
          }
          
          if (data.theme) {
            commit('SET_THEME', data.theme)
            document.body.className = `theme-${data.theme}`
          }
          
          resolve(data)
        })
        .catch(error => {
          reject(error)
        })
    })
  },
  
  // 更新设置
  updateSetting({ commit }, { key, value }) {
    commit('UPDATE_SETTING', { key, value })
  },
  
  // 刷新系统Logo
  refreshSystemLogo({ commit }) {
    getSystemSettings()
      .then(response => {
        const { data } = response
        if (data.systemLogo) {
          commit('SET_SYSTEM_LOGO', data.systemLogo)
        }
      })
  },
  
  // 刷新系统名称
  refreshSystemName({ commit }) {
    getSystemSettings()
      .then(response => {
        const { data } = response
        if (data.systemName) {
          commit('SET_SYSTEM_NAME', data.systemName)
        }
      })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
} 