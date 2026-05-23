package com.example.mc_server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDTO {
    @JsonProperty("wx_code")
    private String wxCode;
    private Long hospitalId;
    @JsonProperty("hospital_name")
    private String hospitalName;
    private Long starttime;
    @JsonProperty("companion_id")
    private Long companionId;
    private String receiveAddress;
    private String tel;
    private String demand;
    private OrderClientDTO client;
    @JsonProperty("order_start_time")
    private Long orderStartTime;
    @JsonProperty("trade_state")
    private String tradeState;
    @JsonProperty("time_end")
    private Long timeEnd;
    @JsonProperty("service_state")
    private String serviceState;
    private String serviceImg;
    @JsonProperty("transaction_id")
    private String transactionId;
    @JsonProperty("out_trade_no")
    private String outTradeNo;
    private BigDecimal price;
    @JsonProperty("code_url")
    private String codeUrl;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("service_name")
    private String serviceName;
    private BigDecimal paidPrice;
    private CompanionList companion;
}
