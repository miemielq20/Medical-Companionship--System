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

@Service
public class H5Service {
    private static final long PAY_EXPIRE_MILLIS = 2 * 60 * 60 * 1000L;

    @Autowired
    private H5Mapper h5Mapper;

    @Autowired
    private UserMapper userMapper;

    public HomeIndexResponse getHomeIndex() {
        HomeIndexResponse response = new HomeIndexResponse();
        response.setNow(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.setSlides(h5Mapper.selectSlides());
        response.setNav2s(h5Mapper.selectNavs("nav2"));
        response.setNavs(h5Mapper.selectNavs("nav"));
        response.setHospitals(h5Mapper.selectHomeHospitals());
        return response;
    }

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
        order.setTradeState(1);
        order.setServiceState(1);
        order.setPrice(hospital.getServicePrice() == null ? service.getPrice() : hospital.getServicePrice());
        order.setPaidPrice(BigDecimal.ZERO);
        order.setCodeUrl(codeUrl);
        order.setOrderStartTime(now);
        order.setTimeEnd(now + PAY_EXPIRE_MILLIS);
        h5Mapper.insertOrder(order);

        return new CreateOrderResponse(codeUrl);
    }

    public List<OrderDTO> getOrderList(Long userId, String state) {
        h5Mapper.cancelExpiredUnpaidOrders(userId, System.currentTimeMillis());
        Integer stateCode = parseState(state);
        return h5Mapper.selectOrdersByUserId(userId, stateCode).stream()
                .map(this::toOrderDTO)
                .collect(Collectors.toList());
    }

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

    private String tradeStateText(Integer state) {
        if (state == null) {
            return "";
        }
        return switch (state) {
            case 1 -> "\u5f85\u652f\u4ed8";
            case 2 -> "\u5f85\u670d\u52a1";
            case 3 -> "\u5df2\u5b8c\u6210";
            case 4 -> "\u5df2\u53d6\u6d88";
            default -> "";
        };
    }

    private String serviceStateText(Integer state) {
        if (state == null) {
            return "";
        }
        return switch (state) {
            case 1 -> "\u5f85\u652f\u4ed8";
            case 2 -> "\u5f85\u670d\u52a1";
            case 3 -> "\u670d\u52a1\u4e2d";
            case 4 -> "\u5df2\u5b8c\u6210";
            case 5 -> "\u5df2\u53d6\u6d88";
            default -> "";
        };
    }
}
