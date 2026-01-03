package com.eams.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.asset.entity.AssetInfo;
import com.eams.asset.mapper.AssetInfoMapper;
import com.eams.common.exception.BusinessException;
import com.eams.finance.dto.PurchaseCreateRequest;
import com.eams.finance.dto.PurchasePageQuery;
import com.eams.finance.entity.AssetPurchase;
import com.eams.finance.mapper.AssetPurchaseMapper;
import com.eams.finance.service.AssetPurchaseService;
import com.eams.finance.vo.PurchaseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 资产采购服务实现
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Service
@RequiredArgsConstructor
public class AssetPurchaseServiceImpl implements AssetPurchaseService {

    private final AssetPurchaseMapper purchaseMapper;
    private final AssetInfoMapper assetInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPurchase(PurchaseCreateRequest request) {
        // 检查资产是否存在
        AssetInfo asset = assetInfoMapper.selectById(request.getAssetId());
        if (asset == null) {
            throw new BusinessException("资产不存在");
        }

        AssetPurchase purchase = new AssetPurchase();
        BeanUtils.copyProperties(request, purchase);
        purchase.setApprovalStatus(0); // 待审批
        // purchaser可从上下文获取当前用户
        purchase.setPurchaser("system");

        purchaseMapper.insert(purchase);
        return purchase.getId();
    }

    @Override
    public PurchaseVO getPurchaseById(Long id) {
        AssetPurchase purchase = purchaseMapper.selectById(id);
        if (purchase == null) {
            throw new BusinessException("采购记录不存在");
        }

        return convertToVO(purchase);
    }

    @Override
    public Page<PurchaseVO> getPurchasePage(PurchasePageQuery query) {
        Page<AssetPurchase> page = new Page<>(query.getCurrent(), query.getSize());

        LambdaQueryWrapper<AssetPurchase> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getAssetId() != null, AssetPurchase::getAssetId, query.getAssetId());
        wrapper.like(query.getSupplierName() != null, AssetPurchase::getSupplierName, query.getSupplierName());
        wrapper.eq(query.getInvoiceNumber() != null, AssetPurchase::getInvoiceNumber, query.getInvoiceNumber());
        wrapper.eq(query.getApprovalStatus() != null, AssetPurchase::getApprovalStatus, query.getApprovalStatus());
        wrapper.orderByDesc(AssetPurchase::getPurchaseDate);

        Page<AssetPurchase> purchasePage = purchaseMapper.selectPage(page, wrapper);

        Page<PurchaseVO> voPage = new Page<>();
        BeanUtils.copyProperties(purchasePage, voPage, "records");
        voPage.setRecords(purchasePage.getRecords().stream()
                .map(this::convertToVO)
                .toList());

        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approvePurchase(Long id, Boolean approved, String approver) {
        AssetPurchase purchase = purchaseMapper.selectById(id);
        if (purchase == null) {
            throw new BusinessException("采购记录不存在");
        }

        purchase.setApprovalStatus(approved ? 1 : 2); // 1-已审批，2-已拒绝
        purchase.setApprover(approver);
        purchase.setApprovalTime(LocalDateTime.now());

        purchaseMapper.updateById(purchase);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePurchase(Long id) {
        AssetPurchase purchase = purchaseMapper.selectById(id);
        if (purchase == null) {
            throw new BusinessException("采购记录不存在");
        }

        purchaseMapper.deleteById(id);
    }

    /**
     * 转换为VO
     */
    private PurchaseVO convertToVO(AssetPurchase purchase) {
        PurchaseVO vo = new PurchaseVO();
        BeanUtils.copyProperties(purchase, vo);

        // 查询资产信息
        AssetInfo asset = assetInfoMapper.selectById(purchase.getAssetId());
        if (asset != null) {
            vo.setAssetNumber(asset.getAssetNumber());
            vo.setAssetName(asset.getAssetName());
        }

        // 设置审批状态文本
        String statusText = switch (purchase.getApprovalStatus()) {
            case 0 -> "待审批";
            case 1 -> "已审批";
            case 2 -> "已拒绝";
            default -> "未知";
        };
        vo.setApprovalStatusText(statusText);

        return vo;
    }
}
