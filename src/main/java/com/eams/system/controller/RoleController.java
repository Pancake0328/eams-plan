package com.eams.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.common.result.Result;
import com.eams.system.dto.AssignPermissionRequest;
import com.eams.system.dto.RoleCreateRequest;
import com.eams.system.service.RoleService;
import com.eams.system.vo.RoleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 角色管理控制器
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "创建角色")
    @PostMapping
    @PreAuthorize("hasAuthority('system:role:add')")
    public Result<Long> createRole(@Valid @RequestBody RoleCreateRequest request) {
        Long id = roleService.createRole(request);
        return Result.success(id);
    }

    @Operation(summary = "更新角色")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<Void> updateRole(@PathVariable Long id, @Valid @RequestBody RoleCreateRequest request) {
        roleService.updateRole(id, request);
        return Result.success();
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public Result<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success();
    }

    @Operation(summary = "获取角色详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:view')")
    public Result<RoleVO> getRoleById(@PathVariable Long id) {
        RoleVO role = roleService.getRoleById(id);
        return Result.success(role);
    }

    @Operation(summary = "分页查询角色")
    @GetMapping
    @PreAuthorize("hasAuthority('system:role:list')")
    public Result<Page<RoleVO>> getRolePage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) Integer status) {
        Page<RoleVO> page = roleService.getRolePage(current, size, roleName, status);
        return Result.success(page);
    }

    @Operation(summary = "启用/禁用角色")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('system:role:status')")
    public Result<Void> updateRoleStatus(@PathVariable Long id, @RequestParam Integer status) {
        roleService.updateRoleStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "为角色分配权限")
    @PostMapping("/assign-permissions")
    @PreAuthorize("hasAuthority('system:role:permission')")
    public Result<Void> assignPermissions(@Valid @RequestBody AssignPermissionRequest request) {
        roleService.assignPermissions(request);
        return Result.success();
    }

    @Operation(summary = "获取角色已分配的菜单ID列表")
    @GetMapping("/{roleId}/menu-ids")
    @PreAuthorize("hasAuthority('system:role:permission')")
    public Result<List<Long>> getRoleMenuIds(@PathVariable Long roleId) {
        List<Long> menuIds = roleService.getRoleMenuIds(roleId);
        return Result.success(menuIds);
    }

    @Operation(summary = "获取角色权限标识集合")
    @GetMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('system:role:permission')")
    public Result<Set<String>> getRolePermissions(@PathVariable Long roleId) {
        Set<String> permissions = roleService.getRolePermissions(roleId);
        return Result.success(permissions);
    }

    @Operation(summary = "获取所有角色列表")
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('system:role:list')")
    public Result<List<RoleVO>> getAllRoles() {
        List<RoleVO> roles = roleService.getAllRoles();
        return Result.success(roles);
    }
}
