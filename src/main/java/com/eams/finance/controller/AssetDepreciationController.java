package com.eams.finance.controller;

import com.eams.aop.OperationLog;
import com.eams.common.result.Result;
import com.eams.finance.service.AssetDepreciationService;
import com.eams.finance.vo.DepreciationInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 资产折旧管理控制器
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Tag(name = "资产折旧管理")
@RestController
@RequestMapping("/finance/depreciation")
@RequiredArgsConstructor
public class AssetDepreciationController {

    private final AssetDepreciationService depreciationService;

    @Operation(summary = "计算单个资产折旧")
    @GetMapping("/asset/{assetId}")
    public Result<DepreciationInfo> calculateAssetDepreciation(
            @PathVariable Long assetId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate targetDate) {
        if (targetDate == null) {
            targetDate = LocalDate.now();
        }
        DepreciationInfo info = depreciationService.calculateDepreciation(assetId, targetDate);
        return Result.success(info);
    }

    @Operation(summary = "批量计算资产折旧")
    @PostMapping("/batch")
    public Result<List<DepreciationInfo>> batchCalculateDepreciation(
            @RequestBody List<Long> assetIds,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate targetDate) {
        if (targetDate == null) {
            targetDate = LocalDate.now();
        }
        List<DepreciationInfo> list = depreciationService.batchCalculateDepreciation(assetIds, targetDate);
        return Result.success(list);
    }

    @Operation(summary = "计算所有资产折旧")
    @GetMapping("/all")
    public Result<List<DepreciationInfo>> calculateAllDepreciation(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate targetDate) {
        if (targetDate == null) {
            targetDate = LocalDate.now();
        }
        List<DepreciationInfo> list = depreciationService.calculateAllDepreciation(targetDate);
        return Result.success(list);
    }
}
