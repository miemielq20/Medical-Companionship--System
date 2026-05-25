package com.example.mc_server.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 控制台仪表盘数据传输对象
 */
@Data
public class DashboardDTO {
    /** 总订单数 */
    private Long totalOrders;
    /** 总用户数 */
    private Long totalUsers;
    /** 总流水金额 */
    private BigDecimal totalRevenue;
    /** 今日订单列表 */
    private List<TodayOrderDTO> todayOrders;
    /** 近7天订单图表数据 */
    private List<OrderChartDTO> orderChart;
    /** 系统运行信息 */
    private SystemInfoDTO systemInfo;

    /**
     * 今日订单条目
     */
    @Data
    public static class TodayOrderDTO {
        /** 陪护师名称 */
        private String companionName;
        /** 用户名称 */
        private String userName;
        /** 陪护师头像URL */
        private String companionAvatar;
        /** 订单金额 */
        private BigDecimal revenue;
        /** 下单时间戳 */
        private Long time;
        /** 交易状态文本 */
        private String tradeState;
    }

    /**
     * 系统信息
     */
    @Data
    public static class SystemInfoDTO {
        /** 系统版本号 */
        private String systemVersion;
        /** 操作系统名称和架构 */
        private String os;
        /** Java运行时版本 */
        private String javaVersion;
        /** 当前时间 */
        private String currentTime;
        /** 运行环境标识 */
        private String runtimeEnv;
        /** 服务器IP地址 */
        private String serverIp;
        /** 系统显示名称 */
        private String systemName;
    }

    /**
     * 订单图表数据点
     */
    @Data
    public static class OrderChartDTO {
        /** 日期（yyyy-MM-dd） */
        private String date;
        /** 当日订单数量 */
        private Long count;
        /** 当日订单金额合计 */
        private java.math.BigDecimal amount;
    }
}