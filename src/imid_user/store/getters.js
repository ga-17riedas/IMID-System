const getters = {
  // App
  sidebar: state => state.app.sidebar,
  device: state => state.app.device,
  
  // User
  token: state => state.user.token,
  userId: state => state.user.id,
  username: state => state.user.username,
  roles: state => state.user.roles,
  userInfo: state => state.user.userInfo,
  permissions: state => state.user.permissions,
  
  // Settings
  systemLogo: state => state.settings.systemLogo,
  systemName: state => state.settings.systemName,
  theme: state => state.settings.theme,
  
  // Permission
  routes: state => state.permission.routes,
  addRoutes: state => state.permission.addRoutes
}

export default getters 