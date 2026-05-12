package com.example.mc_vue.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("rong.sms")
@Data
public class SmsRequest {
    @Value("${sms.account-id}")
    private String accountId;
    @Value("${sms.auth-token}")
    private String authToken;
    @Value("${sms.app-id}")
    private String appId;
}
