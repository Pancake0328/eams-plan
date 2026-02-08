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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 角色管理控制器
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
@Slf4j
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "创建角色")
    @PostMapping
    @PreAuthorize("hasAuthority('system:role:add')")
    public Result<Long> createRole(@Valid @RequestBody RoleCreateRequest request) {
        log.info("创建角色，入参：{}", request);
        Long id = roleService.createRole(request);
        log.info("创建角色完成，id={}", id);
        return Result.success(id);
    }

    @Operation(summary = "更新角色")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<Void> updateRole(@PathVariable Long id, @Valid @RequestBody RoleCreateRequest request) {
        log.info("更新角色，id={}，入参：{}", id, request);
        roleService.updateRole(id, request);
        log.info("更新角色完成，id={}", id);
        return Result.success();
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public Result<Void> deleteRole(@PathVariable Long id) {
        log.info("删除角色，id={}", id);
        roleService.deleteRole(id);
        log.info("删除角色完成，id={}", id);
        return Result.success();
    }

    @Operation(summary = "获取角色详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:list')")
    public Result<RoleVO> getRoleById(@PathVariable Long id) {
        log.info("获取角色详情，id={}", id);
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
        log.info("分页查询角色，current={}，size={}，roleName={}，status={}", current, size, roleName, status);
        Page<RoleVO> page = roleService.getRolePage(current, size, roleName, status);
        return Result.success(page);
    }

    @Operation(summary = "启用/禁用角色")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('system:role:status')")
    public Result<Void> updateRoleStatus(@PathVariable Long id, @RequestParam Integer status) {
        log.info("更新角色状态，id={}，status={}", id, status);
        roleService.updateRoleStatus(id, status);
        log.info("更新角色状态完成，id={}，status={}", id, status);
        return Result.success();
    }

    @Operation(summary = "为角色分配权限")
    @PostMapping("/assign-permissions")
    @PreAuthorize("hasAuthority('system:role:permission')")
    public Result<Void> assignPermissions(@Valid @RequestBody AssignPermissionRequest request) {
        log.info("为角色分配权限，roleId={}，menuIds={}", request.getRoleId(), request.getMenuIds());
        roleService.assignPermissions(request);
        log.info("角色权限分配完成，roleId={}", request.getRoleId());
        return Result.success();
    }

    @Operation(summary = "获取角色已分配的菜单ID列表")
    @GetMapping("/{roleId}/menu-ids")
    @PreAuthorize("hasAuthority('system:role:permission')")
    public Result<List<Long>> getRoleMenuIds(@PathVariable Long roleId) {
        log.info("获取角色菜单ID列表，roleId={}", roleId);
        List<Long> menuIds = roleService.getRoleMenuIds(roleId);
        return Result.success(menuIds);
    }

    @Operation(summary = "获取角色权限标识集合")
    @GetMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('system:role:permission')")
    public Result<Set<String>> getRolePermissions(@PathVariable Long roleId) {
        log.info("获取角色权限标识集合，roleId={}", roleId);
        Set<String> permissions = roleService.getRolePermissions(roleId);
        return Result.success(permissions);
    }

    @Operation(summary = "获取所有角色列表")
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('system:role:list', 'system:role:view')")
    public Result<List<RoleVO>> getAllRoles() {
        log.info("获取所有角色列表");
        List<RoleVO> roles = roleService.getAllRoles();
        return Result.success(roles);
    }
}
