package com.example.mc_server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateOrderRequest {
    @JsonProperty("hospital_id")
    private Long hospitalId;
    @JsonProperty("hospital_name")
    private String hospitalName;
    private Long starttime;
    @JsonProperty("companion_id")
    private Long companionId;
    private String receiveAddress;
    private String tel;
    private String demand;
}
