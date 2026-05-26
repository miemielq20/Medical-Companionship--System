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
            await routerStore.dynamicMenu(res.data.data as unknown as MenuItem[])
            asideStore.syncMenuTag(routerStore.routerList)
            localStorage.setItem('RouterList', JSON.stringify({
                routerList: res.data.data
            }))
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