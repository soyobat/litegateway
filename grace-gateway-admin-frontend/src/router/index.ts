import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw, RouteLocationNormalized, NavigationGuardNext } from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import Cookies from 'js-cookie'

NProgress.configure({ showSpinner: false })

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    meta: { title: '主页', requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '仪表盘', icon: 'Odometer', requiresAuth: true }
      },
      {
        path: 'gateway',
        name: 'Gateway',
        redirect: '/gateway/config',
        meta: { title: '网关管理', icon: 'Connection', requiresAuth: true },
        children: [
          {
            path: 'config',
            name: 'GatewayConfig',
            component: () => import('@/views/gateway/config/index.vue'),
            meta: { title: '配置管理', requiresAuth: true }
          }
        ]
      },
      {
        path: 'statistics',
        name: 'Statistics',
        redirect: '/statistics/overview',
        meta: { title: '统计分析', icon: 'DataAnalysis', requiresAuth: true },
        children: [
          {
            path: 'overview',
            name: 'StatisticsOverview',
            component: () => import('@/views/statistics/overview/index.vue'),
            meta: { title: '数据概览', requiresAuth: true }
          },
          {
            path: 'requests',
            name: 'StatisticsRequests',
            component: () => import('@/views/statistics/requests/index.vue'),
            meta: { title: '请求分析', requiresAuth: true }
          },
          {
            path: 'performance',
            name: 'StatisticsPerformance',
            component: () => import('@/views/statistics/performance/index.vue'),
            meta: { title: '性能分析', requiresAuth: true }
          }
        ]
      },
      {
        path: 'system',
        name: 'System',
        redirect: '/system/user',
        meta: { title: '系统管理', icon: 'Setting', requiresAuth: true },
        children: [
          {
            path: 'user',
            name: 'SystemUser',
            component: () => import('@/views/system/user/index.vue'),
            meta: { title: '用户管理', requiresAuth: true }
          }
        ]
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '页面不存在', requiresAuth: false }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局前置守卫
router.beforeEach((to: RouteLocationNormalized, _from: RouteLocationNormalized, next: NavigationGuardNext) => {
  NProgress.start()

  // 设置页面标题
  document.title = `${to.meta.title} - GraceGateway 网关配置管理系统`

  const TokenKey = 'grace_gateway_token'
  const token = Cookies.get(TokenKey)
  const requiresAuth = to.meta.requiresAuth

  if (requiresAuth && !token) {
    // 需要登录但未登录，跳转到登录页
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else if (to.path === '/login' && token) {
    // 已登录但访问登录页，跳转到首页
    next({ path: '/' })
  } else {
    next()
  }
})

// 全局后置钩子
router.afterEach(() => {
  NProgress.done()
})

export default router
