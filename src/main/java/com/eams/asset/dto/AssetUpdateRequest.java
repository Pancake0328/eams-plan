package com.eams.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 更新资产请求 DTO
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Data
@Schema(description = "更新资产请求")
public class AssetUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 资产名称
     */
    @NotBlank(message = "资产名称不能为空")
    @Size(max = 100, message = "资产名称长度不能超过100个字符")
    @Schema(description = "资产名称")
    private String assetName;

    /**
     * 资产分类ID
     */
    @NotNull(message = "资产分类不能为空")
    @Schema(description = "资产分类ID")
    private Long categoryId;

    /**
     * 采购金额
     */
    @Schema(description = "采购金额")
    private BigDecimal purchaseAmount;

    /**
     * 采购日期
     */
    @Schema(description = "采购日期")
    private LocalDate purchaseDate;

    /**
     * 使用部门ID
     */
    @Schema(description = "使用部门ID")
    private Long departmentId;

    /**
     * 责任人
     */
    @Size(max = 50, message = "责任人长度不能超过50个字符")
    @Schema(description = "责任人")
    private String custodian;

    /**
     * 资产状态
     */
    @Schema(description = "资产状态：1-闲置，2-使用中，3-维修中，4-报废")
    private Integer assetStatus;

    /**
     * 规格型号
     */
    @Schema(description = "规格型号")
    private String specifications;

    /**
     * 生产厂商
     */
    @Size(max = 100, message = "生产厂商长度不能超过100个字符")
    @Schema(description = "生产厂商")
    private String manufacturer;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
