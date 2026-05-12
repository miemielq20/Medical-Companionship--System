package com.example.mc_vue.controller;

import com.example.mc_vue.entity.AuthRequest;
import com.example.mc_vue.entity.User;
import com.example.mc_vue.service.AuthService;
import com.example.mc_vue.service.SmsService;
import com.example.mc_vue.service.UserService;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class SmsController {
    @Autowired
    private SmsService smsService;
    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;

    /// 发送验证码
    @PostMapping("/sendCode")
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
    @GetMapping("/checkCode")
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
    @PostMapping("/authentication")
    public AuthRequest authentication(@RequestBody Map<String, String> map) {
        String userName = map.get("userName");
        String passWord = map.get("passWord");
        String validCode = map.get("validCode");

        Integer code = 500;
        String msg = "failure";
        Object data = null;

        try {
            boolean isCodeValid = authService.checkSms(userName, validCode);
            if (isCodeValid) {
                User user = userService.registerUser(userName, passWord);
                code = 10000;
                msg = "success";
                data = user;
            } else {
                msg = "验证码错误";
            }
        } catch (Exception e) {
            msg = e.getMessage();
        }

        return new AuthRequest(code, msg, data);
    }
}
