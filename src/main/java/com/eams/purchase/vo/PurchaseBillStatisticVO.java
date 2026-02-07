package com.eams.purchase.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 采购账单统计
 */
@Data
public class PurchaseBillStatisticVO {

    /**
     * 账单期间（YYYY-MM 或 YYYY）
     */
    private String billPeriod;

    /**
     * 账单类型（月度/年度）
     */
    private String billType;

    private Long orderCount;

    private BigDecimal totalAmount;

    private BigDecimal averageAmount;
}
