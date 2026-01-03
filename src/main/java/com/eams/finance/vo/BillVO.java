package com.eams.finance.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 账单VO
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
public class BillVO {
    private Long id;
    private String billNumber;
    private Integer billType;
    private String billTypeText;
    private Integer billYear;
    private Integer billMonth;
    private BigDecimal totalPurchaseAmount;
    private BigDecimal totalDepreciationAmount;
    private BigDecimal totalAssetValue;
    private BigDecimal totalNetValue;
    private Integer billStatus;
    private String billStatusText;
    private LocalDateTime generateTime;
    private LocalDateTime confirmTime;
    private String remark;
    private LocalDateTime createTime;
    private List<BillDetailVO> details;
}
