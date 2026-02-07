package com.eams.finance.controller;

import com.eams.common.result.Result;
import com.eams.finance.service.AssetFinanceService;
import com.eams.finance.vo.FinanceStatisticsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * 资金统计管理控制器
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Tag(name = "资金统计管理")
@RestController
@RequestMapping("/finance/statistics")
@RequiredArgsConstructor
public class AssetFinanceController {

    private final AssetFinanceService financeService;

    @Operation(summary = "按部门统计资产金额")
    @GetMapping("/by-department")
    @PreAuthorize("hasAuthority('finance:statistics:view')")
    public Result<List<FinanceStatisticsVO>> statisticsByDepartment() {
        List<FinanceStatisticsVO> result = financeService.statisticsByDepartment();
        return Result.success(result);
    }

    @Operation(summary = "按时间统计资金使用情况")
    @GetMapping("/by-time")
    @PreAuthorize("hasAuthority('finance:statistics:view')")
    public Result<List<FinanceStatisticsVO>> statisticsByTime(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<FinanceStatisticsVO> result = financeService.statisticsByTime(startDate, endDate);
        return Result.success(result);
    }

    @Operation(summary = "获取财务概览")
    @GetMapping("/overview")
    @PreAuthorize("hasAuthority('finance:statistics:view')")
    public Result<FinanceStatisticsVO> getFinanceOverview() {
        FinanceStatisticsVO overview = financeService.getFinanceOverview();
        return Result.success(overview);
    }
}
