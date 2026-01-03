package com.eams.dashboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eams.asset.entity.AssetCategory;
import com.eams.asset.entity.AssetInfo;
import com.eams.asset.mapper.AssetCategoryMapper;
import com.eams.asset.mapper.AssetInfoMapper;
import com.eams.dashboard.service.DashboardService;
import com.eams.dashboard.vo.AssetDistribution;
import com.eams.dashboard.vo.DashboardStatistics;
import com.eams.dashboard.vo.TimeTrendStatistics;
import com.eams.lifecycle.entity.AssetInventory;
import com.eams.lifecycle.entity.AssetRepair;
import com.eams.lifecycle.mapper.AssetInventoryMapper;
import com.eams.lifecycle.mapper.AssetRepairMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 仪表盘数据服务实现
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final AssetInfoMapper assetInfoMapper;
    private final AssetCategoryMapper categoryMapper;
    private final AssetRepairMapper repairMapper;
    private final AssetInventoryMapper inventoryMapper;

    @Override
    public DashboardStatistics getDashboardStatistics() {
        DashboardStatistics statistics = new DashboardStatistics();

        // 查询所有资产
        List<AssetInfo> allAssets = assetInfoMapper.selectList(null);

        // 资产总数
        statistics.setTotalAssets((long) allAssets.size());

        // 资产总金额
        BigDecimal totalAmount = allAssets.stream()
                .map(AssetInfo::getPurchaseAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        statistics.setTotalAmount(totalAmount);

        // 按状态统计
        Map<Integer, Long> statusCount = allAssets.stream()
                .collect(Collectors.groupingBy(AssetInfo::getAssetStatus, Collectors.counting()));

        statistics.setIdleAssets(statusCount.getOrDefault(1, 0L)); // 闲置
        statistics.setInUseAssets(statusCount.getOrDefault(2, 0L)); // 使用中
        statistics.setRepairingAssets(statusCount.getOrDefault(3, 0L)); // 维修中
        statistics.setScrapAssets(statusCount.getOrDefault(4, 0L)); // 报废

        // 计算各项比率
        long total = statistics.getTotalAssets();
        if (total > 0) {
            statistics.setIdleRate(calculatePercentage(statistics.getIdleAssets(), total));
            statistics.setScrapRate(calculatePercentage(statistics.getScrapAssets(), total));
            statistics.setUsageRate(calculatePercentage(statistics.getInUseAssets(), total));
        } else {
            statistics.setIdleRate(0.0);
            statistics.setScrapRate(0.0);
            statistics.setUsageRate(0.0);
        }

        // 本月新增资产数量
        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        long currentMonthNew = allAssets.stream()
                .filter(asset -> asset.getPurchaseDate() != null &&
                        !asset.getPurchaseDate().isBefore(firstDayOfMonth))
                .count();
        statistics.setCurrentMonthNewAssets(currentMonthNew);

        // 待审批报修数量
        LambdaQueryWrapper<AssetRepair> repairWrapper = new LambdaQueryWrapper<>();
        repairWrapper.eq(AssetRepair::getRepairStatus, 1); // 待审批
        statistics.setPendingRepairs(repairMapper.selectCount(repairWrapper));

        // 进行中盘点数量
        LambdaQueryWrapper<AssetInventory> inventoryWrapper = new LambdaQueryWrapper<>();
        inventoryWrapper.eq(AssetInventory::getInventoryStatus, 2); // 进行中
        statistics.setOngoingInventories(inventoryMapper.selectCount(inventoryWrapper));

        return statistics;
    }

    @Override
    public List<AssetDistribution> getDepartmentDistribution() {
        List<AssetInfo> allAssets = assetInfoMapper.selectList(null);

        // 按部门分组统计
        Map<String, List<AssetInfo>> departmentGroup = allAssets.stream()
                .collect(Collectors.groupingBy(asset -> asset.getDepartment() != null ? asset.getDepartment() : "未分配"));

        List<AssetDistribution> distributions = new ArrayList<>();
        long totalAssets = allAssets.size();

        for (Map.Entry<String, List<AssetInfo>> entry : departmentGroup.entrySet()) {
            String department = entry.getKey();
            List<AssetInfo> assets = entry.getValue();

            long count = assets.size();
            BigDecimal amount = assets.stream()
                    .map(AssetInfo::getPurchaseAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            double percentage = calculatePercentage(count, totalAssets);

            distributions.add(new AssetDistribution(department, count, amount, percentage));
        }

        // 按数量降序排序
        distributions.sort((a, b) -> Long.compare(b.getCount(), a.getCount()));

        return distributions;
    }

    @Override
    public List<AssetDistribution> getCategoryDistribution() {
        List<AssetInfo> allAssets = assetInfoMapper.selectList(null);
        Map<Long, List<AssetInfo>> categoryGroup = allAssets.stream()
                .filter(asset -> asset.getCategoryId() != null)
                .collect(Collectors.groupingBy(AssetInfo::getCategoryId));

        List<AssetDistribution> distributions = new ArrayList<>();
        long totalAssets = allAssets.size();

        // 查询所有分类
        Map<Long, String> categoryMap = categoryMapper.selectList(null).stream()
                .collect(Collectors.toMap(AssetCategory::getId, AssetCategory::getCategoryName));

        for (Map.Entry<Long, List<AssetInfo>> entry : categoryGroup.entrySet()) {
            Long categoryId = entry.getKey();
            List<AssetInfo> assets = entry.getValue();

            String categoryName = categoryMap.getOrDefault(categoryId, "未知分类");
            long count = assets.size();
            BigDecimal amount = assets.stream()
                    .map(AssetInfo::getPurchaseAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            double percentage = calculatePercentage(count, totalAssets);

            distributions.add(new AssetDistribution(categoryName, count, amount, percentage));
        }

        // 按数量降序排序
        distributions.sort((a, b) -> Long.compare(b.getCount(), a.getCount()));

        return distributions;
    }

    @Override
    public List<TimeTrendStatistics> getTimeTrendStatistics(LocalDate startDate, LocalDate endDate) {
        List<AssetInfo> allAssets = assetInfoMapper.selectList(null);
        List<TimeTrendStatistics> trends = new ArrayList<>();

        // 按月份统计
        YearMonth start = YearMonth.from(startDate);
        YearMonth end = YearMonth.from(endDate);

        YearMonth current = start;
        while (!current.isAfter(end)) {
            LocalDate monthStart = current.atDay(1);
            LocalDate monthEnd = current.atEndOfMonth();

            // 新增资产
            long newAssets = allAssets.stream()
                    .filter(asset -> asset.getPurchaseDate() != null &&
                            !asset.getPurchaseDate().isBefore(monthStart) &&
                            !asset.getPurchaseDate().isAfter(monthEnd))
                    .count();

            // 报废资产（暂用状态为报废的数量，实际应该查生命周期表）
            long scrapAssets = allAssets.stream()
                    .filter(asset -> asset.getAssetStatus() == 4)
                    .count();

            // 期末资产总数
            long totalAssets = allAssets.stream()
                    .filter(asset -> asset.getPurchaseDate() != null &&
                            !asset.getPurchaseDate().isAfter(monthEnd))
                    .count();

            trends.add(new TimeTrendStatistics(
                    current.toString(),
                    newAssets,
                    scrapAssets,
                    totalAssets));

            current = current.plusMonths(1);
        }

        return trends;
    }

    @Override
    public List<AssetDistribution> getStatusDistribution() {
        List<AssetInfo> allAssets = assetInfoMapper.selectList(null);

        Map<Integer, Long> statusCount = allAssets.stream()
                .collect(Collectors.groupingBy(AssetInfo::getAssetStatus, Collectors.counting()));

        List<AssetDistribution> distributions = new ArrayList<>();
        long totalAssets = allAssets.size();

        Map<Integer, String> statusNames = new HashMap<>();
        statusNames.put(1, "闲置");
        statusNames.put(2, "使用中");
        statusNames.put(3, "维修中");
        statusNames.put(4, "报废");

        for (Map.Entry<Integer, Long> entry : statusCount.entrySet()) {
            String statusName = statusNames.getOrDefault(entry.getKey(), "未知");
            long count = entry.getValue();

            BigDecimal amount = allAssets.stream()
                    .filter(asset -> asset.getAssetStatus().equals(entry.getKey()))
                    .map(AssetInfo::getPurchaseAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            double percentage = calculatePercentage(count, totalAssets);

            distributions.add(new AssetDistribution(statusName, count, amount, percentage));
        }

        return distributions;
    }

    /**
     * 计算百分比
     */
    private double calculatePercentage(long count, long total) {
        if (total == 0) {
            return 0.0;
        }
        return BigDecimal.valueOf(count)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
