package com.eams.system.service;

import com.eams.system.vo.MenuTreeVO;

import java.util.List;
import java.util.Set;

/**
 * 权限服务接口
 *
 * @author Pancake
 * @since 2026-01-03
 */
public interface PermissionService {

    /**
     * 获取用户权限标识集合
     */
    Set<String> getUserPermissions(Long userId);

    /**
     * 获取用户菜单树
     */
    List<MenuTreeVO> getUserMenuTree(Long userId);

    /**
     * 获取所有菜单树
     */
    List<MenuTreeVO> getAllMenuTree();

    /**
     * 为用户分配角色
     */
    void assignRolesToUser(Long userId, List<Long> roleIds);

    /**
     * 获取用户角色ID列表
     */
    List<Long> getUserRoleIds(Long userId);
}
