package com.eams.lifecycle.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.aop.OperationLog;
import com.eams.common.result.PageResult;
import com.eams.common.result.Result;
import com.eams.lifecycle.dto.RepairCreateRequest;
import com.eams.lifecycle.service.AssetRepairService;
import com.eams.lifecycle.vo.RepairVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 资产报修管理Controller
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Tag(name = "资产报修管理")
@RestController
@RequestMapping("/repair")
@RequiredArgsConstructor
public class AssetRepairController {

    private final AssetRepairService repairService;

    @Operation(summary = "创建报修记录")
    @PostMapping
    @OperationLog(module = "报修管理", action = "创建报修")
    public Result<Long> createRepair(@Valid @RequestBody RepairCreateRequest request) {
        Long id = repairService.createRepair(request);
        return Result.success(id);
    }

    @Operation(summary = "审批报修")
    @PutMapping("/{repairId}/approve")
    @OperationLog(module = "报修管理", action = "审批报修")
    public Result<Void> approveRepair(
            @PathVariable Long repairId,
            @RequestParam Boolean approved,
            @RequestParam String approver) {
        repairService.approveRepair(repairId, approved, approver);
        return Result.success();
    }

    @Operation(summary = "开始维修")
    @PutMapping("/{repairId}/start")
    @OperationLog(module = "报修管理", action = "开始维修")
    public Result<Void> startRepair(
            @PathVariable Long repairId,
            @RequestParam String repairPerson) {
        repairService.startRepair(repairId, repairPerson);
        return Result.success();
    }

    @Operation(summary = "完成维修")
    @PutMapping("/{repairId}/complete")
    @OperationLog(module = "报修管理", action = "完成维修")
    public Result<Void> completeRepair(
            @PathVariable Long repairId,
            @RequestParam BigDecimal repairCost,
            @RequestParam String repairResult) {
        repairService.completeRepair(repairId, repairCost, repairResult);
        return Result.success();
    }

    @Operation(summary = "获取报修详情")
    @GetMapping("/{repairId}")
    public Result<RepairVO> getRepairDetail(@PathVariable Long repairId) {
        RepairVO vo = repairService.getRepairDetail(repairId);
        return Result.success(vo);
    }

    @Operation(summary = "分页查询报修记录")
    @GetMapping
    public Result<PageResult<RepairVO>> getRepairPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long assetId) {
        Page<RepairVO> page = repairService.getRepairPage(current, size, status, assetId);
        PageResult<RepairVO> result = PageResult.of(page.getRecords(), page.getTotal());
        return Result.success(result);
    }
}
