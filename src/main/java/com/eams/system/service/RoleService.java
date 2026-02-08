package com.eams.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.system.dto.AssignPermissionRequest;
import com.eams.system.dto.RoleCreateRequest;
import com.eams.system.vo.MenuTreeVO;
import com.eams.system.vo.RoleVO;

import java.util.List;
import java.util.Set;

/**
 * 角色服务接口
 *
 * @author Pancake
 * @since 2026-01-03
 */
public interface RoleService {

    /**
     * 创建角色
     */
    Long createRole(RoleCreateRequest request);

    /**
     * 更新角色
     */
    void updateRole(Long id, RoleCreateRequest request);

    /**
     * 删除角色（逻辑删除）
     */
    void deleteRole(Long id);

    /**
     * 获取角色详情
     */
    RoleVO getRoleById(Long id);

    /**
     * 分页查询角色
     */
    Page<RoleVO> getRolePage(int current, int size, String roleName, Integer status);

    /**
     * 启用/禁用角色
     */
    void updateRoleStatus(Long id, Integer status);

    /**
     * 为角色分配权限
     */
    void assignPermissions(AssignPermissionRequest request);

    /**
     * 获取角色已分配的菜单ID列表
     */
    List<Long> getRoleMenuIds(Long roleId);

    /**
     * 获取角色的权限标识集合
     */
    Set<String> getRolePermissions(Long roleId);

    /**
     * 获取所有角色列表
     */
    List<RoleVO> getAllRoles();
}
