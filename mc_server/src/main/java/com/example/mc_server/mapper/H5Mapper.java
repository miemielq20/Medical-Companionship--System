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

import java.util.List;

@Mapper
public interface H5Mapper {
    @Select("SELECT id, stype, stype_link AS stypeLink, title, stype_text AS stypeText, pic_image_url AS picImageUrl " +
            "FROM h5_home_banner WHERE active = 1 ORDER BY sort ASC, id ASC")
    List<HomeSlideDTO> selectSlides();

    @Select("SELECT id, stype, stype_link AS stypeLink, title, stype_text AS stypeText, " +
            "pic_image_url AS picImageUrl, cat_text AS catText, tcolor " +
            "FROM h5_home_nav WHERE active = 1 AND position = #{position} ORDER BY sort ASC, id ASC")
    List<HomeNavDTO> selectNavs(@Param("position") String position);

    @Select("SELECT id, name, `rank`, label, intro, avatar_url AS avatarUrl " +
            "FROM hospital WHERE active = 1 ORDER BY sort ASC, id ASC")
    List<HomeHospitalDTO> selectHomeHospitals();

    @Select("SELECT id, name, service_id AS serviceId, service_price AS servicePrice " +
            "FROM hospital WHERE active = 1 ORDER BY sort ASC, id ASC")
    List<HospitalOptionDTO> selectHospitalOptions();

    @Select("SELECT * FROM hospital WHERE id = #{id} AND active = 1")
    Hospital selectHospitalById(@Param("id") Long id);

    @Select("SELECT * FROM medical_service WHERE id = #{id} AND active = 1")
    MedicalService selectServiceById(@Param("id") Long id);

    @Select("SELECT * FROM medical_service WHERE active = 1 ORDER BY id ASC LIMIT 1")
    MedicalService selectDefaultService();

    @Select("SELECT create_time AS createTime, id, name, mobile, avatar, sex, age, active " +
            "FROM companion WHERE active = 1 ORDER BY id DESC")
    List<CompanionList> selectActiveCompanions();

    @Select("SELECT create_time AS createTime, id, name, mobile, avatar, sex, age, active " +
            "FROM companion WHERE id = #{id}")
    CompanionList selectCompanionById(@Param("id") Long id);

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

    @Select("<script>" +
            "SELECT * FROM medical_order WHERE user_id = #{userId} " +
            "<if test='state != null and state != \"\"'> AND trade_state = #{state} </if>" +
            "ORDER BY id DESC" +
            "</script>")
    List<MedicalOrder> selectOrdersByUserId(@Param("userId") Long userId, @Param("state") Integer state);

    @Select("SELECT * FROM medical_order WHERE user_id = #{userId} AND out_trade_no = #{outTradeNo} LIMIT 1")
    MedicalOrder selectOrderByNoAndUserId(@Param("outTradeNo") String outTradeNo, @Param("userId") Long userId);
}
