package com.example.mc_server.mapper;

import com.example.mc_server.entity.Menu;
import com.example.mc_server.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    // 添加用户
    @Insert("INSERT INTO user (user_name, mobile, password, permissions_id, create_time) " +
            "VALUES (#{userName}, #{mobile}, #{password}, #{permissionsId}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void addUser(User user);

    // 根据手机号查询用户
    @Select("SELECT * FROM user WHERE user_name = #{username}")
    User selectByMobile(String mobile);

    // 根据id查询用户
    @Select("SELECT * FROM user WHERE id = #{id}")
    User selectById(Long id);

    // 根据手机号和密码查询用户
    @Select("SELECT * FROM user WHERE user_name = #{useNname} AND password = #{password}")
    User selectByMobileAndPassword(@Param("useNname") String mobile, @Param("password") String password);

    // 查询所有用户
    @Select("SELECT * FROM user ORDER BY id DESC")
    List<User> selectAll();

    // 查询菜单
    @Select("SELECT * FROM  menu where 1")
    Menu selectMenu();

    //更新权限组

    @Update("UPDATE permission_group SET name = #{name},  permissions = #{permissions} WHERE id = #{id} AND create_user_id = #{createUserId}")
    int updatePermissionGroup(@Param("id") Long id,
                              @Param("name") String name,
                              @Param("permissions") String permissions,
                              @Param("createUserId") Long createUserId);

    //添加权限组
    @Insert("INSERT INTO permission_group (name, permissions, create_user_id) " +
            "VALUES (#{name}, #{permissions}, #{createUserId})")
    int insertPermissionGroup(@Param("name") String name,
                              @Param("permissions") String permissions,
                              @Param("createUserId") Long createUserId);

    //更新用户权限
    @Update("UPDATE user SET permissions_id = #{permissionsId},nickname=#{name} WHERE id = #{id}")
    int updateUserPermissions(@Param("name") String name,
                              @Param("permissionsId") String permissionsId,
                              @Param("id") Long id);


}
