package com.example.mc_server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;


@Service
public class AuthService {
    @Autowired
    private SmsService smsService;
    @Autowired
    private RedisTemplate redisTemplate;
    public boolean checkSms(String phone, String sms) {
       Object obj= redisTemplate.opsForValue().get(phone);
       if(obj == null){
           throw new RuntimeException("验证码已过期");
       }
       String  sms_redis=(String)obj;
       return  sms_redis.equals(sms);
    }
}
