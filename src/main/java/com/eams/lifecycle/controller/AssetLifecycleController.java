package com.eams.lifecycle.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.aop.OperationLog;
import com.eams.common.result.PageResult;
import com.eams.common.result.Result;
import com.eams.lifecycle.dto.LifecycleCreateRequest;
import com.eams.lifecycle.service.AssetLifecycleService;
import com.eams.lifecycle.vo.LifecycleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资产生命周期管理Controller
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Tag(name = "资产生命周期管理")
@RestController
@RequestMapping("/lifecycle")
@RequiredArgsConstructor
public class AssetLifecycleController {

    private final AssetLifecycleService lifecycleService;

    @Operation(summary = "创建生命周期记录")
    @PostMapping
    @OperationLog(module = "生命周期管理", action = "创建记录")
    @PreAuthorize("hasAuthority('lifecycle:create')")
    public Result<Long> createLifecycle(@Valid @RequestBody LifecycleCreateRequest request) {
        Long id = lifecycleService.createLifecycle(request);
        return Result.success(id);
    }

    @Operation(summary = "获取资产生命周期历史")
    @GetMapping("/asset/{assetId}")
    @PreAuthorize("hasAuthority('lifecycle:history')")
    public Result<List<LifecycleVO>> getAssetLifecycleHistory(@PathVariable Long assetId) {
        List<LifecycleVO> history = lifecycleService.getAssetLifecycleHistory(assetId);
        return Result.success(history);
    }

    @Operation(summary = "获取资产当前生命周期")
    @GetMapping("/asset/{assetId}/current")
    @PreAuthorize("hasAuthority('lifecycle:current')")
    public Result<LifecycleVO> getCurrentLifecycle(@PathVariable Long assetId) {
        LifecycleVO lifecycle = lifecycleService.getCurrentLifecycle(assetId);
        return Result.success(lifecycle);
    }

    @Operation(summary = "分页查询生命周期记录")
    @GetMapping
    @PreAuthorize("hasAuthority('lifecycle:list')")
    public Result<PageResult<LifecycleVO>> getLifecyclePage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer stage) {
        Page<LifecycleVO> page = lifecycleService.getLifecyclePage(current, size, stage);
        PageResult<LifecycleVO> result = PageResult.of(page.getRecords(), page.getTotal());
        return Result.success(result);
    }

    @Operation(summary = "变更资产生命周期阶段")
    @PutMapping("/change-stage")
    @OperationLog(module = "生命周期管理", action = "变更阶段")
    @PreAuthorize("hasAuthority('lifecycle:change')")
    public Result<Long> changeStage(@Valid @RequestBody LifecycleCreateRequest request) {
        Long id = lifecycleService.changeStage(request);
        return Result.success(id);
    }
}
