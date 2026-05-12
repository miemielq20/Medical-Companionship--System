package com.example.mc_vue.mapper;

import com.example.mc_vue.dto.MenuSelectList;
import com.example.mc_vue.entity.Menu;
import com.example.mc_vue.entity.PermissionGroup;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MenuMapper {

    // 查询权限组
    @Select("SELECT id, name FROM permission_group ORDER BY id")
    List<MenuSelectList> selectMenuList();

    @Select("SELECT * FROM permission_group WHERE id = #{permissionId}")
    PermissionGroup selectPermissionGroupById(@Param("permissionId") Integer permissionId);

    @Select("<script>" +
            "SELECT * FROM menu WHERE id IN " +
            "<foreach collection='menuIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            " ORDER BY order_index ASC" +
            "</script>")
    List<Menu> selectMenusByIds(@Param("menuIds")List<Integer> menuIds);

}
