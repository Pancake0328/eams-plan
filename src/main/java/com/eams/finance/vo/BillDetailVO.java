package com.eams.finance.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 账单明细VO
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
public class BillDetailVO {
    private Long id;
    private Long billId;
    private Long assetId;
    private String assetNumber;
    private String assetName;
    private String categoryName;
    private String department;
    private BigDecimal purchaseAmount;
    private BigDecimal accumulatedDepreciation;
    private BigDecimal currentDepreciation;
    private BigDecimal netValue;
    private BigDecimal depreciationRate;
    private Integer usefulLife;
    private Integer usedMonths;
    private Integer assetStatus;
    private String assetStatusText;
}
