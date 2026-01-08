package com.eams.purchase.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 批量入库请求
 */
@Data
public class BatchInboundRequest {

    @NotEmpty(message = "入库明细列表不能为空")
    @Valid
    private List<AssetInboundRequest> inboundList;
}
