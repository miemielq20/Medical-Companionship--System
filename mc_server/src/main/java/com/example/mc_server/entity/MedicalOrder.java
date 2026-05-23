package com.example.mc_server.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MedicalOrder {
    private Long id;
    private String outTradeNo;
    private String transactionId;
    private Long userId;
    private Long hospitalId;
    private String hospitalName;
    private Long serviceId;
    private String serviceName;
    private String serviceImg;
    private Long companionId;
    private Long starttime;
    private String receiveAddress;
    private String tel;
    private String demand;
    private Integer tradeState;
    private Integer serviceState;
    private BigDecimal price;
    private BigDecimal paidPrice;
    private String codeUrl;
    private Long orderStartTime;
    private Long timeEnd;
}
