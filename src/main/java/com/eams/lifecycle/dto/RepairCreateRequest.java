package com.eams.lifecycle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 报修创建请求
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Data
public class RepairCreateRequest {

    /**
     * 资产ID
     */
    @NotNull(message = "资产ID不能为空")
    private Long assetId;

    /**
     * 故障描述
     */
    @NotBlank(message = "故障描述不能为空")
    private String faultDescription;

    /**
     * 报修类型：1-日常维修 2-故障维修 3-预防性维修
     */
    @NotNull(message = "报修类型不能为空")
    private Integer repairType;

    /**
     * 优先级：1-紧急 2-普通 3-低
     */
    @NotNull(message = "优先级不能为空")
    private Integer repairPriority;

    /**
     * 报修人
     */
    @NotBlank(message = "报修人不能为空")
    private String reporter;

    /**
     * 备注
     */
    private String remark;
}
