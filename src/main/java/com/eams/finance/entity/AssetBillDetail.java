package com.eams.finance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 资产账单明细实体
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
@TableName("asset_bill_detail")
public class AssetBillDetail {

    /**
     * 明细ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 账单ID
     */
    private Long billId;

    /**
     * 资产ID
     */
    private Long assetId;

    /**
     * 资产编号
     */
    private String assetNumber;

    /**
     * 资产名称
     */
    private String assetName;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 使用部门
     */
    private String department;

    /**
     * 采购金额
     */
    private BigDecimal purchaseAmount;

    /**
     * 累计折旧
     */
    private BigDecimal accumulatedDepreciation;

    /**
     * 本期折旧
     */
    private BigDecimal currentDepreciation;

    /**
     * 净值
     */
    private BigDecimal netValue;

    /**
     * 折旧率
     */
    private BigDecimal depreciationRate;

    /**
     * 使用年限（月）
     */
    private Integer usefulLife;

    /**
     * 已使用月数
     */
    private Integer usedMonths;

    /**
     * 资产状态
     */
    private Integer assetStatus;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
