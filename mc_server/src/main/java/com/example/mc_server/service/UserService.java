package com.example.mc_server.service;

import com.example.mc_server.dto.LoginResponse;
import com.example.mc_server.entity.User;
import com.example.mc_server.mapper.UserMapper;
import com.example.mc_server.util.JwtUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    //注册
    public boolean registerUser(String mobile, String password) {
        User existingUser = userMapper.selectByMobile(mobile);
        if (existingUser != null) {
            throw new RuntimeException("该手机号已注册");
        }
        User user = new User();
        user.setUserName(mobile);
        user.setMobile(mobile);
        user.setPassword(password);
        user.setPermissionsId(10);
        user.setCreateTime(LocalDateTime.now());

        userMapper.addUser(user);
        return true;
    }

    //登录
    public LoginResponse login(String mobile, String password) {
        User user = userMapper.selectByMobileAndPassword(mobile, password);
        if (user == null) {
            throw new RuntimeException("手机号或密码错误");
        }
        if (user.getActive() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        String token = JwtUtil.generateToken(user.getId(), user.getUserName());

        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setAvatar(user.getAvatar() != null ? user.getAvatar() : "");
        userInfo.setName(user.getNickname());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserInfo(userInfo);

        return response;
    }

    //获取单个用户信息
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    //获取所有用户(分页)
    public PageInfo<User> getAllUsers(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> list = userMapper.selectAll();
        PageInfo<User> pageInfo = new PageInfo<User>(list);
        return pageInfo;
    }

    //更新权限组
    public void updatePermission(Long id, String name, String permissions ,Long createUserId) {
         userMapper.updatePermissionGroup(id, name, permissions, createUserId) ;
    }

    //插入权限组
    public void insertPermission(String name,String permissions,Long createUserId) {
         userMapper.insertPermissionGroup(name, permissions, createUserId);
    }

    public void updateUserPermissions(String name,String permissionsId,Long id) {
         userMapper.updateUserPermissions(name, permissionsId, id);
    }



}
