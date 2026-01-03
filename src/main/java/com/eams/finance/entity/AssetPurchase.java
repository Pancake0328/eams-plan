package com.eams.finance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 资产采购记录实体
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
@TableName("asset_purchase")
public class AssetPurchase {

    /**
     * 采购记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 资产ID
     */
    private Long assetId;

    /**
     * 采购金额
     */
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

    /**
     * 采购人
     */
    private String purchaser;

    /**
     * 审批状态：0-待审批，1-已审批，2-已拒绝
     */
    private Integer approvalStatus;

    /**
     * 审批人
     */
    private String approver;

    /**
     * 审批时间
     */
    private LocalDateTime approvalTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
}
