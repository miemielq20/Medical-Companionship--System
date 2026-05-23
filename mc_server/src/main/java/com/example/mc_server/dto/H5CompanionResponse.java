package com.example.mc_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class H5CompanionResponse {
    private List<CompanionList> companion;
    private List<HospitalOptionDTO> hospitals;
    private ServiceDTO service;
}
