package com.example.mc_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompanionList {
    private LocalDateTime createTime ;
    private Long id;
    private String name;
    private String mobile;
    private String avatar;
    private String sex;
    private Integer age;
    private Integer active;
}