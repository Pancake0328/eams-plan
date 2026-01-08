package com.eams.purchase.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 资产入库请求
 */
@Data
public class AssetInboundRequest {

    @NotNull(message = "采购明细ID不能为空")
    private Long detailId;

    @NotNull(message = "入库数量不能为空")
    @Min(value = 1, message = "入库数量至少为1")
    private Integer quantity;

    private String storageLocation;
    private String department;
    private String custodian;
    private String remark;
}
