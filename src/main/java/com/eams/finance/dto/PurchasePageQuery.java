package com.eams.finance.dto;

import lombok.Data;

/**
 * 采购记录分页查询DTO
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
public class PurchasePageQuery {
    /**
     * 当前页
     */
    private Integer current = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;

    /**
     * 资产ID
     */
    private Long assetId;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 发票号
     */
    private String invoiceNumber;

    /**
     * 审批状态
     */
    private Integer approvalStatus;
}
