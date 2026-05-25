package com.example.mc_server.controller;

import com.example.mc_server.dto.AuthRequest;
import com.example.mc_server.dto.DashboardDTO;
import com.example.mc_server.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 控制台仪表盘接口
 * 提供订单统计、用户统计、收入统计及系统信息
 */
@RestController
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * 获取控制台仪表盘数据
     * 包含：总订单数、总用户数、总流水、今日订单列表、系统信息、近7天订单图表数据
     */
    @GetMapping("/dashboard")
    public AuthRequest<DashboardDTO> dashboard() {
        try {
            return new AuthRequest<>(10000, "success", dashboardService.getDashboard());
        } catch (Exception e) {
            return new AuthRequest<>(500, e.getMessage(), null);
        }
    }
}