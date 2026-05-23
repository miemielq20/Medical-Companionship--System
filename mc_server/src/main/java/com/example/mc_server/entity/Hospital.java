package com.example.mc_server.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Hospital {
    private Long id;
    private String name;
    private String rank;
    private String label;
    private String intro;
    private String avatarUrl;
    private Long serviceId;
    private BigDecimal servicePrice;
    private Integer active;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
