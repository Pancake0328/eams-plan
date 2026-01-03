package com.eams.finance.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 折旧信息VO
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
public class DepreciationInfo {
    /**
     * 资产ID
     */
    private Long assetId;

    /**
     * 资产编号
     */
    private String assetNumber;

    /**
     * 资产名称
     */
    private String assetName;

    /**
     * 采购金额
     */
    private BigDecimal purchaseAmount;

    /**
     * 残值
     */
    private BigDecimal residualValue;

    /**
     * 月折旧额
     */
    private BigDecimal monthlyDepreciation;

    /**
     * 累计折旧
     */
    private BigDecimal accumulatedDepreciation;

    /**
     * 净值
     */
    private BigDecimal netValue;

    /**
     * 折旧率
     */
    private BigDecimal depreciationRate;

    /**
     * 使用年限（月）
     */
    private Integer usefulLife;

    /**
     * 已使用月数
     */
    private Integer usedMonths;

    /**
     * 剩余月数
     */
    private Integer remainingMonths;
}
