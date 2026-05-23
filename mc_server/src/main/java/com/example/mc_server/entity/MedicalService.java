package com.example.mc_server.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MedicalService {
    private Long id;
    private String serviceName;
    private String serviceImg;
    private BigDecimal price;
    private Integer active;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
