package com.example.mc_server.controller;

import com.example.mc_server.dto.AuthRequest;
import com.example.mc_server.dto.PageResponse;
import com.example.mc_server.entity.User;
import com.example.mc_server.service.UserService;
import com.github.pagehelper.PageInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AdminController {

    @Autowired
    private UserService userService;

    //账户列表
    @GetMapping("/admin")
    public AuthRequest<PageResponse<User>> getAdminList(
            @RequestParam(defaultValue = "1") Integer  pageNum,
            @RequestParam(defaultValue = "10") Integer  pageSize,
            HttpServletRequest request) {

        Integer code = 500;
        String message = "failure";
        PageResponse<User> data = null;

        try {
            PageInfo<User> page = userService.getAllUsers(pageNum, pageSize);
            PageResponse<User> pageResponse = new PageResponse<>();
            pageResponse.setList(page.getList());
            pageResponse.setTotal(page.getTotal());

            code = 10000;
            message = "success";
            data = pageResponse;
        } catch (Exception e) {
            message = e.getMessage();
        }

        return new AuthRequest<>(code, message, data);
    }
}
