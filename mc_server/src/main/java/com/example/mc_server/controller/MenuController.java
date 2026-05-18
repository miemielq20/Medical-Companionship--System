package com.example.mc_server.controller;
import com.example.mc_server.dto.MenuSelectList;
import com.example.mc_server.dto.MenuTree;
import com.example.mc_server.dto.PageResponse;
import com.example.mc_server.dto.PermissionGroupVO;
import com.example.mc_server.dto.AuthRequest;
import com.example.mc_server.entity.User;
import com.example.mc_server.mapper.UserMapper;
import com.example.mc_server.service.MenuService;
import com.example.mc_server.util.JwtUtil;
import com.github.pagehelper.PageInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/menu")
public class MenuController {
    @Autowired
    private MenuService menuService;
    @Autowired
    private UserMapper userMapper;

    //权限下拉列表
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


    //权限对应结构
    @GetMapping("/permissions")
    public AuthRequest<List<MenuTree>> getMenuPermissions(HttpServletRequest request) {
        Integer code = 500;
        String message = "failure";
        List<MenuTree> data = null;
        try {
            String token = request.getHeader("x-token");

            Long userId = JwtUtil.getUserIdFromToken(token);
            User user = userMapper.selectById(userId);
            data = menuService.getMenuPermissions(user.getPermissionsId());
            code = 10000;
            message = "success";
        } catch (Exception e) {
            message = e.getMessage();
        }
        return new AuthRequest<>(code, message, data);
    }

    //权限组列表
    @GetMapping("/list")
    public AuthRequest<PageResponse<PermissionGroupVO>> getMenuList(
            @RequestParam(defaultValue = "1") Integer  pageNum,
            @RequestParam(defaultValue = "10") Integer  pageSize,
            HttpServletRequest request)
    {
        Integer code = 500;
        String message = "failure";
        PageResponse<PermissionGroupVO> data = null;
        try {
            String token = request.getHeader("x-token");
            if (token == null || token.isEmpty()) {
                return new AuthRequest<>(401, "没有token", null);
            }

            Long userId = JwtUtil.getUserIdFromToken(token);
            User user = userMapper.selectById(userId);
            if (user == null) {
                return new AuthRequest<>(404, "账号不存在", null);
            }

            try {
                PageInfo<PermissionGroupVO> page = menuService.getPermissionGroupList(pageNum, pageSize, user.getId().intValue());

                PageResponse<PermissionGroupVO> pageResponse = new PageResponse<>();
                System.out.println(page.getList());
                System.out.println(page.getTotal());
                pageResponse.setList(page.getList());
                pageResponse.setTotal(page.getTotal());

                code = 10000;
                message = "success";

                data = pageResponse;
            } catch (Exception e) {
                message = e.getMessage();
            }

        } catch (Exception e) {
            message = e.getMessage();
        }
        return new AuthRequest<>(code, message, data);
    }
}
