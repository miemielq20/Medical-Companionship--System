package com.example.mc_server.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Companion {
    private Long id;
    private String name;
    private String avatar;
    private String sex;
    private Integer age;
    private String mobile;
    private Integer active;
    private Integer createUserId;
}
