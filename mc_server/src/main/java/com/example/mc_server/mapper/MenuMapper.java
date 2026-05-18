package com.example.mc_server.mapper;

import com.example.mc_server.dto.MenuSelectList;
import com.example.mc_server.entity.Menu;
import com.example.mc_server.entity.PermissionGroup;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MenuMapper {

    //查询权限下拉列表
    @Select("SELECT id, name FROM permission_group ORDER BY id")
    List<MenuSelectList> selectMenuList();

    //根据权限id查询权限组
    @Select("SELECT * FROM permission_group WHERE id = #{permissionId}")
    PermissionGroup selectPermissionGroupById(@Param("permissionId") Integer permissionId);

    //根据权限组id查询权限组下的权限
    @Select("<script>" +
            "SELECT * FROM menu WHERE id IN " +
            "<foreach collection='menuIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            " ORDER BY order_index ASC" +
            "</script>")
    List<Menu> selectMenusByIds(@Param("menuIds")List<Integer> menuIds);

    //查找对应id创建的权限组列表
    @Select("SELECT * FROM permission_group WHERE create_user_id = #{createUserId} ORDER BY id DESC")
    List<PermissionGroup> selectPermissionGroupByUserId(@Param("createUserId") Integer createUserId);

    //查找对应id创建的权限组内容(单个）
    @Select("SELECT * FROM permission_group WHERE id = #{id} AND create_user_id = #{createUserId}")
    PermissionGroup selectPermissionGroupByIdAndUserId(@Param("id") Integer id, @Param("createUserId") Integer createUserId);


}
