package com.eams.finance.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 资金统计VO
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
public class FinanceStatisticsVO {
    /**
     * 统计维度（部门名称、年份等）
     */
    private String dimension;

    /**
     * 资产数量
     */
    private Integer assetCount;

    /**
     * 采购总金额
     */
    private BigDecimal totalPurchaseAmount;

    /**
     * 累计折旧总额
     */
    private BigDecimal totalDepreciation;

    /**
     * 资产净值总额
     */
    private BigDecimal totalNetValue;

    /**
     * 平均单价
     */
    private BigDecimal averagePrice;
}
