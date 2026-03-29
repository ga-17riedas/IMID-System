import Vue from 'vue'
import Vuex from 'vuex'
import getters from './getters'

// 导入模块
import app from './modules/app'
import user from './modules/user'
import settings from './modules/settings'
import permission from './modules/permission'
import notification from './modules/notification'

Vue.use(Vuex)

const store = new Vuex.Store({
  modules: {
    app,
    user,
    settings,
    permission,
    notification
  },
  getters
})

export default store 