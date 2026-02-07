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
import org.springframework.web.bind.annotation.*;

/**
 * 资产盘点管理Controller
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Tag(name = "资产盘点管理")
@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class AssetInventoryController {

    private final AssetInventoryService inventoryService;

    @Operation(summary = "创建盘点计划")
    @PostMapping
    @OperationLog(module = "盘点管理", action = "创建盘点计划")
    public Result<Long> createInventory(@Valid @RequestBody InventoryCreateRequest request) {
        Long id = inventoryService.createInventory(request);
        return Result.success(id);
    }

    @Operation(summary = "开始盘点")
    @PutMapping("/{inventoryId}/start")
    @OperationLog(module = "盘点管理", action = "开始盘点")
    public Result<Void> startInventory(@PathVariable Long inventoryId) {
        inventoryService.startInventory(inventoryId);
        return Result.success();
    }

    @Operation(summary = "执行盘点")
    @PutMapping("/execute")
    @OperationLog(module = "盘点管理", action = "执行盘点")
    public Result<Void> executeInventory(@Valid @RequestBody InventoryExecuteRequest request) {
        inventoryService.executeInventory(request);
        return Result.success();
    }

    @Operation(summary = "完成盘点")
    @PutMapping("/{inventoryId}/complete")
    @OperationLog(module = "盘点管理", action = "完成盘点")
    public Result<Void> completeInventory(@PathVariable Long inventoryId) {
        inventoryService.completeInventory(inventoryId);
        return Result.success();
    }

    @Operation(summary = "取消盘点")
    @PutMapping("/{inventoryId}/cancel")
    @OperationLog(module = "盘点管理", action = "取消盘点")
    public Result<Void> cancelInventory(@PathVariable Long inventoryId) {
        inventoryService.cancelInventory(inventoryId);
        return Result.success();
    }

    @Operation(summary = "获取盘点详情")
    @GetMapping("/{inventoryId}")
    public Result<InventoryVO> getInventoryDetail(@PathVariable Long inventoryId) {
        InventoryVO vo = inventoryService.getInventoryDetail(inventoryId);
        return Result.success(vo);
    }

    @Operation(summary = "分页查询盘点明细")
    @GetMapping("/{inventoryId}/details")
    public Result<PageResult<InventoryDetailVO>> getInventoryDetailPage(
            @PathVariable Long inventoryId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "20") Integer size) {
        Page<InventoryDetailVO> page = inventoryService.getInventoryDetailPage(inventoryId, current, size);
        PageResult<InventoryDetailVO> result = PageResult.of(page.getRecords(), page.getTotal());
        return Result.success(result);
    }

    @Operation(summary = "分页查询盘点计划")
    @GetMapping
    public Result<PageResult<InventoryVO>> getInventoryPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        Page<InventoryVO> page = inventoryService.getInventoryPage(current, size, status);
        PageResult<InventoryVO> result = PageResult.of(page.getRecords(), page.getTotal());
        return Result.success(result);
    }
}
