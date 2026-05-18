package com.example.mc_server.controller;

import com.example.mc_server.dto.MenuTree;
import com.example.mc_server.dto.AuthRequest;
import com.example.mc_server.dto.PermissionStruct;
import com.example.mc_server.mapper.UserMapper;
import com.example.mc_server.service.AuthService;
import com.example.mc_server.service.MenuService;
import com.example.mc_server.service.UserService;
import com.example.mc_server.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("user")
public class UserController {
    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private UserMapper userMapper;

    @PostMapping("/authentication")
    //校验并注册
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
                boolean isUserRegistered = userService.registerUser(userName, passWord);
                if (isUserRegistered){
                    code = 10000;
                    msg = "success";
                    data = "";
                }else{
                    msg = "用户已注册";
                }
            } else {
                msg = "验证码错误";
            }
        } catch (Exception e) {
            msg = e.getMessage();
        }
        return new AuthRequest(code, msg, data);
    }
    //获取权限菜单树(默认权限)结构
    @GetMapping("/getMenu")
    public AuthRequest<List<PermissionStruct>> getMenu() {
        Integer code = 500;
        String message = "failure";
        List<PermissionStruct> data = null;
        try {
            data = menuService.getSimpleMenuTree(1 );
            code = 10000;
            message = "success";
        }
        catch (Exception e) {
            message = e.getMessage();
        }
        return new AuthRequest<>(code, message, data);
    }

    //更新权限组或者添加权限组
    @PostMapping("/setMenu")
    public AuthRequest setMenu(@RequestBody Map<String, String> map, HttpServletRequest request) {
        Integer code = 500;
        String message = "failure";
        List<MenuTree> data = null;

        try{
            String token = request.getHeader("x-token");
            Long groupId= Long.parseLong(map.get("id"));
            String name = map.get("name");
            String permissions = map.get("permissions");
            Long userId = JwtUtil.getUserIdFromToken(token);

            //更新权限组
            if (groupId>0){
                userService.updatePermission(groupId, name, permissions, userId);
                return new AuthRequest(10000, "success", "操作成功");
            }else if(groupId==0){
                userService.insertPermission( name, permissions, userId);
                return new AuthRequest(10000, "success", "操作成功");
            }
        } catch (NumberFormatException e) {
            message = e.getMessage();
        }
        return new AuthRequest<>(code, message, data);
    }

    @PostMapping("/update")
    public AuthRequest updateUser(@RequestBody Map<String, String> map, HttpServletRequest request) {
        Integer code = 500;
        String message = "failure";
        List<MenuTree> data = null;
        try{
            String token = request.getHeader("x-token");
            Long userId = JwtUtil.getUserIdFromToken(token);
            String permissionsId = map.get("permissions_id");
            String name = map.get("name");
            System.out.println(permissionsId);
            userService.updateUserPermissions(name, permissionsId, userId);
            return new AuthRequest(10000, "success", "操作成功");
        } catch (NumberFormatException e) {
            message = e.getMessage();
        }

        return new AuthRequest<>(code, message, data);
    }
}


