import { type RouteRecordRaw } from 'vue-router';
// 路由 菜单项
export type MenuItem = RouteRecordRaw & {
  meta?: {
    name?: string;
    icon?: string;
    path?: string;  // 添加 path 字段，用于菜单导航路径
  };
  describe?: string;
  children?: MenuItem[];    // 递归子菜单
};