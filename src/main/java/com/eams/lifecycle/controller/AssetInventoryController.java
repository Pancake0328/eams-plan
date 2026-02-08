package com.eams.lifecycle.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.aop.OperationLog;
import com.eams.common.result.PageResult;
import com.eams.common.result.Result;
import com.eams.lifecycle.dto.InventoryCreateRequest;
import com.eams.lifecycle.dto.InventoryExecuteRequest;
import com.eams.lifecycle.service.AssetInventoryService;
import com.eams.lifecycle.vo.InventoryDetailVO;
import com.eams.lifecycle.vo.InventoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 资产盘点管理Controller
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Tag(name = "资产盘点管理")
@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Slf4j
public class AssetInventoryController {

    private final AssetInventoryService inventoryService;

    @Operation(summary = "创建盘点计划")
    @PostMapping
    @OperationLog(module = "盘点管理", action = "创建盘点计划")
    @PreAuthorize("hasAuthority('inventory:create')")
    public Result<Long> createInventory(@Valid @RequestBody InventoryCreateRequest request) {
        log.info("创建盘点计划，入参：{}", request);
        Long id = inventoryService.createInventory(request);
        log.info("创建盘点计划完成，id={}", id);
        return Result.success(id);
    }

    @Operation(summary = "开始盘点")
    @PutMapping("/{inventoryId}/start")
    @OperationLog(module = "盘点管理", action = "开始盘点")
    @PreAuthorize("hasAuthority('inventory:start')")
    public Result<Void> startInventory(@PathVariable Long inventoryId) {
        log.info("开始盘点，inventoryId={}", inventoryId);
        inventoryService.startInventory(inventoryId);
        log.info("开始盘点完成，inventoryId={}", inventoryId);
        return Result.success();
    }

    @Operation(summary = "执行盘点")
    @PutMapping("/execute")
    @OperationLog(module = "盘点管理", action = "执行盘点")
    @PreAuthorize("hasAuthority('inventory:execute')")
    public Result<Void> executeInventory(@Valid @RequestBody InventoryExecuteRequest request) {
        log.info("执行盘点，入参：{}", request);
        inventoryService.executeInventory(request);
        log.info("执行盘点完成");
        return Result.success();
    }

    @Operation(summary = "完成盘点")
    @PutMapping("/{inventoryId}/complete")
    @OperationLog(module = "盘点管理", action = "完成盘点")
    @PreAuthorize("hasAuthority('inventory:complete')")
    public Result<Void> completeInventory(@PathVariable Long inventoryId) {
        log.info("完成盘点，inventoryId={}", inventoryId);
        inventoryService.completeInventory(inventoryId);
        log.info("完成盘点完成，inventoryId={}", inventoryId);
        return Result.success();
    }

    @Operation(summary = "取消盘点")
    @PutMapping("/{inventoryId}/cancel")
    @OperationLog(module = "盘点管理", action = "取消盘点")
    @PreAuthorize("hasAuthority('inventory:cancel')")
    public Result<Void> cancelInventory(@PathVariable Long inventoryId) {
        log.info("取消盘点，inventoryId={}", inventoryId);
        inventoryService.cancelInventory(inventoryId);
        log.info("取消盘点完成，inventoryId={}", inventoryId);
        return Result.success();
    }

    @Operation(summary = "获取盘点详情")
    @GetMapping("/{inventoryId}")
    @PreAuthorize("hasAuthority('inventory:view')")
    public Result<InventoryVO> getInventoryDetail(@PathVariable Long inventoryId) {
        log.info("获取盘点详情，inventoryId={}", inventoryId);
        InventoryVO vo = inventoryService.getInventoryDetail(inventoryId);
        return Result.success(vo);
    }

    @Operation(summary = "分页查询盘点明细")
    @GetMapping("/{inventoryId}/details")
    @PreAuthorize("hasAuthority('inventory:view')")
    public Result<PageResult<InventoryDetailVO>> getInventoryDetailPage(
            @PathVariable Long inventoryId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "20") Integer size) {
        log.info("分页查询盘点明细，inventoryId={}，current={}，size={}", inventoryId, current, size);
        Page<InventoryDetailVO> page = inventoryService.getInventoryDetailPage(inventoryId, current, size);
        PageResult<InventoryDetailVO> result = PageResult.of(page.getRecords(), page.getTotal());
        return Result.success(result);
    }

    @Operation(summary = "分页查询盘点计划")
    @GetMapping
    @PreAuthorize("hasAuthority('inventory:list')")
    public Result<PageResult<InventoryVO>> getInventoryPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        log.info("分页查询盘点计划，current={}，size={}，status={}", current, size, status);
        Page<InventoryVO> page = inventoryService.getInventoryPage(current, size, status);
        PageResult<InventoryVO> result = PageResult.of(page.getRecords(), page.getTotal());
        return Result.success(result);
    }
}
