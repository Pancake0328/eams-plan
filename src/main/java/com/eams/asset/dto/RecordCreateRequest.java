package com.eams.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 创建流转记录请求 DTO
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
@Schema(description = "创建流转记录请求")
public class RecordCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 资产ID
     */
    @NotNull(message = "资产ID不能为空")
    @Schema(description = "资产ID", example = "1")
    private Long assetId;

    /**
     * 目标部门ID
     */
    @Schema(description = "目标部门ID")
    private Long toDepartmentId;

    /**
     * 目标责任人
     */
    @Size(max = 50, message = "责任人长度不能超过50个字符")
    @Schema(description = "目标责任人")
    private String toCustodian;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
