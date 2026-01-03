package com.eams.finance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.finance.dto.PurchaseCreateRequest;
import com.eams.finance.dto.PurchasePageQuery;
import com.eams.finance.vo.PurchaseVO;

/**
 * 资产采购服务
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
public interface AssetPurchaseService {

    /**
     * 创建采购记录
     *
     * @param request 创建请求
     * @return 采购记录ID
     */
    Long createPurchase(PurchaseCreateRequest request);

    /**
     * 获取采购记录详情
     *
     * @param id 采购记录ID
     * @return 采购记录详情
     */
    PurchaseVO getPurchaseById(Long id);

    /**
     * 分页查询采购记录
     *
     * @param query 查询条件
     * @return 分页结果
     */
    Page<PurchaseVO> getPurchasePage(PurchasePageQuery query);

    /**
     * 审批采购记录
     *
     * @param id       采购记录ID
     * @param approved 是否通过
     * @param approver 审批人
     */
    void approvePurchase(Long id, Boolean approved, String approver);

    /**
     * 删除采购记录
     *
     * @param id 采购记录ID
     */
    void deletePurchase(Long id);
}
