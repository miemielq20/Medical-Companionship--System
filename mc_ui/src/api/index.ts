import http from '@/utils/request'
import {type CodeRequest,type LoginRequest, type MenuRequest, type MenuListRequest,type AutAdminRequest,
  type UpdateUserRequest ,type CompanionRequest,type CompanionListRequest,type DeleteCompanionRequest} from '@/types/api.request'
import { type ApiResponse, type LoginReponse,type AutAdminReponse , type MenuSelectlistReponse, type MenuPermissionsResponse, type UserMenuResponse,
  type MenuListResponse,type PhotoListResponse,type CompanionListResponse  } from '@/types/response'


// 发送验证码
export const getCode = (data: CodeRequest) => {
  return http.post<ApiResponse>('/get/code', data)
}

// 用户权限验证
export const userAuthentication=(data: LoginRequest)=>{
  return http.post<LoginReponse>('/user/authentication',data, {
  })
}

// 登录
export const login=(data:LoginRequest)=>{
  return http.post<ApiResponse>('/login',data)
}


// 获取用户菜单
export const userGetMenu=()=>{
  return http.get<UserMenuResponse>('/user/getMenu')
}

//用户权限修改
export const userSetmenu=(data:MenuRequest)=>{
  return http.post<ApiResponse>('/user/setMenu',data)
}

//账号管理
export const authAdmin=(params:AutAdminRequest)=>{
  return http.get<AutAdminReponse>('/auth/admin',{params})
}

//菜单权限下拉
export const menuSelectlist=()=>{
  return http.get<MenuSelectlistReponse>('/menu/selectlist')
}

//菜单权限列表
export const menuList=(params:MenuListRequest)=>{
  return http.get<MenuListResponse>('/menu/list',{params})
}

//菜单权限添加
export const updateUser=(data:UpdateUserRequest)=>{
  return http.post<ApiResponse>('/user/update', data)
}

//用户菜单权限
export const menuPermissions=()=>{
  return http.get<MenuPermissionsResponse>(`/menu/permissions`)
}

//陪护师头像列表
export const photoList=()=>{
  return http.get<PhotoListResponse>(`/photo/list`)
}

//陪护师创建
export const companion=(data:CompanionRequest)=>{
  return http.post<ApiResponse>(`/set/companion`,data)
}
//陪护师列表
export const companionList=(params:CompanionListRequest)=>{
  return http.get<CompanionListResponse>(`/companion/list`,{params})
}
//陪护师删除
export const deleteCompanion=(data:DeleteCompanionRequest)=>{
  return http.post<ApiResponse>(`/companion/delete`, data)
}
