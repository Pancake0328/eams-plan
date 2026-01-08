package com.eams.purchase.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PurchaseVO {
    private Long id;
    private String purchaseNumber;
    private LocalDate purchaseDate;
    private String supplier;
    private BigDecimal totalAmount;
    private Integer purchaseStatus;
    private String purchaseStatusText;
    private String applicant;
    private String approver;
    private LocalDateTime approveTime;
    private String remark;
    private LocalDateTime createTime;
    private List<PurchaseDetailVO> details;

    @Data
    public static class PurchaseDetailVO {
        private Long id;
        private Long purchaseId;
        private String assetName;
        private Long categoryId;
        private String categoryName;
        private String specifications;
        private String manufacturer;
        private BigDecimal unitPrice;
        private Integer quantity;
        private Integer inboundQuantity;
        private Integer remainingQuantity;
        private BigDecimal totalAmount;
        private Integer expectedLife;
        private String remark;
        private Integer detailStatus;
        private String detailStatusText;
    }
}
