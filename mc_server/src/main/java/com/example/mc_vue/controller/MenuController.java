package com.example.mc_vue.controller;

import com.example.mc_vue.dto.MenuSelectList;
import com.example.mc_vue.dto.MenuTree;
import com.example.mc_vue.entity.AuthRequest;
import com.example.mc_vue.entity.User;
import com.example.mc_vue.mapper.UserMapper;
import com.example.mc_vue.service.MenuService;
import com.example.mc_vue.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@CrossOrigin
@RequestMapping("/menu")
public class MenuController {
    @Autowired
    private MenuService menuService;
    @Autowired
    private UserMapper userMapper;

    //权限下拉菜单
    @GetMapping("/selectlist")
    public AuthRequest<List<MenuSelectList>> getMenuSelectList(){
        Integer code = 500;
        String message = "failure";
        List<MenuSelectList> data = null;
        try {
            data = menuService.getMenuList();
            code = 10000;
            message = "success";
        } catch (Exception e) {
            message = e.getMessage();
        }
        return new AuthRequest<>(code, message, data);
    }


    @GetMapping("/permissions")
    public AuthRequest<List<MenuTree>> getMenuPermissions(HttpServletRequest request) {
        Integer code = 500;
        String message = "failure";
        List<MenuTree> data = null;
        try {
            //获取token
            String token = request.getHeader("x-token");
            if (token == null || token.isEmpty()) {
                return new AuthRequest<>(401, "没有token", null);
            }

            //获取用户Id
            Long userId = JwtUtil.getUserIdFromToken(token);
            //查找对应账户
            User user = userMapper.selectById(userId);
            if (user == null) {
                return new AuthRequest<>(404, "账号不存在", null);
            }
            data = menuService.getMenuPermissions(user.getPermissionsId());
            code = 10000;
            message = "success";
        } catch (Exception e) {
            message = e.getMessage();
        }
        return new AuthRequest<>(code, message, data);
    }
}
