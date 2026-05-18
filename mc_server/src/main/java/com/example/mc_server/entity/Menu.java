package com.example.mc_server.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Menu {
    private Long id;
    private String name;
    private Integer parentId;
    private String path;
    private String component;
    private String icon;
    private Integer orderIndex;
    private Integer disabled;
    private  String describe;
}
