package com.eams.purchase.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class PurchaseCreateRequest {

    @NotNull(message = "采购日期不能为空")
    private LocalDate purchaseDate;

    private String supplier;

    @NotBlank(message = "申请人不能为空")
    private String applicant;

    private String remark;

    @NotEmpty(message = "采购明细不能为空")
    private List<PurchaseDetailRequest> details;

    @Data
    public static class PurchaseDetailRequest {
        @NotBlank(message = "资产名称不能为空")
        private String assetName;
        private Long categoryId;
        private String specifications;
        private String manufacturer;

        @NotNull(message = "单价不能为空")
        private BigDecimal unitPrice;

        @NotNull(message = "数量不能为空")
        private Integer quantity;

        private Integer expectedLife;
        private String remark;
    }
}
