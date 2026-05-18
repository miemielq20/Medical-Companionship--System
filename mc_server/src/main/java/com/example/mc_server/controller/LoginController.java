package com.example.mc_server.controller;

import com.example.mc_server.dto.LoginRequest;
import com.example.mc_server.dto.LoginResponse;
import com.example.mc_server.dto.AuthRequest;
import com.example.mc_server.service.UserService;
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


}