package com.example.mc_server.service;

import cn.hutool.core.util.RandomUtil;
import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import com.example.mc_server.entity.SmsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * 短信验证码服务
 * 通过容联云通讯SDK发送短信验证码，验证码存入Redis并设置有效期
 */
@Service
public class SmsService {
    @Autowired
    private SmsRequest smsRequest;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送短信验证码
     * @param phone 目标手机号
     * @return 生成的验证码，发送失败返回null
     */
    public String sendSms(String phone) {
        // 容联云通讯配置
        String serverIp = "app.cloopen.com";
        String serverPort = "8883";
        String accountSId = smsRequest.getAccountId();
        String accountToken = smsRequest.getAuthToken();
        String appId = smsRequest.getAppId();

        // 初始化SDK
        CCPRestSmsSDK sdk = new CCPRestSmsSDK();
        sdk.init(serverIp, serverPort);
        sdk.setAccount(accountSId, accountToken);
        sdk.setAppId(appId);
        sdk.setBodyType(BodyType.Type_JSON);

        String to = phone;
        String templateId = "1";

        // 生成4位随机验证码
        String randomNum = RandomUtil.randomNumbers(4);
        // 验证码有效期（分钟）
        long expire = 2L;
        String[] data = {randomNum, String.valueOf(expire)};

        // 发送短信
        HashMap<String, Object> result = sdk.sendTemplateSMS(to, templateId, data);
        if ("000000".equals(result.get("statusCode"))) {
            // 发送成功，验证码存入Redis，有效期60秒
            redisTemplate.opsForValue().set(phone, randomNum);
            redisTemplate.expire(phone, 60L, TimeUnit.SECONDS);
            return randomNum;
        } else {
            return null;
        }
    }
}