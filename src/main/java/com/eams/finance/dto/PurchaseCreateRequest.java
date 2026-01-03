package com.eams.finance.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 采购记录创建请求DTO
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
public class PurchaseCreateRequest {

    /**
     * 资产ID
     */
    @NotNull(message = "资产ID不能为空")
    private Long assetId;

    /**
     * 采购金额
     */
    @NotNull(message = "采购金额不能为空")
    @DecimalMin(value = "0.01", message = "采购金额必须大于0")
    private BigDecimal purchaseAmount;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 供应商联系方式
     */
    private String supplierContact;

    /**
     * 发票号
     */
    private String invoiceNumber;

    /**
     * 采购日期
     */
    @NotNull(message = "采购日期不能为空")
    private LocalDate purchaseDate;

    /**
     * 付款方式
     */
    private String paymentMethod;

    /**
     * 质保期（月）
     */
    private Integer warrantyPeriod;

    /**
     * 备注
     */
    private String remark;
}