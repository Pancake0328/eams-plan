package com.eams.finance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.aop.OperationLog;
import com.eams.common.result.PageResult;
import com.eams.common.result.Result;
import com.eams.finance.dto.PurchaseCreateRequest;
import com.eams.finance.dto.PurchasePageQuery;
import com.eams.finance.service.AssetPurchaseService;
import com.eams.finance.vo.PurchaseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 资产采购管理控制器
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Tag(name = "资产采购管理")
@RestController
@RequestMapping("/finance/purchases")
@RequiredArgsConstructor
public class AssetPurchaseController {

    private final AssetPurchaseService purchaseService;

    @Operation(summary = "创建采购记录")
    @PostMapping
    @OperationLog(module = "采购管理", action = "创建采购记录")
    @PreAuthorize("hasAuthority('finance:purchase:create')")
    public Result<Long> createPurchase(@Validated @RequestBody PurchaseCreateRequest request) {
        Long id = purchaseService.createPurchase(request);
        return Result.success(id);
    }

    @Operation(summary = "获取采购记录详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('finance:purchase:view')")
    public Result<PurchaseVO> getPurchaseById(@PathVariable Long id) {
        PurchaseVO purchase = purchaseService.getPurchaseById(id);
        return Result.success(purchase);
    }

    @Operation(summary = "分页查询采购记录")
    @GetMapping
    @PreAuthorize("hasAuthority('finance:purchase:list')")
    public Result<PageResult<PurchaseVO>> getPurchasePage(PurchasePageQuery query) {
        Page<PurchaseVO> page = purchaseService.getPurchasePage(query);
        PageResult<PurchaseVO> result = PageResult.of(page.getRecords(), page.getTotal());
        return Result.success(result);
    }

    @Operation(summary = "审批采购记录")
    @PutMapping("/{id}/approve")
    @OperationLog(module = "采购管理", action = "审批采购记录")
    @PreAuthorize("hasAuthority('finance:purchase:approve')")
    public Result<Void> approvePurchase(
            @PathVariable Long id,
            @RequestParam Boolean approved,
            @RequestParam String approver) {
        purchaseService.approvePurchase(id, approved, approver);
        return Result.success();
    }

    @Operation(summary = "删除采购记录")
    @DeleteMapping("/{id}")
    @OperationLog(module = "采购管理", action = "删除采购记录")
    @PreAuthorize("hasAuthority('finance:purchase:delete')")
    public Result<Void> deletePurchase(@PathVariable Long id) {
        purchaseService.deletePurchase(id);
        return Result.success();
    }
}
