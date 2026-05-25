import http from '@/utils/request'

import {type CodeRequest,type LoginRequest, type MenuRequest, type MenuListRequest,type AutAdminRequest,
  type UpdateUserRequest ,type CompanionRequest,type CompanionListRequest,type DeleteCompanionRequest,
  type CreateOrderRequest
} from '@/types/api.request'

import { type ApiResponse, type LoginReponse,type AutAdminReponse , type MenuSelectlistReponse, type MenuPermissionsResponse, type UserMenuResponse,
  type MenuListResponse,type PhotoListResponse,type CompanionListResponse,type h5CompanionResponse,type h5IndexResponse,type orderListResponse,
  type CreateOrderResponse,type orderDetailResponse
} from '@/types/response'


// ==================== 认证相关 ====================

/** 发送短信验证码 */
export const getCode = (data: CodeRequest) => {
  return http.post<ApiResponse>('/get/code', data)
}

/** 用户注册（校验验证码并注册） */
export const userAuthentication=(data: LoginRequest)=>{
  return http.post<LoginReponse>('/user/authentication',data, {
  })
}

/** 用户登录 */
export const login=(data:LoginRequest)=>{
  return http.post<ApiResponse>('/login',data)
}



// ==================== 菜单权限 ====================

/** 获取用户菜单树 */
export const userGetMenu=()=>{
  return http.get<UserMenuResponse>('/user/getMenu')
}

/** 设置/更新权限组 */
export const userSetmenu=(data:MenuRequest)=>{
  return http.post<ApiResponse>('/user/setMenu',data)
}

/** 获取账号列表（分页） */
export const authAdmin=(params:AutAdminRequest)=>{
  return http.get<AutAdminReponse>('/auth/admin',{params})
}

/** 获取权限组下拉选项 */
export const menuSelectlist=()=>{
  return http.get<MenuSelectlistReponse>('/menu/selectlist')
}

/** 获取菜单权限列表 */
export const menuList=(params:MenuListRequest)=>{
  return http.get<MenuListResponse>('/menu/list',{params})
}

/** 更新用户信息 */
export const updateUser=(data:UpdateUserRequest)=>{
  return http.post<ApiResponse>('/user/update', data)
}

/** 获取菜单权限结构 */
export const menuPermissions=()=>{
  return http.get<MenuPermissionsResponse>(`/menu/permissions`)
}


// ==================== 陪护师管理 ====================

/** 获取陪护师头像列表 */
export const photoList=()=>{
  return http.get<PhotoListResponse>(`/photo/list`)
}

/** 创建/更新陪护师 */
export const companion=(data:CompanionRequest)=>{
  return http.post<ApiResponse>(`/set/companion`,data)
}
/** 获取陪护师列表（分页） */
export const companionList=(params:CompanionListRequest)=>{
  return http.get<CompanionListResponse>(`/companion/list`,{params})
}
/** 批量删除陪护师 */
export const deleteCompanion=(data:DeleteCompanionRequest)=>{
  return http.post<ApiResponse>(`/companion/delete`, data)
}

// ==================== H5端业务 ====================

/** 获取首页数据（轮播图、导航、医院列表） */
export const homeIndex=()=>{
  return http.get<h5IndexResponse>(`/Index/index`)
}

/** 获取陪护师选择页数据 */
export const h5Companion=()=>{
  return http.get<h5CompanionResponse>(`/h5/companion`)
}

/** 创建订单 */
export const createOrder=(data:CreateOrderRequest)=>{
  return http.post<CreateOrderResponse>(`/createOrder`, data)
}

/** 获取订单列表 */
export const orderList=(params?:{ state?: string })=>{
  return http.get<orderListResponse>(`/order/list`, { params })
}

/** 获取订单详情 */
export const orderDetail=(params:{oid:string})=>{
  return http.get<orderDetailResponse>(`/order/detail`,{params})
}


/** 默认导出：将所有API方法组织为一个对象，挂载到全局 $api */
export default {
  getCode,
  userAuthentication,
  login,
  userGetMenu,
  userSetmenu,
  authAdmin,
  menuSelectlist,
  menuList,
  updateUser,
  menuPermissions,
  photoList,
  companion,
  companionList,
  deleteCompanion,
  homeIndex,
  h5Companion,
  createOrder,
  orderList,
  orderDetail,
}
