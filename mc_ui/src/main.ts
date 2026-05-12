import './assets/main.css'
import { createApp } from 'vue'
import { createPinia } from 'pinia'


import getDynamicRouter from './utils/getList'

// pinia 持久化
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
 
// element-plus 图标
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import { useAsideStore } from '@/stores/aside'
import { useRouterStore } from '@/stores/router'

//头部组件
import PanelHead from "@/components/panel/PanelHead.vue"


const pinia = createPinia()
pinia.use(piniaPluginPersistedstate) 

const app = createApp(App)
// 注册 Element Plus 图标组件
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

//注册组件
app.component('PanelHead', PanelHead)
app.use(pinia)

const asideStore = useAsideStore()
const routerStore = useRouterStore()

//动态路由加载标记
let isDynamicRoutesLoaded = false

// 如果刷新页面已经有 token，先加载动态路由，避免首次导航时路由表不完整
const token = localStorage.getItem('token')
if (token) {
  try {
    await getDynamicRouter()
    isDynamicRoutesLoaded = true
  } catch (error) {
    console.error('页面刷新时加载动态路由失败:', error)
  }
}

app.use(router)

//路由守卫  
router.beforeEach(async( to) => {
  const token = localStorage.getItem('token')

  // 已登录（有 token）
  if (token) {
    if (to.path === '/login') {
      return { path: '/' }
    }

    if (!isDynamicRoutesLoaded) { 
      try {
        await getDynamicRouter()
        isDynamicRoutesLoaded = true
        return { ...to, replace: true }
      } catch (error) {
        console.error('路由守卫中加载动态路由失败:', error)
        return { path: '/login' }
      }
    } else {
       return true 
    }

  } 
  // 未登录（无 token）
  else {
    if (to.path === '/login') {
       return true
    } else {
       return { path: '/login' }
    }
  }
})


//判断页面是刷新还是用户手动输入路径
const isRealRefresh = () => {
  if (document.referrer) {
    return false
  }else {
    return true
  }
 
}

router.afterEach((to, from) => {
  if (to.path === '/login') {
    return
  }
  //重定向到根路径，重置菜单标签状态
  if (routerStore.routerList.length > 0) {
    const isRefresh = isRealRefresh()

    const redirectFromRootOrLogin = from.path === '/' 
    if (isRefresh && redirectFromRootOrLogin) {
      asideStore.resetMenuTag( to.path, routerStore.routerList )
    }
    asideStore.updateMenuActiveByPath(to.path, routerStore.routerList)
  }
})

app.mount('#app')