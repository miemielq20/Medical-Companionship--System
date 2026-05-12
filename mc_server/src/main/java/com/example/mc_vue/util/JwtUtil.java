package com.example.mc_vue.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtil {
    // 密钥
    private static final String SECRET = "mc_vue_jwt_secret_key_2024_very_long_and_secure_key_string_for_hmac_sha256";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    public static String generateToken(Long userId, String username) {
        // 生成 JWT
        return Jwts.builder()
                .subject(username)
                //自定义字段
                .claim("userId", userId)
                .issuedAt(new Date())
                // token签发时间
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                // token 过期时间
                .signWith(SECRET_KEY)
                //使用密钥签名
                .compact();
    }

    //解析Token
    public static Claims parseToken(String token) {
        return Jwts.parser()
                //校验密匙
                .verifyWith(SECRET_KEY)
                .build()
                // 解析并校验 Token
                .parseSignedClaims(token)
                //拿到 拿到 Payload（Claims）
                .getPayload();
    }

    //校验 Token 是否有效
    public static boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 从 Token 中获取用户 ID
    public static Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    // 从 Token 中获取用户名
    public static String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }
}
