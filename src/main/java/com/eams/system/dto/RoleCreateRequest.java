package com.eams.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 角色创建请求DTO
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
public class RoleCreateRequest {

    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    private Integer status = 1;

    private String remark;
}
