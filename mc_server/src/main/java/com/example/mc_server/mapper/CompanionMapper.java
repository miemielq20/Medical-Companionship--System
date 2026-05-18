package com.example.mc_server.mapper;

import com.example.mc_server.dto.CompanionList;
import com.example.mc_server.entity.CompanionAvatar;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CompanionMapper {

    //获取陪护师头像列表
    @Select("SELECT * FROM photo")
    List<CompanionAvatar> selectPhotos();

    //添加陪护师
    @Insert("INSERT INTO companion(name,mobile,age,sex,avatar,active,create_user_id)"+
    "VALUES (#{name},#{mobile},#{age},#{sex},#{avatar},#{active},#{createUserId})"
    )
    int insertCompanion(@Param("name") String name,
                        @Param("mobile") String mobile,
                        @Param("age") Integer age,
                        @Param("sex") String sex,
                        @Param("avatar") String avatar,
                        @Param("active") Integer active ,
                        @Param("createUserId") Integer createUserId );
    //更新陪护师
    @Insert("UPDATE companion SET name=#{name},mobile=#{mobile},age=#{age},sex=#{sex},avatar=#{avatar},active=#{active} " +
            "WHERE id=#{id}"
    )
    int updateCompanion(@Param("id") Long id ,
                        @Param("name") String name,
                        @Param("mobile") String mobile,
                        @Param("age") Integer age,
                        @Param("sex") String sex,
                        @Param("avatar") String avatar,
                        @Param("active") Integer active    );
    //陪护师列表
    @Select("SELECT * FROM companion WHERE create_user_id = #{createUserId} ORDER BY id DESC")
    List<CompanionList> getAllCompanionList(@Param("createUserId") Integer createUserId);

    //批量删除陪护员
    @Delete("<script>" +
            "DELETE FROM companion WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchDeleteCompanions(@Param("ids") List<Long> ids);



}
