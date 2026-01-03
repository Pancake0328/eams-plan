package com.eams.dashboard.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 仪表盘统计数据VO
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
public class DashboardStatistics {

    /**
     * 资产总数
     */
    private Long totalAssets;

    /**
     * 资产总金额
     */
    private BigDecimal totalAmount;

    /**
     * 在用资产数量
     */
    private Long inUseAssets;

    /**
     * 闲置资产数量
     */
    private Long idleAssets;

    /**
     * 维修中资产数量
     */
    private Long repairingAssets;

    /**
     * 报废资产数量
     */
    private Long scrapAssets;

    /**
     * 闲置率（百分比）
     */
    private Double idleRate;

    /**
     * 报废率（百分比）
     */
    private Double scrapRate;

    /**
     * 资产使用率（百分比）
     */
    private Double usageRate;

    /**
     * 本月新增资产数量
     */
    private Long currentMonthNewAssets;

    /**
     * 待审批报修数量
     */
    private Long pendingRepairs;

    /**
     * 进行中盘点数量
     */
    private Long ongoingInventories;
}
