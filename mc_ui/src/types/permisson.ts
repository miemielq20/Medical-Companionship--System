// 权限组
export type PermissionGroup =  {
 id:number;
 name:string;
};

// 权限列表
export type Permissions = {
  create_time:string;
  id: number;
  name:string;
  active: string;
  mobile: number;
  permissions_id: number;
};