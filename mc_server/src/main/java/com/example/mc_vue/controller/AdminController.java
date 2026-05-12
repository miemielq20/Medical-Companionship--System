package com.example.mc_vue.controller;

import com.example.mc_vue.entity.AuthRequest;
import com.example.mc_vue.dto.PageResponse;
import com.example.mc_vue.entity.User;
import com.example.mc_vue.service.UserService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理后台 Controller
 */
@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AdminController {

    @Autowired
    private UserService userService;
    @GetMapping("/admin")
    public AuthRequest<PageResponse<User>> getAdminList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest request) {

        Integer code = 500;
        String message = "failure";
        PageResponse<User> data = null;

        try {
            // 开启分页
            PageHelper.startPage(pageNum, pageSize);

            // 查询所有用户
            Page<User> page = (Page<User>) userService.getAllUsers();

            // 构建分页响应
            PageResponse<User> pageResponse = new PageResponse<>();
            pageResponse.setList(page.getResult());
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
