package com.eams.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * 重置密码请求 DTO
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Data
@Schema(description = "重置密码请求")
public class ResetPasswordRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    @Schema(description = "新密码", example = "newpassword123")
    private String newPassword;
}
