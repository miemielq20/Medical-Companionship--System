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

@RestController
public class H5Controller {
    @Autowired
    private H5Service h5Service;

    @GetMapping("/Index/index")
    public AuthRequest<HomeIndexResponse> index() {
        try {
            return new AuthRequest<>(10000, "success", h5Service.getHomeIndex());
        } catch (Exception e) {
            return new AuthRequest<>(500, e.getMessage(), null);
        }
    }

    @GetMapping("/h5/companion")
    public AuthRequest<H5CompanionResponse> companion() {
        try {
            return new AuthRequest<>(10000, "success", h5Service.getCompanionData());
        } catch (Exception e) {
            return new AuthRequest<>(500, e.getMessage(), null);
        }
    }

    @PostMapping("/createOrder")
    public AuthRequest<CreateOrderResponse> createOrder(@RequestBody CreateOrderRequest request, HttpServletRequest httpRequest) {
        try {
            return new AuthRequest<>(10000, "success", h5Service.createOrder(request, currentUserId(httpRequest)));
        } catch (Exception e) {
            return new AuthRequest<>(500, e.getMessage(), null);
        }
    }

    @GetMapping("/order/list")
    public AuthRequest<List<OrderDTO>> orderList(@RequestParam(required = false) String state, HttpServletRequest request) {
        try {
            return new AuthRequest<>(10000, "success", h5Service.getOrderList(currentUserId(request), state));
        } catch (Exception e) {
            return new AuthRequest<>(500, e.getMessage(), null);
        }
    }

    @GetMapping("/order/detail")
    public AuthRequest<OrderDTO> orderDetail(@RequestParam("oid") String oid, HttpServletRequest request) {
        try {
            return new AuthRequest<>(10000, "success", h5Service.getOrderDetail(currentUserId(request), oid));
        } catch (Exception e) {
            return new AuthRequest<>(500, e.getMessage(), null);
        }
    }

    private Long currentUserId(HttpServletRequest request) {
        Object userId = request.getAttribute("userId");
        if (userId instanceof Long id) {
            return id;
        }
        throw new RuntimeException("user not authenticated");
    }
}
