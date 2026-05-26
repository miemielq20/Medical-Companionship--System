import { type MenuItem } from '@/types/router';

// 侧边栏状态类型
export type AsideMenuState = {
  asideCollapse: boolean;
  selectMenu: MenuItem[];
  menuActive: string;
};

// 权限菜单
export type MenuGroup = {
  id: string;
  label: string;
  disabled?: boolean;
  children?: MenuGroup[];
};

// 权限菜单数据
export type MenuList = {
  permissions: number[]; 
  id: string;
  name: string;
  permissionName: string;
};

// 菜单权限树形结构
export type MenuPermissions = {
  path: string;
  name: string;
  meta: {
    id: string;
    name: string;
    icon: string;
    path: string;
    describe: string;
  };
  component?: string;
  children?: MenuPermissions[];  
};

// 权限菜单树形结构
export type UserMenu = {
  id: string;
  label: string;
  disabled?: boolean;
  children?: UserMenu[];
};