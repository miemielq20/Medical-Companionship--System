package com.example.mc_server.service;

import com.example.mc_server.entity.Hospital;
import com.example.mc_server.mapper.H5Mapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GaodeService {

    private static final Logger log = LoggerFactory.getLogger(GaodeService.class);
    private static final String GAODE_URL = "https://restapi.amap.com/v3/place/text";
    private static final int PER_PROVINCE_LIMIT = 20;

    @Value("${gaode.api.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private H5Mapper h5Mapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final List<String> PROVINCES = List.of(
            "北京", "天津", "河北", "山西", "内蒙古", "辽宁", "吉林", "黑龙江",
            "上海", "江苏", "浙江", "安徽", "福建", "江西", "山东",
            "河南", "湖北", "湖南", "广东", "广西", "海南",
            "重庆", "四川", "贵州", "云南", "西藏",
            "陕西", "甘肃", "青海", "宁夏", "新疆"
    );

    public Map<String, Integer> syncAllProvinces() {
        int totalInserted = 0;
        int successProvinces = 0;
        List<String> errorProvinces = new ArrayList<>();

        for (String province : PROVINCES) {
            try {
                int count = syncProvince(province);
                if (count > 0) {
                    totalInserted += count;
                    successProvinces++;
                    log.info("[{}] 写入 {} 条医院", province, count);
                } else {
                    log.warn("[{}] 未获取到医院数据", province);
                }
                Thread.sleep(50);
            } catch (Exception e) {
                errorProvinces.add(province);
                log.error("[{}] 同步失败: {}", province, e.getMessage());
            }
        }
        return Map.of("totalInserted", totalInserted, "successProvinces", successProvinces, "errorCount", errorProvinces.size());
    }

    public int syncProvince(String province) {
        try {
            String encodedCity = URLEncoder.encode(province, StandardCharsets.UTF_8.name());
            String encodedKeyword = URLEncoder.encode("医院", StandardCharsets.UTF_8.name());
            String url = GAODE_URL + "?keywords=" + encodedKeyword + "&city=" + encodedCity
                + "&offset=" + PER_PROVINCE_LIMIT + "&page=1&extensions=all&key=" + apiKey;
            URI uri = new URI(url);
            String jsonStr = restTemplate.getForObject(uri, String.class);
            if (jsonStr == null) return 0;

            JsonNode root = objectMapper.readTree(jsonStr);
            if (!"1".equals(root.path("status").asText())) {
                log.warn("高德 API 返回异常: {}", root.path("info").asText());
                return 0;
            }

            JsonNode pois = root.path("pois");
            if (pois.isEmpty()) return 0;

            int inserted = 0;
            for (JsonNode poi : pois) {
                Hospital hospital = mapPoiToHospital(poi, province);
                if (hospital.getName() == null || hospital.getName().isBlank()) continue;
                h5Mapper.insertHospital(hospital);
                inserted++;
            }
            return inserted;
        } catch (Exception e) {
            log.error("同步省份 [{}] 失败: {}", province, e.getMessage());
            return 0;
        }
    }

    private Hospital mapPoiToHospital(JsonNode poi, String province) {
        Hospital hospital = new Hospital();
        hospital.setName(poi.path("name").asText(""));
        hospital.setProvince(province.replaceAll("\u7701|\u5e02|\u81ea\u6cbb\u533a|\u7279\u522b\u884c\u653f\u533a$", ""));
        hospital.setActive(1);

        JsonNode photosNode = poi.path("photos");
        if (photosNode.isArray() && !photosNode.isEmpty()) {
            hospital.setAvatarUrl(photosNode.get(0).path("url").asText(""));
        }

        String type = poi.path("deep_info").path("type").asText("");
        String label = type;
        if (label.isBlank()) {
            String typeStr = poi.path("type").asText("");
            if (!typeStr.isBlank()) {
                String[] parts = typeStr.split(";");
                label = parts[parts.length - 1];
            }
        }
        hospital.setLabel(label);
        hospital.setRank(extractRank(type));

        String address = poi.path("address").asText("");
        String intro = "";
        if (!hospital.getName().isBlank()) {
            StringBuilder sb = new StringBuilder(hospital.getName());
            if (!province.isBlank()) sb.append("位于").append(province);
            if (!address.isBlank()) sb.append(address);
            if (!type.isBlank()) sb.append("，是一家").append(type);
            sb.append("。");
            intro = sb.toString();
        }
        hospital.setIntro(intro);
        return hospital;
    }

    private String extractRank(String type) {
        if (type == null || type.isBlank()) return "";
        if (type.contains("三级甲等") || type.contains("三甲")) return "三甲";
        if (type.contains("三级")) return "三级";
        if (type.contains("二级甲等") || type.contains("二甲")) return "二甲";
        if (type.contains("二级")) return "二级";
        if (type.contains("一级")) return "一级";
        return "";
    }
}