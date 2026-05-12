package com.example.mc_vue.service;

import com.example.mc_vue.dto.LoginResponse;
import com.example.mc_vue.entity.User;
import com.example.mc_vue.mapper.UserMapper;
import com.example.mc_vue.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    // 注册用户
    public User registerUser(String mobile, String password) {
        User existingUser = userMapper.selectByMobile(mobile);
        if (existingUser != null) {
            throw new RuntimeException("该手机号已注册");
        }
        User user = new User();
        user.setUserName(mobile);
        user.setPassword(password);
        user.setPermissionsId(1);
        user.setCreateTime(LocalDateTime.now());

        userMapper.addUser(user);
        return user;
    }
    // 登录
    public LoginResponse login(String mobile, String password) {
        // 根据手机号和密码查询用户是否存在
        User user = userMapper.selectByMobileAndPassword(mobile, password);
        // 用户不存在
        if (user == null) {
            throw new RuntimeException("手机号或密码错误");
        }
        // 账号被禁用
        if (user.getActive() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        // 生成token
        String token = JwtUtil.generateToken(user.getId(), user.getUserName());

        // 封装用户信息
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setAvatar(user.getAvatar() != null ? user.getAvatar() : "");
        userInfo.setName(user.getNickname());

        // 封装响应数据
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserInfo(userInfo);

        return response;
    }
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    public List<User> getAllUsers() {
        return userMapper.selectAll();
    }

}
