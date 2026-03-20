package com.eams.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 资产使用申请分页查询条件
 */
@Data
@Schema(description = "资产使用申请分页查询条件")
public class UsageApplicationPageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "当前页码", example = "1")
    private Long current = 1L;

    @Schema(description = "每页数量", example = "10")
    private Long size = 10L;

    @Schema(description = "申请状态：1-待审核，2-已通过，3-已拒绝")
    private Integer applyStatus;

    @Schema(description = "资产编号（模糊查询）")
    private String assetNumber;

    @Schema(description = "资产名称（模糊查询）")
    private String assetName;

    @Schema(description = "申请人（模糊查询）")
    private String applicant;
}
