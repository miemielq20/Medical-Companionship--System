package com.example.mc_vue.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MenuTree {
    private String path;
    private String name;
    private Meta meta;
    private String component;
    private List<MenuTree> children;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Meta {
        private String id;
        private String name;
        private String icon;
        private String path;
        private String describe;
    }
}
