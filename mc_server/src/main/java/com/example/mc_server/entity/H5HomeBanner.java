package com.example.mc_server.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class H5HomeBanner {
    private Long id;
    private String stype;
    private String stypeLink;
    private String title;
    private String stypeText;
    private String picImageUrl;
    private Integer sort;
    private Integer active;
}
