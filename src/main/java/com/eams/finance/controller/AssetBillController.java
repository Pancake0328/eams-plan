package com.eams.finance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.aop.OperationLog;
import com.eams.common.result.PageResult;
import com.eams.common.result.Result;
import com.eams.finance.service.AssetBillService;
import com.eams.finance.vo.BillVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 资产账单管理控制器
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Tag(name = "资产账单管理")
@RestController
@RequestMapping("/finance/bills")
@RequiredArgsConstructor
public class AssetBillController {

    private final AssetBillService billService;

    @Operation(summary = "生成月度账单")
    @PostMapping("/monthly")
    @OperationLog(module = "账单管理", action = "生成月度账单")
    public Result<Long> generateMonthlyBill(@RequestParam Integer year, @RequestParam Integer month) {
        Long billId = billService.generateMonthlyBill(year, month);
        return Result.success(billId);
    }

    @Operation(summary = "生成年度账单")
    @PostMapping("/annual")
    @OperationLog(module = "账单管理", action = "生成年度账单")
    public Result<Long> generateAnnualBill(@RequestParam Integer year) {
        Long billId = billService.generateAnnualBill(year);
        return Result.success(billId);
    }

    @Operation(summary = "获取账单详情")
    @GetMapping("/{billId}")
    public Result<BillVO> getBillDetail(@PathVariable Long billId) {
        BillVO bill = billService.getBillDetail(billId);
        return Result.success(bill);
    }

    @Operation(summary = "确认账单")
    @PutMapping("/{billId}/confirm")
    @OperationLog(module = "账单管理", action = "确认账单")
    public Result<Void> confirmBill(@PathVariable Long billId) {
        billService.confirmBill(billId);
        return Result.success();
    }

    @Operation(summary = "分页查询账单")
    @GetMapping
    public Result<PageResult<BillVO>> getBillPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer billType,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        Page<BillVO> page = billService.getBillPage(current, size, billType, year, month);
        PageResult<BillVO> result = PageResult.of(page.getRecords(), page.getTotal());
        return Result.success(result);
    }

    @Operation(summary = "删除账单")
    @DeleteMapping("/{billId}")
    @OperationLog(module = "账单管理", action = "删除账单")
    public Result<Void> deleteBill(@PathVariable Long billId) {
        billService.deleteBill(billId);
        return Result.success();
    }
}
