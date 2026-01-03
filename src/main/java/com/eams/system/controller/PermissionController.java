package com.eams.system.controller;

import com.eams.common.result.Result;
import com.eams.system.service.PermissionService;
import com.eams.system.vo.MenuTreeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 权限管理控制器
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Tag(name = "权限管理")
@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @Operation(summary = "获取用户权限标识集合")
    @GetMapping("/user/{userId}/permissions")
    public Result<Set<String>> getUserPermissions(@PathVariable Long userId) {
        Set<String> permissions = permissionService.getUserPermissions(userId);
        return Result.success(permissions);
    }

    @Operation(summary = "获取用户菜单树")
    @GetMapping("/user/{userId}/menu-tree")
    public Result<List<MenuTreeVO>> getUserMenuTree(@PathVariable Long userId) {
        List<MenuTreeVO> menuTree = permissionService.getUserMenuTree(userId);
        return Result.success(menuTree);
    }

    @Operation(summary = "获取所有菜单树")
    @GetMapping("/menu-tree")
    public Result<List<MenuTreeVO>> getAllMenuTree() {
        List<MenuTreeVO> menuTree = permissionService.getAllMenuTree();
        return Result.success(menuTree);
    }

    @Operation(summary = "为用户分配角色")
    @PostMapping("/user/{userId}/assign-roles")
    public Result<Void> assignRolesToUser(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        permissionService.assignRolesToUser(userId, roleIds);
        return Result.success();
    }

    @Operation(summary = "获取用户角色ID列表")
    @GetMapping("/user/{userId}/role-ids")
    public Result<List<Long>> getUserRoleIds(@PathVariable Long userId) {
        List<Long> roleIds = permissionService.getUserRoleIds(userId);
        return Result.success(roleIds);
    }
}
