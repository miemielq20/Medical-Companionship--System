package com.example.mc_server.service;

import com.example.mc_server.dto.CompanionList;
import com.example.mc_server.dto.CreateOrderRequest;
import com.example.mc_server.dto.CreateOrderResponse;
import com.example.mc_server.dto.H5CompanionResponse;
import com.example.mc_server.dto.HomeIndexResponse;
import com.example.mc_server.dto.OrderClientDTO;
import com.example.mc_server.dto.OrderDTO;
import com.example.mc_server.dto.ServiceDTO;
import com.example.mc_server.entity.Hospital;
import com.example.mc_server.entity.MedicalOrder;
import com.example.mc_server.entity.MedicalService;
import com.example.mc_server.entity.User;
import com.example.mc_server.mapper.H5Mapper;
import com.example.mc_server.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * H5移动端业务服务
 * 负责首页展示、陪护师选择、订单创建/查询/完成等核心业务逻辑
 */
@Service
public class H5Service {
    /** 未支付订单过期时间：2小时（毫秒） */
    private static final long PAY_EXPIRE_MILLIS = 2 * 60 * 60 * 1000L;

    @Autowired
    private H5Mapper h5Mapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 首页数据：当前时间、轮播图、导航入口、推荐医院
     */
    public HomeIndexResponse getHomeIndex() {
        HomeIndexResponse response = new HomeIndexResponse();
        response.setNow(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.setSlides(h5Mapper.selectSlides());
        response.setNav2s(h5Mapper.selectNavs("nav2"));
        response.setNavs(h5Mapper.selectNavs("nav"));
        response.setHospitals(h5Mapper.selectHomeHospitals());
        return response;
    }

    /**
     * 陪护师选择页数据：可选陪护师、医院下拉选项、默认服务信息
     */
    public H5CompanionResponse getCompanionData() {
        MedicalService service = h5Mapper.selectDefaultService();
        ServiceDTO serviceDTO = new ServiceDTO(
                service == null ? "" : service.getServiceName(),
                service == null ? "" : service.getServiceImg()
        );
        return new H5CompanionResponse(
                h5Mapper.selectActiveCompanions(),
                h5Mapper.selectHospitalOptions(),
                serviceDTO
        );
    }

    /**
     * 创建订单
     * 校验医院、陪护师、联系方式，生成唯一订单号，初始状态为待支付
     */
    public CreateOrderResponse createOrder(CreateOrderRequest request, Long userId) {
        if (request == null) {
            throw new RuntimeException("request is empty");
        }
        if (request.getHospitalId() == null || request.getCompanionId() == null || request.getStarttime() == null) {
            throw new RuntimeException("missing required order fields");
        }
        if (!StringUtils.hasText(request.getReceiveAddress()) || !StringUtils.hasText(request.getTel())) {
            throw new RuntimeException("missing contact fields");
        }

        Hospital hospital = h5Mapper.selectHospitalById(request.getHospitalId());
        if (hospital == null) {
            throw new RuntimeException("hospital not found");
        }

        MedicalService service = h5Mapper.selectServiceById(hospital.getServiceId());
        if (service == null) {
            service = h5Mapper.selectDefaultService();
        }
        if (service == null) {
            throw new RuntimeException("service not configured");
        }

        CompanionList companion = h5Mapper.selectCompanionById(request.getCompanionId());
        if (companion == null || companion.getActive() == null || companion.getActive() != 1) {
            throw new RuntimeException("companion not available");
        }

        // 生成唯一订单号：MC + 时间戳 + UUID前8位
        long now = System.currentTimeMillis();
        String outTradeNo = "MC" + now + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        String codeUrl = "weixin://wxpay/bizpayurl?pr=" + outTradeNo;

        MedicalOrder order = new MedicalOrder();
        order.setOutTradeNo(outTradeNo);
        order.setTransactionId("");
        order.setUserId(userId);
        order.setHospitalId(hospital.getId());
        order.setHospitalName(hospital.getName());
        order.setServiceId(service.getId());
        order.setServiceName(service.getServiceName());
        order.setServiceImg(service.getServiceImg());
        order.setCompanionId(request.getCompanionId());
        order.setStarttime(request.getStarttime());
        order.setReceiveAddress(request.getReceiveAddress());
        order.setTel(request.getTel());
        order.setDemand(request.getDemand());
        order.setTradeState(1);   // 待支付
        order.setServiceState(1); // 待支付
        order.setPrice(hospital.getServicePrice() == null ? service.getPrice() : hospital.getServicePrice());
        order.setPaidPrice(BigDecimal.ZERO);
        order.setCodeUrl(codeUrl);
        order.setOrderStartTime(now);
        order.setTimeEnd(now + PAY_EXPIRE_MILLIS);
        h5Mapper.insertOrder(order);

        return new CreateOrderResponse(codeUrl);
    }

    /**
     * 获取用户订单列表，先自动取消超时未支付订单，再按状态筛选
     */
    public List<OrderDTO> getOrderList(Long userId, String state) {
        h5Mapper.cancelExpiredUnpaidOrders(userId, System.currentTimeMillis());
        Integer stateCode = parseState(state);
        return h5Mapper.selectOrdersByUserId(userId, stateCode).stream()
                .map(this::toOrderDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取单个订单详情
     */
    public OrderDTO getOrderDetail(Long userId, String outTradeNo) {
        if (!StringUtils.hasText(outTradeNo)) {
            throw new RuntimeException("oid is required");
        }
        h5Mapper.cancelExpiredUnpaidOrders(userId, System.currentTimeMillis());
        MedicalOrder order = h5Mapper.selectOrderByNoAndUserId(outTradeNo, userId);
        if (order == null) {
            throw new RuntimeException("order not found");
        }
        return toOrderDTO(order);
    }

    /**
     * 完成订单：将待服务订单标记为已完成
     */
    public void completeOrder(String outTradeNo) {
        int rows = h5Mapper.completeOrder(outTradeNo);
        if (rows == 0) {
            throw new RuntimeException("订单不存在或状态不允许完成");
        }
    }

    /**
     * 将状态字符串转为数字：1待支付/2待服务/3已完成/4已取消
     */
    private Integer parseState(String state) {
        if (!StringUtils.hasText(state)) {
            return null;
        }
        try {
            return Integer.parseInt(state);
        } catch (NumberFormatException e) {
            throw new RuntimeException("invalid state");
        }
    }

    /**
     * 将订单实体转为前端DTO，关联用户信息和陪护师信息
     */
    private OrderDTO toOrderDTO(MedicalOrder order) {
        User user = userMapper.selectById(order.getUserId());
        CompanionList companion = h5Mapper.selectCompanionById(order.getCompanionId());
        OrderClientDTO client = new OrderClientDTO(
                user == null ? order.getUserId() : user.getId(),
                user == null ? "" : (StringUtils.hasText(user.getNickname()) ? user.getNickname() : user.getUserName()),
                user == null ? "" : user.getMobile()
        );

        OrderDTO dto = new OrderDTO();
        dto.setWxCode(order.getCodeUrl());
        dto.setHospitalId(order.getHospitalId());
        dto.setHospitalName(order.getHospitalName());
        dto.setStarttime(order.getStarttime());
        dto.setCompanionId(order.getCompanionId());
        dto.setReceiveAddress(order.getReceiveAddress());
        dto.setTel(order.getTel());
        dto.setDemand(order.getDemand());
        dto.setClient(client);
        dto.setOrderStartTime(order.getOrderStartTime());
        dto.setTradeState(tradeStateText(order.getTradeState()));
        dto.setTimeEnd(order.getTimeEnd());
        dto.setServiceState(serviceStateText(order.getServiceState()));
        dto.setServiceImg(order.getServiceImg());
        dto.setTransactionId(order.getTransactionId());
        dto.setOutTradeNo(order.getOutTradeNo());
        dto.setPrice(order.getPrice());
        dto.setCodeUrl(order.getCodeUrl());
        dto.setUserId(order.getUserId());
        dto.setServiceName(order.getServiceName());
        dto.setPaidPrice(order.getPaidPrice());
        dto.setCompanion(companion);
        return dto;
    }

    /**
     * 交易状态数字转中文：1待支付/2待服务/3已完成/4已取消
     */
    private String tradeStateText(Integer state) {
        if (state == null) {
            return "";
        }
        return switch (state) {
            case 1 -> "待支付";
            case 2 -> "待服务";
            case 3 -> "已完成";
            case 4 -> "已取消";
            default -> "";
        };
    }

    /**
     * 服务状态数字转中文
     */
    private String serviceStateText(Integer state) {
        if (state == null) {
            return "";
        }
        return switch (state) {
            case 1 -> "待支付";
            case 2 -> "待服务";
            case 3 -> "服务中";
            case 4 -> "已完成";
            case 5 -> "已取消";
            default -> "";
        };
    }

}
