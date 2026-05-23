package com.example.mc_server.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PermissionStruct {
    private Integer id;
    private String label;
    private Boolean disabled;
    private List<PermissionStruct> children;
}
