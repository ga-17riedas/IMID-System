/**
 * 路由配置模块
 * 定义应用的页面结构和路由导航关系
 * 包含基于角色的访问控制和导航守卫
 */
import Vue from 'vue'
import Router from 'vue-router'
import ImageAnalysis from '@/views/doctor/ImageAnalysis.vue'
import Layout from '@/views/doctor/Layout.vue'
import PatientLayout from '@/views/patient/Layout.vue'
import Home from '../views/Home.vue'
import { getToken, getUser } from '@/utils/auth'
import UserGuide from '../views/UserGuide.vue'

// 使用 Vue Router
Vue.use(Router)

/**
 * 创建路由实例
 * 配置路由模式和路由表
 */
const router = new Router({
  mode: 'history',
  routes: [
    // 公共路由
    {
      path: '/',
      name: 'Home',
      component: Home,
      meta: { 
        requiresAuth: false 
      }
    },
    {
      path: '/login',
      component: () => import('@/views/Login'),
      meta: { title: '登录' }
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/Register'),
      meta: { 
        requiresAuth: false,
        allowIfLoggedIn: false
      }
    },
    
    // 医生角色路由
    {
      path: '/doctor',
      component: Layout,
      redirect: '/doctor/home',
      meta: { requiresAuth: true, role: 'DOCTOR' },
      children: [
        {
          path: 'home',
          name: 'DoctorHome',
          component: () => import('@/views/doctor/Home.vue'),
          meta: { title: '首页', icon: 'el-icon-s-home' }
        },
        {
          path: 'profile',
          name: 'DoctorProfile',
          component: () => import('@/views/doctor/Profile.vue'),
          meta: { title: '个人信息', icon: 'el-icon-user' }
        },
        {
          path: 'image-analysis',
          name: 'ImageAnalysis',
          component: ImageAnalysis,
          meta: { 
            title: '医学影像分析',
            icon: 'el-icon-picture'
          }
        },
        {
          path: 'reports',
          name: 'ReportManagement',
          component: () => import('@/views/doctor/ReportManagement'),
          meta: {
            title: '报告管理',
            icon: 'el-icon-document'
          }
        },
        {
          path: 'patients',
          component: () => import('@/views/doctor/Patients.vue'),
          name: 'Patients',
          meta: { 
            title: '患者管理',
            icon: 'el-icon-user-solid'
          }
        },
        {
          path: 'schedule',
          name: 'Schedule',
          component: () => import('@/views/doctor/Schedule.vue'),
          meta: { 
            title: '日程安排',
            icon: 'el-icon-date'
          }
        }
      ]
    },
    
    // 患者角色路由
    {
      path: '/patient',
      component: PatientLayout,
      redirect: '/patient/home',
      meta: { requiresAuth: true, role: 'PATIENT' },
      children: [
        {
          path: 'home',
          name: 'PatientHome',
          component: () => import('@/views/patient/Home.vue'),
          meta: { title: '首页', icon: 'el-icon-s-home' }
        },
        {
          path: 'reports',
          name: 'PatientReports',
          component: () => import('@/views/patient/Reports.vue'),
          meta: { title: '诊断记录', icon: 'el-icon-document' }
        },
        {
          path: 'health',
          name: 'PatientHealth',
          component: () => import('@/views/patient/Health.vue'),
          meta: { title: '健康管理', icon: 'el-icon-s-data' }
        },
        {
          path: 'profile',
          name: 'PatientProfile',
          component: () => import('@/views/patient/Profile.vue'),
          meta: { title: '个人信息', icon: 'el-icon-user' }
        }
      ]
    },
    
    // 管理员角色路由
    {
      path: '/admin',
      component: () => import('@/views/admin/Layout'),
      redirect: '/admin/dashboard',
      meta: { 
        requiresAuth: true,
        roles: ['ROLE_ADMIN']  // 指定需要管理员角色
      },
      children: [
        {
          path: 'dashboard',
          component: () => import('@/views/admin/Dashboard'),
          name: 'AdminDashboard',
          meta: { 
            title: '管理控制台',
            roles: ['ROLE_ADMIN']
          }
        },
        {
          path: 'users',
          name: 'UserManagement',
          component: () => import('@/views/admin/users/index.vue'),
          meta: { title: '用户管理', requiresAuth: true, role: 'ADMIN' },
          children: [
            {
              path: 'doctors',
              name: 'DoctorManagement',
              component: () => import('@/views/admin/users/Doctors.vue'),
              meta: { title: '医生管理', requiresAuth: true, role: 'ADMIN' }
            },
            {
              path: 'patients',
              name: 'PatientManagement',
              component: () => import('@/views/admin/users/Patients.vue'),
              meta: { title: '患者管理', requiresAuth: true, role: 'ADMIN' }
            },
            {
              path: 'admins',
              name: 'AdminManagement',
              component: () => import('@/views/admin/users/Admins.vue'),
              meta: { title: '管理员管理', requiresAuth: true, role: 'ADMIN' }
            }
          ]
        },
        {
          path: 'medical-data',
          component: () => import('@/views/admin/images/ImageManagement.vue'),
          name: 'MedicalData',
          meta: {
            title: '影像数据管理',
            roles: ['ADMIN']
          }
        },
        {
          path: 'model',
          name: 'ModelManagement',
          component: () => import('@/views/admin/ModelManagement.vue'),
          meta: { title: '模型管理', requiresAuth: true, role: 'ADMIN' }
        },
        {
          path: 'analysis',
          name: 'DataAnalysis',
          component: () => import('@/views/admin/DataAnalysis.vue'),
          meta: { title: '数据分析', requiresAuth: true, role: 'ADMIN' }
        },
        {
          path: 'system-logs',
          name: 'SystemLogs',
          component: () => import('@/views/admin/SystemLogs.vue'),
          meta: { title: '系统日志', requiresAuth: true, role: 'ADMIN' }
        },
        {
          path: 'profile',
          name: 'AdminProfile',
          component: () => import('@/views/admin/Profile.vue'),
          meta: { title: '个人信息', requiresAuth: true, role: 'ADMIN' }
        },
        {
          path: 'settings',
          name: 'AdminSettings',
          component: () => import('@/views/admin/Settings.vue'),
          meta: { title: '系统设置', requiresAuth: true, role: 'ADMIN' }
        }
      ]
    },
    
    // 其他路由
    {
      path: '/user-guide',
      name: 'UserGuide',
      component: UserGuide
    }
  ]
})

