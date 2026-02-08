package com.eams.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eams.system.entity.SysMenu;
import com.eams.system.entity.SysRoleMenu;
import com.eams.system.entity.SysUserRole;
import com.eams.system.mapper.SysMenuMapper;
import com.eams.system.mapper.SysRoleMenuMapper;
import com.eams.system.mapper.SysUserRoleMapper;
import com.eams.system.service.PermissionService;
import com.eams.system.vo.MenuTreeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限服务实现
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final SysUserRoleMapper userRoleMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final SysMenuMapper menuMapper;

    @Override
    public Set<String> getUserPermissions(Long userId) {
        // 1. 查询用户的角色ID列表
        List<Long> roleIds = getUserRoleIds(userId);
        if (roleIds.isEmpty()) {
            return new HashSet<>();
        }

        // 2. 查询角色的菜单ID列表
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysRoleMenu::getRoleId, roleIds);
        List<SysRoleMenu> roleMenus = roleMenuMapper.selectList(wrapper);

        List<Long> menuIds = roleMenus.stream()
                .map(SysRoleMenu::getMenuId)
                .distinct()
                .collect(Collectors.toList());

        if (menuIds.isEmpty()) {
            return new HashSet<>();
        }

        // 3. 查询菜单的权限标识
        List<SysMenu> menus = menuMapper.selectBatchIds(menuIds);
        return menus.stream()
                .map(SysMenu::getPermissionCode)
                .filter(code -> code != null && !code.isEmpty())
                .collect(Collectors.toSet());
    }

    @Override
    public List<MenuTreeVO> getUserMenuTree(Long userId) {
        // 1. 获取用户权限菜单ID
        List<Long> roleIds = getUserRoleIds(userId);
        if (roleIds.isEmpty()) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysRoleMenu::getRoleId, roleIds);
        List<SysRoleMenu> roleMenus = roleMenuMapper.selectList(wrapper);

        List<Long> menuIds = roleMenus.stream()
                .map(SysRoleMenu::getMenuId)
                .distinct()
                .collect(Collectors.toList());

        if (menuIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 查询菜单（只查询DIR和MENU类型）
        LambdaQueryWrapper<SysMenu> menuWrapper = new LambdaQueryWrapper<>();
        menuWrapper.in(SysMenu::getId, menuIds);
        menuWrapper.in(SysMenu::getMenuType, Arrays.asList("DIR", "MENU"));
        menuWrapper.eq(SysMenu::getStatus, 1);
        menuWrapper.orderByAsc(SysMenu::getSortOrder);
        List<SysMenu> menus = menuMapper.selectList(menuWrapper);

        // 3. 构建菜单树
        return buildMenuTree(menus, 0L);
    }

    @Override
    public List<MenuTreeVO> getAllMenuTree() {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysMenu::getSortOrder);
        List<SysMenu> menus = menuMapper.selectList(wrapper);
        return buildMenuTree(menus, 0L);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRolesToUser(Long userId, List<Long> roleIds) {
        // 删除旧的角色关联
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        userRoleMapper.delete(wrapper);

        // 添加新的角色关联
        for (Long roleId : roleIds) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleMapper.insert(userRole);
        }
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> userRoles = userRoleMapper.selectList(wrapper);
        return userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());
    }

    /**
     * 构建菜单树
     */
    private List<MenuTreeVO> buildMenuTree(List<SysMenu> menus, Long parentId) {
        List<MenuTreeVO> tree = new ArrayList<>();

        for (SysMenu menu : menus) {
            if (menu.getParentId().equals(parentId)) {
                MenuTreeVO node = new MenuTreeVO();
                BeanUtils.copyProperties(menu, node);

                // 递归查找子节点
                List<MenuTreeVO> children = buildMenuTree(menus, menu.getId());
                node.setChildren(children);

                tree.add(node);
            }
        }

        return tree;
    }
}
