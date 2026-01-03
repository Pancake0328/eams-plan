package com.eams.system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 资产分配请求DTO
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
public class AssetAssignRequest {

    /**
     * 资产ID
     */
    @NotNull(message = "资产ID不能为空")
    private Long assetId;

    /**
     * 目标员工ID
     */
    @NotNull(message = "目标员工ID不能为空")
    private Long toEmpId;

    /**
     * 目标部门ID
     */
    private Long toDeptId;

    /**
     * 备注
     */
    private String remark;
}