/**
 * 全局路由守卫
 * 在路由跳转前检查用户认证状态和权限
 * 实现基于角色的访问控制
 */
router.beforeEach((to, from, next) => {
  const token = getToken()
  const user = getUser()

  // 检查路由是否需要认证
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!token) {
      // 无令牌，重定向到登录页面并保存原始目标路径
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    } else {
      // 有令牌，检查用户角色权限
      const userRole = user ? user.role : null
      const requiredRoles = to.matched.some(record => record.meta.roles)
        ? to.matched.find(record => record.meta.roles).meta.roles
        : []

      if (requiredRoles.length && !requiredRoles.includes(userRole)) {
        next({ path: '/403' }) // 跳转到无权限页面
      } else {
        next() // 有权限，继续导航
      }
    }
  } else {
    // 不需要认证的路由，直接通过
    next()
  }
})

// 注册静态路由（无权限要求）
export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/Login'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register'),
    meta: { 
      requiresAuth: false,
      allowIfLoggedIn: false
    }
  },
  {
    path: '/403',
    component: { template: '<div>403 - 权限不足</div>' },
    hidden: true
  },
  {
    path: '/404',
    component: { template: '<div>404 - 页面不存在</div>' },
    hidden: true
  },
  {
    path: '/500',
    component: { template: '<div>500 - 服务器错误</div>' },
    hidden: true
  }
]

// 权限路由（需要根据用户角色动态加载）
export const asyncRoutes = [
  // 医生角色路由
  {
    path: '/doctor',
    meta: { role: 'DOCTOR' },
    children: []
  },
  // 患者角色路由
  {
    path: '/patient',
    meta: { role: 'PATIENT' },
    children: []
  },
  // 管理员角色路由
  {
    path: '/admin',
    meta: { role: 'ADMIN' },
    children: []
  }
]

export default router 