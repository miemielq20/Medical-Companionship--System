package com.example.mc_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthRequest<T> implements Serializable {
    // 状态码
    private  Integer code;
    // 响应信息
    private  String msg;
    // 响应数据
    private T data;
}
