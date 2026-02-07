package com.eams.purchase.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.purchase.dto.AssetInboundRequest;
import com.eams.purchase.dto.BatchInboundRequest;
import com.eams.purchase.dto.PurchaseCreateRequest;
import com.eams.purchase.vo.PurchaseBillStatisticVO;
import com.eams.purchase.vo.PurchaseFundOverviewVO;
import com.eams.purchase.vo.PurchaseFundStatisticVO;
import com.eams.purchase.vo.PurchaseVO;

import java.util.List;
import java.time.LocalDate;

public interface PurchaseService {

    Long createPurchase(PurchaseCreateRequest request);

    PurchaseVO getPurchaseById(Long id);

    Page<PurchaseVO> getPurchasePage(int current, int size, String purchaseNumber, Integer status);

    void cancelPurchase(Long id);

    Page<PurchaseVO.PurchaseDetailVO> getPendingInboundDetails(int current, int size);

    /**
     * 资产入库（单个明细，支持多数量）
     * 
     * @return 入库的资产ID列表
     */
    List<Long> inboundAsset(AssetInboundRequest request);

    /**
     * 批量入库（多个明细）
     * 
     * @return 入库的资产ID列表
     */
    List<Long> batchInbound(BatchInboundRequest request);

    PurchaseBillStatisticVO getMonthlyBillStatistic(int year, int month);

    PurchaseBillStatisticVO getAnnualBillStatistic(int year);

    PurchaseFundOverviewVO getFundOverview();

    List<PurchaseFundStatisticVO> getFundStatisticsBySupplier();

    List<PurchaseFundStatisticVO> getFundStatisticsByTime(LocalDate startDate, LocalDate endDate);
}
