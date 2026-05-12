

// 验证码请求参数类型
export type CodeRequest = {
  tel: string
}

// 登录请求参数类型
export type LoginRequest = {
  userName: string
  passWord: string
  validCode?: string
}

// 菜单请求参数类型
export type MenuRequest = {
  id: number,
  name: string,
  permissions: string
}

// 获取用户信息请求参数类型
export type AutAdminRequest = {
  pageNum: number,
  pageSize: number,
}

// 修改用户信息请求参数类型
export type UpdateUserRequest = {
  name: string
  permissions_id: number
}

// 菜单列表请求参数类型
export type MenuListRequest = AutAdminRequest


//陪护员请求参数类型
export type CompanionRequest = { 
  create_time?: string,
  id: number,
  mobile: string,
  name:string,
  active:number
  age: number,
  avatar: string,
  sex:string,
}

//陪护员列表请求参数类型
export type CompanionListRequest = AutAdminRequest

export type DeleteCompanionRequest = {
  id: { id: number }[] 
}
