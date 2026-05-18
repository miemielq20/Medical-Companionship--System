package com.example.mc_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionGroupVO {
    private Long id;
    private String name;
    private List<Integer> permissions;
    private String permissionName;
}
