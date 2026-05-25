import http from '@/utils/request'

import {type CodeRequest,type LoginRequest, type MenuRequest, type MenuListRequest,type AutAdminRequest,
  type UpdateUserRequest ,type CompanionRequest,type CompanionListRequest,type DeleteCompanionRequest,
} from '@/types/api.request'

import { type ApiResponse, type LoginReponse,type AutAdminReponse , type MenuSelectlistReponse, type MenuPermissionsResponse, type UserMenuResponse,
  type MenuListResponse,type PhotoListResponse,type CompanionListResponse,type orderListResponse,type orderDetailResponse,type dashboardResponse
} from '@/types/response'


// ==================== 认证相关 ====================

/** 获取短信验证码 */
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

/** 获取当前用户菜单树 */
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

/** 更新用户信息（权限组+昵称） */
export const updateUser=(data:UpdateUserRequest)=>{
  return http.post<ApiResponse>('/user/update', data)
}

/** 获取菜单权限结构 */
export const menuPermissions=()=>{
  return http.get<MenuPermissionsResponse>("/menu/permissions")
}


// ==================== 陪护师管理 ====================

/** 获取陪护师头像列表（用于头像选择弹窗） */
export const photoList=()=>{
  return http.get<PhotoListResponse>('/photo/list')
}

/** 创建/更新陪护师 */
export const companion=(data:CompanionRequest)=>{
  return http.post<ApiResponse>('/set/companion',data)
}

/** 获取陪护师列表（分页） */
export const companionList=(params:CompanionListRequest)=>{
  return http.get<CompanionListResponse>('/companion/list',{params})
}

/** 批量删除陪护师 */
export const deleteCompanion=(data:DeleteCompanionRequest)=>{
  return http.post<ApiResponse>('/companion/delete', data)
}


// ==================== 订单管理 ====================

/** 获取订单列表，可按交易状态筛选（1待支付/2待服务/3已完成/4已取消） */
export const orderList=(params?:{ state?: string })=>{
  return http.get<orderListResponse>('/order/list', { params })
}

/** 获取订单详情 */
export const orderDetail=(params:{oid:string})=>{
  return http.get<orderDetailResponse>('/order/detail',{params})
}

/** 完成订单（待服务 -> 已完成） */
export const completeOrder = (oid: string) => {
  return http.post<ApiResponse>('/order/complete', { oid })
}


// ==================== 控制台 ====================

/** 获取控制台仪表盘数据（统计卡片、今日订单、系统信息、图表数据） */
export const dashboard = () => {
  return http.get<dashboardResponse>('/dashboard')
}