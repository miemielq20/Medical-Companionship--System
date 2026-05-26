import { type MenuList, type MenuPermissions, type UserMenu } from '@/types/menu'
import { type PhotoList, type CompanionList } from '@/types/companion'
import { type LoginParams } from '@/types/login'
import { type AccountInfo } from '@/types/account'
import { type PermissionGroup } from '@/types/permission_group'
import { type order } from '@/types/order'

/** 通用API响应结构 */
export type ApiResponse<T = unknown> = {
  msg: string;
  code: number;
  data: T;
};

/** 登录响应 */
export type LoginReponse = ApiResponse & {
  data: LoginParams;
};

/** 账号列表响应 */
export type AutAdminReponse = ApiResponse & {
  data: {
    list: AccountInfo[];
    total: number;
  };
};


/** 菜单权限下拉响应 */
export type MenuSelectlistReponse = ApiResponse & {
  data: PermissionGroup[];
};

/** 用户菜单权限响应 */
export type MenuPermissionsResponse = ApiResponse & {
  data: MenuPermissions[];
};

/** 菜单权限响应 */
export type UserMenuResponse = ApiResponse & {
  data:UserMenu[];
};

/** 菜单权限列表响应 */
export type MenuListResponse = ApiResponse & {
  data: {
    list: MenuList[];
  };
  total: number;
};

/** 陪护师头像列表响应 */
export type PhotoListResponse = ApiResponse & {
  data: PhotoList[];
};

/** 陪护师列表响应 */
export type CompanionListResponse = ApiResponse & {
  data: {
    list: CompanionList[];
    total: number;
  };
};


/** 订单列表响应 */
export type orderListResponse = ApiResponse & {
  data: order[];
}

/** 订单详情响应 */
export type orderDetailResponse = ApiResponse & {
  data: order;
}


// ==================== 控制台仪表盘 ====================

/** 控制台仪表盘响应 */
export type dashboardResponse = ApiResponse & {
  data: DashboardData;
}

export type DashboardData = {
  totalOrders: number;
  totalUsers: number;
  totalRevenue: number;
  todayOrders: TodayOrder[];
  orderChart: OrderChartItem[];
  systemInfo: SystemInfo;
};

export type TodayOrder = {
  companionName: string;
  userName: string;
  companionAvatar: string;
  revenue: number;
  time: number;
  tradeState: string;
};

export type SystemInfo = {
  systemVersion: string;
  os: string;
  javaVersion: string;
  currentTime: string;
  runtimeEnv: string;
  serverIp: string;
  systemName: string;
};
export type OrderChartItem = {
  date: string;
  count: number;
  amount: number;
};