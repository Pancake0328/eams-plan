package com.eams.purchase.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssetInboundRequest {

    @NotNull(message = "采购明细ID不能为空")
    private Long detailId;

    private String storageLocation;
    private String department;
    private String custodian;
    private String remark;
}
