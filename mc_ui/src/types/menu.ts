import {type MenuItem} from "@/types/router";
// 侧边栏状态类型
export type AsideMenuState ={
  asideCollapse: boolean;
  selectMenu: MenuItem[];
  menuActive: string; 
}

// 权限菜单
export type MenuGroup = {
    id: number;
    label: string;
    disabled?: boolean;
    children?: MenuGroup[];
};

//权限菜单数据
export type MenuList = {
    permission: number[],
    id: number,
    name: string,
    permissionName: string,
};






