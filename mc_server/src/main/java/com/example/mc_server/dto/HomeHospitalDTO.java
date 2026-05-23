package com.example.mc_server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class HomeHospitalDTO {
    private Long id;
    private String name;
    private String rank;
    private String label;
    private String intro;
    @JsonProperty("avatar_url")
    private String avatarUrl;
}
