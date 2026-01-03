package com.eams.dashboard.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 资产分布VO
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetDistribution {

    /**
     * 分组名称（部门名称或分类名称）
     */
    private String name;

    /**
     * 资产数量
     */
    private Long count;

    /**
     * 资产金额
     */
    private BigDecimal amount;

    /**
     * 占比（百分比）
     */
    private Double percentage;
}
