package com.eams.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eams.asset.entity.AssetInfo;
import com.eams.asset.mapper.AssetInfoMapper;
import com.eams.common.exception.BusinessException;
import com.eams.finance.entity.AssetPurchase;
import com.eams.finance.mapper.AssetPurchaseMapper;
import com.eams.finance.service.AssetDepreciationService;
import com.eams.finance.vo.DepreciationInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * 资产折旧计算服务实现
 * 采用直线法计算折旧
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssetDepreciationServiceImpl implements AssetDepreciationService {

    private final AssetInfoMapper assetInfoMapper;
    private final AssetPurchaseMapper purchaseMapper;

    /**
     * 残值率（默认5%）
     */
    @Value("${asset.depreciation.residual-rate:5}")
    private Integer residualRate;

    /**
     * 默认使用年限（月，默认60个月即5年）
     */
    @Value("${asset.depreciation.default-useful-life:60}")
    private Integer defaultUsefulLife;

    /**
     * 计算精度（小数位）
     */
    @Value("${asset.depreciation.scale:2}")
    private Integer scale;

    @Override
    public DepreciationInfo calculateDepreciation(Long assetId, LocalDate targetDate) {
        // 查询资产信息
        AssetInfo asset = assetInfoMapper.selectById(assetId);
        if (asset == null) {
            throw new BusinessException("资产不存在");
        }

        // 查询采购记录
        LambdaQueryWrapper<AssetPurchase> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetPurchase::getAssetId, assetId);
        wrapper.eq(AssetPurchase::getApprovalStatus, 1); // 已审批
        wrapper.orderByDesc(AssetPurchase::getPurchaseDate);
        wrapper.last("LIMIT 1");
        AssetPurchase purchase = purchaseMapper.selectOne(wrapper);

        if (purchase == null) {
            throw new BusinessException("未找到该资产的采购记录");
        }

        return calculateDepreciationInfo(asset, purchase, targetDate);
    }

    @Override
    public List<DepreciationInfo> batchCalculateDepreciation(List<Long> assetIds, LocalDate targetDate) {
        List<DepreciationInfo> result = new ArrayList<>();
        for (Long assetId : assetIds) {
            try {
                result.add(calculateDepreciation(assetId, targetDate));
            } catch (Exception e) {
                log.error("计算资产{}折旧失败: {}", assetId, e.getMessage());
            }
        }
        return result;
    }

    @Override
    public List<DepreciationInfo> calculateAllDepreciation(LocalDate targetDate) {
        // 查询所有非报废资产
        LambdaQueryWrapper<AssetInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(AssetInfo::getAssetStatus, 5); // 排除已报废
        List<AssetInfo> assets = assetInfoMapper.selectList(wrapper);

        List<DepreciationInfo> result = new ArrayList<>();
        for (AssetInfo asset : assets) {
            try {
                result.add(calculateDepreciation(asset.getId(), targetDate));
            } catch (Exception e) {
                log.error("计算资产{}折旧失败: {}", asset.getId(), e.getMessage());
            }
        }
        return result;
    }

    /**
     * 计算单个资产的折旧信息
     *
     * @param asset      资产信息
     * @param purchase   采购记录
     * @param targetDate 计算截止日期
     * @return 折旧信息
     */
    private DepreciationInfo calculateDepreciationInfo(AssetInfo asset, AssetPurchase purchase, LocalDate targetDate) {
        DepreciationInfo info = new DepreciationInfo();
        info.setAssetId(asset.getId());
        info.setAssetNumber(asset.getAssetNumber());
        info.setAssetName(asset.getAssetName());

        BigDecimal purchaseAmount = purchase.getPurchaseAmount();
        info.setPurchaseAmount(purchaseAmount);

        // 计算残值 = 采购金额 × 残值率
        BigDecimal residualValue = purchaseAmount
                .multiply(new BigDecimal(residualRate))
                .divide(new BigDecimal(100), scale, RoundingMode.HALF_UP);
        info.setResidualValue(residualValue);

        // 使用年限（月）
        Integer usefulLife = defaultUsefulLife;
        info.setUsefulLife(usefulLife);

        // 计算已使用月数
        LocalDate purchaseDate = purchase.getPurchaseDate();
        Period period = Period.between(purchaseDate, targetDate);
        int usedMonths = period.getYears() * 12 + period.getMonths();

        // 如果资产已报废，使用报废时的月数
        if (asset.getAssetStatus() == 5) {
            // 这里简化处理，实际应查询报废记录获取报废日期
            usedMonths = Math.min(usedMonths, usefulLife);
        }

        info.setUsedMonths(usedMonths);
        info.setRemainingMonths(Math.max(0, usefulLife - usedMonths));

        // 计算月折旧额 = (采购金额 - 残值) / 使用年限
        BigDecimal depreciableAmount = purchaseAmount.subtract(residualValue);
        BigDecimal monthlyDepreciation = depreciableAmount
                .divide(new BigDecimal(usefulLife), scale, RoundingMode.HALF_UP);
        info.setMonthlyDepreciation(monthlyDepreciation);

        // 计算累计折旧 = 月折旧额 × 已使用月数
        BigDecimal accumulatedDepreciation = monthlyDepreciation
                .multiply(new BigDecimal(usedMonths))
                .setScale(scale, RoundingMode.HALF_UP);

        // 累计折旧不超过(采购金额 - 残值)
        if (accumulatedDepreciation.compareTo(depreciableAmount) > 0) {
            accumulatedDepreciation = depreciableAmount;
        }
        info.setAccumulatedDepreciation(accumulatedDepreciation);

        // 计算净值 = 采购金额 - 累计折旧
        BigDecimal netValue = purchaseAmount.subtract(accumulatedDepreciation);
        info.setNetValue(netValue);

        // 计算折旧率 = 月折旧额 / 采购金额
        BigDecimal depreciationRate = monthlyDepreciation
                .divide(purchaseAmount, 4, RoundingMode.HALF_UP);
        info.setDepreciationRate(depreciationRate);

        return info;
    }
}
