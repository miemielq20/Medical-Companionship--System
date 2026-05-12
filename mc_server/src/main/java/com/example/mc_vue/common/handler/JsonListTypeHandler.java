package com.example.mc_vue.common.handler;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Component
@MappedTypes(List.class)
public class JsonListTypeHandler extends BaseTypeHandler<List<Integer>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<Integer> parameter, JdbcType jdbcType) throws SQLException {
        System.out.println(">>> setNonNullParameter called");
        ps.setString(i, JSONUtil.toJsonStr(parameter));
    }

    @Override
    public List<Integer> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        System.out.println(">>> setNonNullParameter called");
        String json = rs.getString(columnName);
        return parse(json);
    }

    @Override
    public List<Integer> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        System.out.println(">>> setNonNullParameter called");
        String json = rs.getString(columnIndex);
        return parse(json);
    }

    @Override
    public List<Integer> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        System.out.println(">>> setNonNullParameter called");
        String json = cs.getString(columnIndex);
        return parse(json);
    }

    private List<Integer> parse(String json) {
        System.out.println("json = " + json);
        if (json == null || json.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return JSONUtil.parseArray(json).toList(Integer.class);
        } catch (Exception e) {
            // 一定要打日志，不然你永远不知道为什么是 null
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}