package com.eams.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.aop.OperationLog;
import com.eams.common.result.PageResult;
import com.eams.common.result.Result;
import com.eams.system.dto.AssignRecordPageQuery;
import com.eams.system.dto.AssetAssignRequest;
import com.eams.system.service.AssetAssignService;
import com.eams.system.vo.AssetAssignRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资产分配管理控制器
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Tag(name = "资产分配管理")
@RestController
@RequestMapping("/asset-assigns")
@RequiredArgsConstructor
public class AssetAssignController {

    private final AssetAssignService assetAssignService;

    @Operation(summary = "分配资产给员工")
    @PostMapping("/assign")
    @OperationLog(module = "资产分配", action = "分配资产")
    @PreAuthorize("hasAuthority('system:asset-assign:assign')")
    public Result<Long> assignAsset(@Validated @RequestBody AssetAssignRequest request) {
        Long id = assetAssignService.assignAssetToEmployee(request);
        return Result.success(id);
    }

    @Operation(summary = "回收资产")
    @PostMapping("/return/{assetId}")
    @OperationLog(module = "资产分配", action = "回收资产")
    @PreAuthorize("hasAuthority('system:asset-assign:return')")
    public Result<Long> returnAsset(@PathVariable Long assetId, @RequestParam(required = false) String remark) {
        Long id = assetAssignService.returnAsset(assetId, remark);
        return Result.success(id);
    }

    @Operation(summary = "部门内调拨资产")
    @PostMapping("/transfer")
    @OperationLog(module = "资产分配", action = "调拨资产")
    @PreAuthorize("hasAuthority('system:asset-assign:transfer')")
    public Result<Long> transferAsset(@Validated @RequestBody AssetAssignRequest request) {
        Long id = assetAssignService.transferAsset(request);
        return Result.success(id);
    }

    @Operation(summary = "分页查询分配记录")
    @GetMapping
    @PreAuthorize("hasAuthority('system:asset-assign:list')")
    public Result<PageResult<AssetAssignRecordVO>> getAssignRecordPage(AssignRecordPageQuery query) {
        Page<AssetAssignRecordVO> page = assetAssignService.getAssignRecordPage(query);
        PageResult<AssetAssignRecordVO> result = PageResult.of(page.getRecords(), page.getTotal());
        return Result.success(result);
    }

    @Operation(summary = "查询资产分配历史")
    @GetMapping("/asset/{assetId}")
    @PreAuthorize("hasAuthority('system:asset-assign:view')")
    public Result<List<AssetAssignRecordVO>> getAssetAssignHistory(@PathVariable Long assetId) {
        List<AssetAssignRecordVO> history = assetAssignService.getAssetAssignHistory(assetId);
        return Result.success(history);
    }

    @Operation(summary = "查询员工资产分配历史")
    @GetMapping("/employee/{empId}")
    @PreAuthorize("hasAuthority('system:asset-assign:view')")
    public Result<List<AssetAssignRecordVO>> getEmployeeAssignHistory(@PathVariable Long empId) {
        List<AssetAssignRecordVO> history = assetAssignService.getEmployeeAssignHistory(empId);
        return Result.success(history);
    }
}
