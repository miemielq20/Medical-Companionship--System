package com.example.mc_vue.mapper;

import com.example.mc_vue.entity.Menu;
import com.example.mc_vue.entity.User;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    // 注册用户
    @Insert("INSERT INTO user (user_name, password, permissions_id,create_time ) " +
            "VALUES (#{userName}, #{password}, #{permissionsId},#{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void addUser(User user);

    // 根据手机号查询用户(查看是否已注册）
    @Select("SELECT * FROM user WHERE user_name = #{username}")
    User selectByMobile(String mobile);

    // 根据id查询用户
    @Select("SELECT * FROM user WHERE id = #{id}")
    User selectById(Long id);

    // 根据手机号和密码查询用户（登录）
    @Select("SELECT * FROM user WHERE user_name = #{useNname} AND password = #{password}")
    User selectByMobileAndPassword(@Param("useNname") String mobile, @Param("password") String password);

    // 查询所有用户（分页）
    @Select("SELECT * FROM user ORDER BY id DESC")
    Page<User> selectAll();
}
