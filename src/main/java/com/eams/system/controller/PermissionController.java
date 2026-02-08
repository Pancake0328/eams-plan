package com.eams.system.controller;

import com.eams.common.result.Result;
import com.eams.system.service.PermissionService;
import com.eams.system.vo.MenuTreeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 权限管理控制器
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Tag(name = "权限管理")
@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
@Slf4j
public class PermissionController {

    private final PermissionService permissionService;

    @Operation(summary = "获取用户权限标识集合")
    @GetMapping("/user/{userId}/permissions")
    @PreAuthorize("@permissionChecker.isSelfOrHasPermission(#userId, 'system:user:assign')")
    public Result<Set<String>> getUserPermissions(@PathVariable Long userId) {
        log.info("获取用户权限标识集合，userId={}", userId);
        Set<String> permissions = permissionService.getUserPermissions(userId);
        return Result.success(permissions);
    }

    @Operation(summary = "获取用户菜单树")
    @GetMapping("/user/{userId}/menu-tree")
    @PreAuthorize("@permissionChecker.isSelfOrHasPermission(#userId, 'system:user:assign')")
    public Result<List<MenuTreeVO>> getUserMenuTree(@PathVariable Long userId) {
        log.info("获取用户菜单树，userId={}", userId);
        List<MenuTreeVO> menuTree = permissionService.getUserMenuTree(userId);
        return Result.success(menuTree);
    }

    @Operation(summary = "获取所有菜单树")
    @GetMapping("/menu-tree")
    @PreAuthorize("hasAuthority('system:role:permission')")
    public Result<List<MenuTreeVO>> getAllMenuTree() {
        log.info("获取所有菜单树");
        List<MenuTreeVO> menuTree = permissionService.getAllMenuTree();
        return Result.success(menuTree);
    }

    @Operation(summary = "为用户分配角色")
    @PostMapping("/user/{userId}/assign-roles")
    @PreAuthorize("hasAuthority('system:user:assign')")
    public Result<Void> assignRolesToUser(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        log.info("为用户分配角色，userId={}，roleIds={}", userId, roleIds);
        permissionService.assignRolesToUser(userId, roleIds);
        log.info("用户角色分配完成，userId={}", userId);
        return Result.success();
    }

    @Operation(summary = "获取用户角色ID列表")
    @GetMapping("/user/{userId}/role-ids")
    @PreAuthorize("@permissionChecker.isSelfOrHasPermission(#userId, 'system:user:assign')")
    public Result<List<Long>> getUserRoleIds(@PathVariable Long userId) {
        log.info("获取用户角色ID列表，userId={}", userId);
        List<Long> roleIds = permissionService.getUserRoleIds(userId);
        return Result.success(roleIds);
    }
}
