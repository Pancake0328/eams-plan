package com.eams.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 资产分页查询条件 DTO
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Data
@Schema(description = "资产分页查询条件")
public class AssetPageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    @Schema(description = "当前页码", example = "1")
    private Long current = 1L;

    /**
     * 每页数量
     */
    @Schema(description = "每页数量", example = "10")
    private Long size = 10L;

    /**
     * 资产编号（模糊查询）
     */
    @Schema(description = "资产编号（模糊查询）")
    private String assetNumber;

    /**
     * 资产名称（模糊查询）
     */
    @Schema(description = "资产名称（模糊查询）")
    private String assetName;

    /**
     * 资产分类ID
     */
    @Schema(description = "资产分类ID")
    private Long categoryId;

    /**
     * 使用部门ID
     */
    @Schema(description = "使用部门ID")
    private Long departmentId;

    /**
     * 责任人（模糊查询）
     */
    @Schema(description = "责任人（模糊查询）")
    private String custodian;

    /**
     * 资产状态
     */
    @Schema(description = "资产状态：1-闲置，2-使用中，3-维修中，4-报废")
    private Integer assetStatus;
}
