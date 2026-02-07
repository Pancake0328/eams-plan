package com.eams.purchase.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 采购资金统计维度
 */
@Data
public class PurchaseFundStatisticVO {

    private String dimension;

    private Long orderCount;

    private BigDecimal totalAmount;
}
