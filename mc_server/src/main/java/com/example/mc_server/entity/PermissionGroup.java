package com.example.mc_server.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.example.mc_server.common.handler.JsonListTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PermissionGroup {
    private Long id;
    private String name;
    @TableField(typeHandler = JsonListTypeHandler.class)
    private List<Integer> permissions;
    private LocalDateTime createTime;
    private Integer createUserId;
}
