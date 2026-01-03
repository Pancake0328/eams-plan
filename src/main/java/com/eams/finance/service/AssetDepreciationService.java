package com.eams.finance.service;

import com.eams.finance.vo.DepreciationInfo;

import java.time.LocalDate;
import java.util.List;

/**
 * 资产折旧计算服务
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
public interface AssetDepreciationService {

    /**
     * 计算单个资产的折旧信息
     *
     * @param assetId    资产ID
     * @param targetDate 计算截止日期
     * @return 折旧信息
     */
    DepreciationInfo calculateDepreciation(Long assetId, LocalDate targetDate);

    /**
     * 批量计算多个资产的折旧
     *
     * @param assetIds   资产ID列表
     * @param targetDate 计算截止日期
     * @return 折旧信息列表
     */
    List<DepreciationInfo> batchCalculateDepreciation(List<Long> assetIds, LocalDate targetDate);

    /**
     * 计算所有资产的折旧
     *
     * @param targetDate 计算截止日期
     * @return 折旧信息列表
     */
    List<DepreciationInfo> calculateAllDepreciation(LocalDate targetDate);
}
