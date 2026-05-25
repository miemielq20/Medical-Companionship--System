package com.example.mc_server.service;

import com.example.mc_server.dto.DashboardDTO;
import com.example.mc_server.dto.DashboardDTO.TodayOrderDTO;
import com.example.mc_server.dto.DashboardDTO.SystemInfoDTO;
import com.example.mc_server.dto.DashboardDTO.OrderChartDTO;
import com.example.mc_server.mapper.H5Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 控制台仪表盘服务
 * 负责组装仪表盘所需的各类统计数据
 */
@Service
public class DashboardService {

    @Autowired
    private H5Mapper h5Mapper;

    /**
     * 获取仪表盘完整数据
     * 包括：总订单数、总用户数、总流水、今日订单、系统信息、近7天图表数据
     */
    public DashboardDTO getDashboard() {
        DashboardDTO dto = new DashboardDTO();

        // 统计卡片数据
        dto.setTotalOrders(h5Mapper.countTotalOrders());
        dto.setTotalUsers(h5Mapper.countTotalUsers());
        dto.setTotalRevenue(h5Mapper.sumTotalRevenue());

        // 今日最新10条订单
        List<Map<String, Object>> todayRows = h5Mapper.selectTodayOrders();
        List<TodayOrderDTO> todayOrders = todayRows.stream().map(row -> {
            TodayOrderDTO order = new TodayOrderDTO();
            order.setCompanionName((String) row.getOrDefault("companionName", ""));
            order.setUserName((String) row.getOrDefault("userName", ""));
            order.setCompanionAvatar((String) row.getOrDefault("companionAvatar", ""));
            Object revenue = row.get("revenue");
            if (revenue instanceof java.math.BigDecimal bd) {
                order.setRevenue(bd);
            }
            Object time = row.get("time");
            if (time instanceof Long l) {
                order.setTime(l);
            }
            order.setTradeState((String) row.getOrDefault("tradeState", ""));
            return order;
        }).collect(Collectors.toList());
        dto.setTodayOrders(todayOrders);

        // 系统运行信息
        SystemInfoDTO sysInfo = new SystemInfoDTO();
        sysInfo.setSystemVersion("1.0.0");
        sysInfo.setOs(System.getProperty("os.name") + " " + System.getProperty("os.arch"));
        sysInfo.setJavaVersion(System.getProperty("java.version"));
        sysInfo.setCurrentTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        sysInfo.setRuntimeEnv(System.getProperty("spring.profiles.active", "default"));
        sysInfo.setServerIp(getServerIp());
        sysInfo.setSystemName(getSystemName());
        dto.setSystemInfo(sysInfo);

        // 近7天订单图表数据
        long sevenDaysAgo = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000;
        List<Map<String, Object>> chartRows = h5Mapper.selectOrderChartData(sevenDaysAgo);
        List<OrderChartDTO> chartData = chartRows.stream().map(row -> {
            OrderChartDTO item = new OrderChartDTO();
            item.setDate((String) row.get("date"));
            Object cnt = row.get("count");
            if (cnt instanceof Long l) item.setCount(l);
            Object amt = row.get("amount");
            if (amt instanceof java.math.BigDecimal bd) item.setAmount(bd);
            return item;
        }).collect(Collectors.toList());
        dto.setOrderChart(chartData);

        return dto;
    }

    /**
     * 获取系统显示名称
     */
    private String getSystemName() {
        return "医疗陪护管理系统";
    }

    /**
     * 获取当前服务器IP地址，获取失败返回127.0.0.1
     */
    private String getServerIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }
}