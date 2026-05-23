import  {type CompanionList} from '@/types/companion';


//医院
export type Hospitals={
    id: number;
    name: string;
    service_id: number;
    service_price:string;
}

//订单
export type service={
    serviceName:string;
    serviceImg:string;
}


//订单数据类型
export type h5Companion = {
    companion:CompanionList[];
    hospitals:Hospitals[];
    service:service;
}
