package com.eams.asset.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 资产信息视图对象 VO
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "资产信息")
public class AssetVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 资产ID
     */
    @Schema(description = "资产ID")
    private Long id;

    /**
     * 资产编号
     */
    @Schema(description = "资产编号")
    private String assetNumber;

    /**
     * 资产名称
     */
    @Schema(description = "资产名称")
    private String assetName;

    /**
     * 资产分类ID
     */
    @Schema(description = "资产分类ID")
    private Long categoryId;

    /**
     * 资产分类名称
     */
    @Schema(description = "资产分类名称")
    private String categoryName;

    /**
     * 采购金额
     */
    @Schema(description = "采购金额")
    private BigDecimal purchaseAmount;

    /**
     * 采购日期
     */
    @Schema(description = "采购日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate;

    /**
     * 使用部门
     */
    @Schema(description = "使用部门")
    private String department;

    /**
     * 责任人
     */
    @Schema(description = "责任人")
    private String custodian;

    /**
     * 资产状态：1-闲置，2-使用中，3-维修中，4-报废
     */
    @Schema(description = "资产状态")
    private Integer assetStatus;

    /**
     * 资产状态文本
     */
    @Schema(description = "资产状态文本")
    private String assetStatusText;

    /**
     * 规格型号
     */
    @Schema(description = "规格型号")
    private String specifications;

    /**
     * 生产厂商
     */
    @Schema(description = "生产厂商")
    private String manufacturer;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
