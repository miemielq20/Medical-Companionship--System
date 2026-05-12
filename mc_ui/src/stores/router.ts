import { defineStore } from 'pinia'
import { type MenuItem } from '@/types/router'

export const useRouterStore = defineStore('RouterList', {
  state: () => {
    return {
      routerList: <MenuItem[]>([])
    }
  },
  persist: true,
  actions: {
    dynamicMenu(data: MenuItem[]) {
        // 获取所有模块
       const modules = import.meta.glob('../views/**/*.vue')
       // 获取所有模块名称
       const routerSet=(router: MenuItem[])=>{
        // 遍历所有模块
         router.forEach(route=>{ 
            // 判断是否有子菜单
            if(!route.children){
                const url=`../views${route.meta?.path}/index.vue`
                 //拿到获取的view组件
                 route.component = modules[url]
            }else{
                routerSet(route.children)
            }
         })
       }
       routerSet(data)
       this.routerList = data
    }
  },
  
})