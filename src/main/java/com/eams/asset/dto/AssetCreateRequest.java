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
 * 创建资产请求 DTO
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
@Schema(description = "创建资产请求")
public class AssetCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 资产名称
     */
    @NotBlank(message = "资产名称不能为空")
    @Size(max = 100, message = "资产名称长度不能超过100个字符")
    @Schema(description = "资产名称", example = "联想ThinkPad笔记本")
    private String assetName;

    /**
     * 资产分类ID
     */
    @NotNull(message = "资产分类不能为空")
    @Schema(description = "资产分类ID", example = "4")
    private Long categoryId;

    /**
     * 采购金额
     */
    @Schema(description = "采购金额", example = "5999.00")
    private BigDecimal purchaseAmount;

    /**
     * 采购日期
     */
    @Schema(description = "采购日期", example = "2026-01-03")
    private LocalDate purchaseDate;

    /**
     * 使用部门
     */
    @Size(max = 100, message = "使用部门长度不能超过100个字符")
    @Schema(description = "使用部门", example = "技术部")
    private String department;

    /**
     * 责任人
     */
    @Size(max = 50, message = "责任人长度不能超过50个字符")
    @Schema(description = "责任人", example = "张三")
    private String custodian;

    /**
     * 资产状态：1-闲置，2-使用中，3-维修中，4-报废
     */
    @Schema(description = "资产状态：1-闲置，2-使用中，3-维修中，4-报废", example = "1")
    private Integer assetStatus;

    /**
     * 规格型号
     */
    @Schema(description = "规格型号", example = "ThinkPad X1 Carbon")
    private String specifications;

    /**
     * 生产厂商
     */
    @Size(max = 100, message = "生产厂商长度不能超过100个字符")
    @Schema(description = "生产厂商", example = "联想")
    private String manufacturer;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
