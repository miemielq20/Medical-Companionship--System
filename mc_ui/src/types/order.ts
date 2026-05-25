import {type CompanionList} from "@/types/companion";
export type createOrder={
    wx_code: string;
}

export type CreateOrderForm = {
    hospital_id: number;
    hospital_name: string;
    starttime:number;
    companion_id: number;
    receiveAddress: string;
    tel: string;
    demand: string;
};

export type clientList={
    id: number;
    name: string;
    mobile: string;
}

export type order= CreateOrderForm&createOrder&{
    client: clientList;
    order_start_time: number;  
    trade_state: string;  
    time_end: number;  
    service_state: string;
    serviceImg: string;
    transaction_id: string;  
    out_trade_no: string;
    price: number; 
    code_url?: string;
    user_id: string;
    service_name: string;
    paidPrice:number;
    companion:CompanionList
}