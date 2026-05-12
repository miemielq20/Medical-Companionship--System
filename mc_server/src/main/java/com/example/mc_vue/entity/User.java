package com.example.mc_vue.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    private Long id;
    private String userName;
    private String nickname;
    private String password;
    private Integer permissionsId;
    private String avatar;
    private Integer active;
    private LocalDateTime createTime;
}
