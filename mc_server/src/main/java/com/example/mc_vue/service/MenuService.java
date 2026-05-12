package com.example.mc_vue.service;

import com.example.mc_vue.dto.MenuSelectList;
import com.example.mc_vue.dto.MenuTree;
import com.example.mc_vue.entity.Menu;
import com.example.mc_vue.entity.PermissionGroup;
import com.example.mc_vue.mapper.MenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {
    @Autowired
    private MenuMapper menuMapper;

    // 获取菜单下拉列表
    public List<MenuSelectList> getMenuList() {
        return menuMapper.selectMenuList();
    }

    // 获取权限组菜单组
    public List<MenuTree> getMenuPermissions(Integer permissionId) {
        PermissionGroup group = menuMapper.selectPermissionGroupById(permissionId);
        System.out.println("group = " + group);
        if (group == null || group.getPermissions() == null || group.getPermissions().isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> menuIds = group.getPermissions();
        List<Menu> menus = menuMapper.selectMenusByIds(menuIds);

        return buildMenuTree(menus, null);
    }

    private List<MenuTree> buildMenuTree(List<Menu> menus, Integer parentId) {
        //过滤判断父级还是子级菜单，然后再递归构建子菜单树形结构
        return menus.stream()
                .filter(menu -> (parentId == null && menu.getParent_id() == null) ||
                        (parentId != null && parentId.equals(menu.getParent_id())))
                .map(menu -> {
                    MenuTree tree = new MenuTree();
                    tree.setPath(menu.getPath());
                    tree.setName(menu.getPath());
                    tree.setComponent(menu.getComponent());

                    MenuTree.Meta meta = new MenuTree.Meta();
                    meta.setId(menu.getId().toString());
                    meta.setName(menu.getName());
                    meta.setIcon(menu.getIcon());
                    meta.setPath(menu.getPath());
                    meta.setDescribe("");
                    tree.setMeta(meta);


                    //递归构建子菜单
                    List<MenuTree> children = buildMenuTree(menus, menu.getId());
                    if (!children.isEmpty()) {
                        tree.setChildren(children);
                    }

                    return tree;
                })
                .collect(Collectors.toList());
    }

}
