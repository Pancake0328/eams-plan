package com.eams.purchase.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 采购单实体
 *
 * @author EAMS Team
 * @since 2026-01-08
 */
@Data
@TableName("asset_purchase_order")
public class PurchaseOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String purchaseNumber;
    private LocalDate purchaseDate;
    private String supplier;
    private BigDecimal totalAmount;
    private Integer purchaseStatus;
    private String applicant;
    private String approver;
    private LocalDateTime approveTime;
    private String remark;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
