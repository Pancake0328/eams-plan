package com.eams.system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 分配权限请求DTO
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
public class AssignPermissionRequest {

    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    @NotNull(message = "菜单ID列表不能为空")
    private List<Long> menuIds;
}
