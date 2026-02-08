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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 资产报修管理Controller
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Tag(name = "资产报修管理")
@RestController
@RequestMapping("/repair")
@RequiredArgsConstructor
@Slf4j
public class AssetRepairController {

    private final AssetRepairService repairService;

    @Operation(summary = "创建报修记录")
    @PostMapping
    @OperationLog(module = "报修管理", action = "创建报修")
    @PreAuthorize("hasAuthority('repair:create') or hasAuthority('asset:record:repair')")
    public Result<Long> createRepair(@Valid @RequestBody RepairCreateRequest request) {
        log.info("创建报修记录，入参：{}", request);
        Long id = repairService.createRepair(request);
        log.info("创建报修记录完成，id={}", id);
        return Result.success(id);
    }

    @Operation(summary = "审批报修")
    @PutMapping("/{repairId}/approve")
    @OperationLog(module = "报修管理", action = "审批报修")
    @PreAuthorize("hasAuthority('repair:approve')")
    public Result<Void> approveRepair(
            @PathVariable Long repairId,
            @RequestParam Boolean approved,
            @RequestParam String approver) {
        log.info("审批报修，repairId={}，approved={}，approver={}", repairId, approved, approver);
        repairService.approveRepair(repairId, approved, approver);
        log.info("审批报修完成，repairId={}", repairId);
        return Result.success();
    }

    @Operation(summary = "开始维修")
    @PutMapping("/{repairId}/start")
    @OperationLog(module = "报修管理", action = "开始维修")
    @PreAuthorize("hasAuthority('repair:start')")
    public Result<Void> startRepair(
            @PathVariable Long repairId,
            @RequestParam String repairPerson) {
        log.info("开始维修，repairId={}，repairPerson={}", repairId, repairPerson);
        repairService.startRepair(repairId, repairPerson);
        log.info("开始维修完成，repairId={}", repairId);
        return Result.success();
    }

    @Operation(summary = "完成维修")
    @PutMapping("/{repairId}/complete")
    @OperationLog(module = "报修管理", action = "完成维修")
    @PreAuthorize("hasAuthority('repair:complete')")
    public Result<Void> completeRepair(
            @PathVariable Long repairId,
            @RequestParam BigDecimal repairCost,
            @RequestParam String repairResult) {
        log.info("完成维修，repairId={}，repairCost={}，repairResult={}", repairId, repairCost, repairResult);
        repairService.completeRepair(repairId, repairCost, repairResult);
        log.info("完成维修完成，repairId={}", repairId);
        return Result.success();
    }

    @Operation(summary = "获取报修详情")
    @GetMapping("/{repairId}")
    @PreAuthorize("hasAuthority('repair:view')")
    public Result<RepairVO> getRepairDetail(@PathVariable Long repairId) {
        log.info("获取报修详情，repairId={}", repairId);
        RepairVO vo = repairService.getRepairDetail(repairId);
        return Result.success(vo);
    }

    @Operation(summary = "分页查询报修记录")
    @GetMapping
    @PreAuthorize("hasAuthority('repair:list')")
    public Result<PageResult<RepairVO>> getRepairPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long assetId) {
        log.info("分页查询报修记录，current={}，size={}，status={}，assetId={}", current, size, status, assetId);
        Page<RepairVO> page = repairService.getRepairPage(current, size, status, assetId);
        PageResult<RepairVO> result = PageResult.of(page.getRecords(), page.getTotal());
        return Result.success(result);
    }
}
