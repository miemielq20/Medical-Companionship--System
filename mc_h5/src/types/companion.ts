
//陪护员请求参数类型
export type PhotoList = { 
  name: string;
  url: string;
};

//陪护员列表结构
export type CompanionList = {
  createTime?: string;  
  id: number;
  name: string;
  mobile: string;
  avatar?: string;
  sex: string;
  age: number;
  active?: number;
};

