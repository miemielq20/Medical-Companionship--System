import { useRouterStore } from '@/stores/router'
import { useAsideStore } from '@/stores/aside'
import { type MenuItem } from '@/types/router'
import router from '@/router'
import { menuPermissions } from '@/api/index'

export default async function getDynamicRouter() {
    const routerStore = useRouterStore()
    const asideStore = useAsideStore()

    try {
        const res = await menuPermissions()
        if (res.data.code === 10000) {
            // 更新 store 中的菜单数据
            await routerStore.dynamicMenu(res.data.data as MenuItem[])
            // 同步标签页，移除当前权限范围外的旧 tag
            asideStore.syncMenuTag(routerStore.routerList)
            // 将路由数据保存到 localStorage
            localStorage.setItem('RouterList', JSON.stringify({
                routerList: res.data.data
            }))
            // 添加路由到 router
            routerStore.routerList.forEach(item => {
                router.addRoute('main', item)
            })
        } else {
            console.error('获取菜单权限失败:', res.data.msg)
            localStorage.removeItem('token')
            localStorage.removeItem('userInfo')
            localStorage.removeItem('RouterList')
            throw new Error(res.data.msg)
        }
    } catch (error) {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        localStorage.removeItem('RouterList')
        throw error
    }
}