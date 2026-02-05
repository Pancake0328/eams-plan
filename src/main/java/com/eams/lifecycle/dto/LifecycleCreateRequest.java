package com.eams.lifecycle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 生命周期记录创建请求
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
public class LifecycleCreateRequest {

    /**
     * 资产ID
     */
    @NotNull(message = "资产ID不能为空")
    private Long assetId;

    /**
     * 生命周期阶段：1-购入 2-使用中 3-维修中 4-闲置 5-报废 6-取消采购
     */
    @NotNull(message = "生命周期阶段不能为空")
    private Integer stage;

    /**
     * 阶段变更日期
     */
    @NotNull(message = "阶段变更日期不能为空")
    private LocalDate stageDate;

    /**
     * 变更原因
     */
    @NotBlank(message = "变更原因不能为空")
    private String reason;

    /**
     * 操作人
     */
    @NotBlank(message = "操作人不能为空")
    private String operator;

    /**
     * 备注
     */
    private String remark;
}
