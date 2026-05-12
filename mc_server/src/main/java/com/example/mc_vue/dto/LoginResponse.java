package com.example.mc_vue.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginResponse {
    private String token;
    private UserInfo userInfo;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class UserInfo {
        private String avatar;
        private String name;
    }
}
