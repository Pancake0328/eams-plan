package com.eams.purchase.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 采购明细实体
 *
 * @author Pancake
 * @since 2026-01-08
 */
@Data
@TableName("asset_purchase_detail")
public class PurchaseOrderDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long purchaseId;
    private String assetName;
    private Long categoryId;
    private String specifications;
    private String manufacturer;
    private BigDecimal unitPrice;
    private Integer quantity;
    private Integer inboundQuantity;
    private BigDecimal totalAmount;
    private Integer expectedLife;
    private String remark;
    private Integer detailStatus;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
