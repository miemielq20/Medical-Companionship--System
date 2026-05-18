package com.example.mc_server.controller;

import com.example.mc_server.dto.AuthRequest;
import com.example.mc_server.service.AuthService;
import com.example.mc_server.service.SmsService;
import com.example.mc_server.service.UserService;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping
public class SmsController {
    @Autowired
    private SmsService smsService;
    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;

    /// 发送验证码
    @PostMapping("/get/code")
    public AuthRequest sendCode(@RequestBody Map<String,String> map) {
        String sms = smsService.sendSms(map.get("phone"));
        Integer code = 500;
        String msg = "failure";
        Object data = null;
        if (StringUtils.hasText(sms)) {
            code = 10000;
            msg = "success";
            data = sms;
        }
        return new AuthRequest(code, msg, data);
    }
    // 验证码校验
    @GetMapping("/check/code")
    public AuthRequest checkCode(String phone, String sms) {
        boolean result = authService.checkSms(phone, sms);
        Integer code = 500;
        String msg = "failure";
        Object data = null;
        if (result) {
            code = 10000;
            msg = "success";
        }
        return new AuthRequest(code, msg, data);
    }

    // 校验验证码并注册

}
