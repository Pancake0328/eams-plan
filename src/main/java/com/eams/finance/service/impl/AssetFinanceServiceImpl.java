package com.eams.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eams.asset.entity.AssetInfo;
import com.eams.asset.mapper.AssetInfoMapper;
import com.eams.finance.service.AssetDepreciationService;
import com.eams.finance.service.AssetFinanceService;
import com.eams.finance.vo.DepreciationInfo;
import com.eams.finance.vo.FinanceStatisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 资金统计服务实现
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Service
@RequiredArgsConstructor
public class AssetFinanceServiceImpl implements AssetFinanceService {

    private final AssetInfoMapper assetInfoMapper;
    private final AssetDepreciationService depreciationService;

    @Override
    public List<FinanceStatisticsVO> statisticsByDepartment() {
        // 查询所有非报废资产
        LambdaQueryWrapper<AssetInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(AssetInfo::getAssetStatus, 5);
        List<AssetInfo> assets = assetInfoMapper.selectList(wrapper);

        // 计算所有资产折旧
        LocalDate now = LocalDate.now();
        List<DepreciationInfo> depreciationList = depreciationService.calculateAllDepreciation(now);

        // 按部门分组统计
        Map<String, List<DepreciationInfo>> groupedByDept = depreciationList.stream()
                .collect(Collectors.groupingBy(info -> {
                    AssetInfo asset = assets.stream()
                            .filter(a -> a.getId().equals(info.getAssetId()))
                            .findFirst()
                            .orElse(null);
                    return asset != null && asset.getDepartment() != null ? asset.getDepartment() : "未分配";
                }));

        List<FinanceStatisticsVO> result = new ArrayList<>();
        for (Map.Entry<String, List<DepreciationInfo>> entry : groupedByDept.entrySet()) {
            FinanceStatisticsVO vo = new FinanceStatisticsVO();
            vo.setDimension(entry.getKey());
            vo.setAssetCount(entry.getValue().size());

            BigDecimal totalPurchase = entry.getValue().stream()
                    .map(DepreciationInfo::getPurchaseAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            vo.setTotalPurchaseAmount(totalPurchase);

            BigDecimal totalDepreciation = entry.getValue().stream()
                    .map(DepreciationInfo::getAccumulatedDepreciation)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            vo.setTotalDepreciation(totalDepreciation);

            BigDecimal totalNetValue = entry.getValue().stream()
                    .map(DepreciationInfo::getNetValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            vo.setTotalNetValue(totalNetValue);

            if (vo.getAssetCount() > 0) {
                vo.setAveragePrice(totalPurchase.divide(
                        new BigDecimal(vo.getAssetCount()), 2, RoundingMode.HALF_UP));
            }

            result.add(vo);
        }

        return result;
    }

    @Override
    public List<FinanceStatisticsVO> statisticsByTime(LocalDate startDate, LocalDate endDate) {
        // 简化实现：按年度统计
        List<FinanceStatisticsVO> result = new ArrayList<>();

        int startYear = startDate.getYear();
        int endYear = endDate.getYear();

        for (int year = startYear; year <= endYear; year++) {
            LocalDate targetDate = LocalDate.of(year, 12, 31);
            if (targetDate.isAfter(LocalDate.now())) {
                targetDate = LocalDate.now();
            }

            List<DepreciationInfo> depreciationList = depreciationService.calculateAllDepreciation(targetDate);

            FinanceStatisticsVO vo = new FinanceStatisticsVO();
            vo.setDimension(String.valueOf(year));
            vo.setAssetCount(depreciationList.size());

            BigDecimal totalPurchase = depreciationList.stream()
                    .map(DepreciationInfo::getPurchaseAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            vo.setTotalPurchaseAmount(totalPurchase);

            BigDecimal totalDepreciation = depreciationList.stream()
                    .map(DepreciationInfo::getAccumulatedDepreciation)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            vo.setTotalDepreciation(totalDepreciation);

            BigDecimal totalNetValue = depreciationList.stream()
                    .map(DepreciationInfo::getNetValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            vo.setTotalNetValue(totalNetValue);

            if (vo.getAssetCount() > 0) {
                vo.setAveragePrice(totalPurchase.divide(
                        new BigDecimal(vo.getAssetCount()), 2, RoundingMode.HALF_UP));
            }

            result.add(vo);
        }

        return result;
    }

    @Override
    public FinanceStatisticsVO getFinanceOverview() {
        LocalDate now = LocalDate.now();
        List<DepreciationInfo> depreciationList = depreciationService.calculateAllDepreciation(now);

        FinanceStatisticsVO vo = new FinanceStatisticsVO();
        vo.setDimension("全部");
        vo.setAssetCount(depreciationList.size());

        BigDecimal totalPurchase = depreciationList.stream()
                .map(DepreciationInfo::getPurchaseAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalPurchaseAmount(totalPurchase);

        BigDecimal totalDepreciation = depreciationList.stream()
                .map(DepreciationInfo::getAccumulatedDepreciation)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalDepreciation(totalDepreciation);

        BigDecimal totalNetValue = depreciationList.stream()
                .map(DepreciationInfo::getNetValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalNetValue(totalNetValue);

        if (vo.getAssetCount() > 0) {
            vo.setAveragePrice(totalPurchase.divide(
                    new BigDecimal(vo.getAssetCount()), 2, RoundingMode.HALF_UP));
        }

        return vo;
    }
}
