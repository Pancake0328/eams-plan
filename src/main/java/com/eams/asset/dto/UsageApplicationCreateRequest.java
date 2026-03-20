package com.eams.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 创建资产使用申请请求
 */
@Data
@Schema(description = "创建资产使用申请请求")
public class UsageApplicationCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "资产ID不能为空")
    @Schema(description = "资产ID", example = "1")
    private Long assetId;

    @NotBlank(message = "申请原因不能为空")
    @Size(max = 500, message = "申请原因长度不能超过500个字符")
    @Schema(description = "申请原因", example = "项目开发临时使用")
    private String applyReason;
}
