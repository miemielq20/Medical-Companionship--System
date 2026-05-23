package com.example.mc_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderClientDTO {
    private Long id;
    private String name;
    private String mobile;
}
