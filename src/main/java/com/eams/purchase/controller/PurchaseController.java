package com.eams.purchase.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.common.result.Result;
import com.eams.purchase.dto.AssetInboundRequest;
import com.eams.purchase.dto.BatchInboundRequest;
import com.eams.purchase.dto.PurchaseCreateRequest;
import com.eams.purchase.service.PurchaseService;
import com.eams.purchase.vo.PurchaseBillStatisticVO;
import com.eams.purchase.vo.PurchaseFundOverviewVO;
import com.eams.purchase.vo.PurchaseFundStatisticVO;
import com.eams.purchase.vo.PurchaseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "采购管理")
@RestController
@RequestMapping("/purchase")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @Operation(summary = "创建采购单")
    @PostMapping
    @PreAuthorize("hasAuthority('purchase:create')")
    public Result<Long> createPurchase(@Valid @RequestBody PurchaseCreateRequest request) {
        Long id = purchaseService.createPurchase(request);
        return Result.success(id);
    }

    @Operation(summary = "获取采购单详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('purchase:view')")
    public Result<PurchaseVO> getPurchaseById(@PathVariable Long id) {
        PurchaseVO purchase = purchaseService.getPurchaseById(id);
        return Result.success(purchase);
    }

    @Operation(summary = "分页查询采购单")
    @GetMapping
    @PreAuthorize("hasAuthority('purchase:list')")
    public Result<Page<PurchaseVO>> getPurchasePage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String purchaseNumber,
            @RequestParam(required = false) Integer status) {
        Page<PurchaseVO> page = purchaseService.getPurchasePage(current, size, purchaseNumber, status);
        return Result.success(page);
    }

    @Operation(summary = "取消采购单")
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('purchase:cancel')")
    public Result<Void> cancelPurchase(@PathVariable Long id) {
        purchaseService.cancelPurchase(id);
        return Result.success();
    }

    @Operation(summary = "获取待入库明细列表")
    @GetMapping("/pending-inbound")
    @PreAuthorize("hasAuthority('asset:record:in') or hasAuthority('purchase:pending-inbound') or hasAuthority('purchase:batch-inbound')")
    public Result<Page<PurchaseVO.PurchaseDetailVO>> getPendingInboundDetails(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size) {
        Page<PurchaseVO.PurchaseDetailVO> page = purchaseService.getPendingInboundDetails(current, size);
        return Result.success(page);
    }

    @Operation(summary = "资产入库")
    @PostMapping("/inbound")
    @PreAuthorize("hasAuthority('asset:record:in') or hasAuthority('purchase:inbound') or hasAuthority('purchase:batch-inbound')")
    public Result<List<Long>> inboundAsset(@Valid @RequestBody AssetInboundRequest request) {
        List<Long> assetIds = purchaseService.inboundAsset(request);
        return Result.success(assetIds);
    }

    @Operation(summary = "批量入库")
    @PostMapping("/batch-inbound")
    @PreAuthorize("hasAuthority('asset:record:in') or hasAuthority('purchase:batch-inbound')")
    public Result<List<Long>> batchInbound(@Valid @RequestBody BatchInboundRequest request) {
        List<Long> assetIds = purchaseService.batchInbound(request);
        return Result.success(assetIds);
    }

    @Operation(summary = "月度账单统计")
    @GetMapping("/statistics/bill/monthly")
    @PreAuthorize("hasAuthority('finance:bill:list')")
    public Result<PurchaseBillStatisticVO> getMonthlyBillStatistic(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        PurchaseBillStatisticVO stat = purchaseService.getMonthlyBillStatistic(year, month);
        return Result.success(stat);
    }

    @Operation(summary = "年度账单统计")
    @GetMapping("/statistics/bill/annual")
    @PreAuthorize("hasAuthority('finance:bill:list')")
    public Result<PurchaseBillStatisticVO> getAnnualBillStatistic(@RequestParam Integer year) {
        PurchaseBillStatisticVO stat = purchaseService.getAnnualBillStatistic(year);
        return Result.success(stat);
    }

    @Operation(summary = "采购资金概览")
    @GetMapping("/statistics/overview")
    @PreAuthorize("hasAuthority('finance:statistics:view')")
    public Result<PurchaseFundOverviewVO> getFundOverview() {
        PurchaseFundOverviewVO overview = purchaseService.getFundOverview();
        return Result.success(overview);
    }

    @Operation(summary = "按供应商统计")
    @GetMapping("/statistics/by-supplier")
    @PreAuthorize("hasAuthority('finance:statistics:view')")
    public Result<List<PurchaseFundStatisticVO>> getFundStatisticsBySupplier() {
        List<PurchaseFundStatisticVO> stats = purchaseService.getFundStatisticsBySupplier();
        return Result.success(stats);
    }

    @Operation(summary = "按时间统计")
    @GetMapping("/statistics/by-time")
    @PreAuthorize("hasAuthority('finance:statistics:view')")
    public Result<List<PurchaseFundStatisticVO>> getFundStatisticsByTime(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<PurchaseFundStatisticVO> stats = purchaseService.getFundStatisticsByTime(startDate, endDate);
        return Result.success(stats);
    }
}
