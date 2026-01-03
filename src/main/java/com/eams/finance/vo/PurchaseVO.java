package com.eams.finance.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 采购记录VO
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
public class PurchaseVO {
    private Long id;
    private Long assetId;
    private String assetNumber;
    private String assetName;
    private BigDecimal purchaseAmount;
    private String supplierName;
    private String supplierContact;
    private String invoiceNumber;
    private LocalDate purchaseDate;
    private String paymentMethod;
    private Integer warrantyPeriod;
    private String remark;
    private String purchaser;
    private Integer approvalStatus;
    private String approvalStatusText;
    private String approver;
    private LocalDateTime approvalTime;
    private LocalDateTime createTime;
}
