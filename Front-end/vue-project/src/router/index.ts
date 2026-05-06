import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import Layout from '@/views/Layout.vue'

//动态路由加载函数
const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'main',
    component: Layout,
    meta: { requiresAuth: true },
    redirect: () => {
      const localData = localStorage.getItem('RouterList')
      if (localData) {
        const routerList = JSON.parse(localData).routerList
        if (routerList && routerList.length > 0) {
          // 默认重定向到第一个路由的第一个子路由
          const firstRoute = routerList[0]
          const children = firstRoute.children
          // 如果第一个路由有子路由，重定向到第一个子路由，否则重定向到第一个路由
          if (children && children.length > 0) {
            return { path: children[0].meta.path }
          } else {
            return { path: firstRoute.meta.path }
          }
        }
      }
      return { path: '/login' }
    },
    children: [
      
      // {
      //   path: "dashboard",
      //   name: 'dashboard',
      //   meta: { id: '1', icon: 'Platform', path: '/dashboard', name: '控制台'  },
      //   component: Dashboard,
      // },
      // {
      //   path: "auth",
      //   name: 'auth',
      //   meta: { id: '2', icon: 'Grid', path: '/auth', name: '权限管理' },
      //   children: [
      //     {
      //       path: "admin",
      //       name: 'admin',
      //       meta: { id: '1', icon: 'User', path: '/auth/admin', name: '用户管理' },
      //       component: User,
      //     },
      //     {
      //       path: "group",
      //       name: 'group',
      //       meta: { id: '2', icon: 'Menu', path: '/auth/group', name: '菜单管理' },
      //       component: Menu,
      //     }
      //   ]
      // },
      // {
      //   path: "vppz",
      //   name: 'vppz',
      //   meta: { id: '3', icon: 'Bell', path: '/vppz', name: 'DIDI陪诊' },
      //   children: [
      //     {
      //       path: "staff",
      //       name: 'staff',
      //       meta: { id: '1', icon: 'Checked', path: '/vppz/staff', name: '陪护管理' },
      //       component: Companion,
      //     },
      //     {
      //       path: "order",
      //       name: 'order',
      //       meta: { id: '2', icon: 'List', path: '/vppz/order', name: '订单管理' },
      //       component: Order,
      //     }
      //   ]
      // }
      
    ],
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: { requiresAuth: false }
  },

]
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router
