package com.eams.finance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 资产账单实体
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
@TableName("asset_bill")
public class AssetBill {

    /**
     * 账单ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 账单编号
     */
    private String billNumber;

    /**
     * 账单类型：1-月度账单，2-年度账单
     */
    private Integer billType;

    /**
     * 账单年份
     */
    private Integer billYear;

    /**
     * 账单月份
     */
    private Integer billMonth;

    /**
     * 采购总金额
     */
    private BigDecimal totalPurchaseAmount;

    /**
     * 折旧总金额
     */
    private BigDecimal totalDepreciationAmount;

    /**
     * 资产总价值
     */
    private BigDecimal totalAssetValue;

    /**
     * 资产净值总额
     */
    private BigDecimal totalNetValue;

    /**
     * 账单状态：0-草稿，1-已生成，2-已确认
     */
    private Integer billStatus;

    /**
     * 生成时间
     */
    private LocalDateTime generateTime;

    /**
     * 确认时间
     */
    private LocalDateTime confirmTime;

    /**
     * 备注
     */
    private String remark;

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
