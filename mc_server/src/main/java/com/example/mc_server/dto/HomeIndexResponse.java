package com.example.mc_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class HomeIndexResponse {
    private String now;
    private List<HomeSlideDTO> slides;
    private List<HomeNavDTO> nav2s;
    private List<HomeNavDTO> navs;
    private List<HomeHospitalDTO> hospitals;
}
