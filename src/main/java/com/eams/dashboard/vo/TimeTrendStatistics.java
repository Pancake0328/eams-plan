package com.eams.dashboard.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 时间趋势统计VO
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeTrendStatistics {

    /**
     * 时间点（如：2024-01）
     */
    private String period;

    /**
     * 新增资产数量
     */
    private Long newAssets;

    /**
     * 报废资产数量
     */
    private Long scrapAssets;

    /**
     * 期末资产总数
     */
    private Long totalAssets;
}
