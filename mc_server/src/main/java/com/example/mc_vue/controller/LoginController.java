package com.example.mc_vue.controller;

import com.example.mc_vue.dto.LoginRequest;
import com.example.mc_vue.dto.LoginResponse;
import com.example.mc_vue.entity.AuthRequest;
import com.example.mc_vue.entity.User;
import com.example.mc_vue.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin
public class LoginController {


    @Autowired
    private UserService userService;


    @PostMapping("/login")
    public AuthRequest<LoginResponse> login(
            @RequestHeader(value = "terminal", required = false) String terminal,
            @RequestBody LoginRequest loginRequest) {


        Integer code = 500;
        String msg = "failure";
        LoginResponse data = null;

        try {
            //
            data = userService.login(
                    loginRequest.getUserName(),
                    loginRequest.getPassWord()
            );

            code = 10000;
            msg = "success";

        } catch (Exception e) {

            msg = e.getMessage();
        }

        return new AuthRequest<>(code, msg, data);
    }

    @GetMapping("/userInfo")
    public AuthRequest getUserInfo(HttpServletRequest request) {

        //  request 中取出拦截器里放的 userId
        Long userId = (Long) request.getAttribute("userId");

        try {
            //  根据 userId 查询用户详情
            User user = userService.getUserById(userId);

            return new AuthRequest(10000, "success", user);

        } catch (Exception e) {
            return new AuthRequest(500, e.getMessage(), null);
        }
    }
}