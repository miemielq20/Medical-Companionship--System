package com.example.mc_server.mapper;

import com.example.mc_server.dto.CompanionList;
import com.example.mc_server.dto.HomeHospitalDTO;
import com.example.mc_server.dto.HomeNavDTO;
import com.example.mc_server.dto.HomeSlideDTO;
import com.example.mc_server.dto.HospitalOptionDTO;
import com.example.mc_server.entity.Hospital;
import com.example.mc_server.entity.MedicalOrder;
import com.example.mc_server.entity.MedicalService;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * H5端及控制台数据访问层
 * 负责首页内容、医院、服务、陪护师、订单等数据的数据库查询
 */
@Mapper
public interface H5Mapper {

    // ==================== 首页轮播与导航 ====================

    /** 查询启用的首页轮播图，按排序升序 */
    @Select("SELECT id, stype, stype_link AS stypeLink, title, stype_text AS stypeText, pic_image_url AS picImageUrl " +
            "FROM h5_home_banner WHERE active = 1 ORDER BY sort ASC, id ASC")
    List<HomeSlideDTO> selectSlides();

    /** 查询指定位置的首页导航入口 */
    @Select("SELECT id, stype, stype_link AS stypeLink, title, stype_text AS stypeText, " +
            "pic_image_url AS picImageUrl, cat_text AS catText, tcolor " +
            "FROM h5_home_nav WHERE active = 1 AND position = #{position} ORDER BY sort ASC, id ASC")
    List<HomeNavDTO> selectNavs(@Param("position") String position);

    // ==================== 医院与服务 ====================

    /** 查询启用的首页推荐医院，支持按省份过滤 */
    @Select("<script>SELECT id, name, `rank`, label, intro, avatar_url AS avatarUrl FROM hospital WHERE active = 1 <if test=\"province != null and province != ''\">AND province = #{province}</if> ORDER BY id ASC</script>")
    List<HomeHospitalDTO> selectHomeHospitals(@Param("province") String province);

    /** 查询所有启用医院的下拉选项 */
    @Select("SELECT id, name, service_id AS serviceId, service_price AS servicePrice " +
            "FROM hospital WHERE active = 1 ORDER BY sort ASC, id ASC")
    List<HospitalOptionDTO> selectHospitalOptions();

    /** 根据ID查询启用的医院 */
    @Select("SELECT * FROM hospital WHERE id = #{id} AND active = 1")
    Hospital selectHospitalById(@Param("id") Long id);

    /** 根据ID查询启用的医疗服务 */
    @Select("SELECT * FROM medical_service WHERE id = #{id} AND active = 1")
    MedicalService selectServiceById(@Param("id") Long id);

    /** 查询第一个启用的默认医疗服务 */
    @Select("SELECT * FROM medical_service WHERE active = 1 ORDER BY id ASC LIMIT 1")
    MedicalService selectDefaultService();

    // ==================== 陪护师 ====================

    /** 查询所有启用的陪护师 */
    @Select("SELECT create_time AS createTime, id, name, mobile, avatar, sex, age, active " +
            "FROM companion WHERE active = 1 ORDER BY id DESC")
    List<CompanionList> selectActiveCompanions();

    /** 根据ID查询陪护师（不限状态） */
    @Select("SELECT create_time AS createTime, id, name, mobile, avatar, sex, age, active " +
            "FROM companion WHERE id = #{id}")
    CompanionList selectCompanionById(@Param("id") Long id);

    // ==================== 订单操作 ====================

    /** 插入新订单 */
    @Insert("INSERT INTO medical_order(" +
            "out_trade_no, transaction_id, user_id, hospital_id, hospital_name, service_id, service_name, service_img, " +
            "companion_id, starttime, receive_address, tel, demand, trade_state, service_state, price, paid_price, " +
            "code_url, order_start_time, time_end" +
            ") VALUES (" +
            "#{outTradeNo}, #{transactionId}, #{userId}, #{hospitalId}, #{hospitalName}, #{serviceId}, #{serviceName}, #{serviceImg}, " +
            "#{companionId}, #{starttime}, #{receiveAddress}, #{tel}, #{demand}, #{tradeState}, #{serviceState}, #{price}, #{paidPrice}, " +
            "#{codeUrl}, #{orderStartTime}, #{timeEnd}" +
            ")")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertOrder(MedicalOrder order);

