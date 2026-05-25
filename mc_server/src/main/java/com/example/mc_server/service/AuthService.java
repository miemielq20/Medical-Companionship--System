package com.example.mc_server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 认证服务
 * 负责短信验证码校验等认证相关操作
 */
@Service
public class AuthService {
    @Autowired
    private SmsService smsService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 校验短信验证码
     * @param phone 手机号（作为Redis key）
     * @param sms   用户输入的验证码
     * @return 验证码匹配返回true
     * @throws RuntimeException 验证码已过期
     */
    public boolean checkSms(String phone, String sms) {
        // 从Redis获取验证码
        Object obj = redisTemplate.opsForValue().get(phone);
        if (obj == null) {
            throw new RuntimeException("验证码已过期");
        }
        String smsRedis = (String) obj;
        return smsRedis.equals(sms);
    }
}