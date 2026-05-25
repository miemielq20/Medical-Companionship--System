import { createWebHistory, createRouter } from 'vue-router'

import Layout from '@/pages/Main.vue'
import Home from '@/pages/home/index.vue'
import Order from '@/pages/order/index.vue'
import User from '@/pages/user/index.vue'
import Login from '@/pages/login/index.vue'
import Register from '@/pages/register/index.vue'
import createOrder from '@/pages/createOrder/index.vue'
import detail from '@/pages/detail/index.vue'

/**
 * 路由配置
 * meta.requiresAuth: true 表示需要登录才能访问
 * meta.roles: 允许访问的角色数组（预留扩展）
 */
const routes = [
  { 
    path: '/',
    component: Layout,
    redirect: '/home',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'home',
        meta: { 
          icon: 'home-o',
          name: '首页',
          requiresAuth: true
        },
        component: Home
      },
      {
        path: 'order',
        meta: { 
          icon: 'orders-o',
          name: '订单',
          requiresAuth: true
        },
        component: Order
      },
      {
        path: 'user',
        meta: {
          icon: 'user-circle-o',
          name: '我的',
          requiresAuth: true
        },
        component: User
      }
    ]
  },
  {
    path: '/login',
    name: 'login',
    meta: { requiresAuth: false },
    component: Login
  },
  {
    path: '/register',
    name: 'register',
    meta: { requiresAuth: false },
    component: Register
  },
  {
    path: '/createOrder',
    name: 'createOrder',
    meta: { requiresAuth: true },
    component: createOrder
  },
  {
    path: '/detail',
    name: 'detail',
    meta: { requiresAuth: true },
    component: detail
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

/**
 * 全局前置守卫：路由级权限控制
 * 1. 检查目标路由是否需要登录
 * 2. 已登录用户访问登录/注册页 → 重定向到首页
 * 3. 未登录用户访问需要认证的路由 → 重定向到登录页
 */
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('h5-token')
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth === true)

  // 已登录用户访问登录/注册页，重定向到首页
  if (token && (to.path === '/login' || to.path === '/register')) {
    return next('/')
  }

  // 需要认证但未登录，跳转登录页并携带原目标路径
  if (requiresAuth && !token) {
    return next({ path: '/login', query: { redirect: to.fullPath } })
  }

  next()
})

export default router