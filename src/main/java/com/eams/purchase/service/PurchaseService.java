package com.eams.purchase.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.purchase.dto.AssetInboundRequest;
import com.eams.purchase.dto.PurchaseCreateRequest;
import com.eams.purchase.vo.PurchaseVO;

public interface PurchaseService {

    Long createPurchase(PurchaseCreateRequest request);

    PurchaseVO getPurchaseById(Long id);

    Page<PurchaseVO> getPurchasePage(int current, int size, String purchaseNumber, Integer status);

    void cancelPurchase(Long id);

    Page<PurchaseVO.PurchaseDetailVO> getPendingInboundDetails(int current, int size);

    Long inboundAsset(AssetInboundRequest request);
}
