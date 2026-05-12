import { defineStore } from 'pinia'
import { type MenuItem } from '@/types/router'
 import {type AsideMenuState} from "@/types/menu"

export const useAsideStore = defineStore('isCollapse', {
  state: (): AsideMenuState => {
    return {
      // 侧边栏折叠状态
      asideCollapse: false, 
      // 菜单列表
      selectMenu: <MenuItem[]>([]),
      // 激活菜单的id
      menuActive:'1-1'
    }
  },
  persist: {
     pick: ['selectMenu', 'menuActive'],
  },
  
  actions: {
    // 侧边栏折叠
    isCollapse() {
      this.asideCollapse = !this.asideCollapse
    },

    // 添加菜单
    addMenu(payload: MenuItem) {
      // 判断当前菜单是否已存在
      const existItem = this.selectMenu.find(item => item.name === payload.name);
      if (existItem) {
        return;
      } else {
        this.selectMenu.push(payload);
      }
    },

    // 删除菜单
    closeMenu(index: number) {
      this.selectMenu.splice(index, 1);
    },
    // 根据路径查找菜单项
    findMenuItemByPath(path: string, menuList: MenuItem[]): MenuItem | null {
      for (const item of menuList) {
        if (item.meta?.path === path) {
          return item
        }
        if (item.children && item.children.length > 0) {
          const childItem = this.findMenuItemByPath(path, item.children)
          if (childItem) {
            return childItem
          }
        }
      }
      return null
    },
    // 根据路径查找菜单索引
    findMenuIndexByPath(path: string, menuList: MenuItem[], parentIndex = '1'): string | null {
      for (const item of menuList) {
        const currentIndex = `${parentIndex}-${item.meta?.id}`
        if (item.meta?.path === path) {
          return currentIndex
        }
        // 递归查找子菜单
        if (item.children && item.children.length > 0) {
          const childIndex = this.findMenuIndexByPath(path, item.children, currentIndex)
          if (childIndex) {
            return childIndex
          }
        }
      }
      return null
    },

     // 同步标签页，仅保留当前权限菜单中的有效 tag
    syncMenuTag(menuList: MenuItem[]) {
      this.selectMenu = this.selectMenu.filter(item => this.findMenuItemByPath(item.meta?.path || '', menuList))
      if (this.selectMenu.length === 0) {
        this.menuActive = ''
      }
    },
      // 重定向时清空 selectMenu 并仅保留当前路由对应 tag
    resetMenuTag(path: string, menuList: MenuItem[]) {
      const menuItem = this.findMenuItemByPath(path, menuList)
      if (menuItem) {
        this.selectMenu = [menuItem]
      }
    },
  
    // 根据路由路径更新当前激活菜单
    updateMenuActiveByPath(path: string, menuList: MenuItem[]) {
      const index = this.findMenuIndexByPath(path, menuList)
      if (index) {
        this.menuActive = index
      }
    },
    // 菜单激活 
    updateMenuActive(payload: string) {
      this.menuActive = payload
    }
  }
})

