package com.eams.dashboard.service;

import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDate;

/**
 * 报表导出服务
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
public interface ReportExportService
{

    /**
     * 导出资产列表为Excel
     *
     * @param response HTTP响应
     */
    void exportAssetListToExcel(HttpServletResponse response);

    /**
     * 导出仪表盘统计报表为Excel
     *
     * @param response HTTP响应
     */
    void exportDashboardStatisticsToExcel(HttpServletResponse response);

    /**
     * 导出时间趋势统计为Excel
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param response  HTTP响应
     */
    void exportTimeTrendToExcel(LocalDate startDate, LocalDate endDate, HttpServletResponse response);
}
