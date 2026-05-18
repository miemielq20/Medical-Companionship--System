package com.example.mc_server.service;

import com.example.mc_server.dto.MenuSelectList;
import com.example.mc_server.dto.MenuTree;
import com.example.mc_server.dto.PermissionGroupVO;
import com.example.mc_server.dto.PermissionStruct;
import com.example.mc_server.entity.Menu;
import com.example.mc_server.entity.PermissionGroup;
import com.example.mc_server.mapper.MenuMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MenuService {
    @Autowired
    private MenuMapper menuMapper;


    //获取权限下拉列表
    public List<MenuSelectList> getMenuList() {
        return menuMapper.selectMenuList();
    }

    //根据权限id获取权限组对应的结构树（简化版，用于权限选择）
    public List<PermissionStruct> getSimpleMenuTree(Integer permissionId) {
        PermissionGroup group = menuMapper.selectPermissionGroupById(permissionId);
        if (group == null || group.getPermissions() == null || group.getPermissions().isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> menuIds = group.getPermissions();
        List<Menu> menus = menuMapper.selectMenusByIds(menuIds);

        return buildSimpleMenuTree(menus, null);
    }

    //构建简化版菜单树
    private List<PermissionStruct> buildSimpleMenuTree(List<Menu> menus, Integer parentId) {
        return menus.stream()
                .filter(menu -> (parentId == null && menu.getParentId() == null) ||
                        (parentId != null && parentId.equals(menu.getParentId())))
                .map(menu -> {
                    PermissionStruct tree = new PermissionStruct();
                    tree.setId(menu.getId().intValue());
                    tree.setLabel(menu.getName());
                    if ( menu.getDisabled() == 1) {
                        tree.setDisabled(true);
                    }

                    List<PermissionStruct> children = buildSimpleMenuTree(menus, menu.getId().intValue());
                    if (!children.isEmpty()) {
                        tree.setChildren(children);
                    }

                    return tree;
                })
                .collect(Collectors.toList());
    }


    //根据权限id获取权限组对应的结构树
    public List<MenuTree> getMenuPermissions(Integer permissionId) {
        PermissionGroup group = menuMapper.selectPermissionGroupById(permissionId);
        if (group == null || group.getPermissions() == null || group.getPermissions().isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> menuIds = group.getPermissions();
        List<Menu> menus = menuMapper.selectMenusByIds(menuIds);

        return buildMenuTree(menus, null);
    }



    //生成权限组的结构树
    private List<MenuTree> buildMenuTree(List<Menu> menus, Integer parentId) {
        return menus.stream()
                .filter(menu -> (parentId == null && menu.getParentId() == null) ||
                        (parentId != null && parentId.equals(menu.getParentId())))
                .map(menu -> {
                    MenuTree tree = new MenuTree();
                    tree.setPath(menu.getPath());
                    tree.setName(menu.getPath());

                    if (menu.getComponent() != null) {
                        tree.setComponent(menu.getComponent());
                    }

                    MenuTree.Meta meta = new MenuTree.Meta();
                    meta.setId(menu.getId().toString());
                    meta.setName(menu.getName());
                    meta.setIcon(menu.getIcon());
                    meta.setPath(menu.getPath());
                    meta.setDescribe(menu.getDescribe());
                    tree.setMeta(meta);

                    List<MenuTree> children = buildMenuTree(menus, menu.getId().intValue());
                    if (!children.isEmpty()) {
                        tree.setChildren(children);
                    }

                    return tree;
                })
                .collect(Collectors.toList());
    }

    //获取权限组列表(分页)
    public PageInfo<PermissionGroupVO> getPermissionGroupList(Integer pageNum, Integer pageSize, Integer createUserId) {
        PageHelper.startPage(pageNum, pageSize);
        List<PermissionGroup> list = menuMapper.selectPermissionGroupByUserId(createUserId);

        //提取需要返回的menu下的 信息
        List<PermissionGroupVO> voList = list.stream().map(group -> {
            PermissionGroupVO vo = new PermissionGroupVO();
            vo.setId(group.getId());
            vo.setName(group.getName());
            vo.setPermissions(group.getPermissions());

            //查找权限组(gPermissions)下的对应权限名称设置到permissionName
            if (group.getPermissions() != null && !group.getPermissions().isEmpty()) {
                List<Menu> menus = menuMapper.selectMenusByIds(group.getPermissions());
                String permissionName = menus.stream()
                        .map(Menu::getName)
                        .collect(Collectors.joining(","));
                vo.setPermissionName(permissionName);
            } else {
                vo.setPermissionName("");
            }

            return vo;
        }).collect(Collectors.toList());

        PageInfo<PermissionGroupVO> pageInfo = new PageInfo<>(voList);
        pageInfo.setTotal(new PageInfo<>(list).getTotal());

        return pageInfo;
    }


}