    /** 查询用户订单列表，可按交易状态筛选 */
    @Select("<script>" +
            "SELECT * FROM medical_order WHERE user_id = #{userId} " +
            "<if test='state != null and state != \"\"'> AND trade_state = #{state} </if>" +
            "ORDER BY id DESC" +
            "</script>")
    List<MedicalOrder> selectOrdersByUserId(@Param("userId") Long userId, @Param("state") Integer state);

    /** 根据订单号和用户ID查询单个订单 */
    @Select("SELECT * FROM medical_order WHERE user_id = #{userId} AND out_trade_no = #{outTradeNo} LIMIT 1")
    MedicalOrder selectOrderByNoAndUserId(@Param("outTradeNo") String outTradeNo, @Param("userId") Long userId);

    /** 自动取消超时的未支付订单 */
    @Update("UPDATE medical_order SET trade_state = 4, service_state = 5 " +
            "WHERE user_id = #{userId} AND trade_state = 1 AND time_end <= #{now}")
    int cancelExpiredUnpaidOrders(@Param("userId") Long userId, @Param("now") Long now);

    /** 完成订单：待服务(2) -> 已完成(3) */
    @Update("UPDATE medical_order SET trade_state = 3, service_state = 3 WHERE out_trade_no = #{outTradeNo} AND trade_state = 2")
    int completeOrder(@Param("outTradeNo") String outTradeNo);

    // ==================== 控制台仪表盘查询 ====================

    /** 统计总订单数 */
    @Select("SELECT COUNT(*) FROM medical_order")
    Long countTotalOrders();

    /** 统计总用户数 */
    @Select("SELECT COUNT(*) FROM user")
    Long countTotalUsers();

    /** 统计总流水金额 */
    @Select("SELECT COALESCE(SUM(price), 0) FROM medical_order")
    java.math.BigDecimal sumTotalRevenue();

    /** 查询最新10条订单（关联陪护师和用户信息），交易状态转为中文 */
    @Select("SELECT " +
            "c.name AS companionName, " +
            "c.avatar AS companionAvatar, " +
            "u.nickname AS userName, " +
            "mo.price AS revenue, " +
            "mo.order_start_time AS time, " +
            "CASE mo.trade_state WHEN 1 THEN '待支付' WHEN 2 THEN '待服务' WHEN 3 THEN '已完成' WHEN 4 THEN '已取消' ELSE '' END AS tradeState " +
            "FROM medical_order mo " +
            "LEFT JOIN companion c ON mo.companion_id = c.id " +
            "LEFT JOIN user u ON mo.user_id = u.id " +
            "ORDER BY mo.id DESC LIMIT 10")
    List<Map<String, Object>> selectTodayOrders();

    /** 查询近N天每日订单数和金额（用于图表），按天分组 */
    @Select("SELECT " +
            "DATE_FORMAT(FROM_UNIXTIME(order_start_time / 1000), '%Y-%m-%d') AS date, " +
            "COUNT(*) AS count, " +
            "COALESCE(SUM(price), 0) AS amount " +
            "FROM medical_order " +
            "WHERE order_start_time >= #{since} " +
            "GROUP BY DATE_FORMAT(FROM_UNIXTIME(order_start_time / 1000), '%Y-%m-%d') " +
            "ORDER BY date ASC")
    List<Map<String, Object>> selectOrderChartData(@Param("since") Long since);

    /** 支付成功后更新订单：待支付(1) -> 待服务(2)，记录微信交易号 */
    @Update("UPDATE medical_order SET trade_state = 2, service_state = 2, transaction_id = #{transactionId} " +
            "WHERE out_trade_no = #{outTradeNo} AND trade_state = 1")
    int updateOrderPaid(@Param("outTradeNo") String outTradeNo, @Param("transactionId") String transactionId);

    /** 批量插入医院（高德POI同步） */
    @Insert("INSERT INTO hospital (name, province, `rank`, label, intro, avatar_url, active, create_time, update_time) " +
            "VALUES (#{name}, #{province}, #{rank}, #{label}, #{intro}, #{avatarUrl}, 1, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertHospital(Hospital hospital);
}