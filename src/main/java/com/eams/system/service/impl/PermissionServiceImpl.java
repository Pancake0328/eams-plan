package com.eams.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eams.common.util.MybatisBatchExecutor;
import com.eams.system.entity.SysMenu;
import com.eams.system.entity.SysRole;
import com.eams.system.entity.SysRoleMenu;
import com.eams.system.entity.SysUserRole;
import com.eams.system.mapper.SysMenuMapper;
import com.eams.system.mapper.SysRoleMapper;
import com.eams.system.mapper.SysRoleMenuMapper;
import com.eams.system.mapper.SysUserRoleMapper;
import com.eams.system.service.PermissionService;
import com.eams.system.vo.MenuTreeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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
    private final SysRoleMapper roleMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final SysMenuMapper menuMapper;
    private final MybatisBatchExecutor batchExecutor;
    private final ConcurrentMap<Long, Set<String>> userPermissionCache = new ConcurrentHashMap<>();

    @Override
    public Set<String> getUserPermissions(Long userId) {
        if (userId == null) {
            return new HashSet<>();
        }
        Set<String> cached = userPermissionCache.get(userId);
        if (cached != null) {
            return cached;
        }

        Set<String> permissions = loadUserPermissionsFromDatabase(userId);
        Set<String> immutablePermissions = Collections.unmodifiableSet(new HashSet<>(permissions));
        Set<String> oldPermissions = userPermissionCache.putIfAbsent(userId, immutablePermissions);
        return oldPermissions != null ? oldPermissions : immutablePermissions;
    }

    private Set<String> loadUserPermissionsFromDatabase(Long userId) {
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
        LambdaQueryWrapper<SysMenu> menuWrapper = new LambdaQueryWrapper<>();
        menuWrapper.in(SysMenu::getId, menuIds);
        menuWrapper.eq(SysMenu::getStatus, 1);
        List<SysMenu> menus = menuMapper.selectList(menuWrapper);
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

        // 2. 加载启用菜单，用于从授权菜单向上回溯父级链路
        LambdaQueryWrapper<SysMenu> enabledWrapper = new LambdaQueryWrapper<>();
        enabledWrapper.eq(SysMenu::getStatus, 1);
        List<SysMenu> enabledMenus = menuMapper.selectList(enabledWrapper);
        if (enabledMenus.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Long> grantedMenuIds = new HashSet<>(menuIds);
        Map<Long, SysMenu> menuById = enabledMenus.stream()
                .collect(Collectors.toMap(SysMenu::getId, menu -> menu));
        Set<Long> displayMenuIds = new HashSet<>();

        for (Long menuId : grantedMenuIds) {
            SysMenu current = menuById.get(menuId);
            while (current != null) {
                displayMenuIds.add(current.getId());
                Long parentId = current.getParentId();
                if (parentId == null || parentId == 0L) {
                    break;
                }
                current = menuById.get(parentId);
            }
        }

        List<SysMenu> menus = enabledMenus.stream()
                .filter(menu -> Arrays.asList("DIR", "MENU").contains(menu.getMenuType()))
                .filter(menu -> menu.getVisible() != null && menu.getVisible() == 1)
                .filter(menu -> displayMenuIds.contains(menu.getId()))
                .sorted(Comparator.comparing(SysMenu::getSortOrder, Comparator.nullsLast(Integer::compareTo)))
                .collect(Collectors.toList());

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
        if (roleIds == null || roleIds.isEmpty()) {
            evictUserPermissionCache(userId);
            return;
        }

        List<SysUserRole> userRoles = new ArrayList<>(roleIds.size());
        for (Long roleId : roleIds) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoles.add(userRole);
        }
        batchExecutor.execute(userRoles, userRoleMapper::insertBatch);
        evictUserPermissionCache(userId);
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> userRoles = userRoleMapper.selectList(wrapper);
        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .distinct()
                .collect(Collectors.toList());
        if (roleIds.isEmpty()) {
            return new ArrayList<>();
        }
        return roleMapper.selectBatchIds(roleIds).stream()
                .filter(role -> role.getStatus() != null && role.getStatus() == 1)
                .map(SysRole::getId)
                .collect(Collectors.toList());
    }

    @Override
    public void evictUserPermissionCache(Long userId) {
        if (userId == null) {
            return;
        }
        userPermissionCache.remove(userId);
    }

    @Override
    public void evictUserPermissionCacheByRoleId(Long roleId) {
        if (roleId == null) {
            return;
        }
        LambdaQueryWrapper<SysUserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(SysUserRole::getRoleId, roleId);
        List<SysUserRole> userRoles = userRoleMapper.selectList(userRoleWrapper);
        if (userRoles.isEmpty()) {
            return;
        }
        userRoles.stream()
                .map(SysUserRole::getUserId)
                .distinct()
                .forEach(this::evictUserPermissionCache);
    }

    @Override
    public void evictUserPermissionCacheByMenuId(Long menuId) {
        if (menuId == null) {
            return;
        }
        LambdaQueryWrapper<SysRoleMenu> roleMenuWrapper = new LambdaQueryWrapper<>();
        roleMenuWrapper.eq(SysRoleMenu::getMenuId, menuId);
        List<SysRoleMenu> roleMenus = roleMenuMapper.selectList(roleMenuWrapper);
        if (roleMenus.isEmpty()) {
            return;
        }

        List<Long> roleIds = roleMenus.stream()
                .map(SysRoleMenu::getRoleId)
                .distinct()
                .collect(Collectors.toList());
        if (roleIds.isEmpty()) {
            return;
        }

        LambdaQueryWrapper<SysUserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.in(SysUserRole::getRoleId, roleIds);
        List<SysUserRole> userRoles = userRoleMapper.selectList(userRoleWrapper);
        if (userRoles.isEmpty()) {
            return;
        }
        userRoles.stream()
                .map(SysUserRole::getUserId)
                .distinct()
                .forEach(this::evictUserPermissionCache);
    }

    /**
     * 构建菜单树
     */
    private List<MenuTreeVO> buildMenuTree(List<SysMenu> menus, Long parentId) {
        List<MenuTreeVO> tree = new ArrayList<>();

        for (SysMenu menu : menus) {
            if (Objects.equals(menu.getParentId(), parentId)) {
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
