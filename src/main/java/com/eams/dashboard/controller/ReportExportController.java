package com.eams.dashboard.controller;

import com.eams.dashboard.service.ReportExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 报表导出控制器
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Tag(name = "报表导出")
@RestController
@RequestMapping("/export")
@RequiredArgsConstructor
public class ReportExportController {

    private final ReportExportService exportService;

    @Operation(summary = "导出资产列表Excel")
    @GetMapping("/asset-list")
    @PreAuthorize("hasAuthority('dashboard:export')")
    public void exportAssetList(HttpServletResponse response) {
        exportService.exportAssetListToExcel(response);
    }

    @Operation(summary = "导出仪表盘统计报表Excel")
    @GetMapping("/dashboard-statistics")
    @PreAuthorize("hasAuthority('dashboard:export')")
    public void exportDashboardStatistics(HttpServletResponse response) {
        exportService.exportDashboardStatisticsToExcel(response);
    }

    @Operation(summary = "导出时间趋势统计Excel")
    @GetMapping("/time-trend")
    @PreAuthorize("hasAuthority('dashboard:export')")
    public void exportTimeTrend(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletResponse response) {
        exportService.exportTimeTrendToExcel(startDate, endDate, response);
    }
}
