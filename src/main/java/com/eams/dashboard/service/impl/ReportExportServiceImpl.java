package com.eams.dashboard.service.impl;

import com.eams.asset.entity.AssetInfo;
import com.eams.asset.mapper.AssetInfoMapper;
import com.eams.dashboard.service.DashboardService;
import com.eams.dashboard.service.ReportExportService;
import com.eams.dashboard.vo.AssetDistribution;
import com.eams.dashboard.vo.DashboardStatistics;
import com.eams.dashboard.vo.TimeTrendStatistics;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 报表导出服务实现
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Service
@RequiredArgsConstructor
public class ReportExportServiceImpl implements ReportExportService {

    private final AssetInfoMapper assetInfoMapper;
    private final DashboardService dashboardService;
    private final com.eams.system.mapper.DepartmentMapper departmentMapper;

    @Override
    public void exportAssetListToExcel(HttpServletResponse response) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("资产列表");

            // 创建标题行
            Row headerRow = sheet.createRow(0);
            String[] headers = { "资产编号", "资产名称", "采购金额", "采购日期", "使用部门",
                    "责任人", "资产状态", "规格型号", "生产厂商", "备注" };

            CellStyle headerStyle = createHeaderStyle(workbook);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 填充数据
            List<AssetInfo> assets = assetInfoMapper.selectList(null);
            int rowNum = 1;

            for (AssetInfo asset : assets) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(asset.getAssetNumber());
                row.createCell(1).setCellValue(asset.getAssetName());
                row.createCell(2)
                        .setCellValue(asset.getPurchaseAmount() != null ? asset.getPurchaseAmount().doubleValue() : 0);
                row.createCell(3)
                        .setCellValue(asset.getPurchaseDate() != null ? asset.getPurchaseDate().toString() : "");
                row.createCell(4).setCellValue(getDepartmentName(asset.getDepartmentId()));
                row.createCell(5).setCellValue(asset.getCustodian() != null ? asset.getCustodian() : "");
                row.createCell(6).setCellValue(getStatusText(asset.getAssetStatus()));
                row.createCell(7).setCellValue(asset.getSpecifications() != null ? asset.getSpecifications() : "");
                row.createCell(8).setCellValue(asset.getManufacturer() != null ? asset.getManufacturer() : "");
                row.createCell(9).setCellValue(asset.getRemark() != null ? asset.getRemark() : "");
            }

            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 设置响应头
            setExcelResponse(response, "资产列表_" + LocalDate.now() + ".xlsx");

            // 写入输出流
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("导出Excel失败", e);
        }
    }

    @Override
    public void exportDashboardStatisticsToExcel(HttpServletResponse response) {
        try (Workbook workbook = new XSSFWorkbook()) {
            // 概览数据
            Sheet summarySheet = workbook.createSheet("仪表盘概览");
            DashboardStatistics statistics = dashboardService.getDashboardStatistics();

            int rowNum = 0;
            createDataRow(summarySheet, rowNum++, "统计项", "数值");
            createDataRow(summarySheet, rowNum++, "资产总数", statistics.getTotalAssets().toString());
            createDataRow(summarySheet, rowNum++, "资产总金额", statistics.getTotalAmount().toString());
            createDataRow(summarySheet, rowNum++, "在用资产", statistics.getInUseAssets().toString());
            createDataRow(summarySheet, rowNum++, "闲置资产", statistics.getIdleAssets().toString());
            createDataRow(summarySheet, rowNum++, "维修中资产", statistics.getRepairingAssets().toString());
            createDataRow(summarySheet, rowNum++, "报废资产", statistics.getScrapAssets().toString());
            createDataRow(summarySheet, rowNum++, "闲置率", statistics.getIdleRate() + "%");
            createDataRow(summarySheet, rowNum++, "报废率", statistics.getScrapRate() + "%");
            createDataRow(summarySheet, rowNum++, "使用率", statistics.getUsageRate() + "%");

            // 部门分布
            Sheet deptSheet = workbook.createSheet("部门分布");
            List<AssetDistribution> deptDistributions = dashboardService.getDepartmentDistribution();
            exportDistribution(deptSheet, deptDistributions, "部门");

            // 分类分布
            Sheet categorySheet = workbook.createSheet("分类分布");
            List<AssetDistribution> categoryDistributions = dashboardService.getCategoryDistribution();
            exportDistribution(categorySheet, categoryDistributions, "分类");

            // 状态分布
            Sheet statusSheet = workbook.createSheet("状态分布");
            List<AssetDistribution> statusDistributions = dashboardService.getStatusDistribution();
            exportDistribution(statusSheet, statusDistributions, "状态");

            // 设置响应头
            setExcelResponse(response, "仪表盘统计报表_" + LocalDate.now() + ".xlsx");

            // 写入输出流
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("导出Excel失败", e);
        }
    }

    @Override
    public void exportTimeTrendToExcel(LocalDate startDate, LocalDate endDate, HttpServletResponse response) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("时间趋势统计");

            // 创建标题行
            Row headerRow = sheet.createRow(0);
            String[] headers = { "时间", "新增资产", "报废资产", "期末总数" };

            CellStyle headerStyle = createHeaderStyle(workbook);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 填充数据
            List<TimeTrendStatistics> trends = dashboardService.getTimeTrendStatistics(startDate, endDate);
            int rowNum = 1;

            for (TimeTrendStatistics trend : trends) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(trend.getPeriod());
                row.createCell(1).setCellValue(trend.getNewAssets());
                row.createCell(2).setCellValue(trend.getScrapAssets());
                row.createCell(3).setCellValue(trend.getTotalAssets());
            }

            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 设置响应头
            setExcelResponse(response, "时间趋势统计_" + LocalDate.now() + ".xlsx");

            // 写入输出流
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("导出Excel失败", e);
        }
    }

    /**
     * 导出分布数据到Sheet
     */
    private void exportDistribution(Sheet sheet, List<AssetDistribution> distributions, String type) {
        int rowNum = 0;
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue(type);
        headerRow.createCell(1).setCellValue("数量");
        headerRow.createCell(2).setCellValue("金额");
        headerRow.createCell(3).setCellValue("占比(%)");

        for (AssetDistribution dist : distributions) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(dist.getName());
            row.createCell(1).setCellValue(dist.getCount());
            row.createCell(2).setCellValue(dist.getAmount() != null ? dist.getAmount().doubleValue() : 0);
            row.createCell(3).setCellValue(dist.getPercentage());
        }

        // 自动调整列宽
        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * 创建数据行
     */
    private void createDataRow(Sheet sheet, int rowNum, String key, String value) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(key);
        row.createCell(1).setCellValue(value);
    }

    /**
     * 创建标题样式
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    /**
     * 设置Excel响应头
     */
    private void setExcelResponse(HttpServletResponse response, String fileName) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);
    }

    /**
     * 获取状态文本
     */
    private String getStatusText(Integer status) {
        return switch (status) {
            case 1 -> "闲置";
            case 2 -> "使用中";
            case 3 -> "维修中";
            case 4 -> "报废";
            default -> "未知";
        };
    }

    /**
     * 根据部门ID获取部门名称
     */
    private String getDepartmentName(Long departmentId) {
        if (departmentId == null) {
            return "";
        }
        com.eams.system.entity.Department department = departmentMapper.selectById(departmentId);
        return department != null ? department.getDeptName() : "";
    }
}
