package com.eams.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 资产使用申请审核请求
 */
@Data
@Schema(description = "资产使用申请审核请求")
public class UsageApplicationAuditRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "是否通过不能为空")
    @Schema(description = "是否通过：true-通过，false-拒绝", example = "true")
    private Boolean approved;

    @Size(max = 500, message = "审核备注长度不能超过500个字符")
    @Schema(description = "审核备注", example = "同意领用")
    private String auditRemark;
}
