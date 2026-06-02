package com.example.mc_server.interceptor;

import com.example.mc_server.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 放行 /gaode/ 接口（高德同步用，不走 token）
        if (request.getRequestURI().contains("/gaode/")) {
            return true;
        }

        // 放行认证相关接口（登录注册等不需要token）
        String uri = request.getRequestURI();
        if (uri.contains("/get/code") || uri.contains("/user/authentication") || uri.contains("/login")) {
            return true;
        }

        // 从请求头中获取 token（支持 Authorization 和 x-token 两种方式）
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            token = request.getHeader("x-token");
        }
        if (token == null || token.isEmpty()) {
            token = request.getHeader("h-token");
        }

        // 检查 token 是否存在
        if (token == null || token.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未提供token\"}");
            return false;
        }

        // 处理 Bearer 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 验证 token
        if (!JwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"token无效或已过期\"}");
            return false;
        }

        // 从 token 中获取用户信息并设置到请求属性中
        Long userId = JwtUtil.getUserIdFromToken(token);
        request.setAttribute("userId", userId);

        return true;
    }
}
