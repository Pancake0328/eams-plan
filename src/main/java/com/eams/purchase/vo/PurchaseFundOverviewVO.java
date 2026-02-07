package com.eams.purchase.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 采购资金概览
 */
@Data
public class PurchaseFundOverviewVO {

    private Long orderCount;

    private BigDecimal totalAmount;

    private BigDecimal averageAmount;
}
