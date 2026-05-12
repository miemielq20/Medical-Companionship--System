package com.example.mc_vue.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Menu {
    private Integer id;
    private String name;
    private Integer parent_id;
    private String path;
    private String component;
    private String icon;
    private Integer order_index;
    private Integer disabled;
}
