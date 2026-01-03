package com.eams.asset.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 资产信息实体
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
@TableName("asset_info")
public class AssetInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 资产ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 资产编号（唯一）
     */
    private String assetNumber;

    /**
     * 资产名称
     */
    private String assetName;

    /**
     * 资产分类ID
     */
    private Long categoryId;

    /**
     * 采购金额
     */
    private BigDecimal purchaseAmount;

    /**
     * 采购日期
     */
    private LocalDate purchaseDate;

    /**
     * 使用部门
     */
    private String department;

    /**
     * 责任人
     */
    private String custodian;

    /**
     * 资产状态：1-闲置，2-使用中，3-维修中，4-报废
     */
    private Integer assetStatus;

    /**
     * 规格型号
     */
    private String specifications;

    /**
     * 生产厂商
     */
    private String manufacturer;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标记：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;

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
}
