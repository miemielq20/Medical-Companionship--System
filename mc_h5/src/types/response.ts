import { type MenuList, type MenuPermissions, type UserMenu } from '@/types/menu'
import { type PhotoList, type CompanionList } from '@/types/companion'
import { type LoginParams } from '@/types/login'
import { type AccountInfo } from '@/types/account'
import { type PermissionGroup } from '@/types/permission_group'
import { type HomeIndex } from '@/types/h5_index'
import {type h5Companion} from '@/types/h5_companion'
import type { createOrder,order } from './order'

// 通用API响应类型
export type ApiResponse<T = unknown> = {
  msg: string;
  code: number;
  data: T;
};

export type LoginReponse = ApiResponse & {
  data: LoginParams;
};

// 账号管理响应
export type AutAdminReponse = ApiResponse & {
  data: {
    list: AccountInfo[];
    total: number;
  };
};

//菜单权限下拉响应
export type MenuSelectlistReponse = ApiResponse & {
  data: PermissionGroup;
};

//用户菜单权限响应
export type MenuPermissionsResponse = ApiResponse & {
  data: {
    list: MenuPermissions[];
  };
};

//菜单权限响应
export type UserMenuResponse = ApiResponse & {
  data: UserMenu[];
};

//菜单权限列表响应
export type MenuListResponse = ApiResponse & {
  data: {
    list: MenuList[];
  };
  total: number;
};

//用户头像列表响应
export type PhotoListResponse = ApiResponse & {
  data: PhotoList[];
};

//陪护员列表响应
export type CompanionListResponse = ApiResponse & {
  data: {
    list: CompanionList[];
    total: number;
  };
};

// 首页数据响应
export type h5IndexResponse  = ApiResponse & { 
  data: HomeIndex;
};

//陪护员详情响应
export type h5CompanionResponse  = ApiResponse & { 
  data: h5Companion;
};


//创建订单响应
export type  CreateOrderResponse = ApiResponse & {
  data: createOrder;
}

//订单列表响应
export type  orderListResponse = ApiResponse & {
  data: order[];
}


export type  orderDetailResponse = ApiResponse & {
  data: order;
}

