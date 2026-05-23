package com.example.mc_server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class HospitalOptionDTO {
    private Long id;
    private String name;
    @JsonProperty("service_id")
    private Long serviceId;
    @JsonProperty("service_price")
    private BigDecimal servicePrice;
}
