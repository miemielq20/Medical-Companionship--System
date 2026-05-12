package com.example.mc_vue.service;

import cn.hutool.core.util.RandomUtil;
import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import com.example.mc_vue.entity.SmsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;


@Service
public class SmsService {
    @Autowired
    private SmsRequest smsRequest;
    @Autowired
    private RedisTemplate redisTemplate;
    public String sendSms(String phone) {
        String serverIp = "app.cloopen.com";
        //请求端口
        String serverPort = "8883";
        //主账号,登陆云通讯网站后,可在控制台首页看到开发者主账号ACCOUNT SID和主账号令牌AUTH TOKEN
        String accountSId = smsRequest.getAccountId();
        String accountToken = smsRequest.getAuthToken();
        //请使用管理控制台中已创建应用的APPID
        String appId = smsRequest.getAppId();
        CCPRestSmsSDK sdk = new CCPRestSmsSDK();
        sdk.init(serverIp, serverPort);
        sdk.setAccount(accountSId, accountToken);
        sdk.setAppId(appId);
        sdk.setBodyType(BodyType.Type_JSON);
        String to = "18779039760";
        String templateId= "1";
        //创建4位随机数
        String randomNum = RandomUtil.randomNumbers(4);
        //验证码有效期
        long expire = 2L;
        String[] data={randomNum,String.valueOf(expire)};
        //发送验证码
        HashMap<String, Object> result = sdk.sendTemplateSMS(to, templateId, data);
        if("000000".equals(result.get("statusCode"))){
            redisTemplate.opsForValue().set(phone,randomNum);
            redisTemplate.expire(phone,60L, TimeUnit.SECONDS);
            return randomNum;
        }else{
           return  null;
        }
    }
}
