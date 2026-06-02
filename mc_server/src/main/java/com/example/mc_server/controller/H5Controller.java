package com.example.mc_server.controller;

import com.example.mc_server.dto.AuthRequest;
import com.example.mc_server.dto.CreateOrderRequest;
import com.example.mc_server.dto.CreateOrderResponse;
import com.example.mc_server.dto.H5CompanionResponse;
import com.example.mc_server.dto.HomeIndexResponse;
import com.example.mc_server.dto.OrderDTO;
import com.example.mc_server.service.H5Service;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * H5移动端接口控制器
 * 提供首页、陪护师、订单创建/列表/详情/完成等功能
 */
@RestController
public class H5Controller {
    @Autowired
    private H5Service h5Service;

    /**
     * H5首页数据：轮播图、导航、推荐医院
     * @param province 可选省份过滤
     */
    @GetMapping("/Index/index")
    public AuthRequest<HomeIndexResponse> index(@RequestParam(required = false) String province) {
        try {
            return new AuthRequest<>(10000, "success", h5Service.getHomeIndex(province));
        } catch (Exception e) {
            return new AuthRequest<>(500, e.getMessage(), null);
        }
    }

    /**
     * 陪护师选择页数据：可选陪护师列表、医院选项、默认服务
     */
    @GetMapping("/h5/companion")
    public AuthRequest<H5CompanionResponse> companion() {
        try {
            return new AuthRequest<>(10000, "success", h5Service.getCompanionData());
        } catch (Exception e) {
            return new AuthRequest<>(500, e.getMessage(), null);
        }
    }

    /**
     * 创建订单
     * 根据医院、陪护师、预约时间等信息生成订单，返回支付二维码链接
     */
    @PostMapping("/createOrder")
    public AuthRequest<CreateOrderResponse> createOrder(@RequestBody CreateOrderRequest request, HttpServletRequest httpRequest) {
        try {
            return new AuthRequest<>(10000, "success", h5Service.createOrder(request, currentUserId(httpRequest)));
        } catch (Exception e) {
            return new AuthRequest<>(500, e.getMessage(), null);
        }
    }

    /**
     * 当前用户的订单列表，可按state筛选（1待支付 2待服务 3已完成 4已取消）
     */
    @GetMapping("/order/list")
    public AuthRequest<List<OrderDTO>> orderList(@RequestParam(required = false) String state, HttpServletRequest request) {
        try {
            return new AuthRequest<>(10000, "success", h5Service.getOrderList(currentUserId(request), state));
        } catch (Exception e) {
            return new AuthRequest<>(500, e.getMessage(), null);
        }
    }

    /**
     * 订单详情
     */
    @GetMapping("/order/detail")
    public AuthRequest<OrderDTO> orderDetail(@RequestParam("oid") String oid, HttpServletRequest request) {
        try {
            return new AuthRequest<>(10000, "success", h5Service.getOrderDetail(currentUserId(request), oid));
        } catch (Exception e) {
            return new AuthRequest<>(500, e.getMessage(), null);
        }
    }

    /**
     * 完成订单：将待服务订单(trade_state=2)改为已完成(trade_state=3)
     */
    @PostMapping("/order/complete")
    public AuthRequest<String> completeOrder(@RequestBody Map<String, String> body) {
        try {
            String oid = body.get("oid");
            if (oid == null || oid.isEmpty()) {
                return new AuthRequest<>(400, "订单号不能为空", null);
            }
            h5Service.completeOrder(oid);
            return new AuthRequest<>(10000, "success", "订单已完成");
        } catch (Exception e) {
            return new AuthRequest<>(500, e.getMessage(), null);
        }
    }

    /**
     * 从请求中提取当前登录用户的ID
     * Token拦截器会将userId存入request属性
     */
    private Long currentUserId(HttpServletRequest request) {
        Object userId = request.getAttribute("userId");
        if (userId instanceof Long id) {
            return id;
        }
        throw new RuntimeException("user not authenticated");
    }
}