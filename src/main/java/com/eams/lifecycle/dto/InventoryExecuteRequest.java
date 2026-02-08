package com.eams.lifecycle.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 盘点执行请求
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Data
public class InventoryExecuteRequest {

    /**
     * 盘点明细ID
     */
    @NotNull(message = "盘点明细ID不能为空")
    private Long detailId;

    /**
     * 实际位置
     */
    private String actualLocation;

    /**
     * 盘点结果：1-未盘点 2-正常 3-位置异常 4-状态异常 5-丢失
     */
    @NotNull(message = "盘点结果不能为空")
    private Integer inventoryResult;

    /**
     * 盘点人
     */
    private String inventoryPerson;

    /**
     * 备注
     */
    private String remark;
}
