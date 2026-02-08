package com.eams.system.controller;

import com.eams.common.result.Result;
import com.eams.system.dto.MenuCreateRequest;
import com.eams.system.dto.MenuUpdateRequest;
import com.eams.system.service.MenuService;
import com.eams.system.service.PermissionService;
import com.eams.system.vo.MenuTreeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单权限管理控制器
 *
 * @author Pancake
 * @since 2026-02-08
 */
@Tag(name = "菜单权限管理")
@RestController
@RequestMapping("/menus")
@RequiredArgsConstructor
@Slf4j
public class MenuController {

    private final MenuService menuService;
    private final PermissionService permissionService;

    @Operation(summary = "获取菜单权限树")
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('system:permission:list')")
    public Result<List<MenuTreeVO>> getMenuTree() {
        log.info("获取菜单树");
        List<MenuTreeVO> tree = permissionService.getAllMenuTree();
        return Result.success(tree);
    }

    @Operation(summary = "创建菜单权限")
    @PostMapping
    @PreAuthorize("hasAuthority('system:permission:add')")
    public Result<Long> createMenu(@Valid @RequestBody MenuCreateRequest request) {
        log.info("创建菜单权限，入参：{}", request);
        Long id = menuService.createMenu(request);
        log.info("创建菜单权限完成，id={}", id);
        return Result.success(id);
    }

    @Operation(summary = "更新菜单权限")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:permission:edit')")
    public Result<Void> updateMenu(@PathVariable Long id, @Valid @RequestBody MenuUpdateRequest request) {
        log.info("更新菜单权限，id={}，入参：{}", id, request);
        menuService.updateMenu(id, request);
        log.info("更新菜单权限完成，id={}", id);
        return Result.success();
    }

    @Operation(summary = "删除菜单权限")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:permission:delete')")
    public Result<Void> deleteMenu(@PathVariable Long id) {
        log.info("删除菜单权限，id={}", id);
        menuService.deleteMenu(id);
        log.info("删除菜单权限完成，id={}", id);
        return Result.success();
    }
}
