package com.eams.dashboard.controller;

import com.eams.common.result.Result;
import com.eams.dashboard.service.DashboardService;
import com.eams.dashboard.vo.AssetDistribution;
import com.eams.dashboard.vo.DashboardStatistics;
import com.eams.dashboard.vo.TimeTrendStatistics;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 仪表盘控制器
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Tag(name = "仪表盘管理")
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "获取仪表盘概览数据")
    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('dashboard:view')")
    public Result<DashboardStatistics> getDashboardStatistics() {
        DashboardStatistics statistics = dashboardService.getDashboardStatistics();
        return Result.success(statistics);
    }

    @Operation(summary = "获取部门资产分布")
    @GetMapping("/distribution/department")
    @PreAuthorize("hasAuthority('dashboard:view')")
    public Result<List<AssetDistribution>> getDepartmentDistribution() {
        List<AssetDistribution> distributions = dashboardService.getDepartmentDistribution();
        return Result.success(distributions);
    }

    @Operation(summary = "获取资产分类分布")
    @GetMapping("/distribution/category")
    @PreAuthorize("hasAuthority('dashboard:view')")
    public Result<List<AssetDistribution>> getCategoryDistribution() {
        List<AssetDistribution> distributions = dashboardService.getCategoryDistribution();
        return Result.success(distributions);
    }

    @Operation(summary = "获取资产状态分布")
    @GetMapping("/distribution/status")
    @PreAuthorize("hasAuthority('dashboard:view')")
    public Result<List<AssetDistribution>> getStatusDistribution() {
        List<AssetDistribution> distributions = dashboardService.getStatusDistribution();
        return Result.success(distributions);
    }

    @Operation(summary = "获取时间趋势统计")
    @GetMapping("/trend")
    @PreAuthorize("hasAuthority('dashboard:view')")
    public Result<List<TimeTrendStatistics>> getTimeTrendStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<TimeTrendStatistics> trends = dashboardService.getTimeTrendStatistics(startDate, endDate);
        return Result.success(trends);
    }
}
