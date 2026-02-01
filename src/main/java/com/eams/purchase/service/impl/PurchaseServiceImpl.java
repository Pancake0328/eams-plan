package com.eams.purchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.asset.entity.AssetCategory;
import com.eams.asset.entity.AssetInfo;
import com.eams.asset.mapper.AssetCategoryMapper;
import com.eams.asset.mapper.AssetInfoMapper;
import com.eams.asset.service.AssetNumberGenerator;
import com.eams.exception.BusinessException;
import com.eams.lifecycle.entity.AssetLifecycle;
import com.eams.lifecycle.mapper.AssetLifecycleMapper;
import com.eams.purchase.dto.AssetInboundRequest;
import com.eams.purchase.dto.BatchInboundRequest;
import com.eams.purchase.dto.PurchaseCreateRequest;
import com.eams.purchase.entity.PurchaseOrder;
import com.eams.purchase.entity.PurchaseOrderDetail;
import com.eams.purchase.mapper.PurchaseOrderDetailMapper;
import com.eams.purchase.mapper.PurchaseOrderMapper;
import com.eams.purchase.service.PurchaseService;
import com.eams.purchase.vo.PurchaseVO;
import com.eams.system.entity.User;
import com.eams.system.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseOrderMapper purchaseMapper;
    private final PurchaseOrderDetailMapper detailMapper;
    private final AssetInfoMapper assetInfoMapper;
    private final AssetCategoryMapper categoryMapper;
    private final AssetLifecycleMapper lifecycleMapper;
    private final UserMapper userMapper;
    private final AssetNumberGenerator numberGenerator;

    private static final Map<Integer, String> PURCHASE_STATUS_MAP = new HashMap<>();
    private static final Map<Integer, String> DETAIL_STATUS_MAP = new HashMap<>();

    static {
        PURCHASE_STATUS_MAP.put(1, "待入库");
        PURCHASE_STATUS_MAP.put(2, "部分入库");
        PURCHASE_STATUS_MAP.put(3, "已入库");
        PURCHASE_STATUS_MAP.put(4, "已取消");

        DETAIL_STATUS_MAP.put(1, "待入库");
        DETAIL_STATUS_MAP.put(2, "已入库");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPurchase(PurchaseCreateRequest request) {
        // 验证申请人用户是否存在
        User applicant = userMapper.selectById(request.getApplicantId());
        if (applicant == null) {
            throw new BusinessException("申请人用户不存在");
        }

        String purchaseNumber = numberGenerator.generatePurchaseNumber();

        PurchaseOrder purchase = new PurchaseOrder();
        purchase.setPurchaseNumber(purchaseNumber);
        purchase.setPurchaseDate(request.getPurchaseDate());
        purchase.setSupplier(request.getSupplier());
        purchase.setApplicantId(request.getApplicantId());
        purchase.setRemark(request.getRemark());
        purchase.setPurchaseStatus(1);

        BigDecimal totalAmount = request.getDetails().stream()
                .map(d -> d.getUnitPrice().multiply(BigDecimal.valueOf(d.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        purchase.setTotalAmount(totalAmount);

        purchaseMapper.insert(purchase);

        // TODO: 后续入库时创建生命周期记录
        // 当前采购明细没有对应的资产，只有在入库时才创建资产和生命周期

        for (PurchaseCreateRequest.PurchaseDetailRequest detail : request.getDetails()) {
            PurchaseOrderDetail purchaseDetail = new PurchaseOrderDetail();
            purchaseDetail.setPurchaseId(purchase.getId());
            purchaseDetail.setAssetName(detail.getAssetName());
            purchaseDetail.setCategoryId(detail.getCategoryId());
            purchaseDetail.setSpecifications(emptyToNull(detail.getSpecifications()));
            purchaseDetail.setManufacturer(emptyToNull(detail.getManufacturer()));
            purchaseDetail.setUnitPrice(detail.getUnitPrice());
            purchaseDetail.setQuantity(detail.getQuantity());
            purchaseDetail.setInboundQuantity(0);
            purchaseDetail.setTotalAmount(detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity())));
            purchaseDetail.setExpectedLife(detail.getExpectedLife());
            purchaseDetail.setRemark(emptyToNull(detail.getRemark()));
            purchaseDetail.setDetailStatus(1);

            detailMapper.insert(purchaseDetail);
        }

        log.info("创建采购成功，采购单号: {}, 申请人: {}", purchaseNumber, applicant.getUsername());
        return purchase.getId();
    }

    /**
     * 将空字符串转换为null
     */
    private String emptyToNull(String str) {
        return (str == null || str.trim().isEmpty()) ? null : str;
    }

    @Override
    public PurchaseVO getPurchaseById(Long id) {
        PurchaseOrder purchase = purchaseMapper.selectById(id);
        if (purchase == null) {
            return null;
        }

        PurchaseVO vo = convertToVO(purchase);

        LambdaQueryWrapper<PurchaseOrderDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurchaseOrderDetail::getPurchaseId, id);
        List<PurchaseOrderDetail> details = detailMapper.selectList(wrapper);

        vo.setDetails(details.stream().map(this::convertDetailToVO).collect(Collectors.toList()));

        return vo;
    }

    @Override
    public Page<PurchaseVO> getPurchasePage(int current, int size, String purchaseNumber, Integer status) {
        Page<PurchaseOrder> page = new Page<>(current, size);
        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();

        if (purchaseNumber != null && !purchaseNumber.isEmpty()) {
            wrapper.like(PurchaseOrder::getPurchaseNumber, purchaseNumber);
        }
        if (status != null) {
            wrapper.eq(PurchaseOrder::getPurchaseStatus, status);
        }

        wrapper.orderByDesc(PurchaseOrder::getCreateTime);

        Page<PurchaseOrder> purchasePage = purchaseMapper.selectPage(page, wrapper);
        Page<PurchaseVO> voPage = new Page<>(purchasePage.getCurrent(), purchasePage.getSize(),
                purchasePage.getTotal());
        voPage.setRecords(purchasePage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelPurchase(Long id) {
        PurchaseOrder purchase = purchaseMapper.selectById(id);
        if (purchase != null) {
            purchase.setPurchaseStatus(4);
            purchaseMapper.updateById(purchase);
        }
    }

    @Override
    public Page<PurchaseVO.PurchaseDetailVO> getPendingInboundDetails(int current, int size) {
        Page<PurchaseOrderDetail> page = new Page<>(current, size);
        LambdaQueryWrapper<PurchaseOrderDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurchaseOrderDetail::getDetailStatus, 1);
        wrapper.orderByDesc(PurchaseOrderDetail::getCreateTime);

        Page<PurchaseOrderDetail> detailPage = detailMapper.selectPage(page, wrapper);
        Page<PurchaseVO.PurchaseDetailVO> voPage = new Page<>(detailPage.getCurrent(), detailPage.getSize(),
                detailPage.getTotal());
        voPage.setRecords(detailPage.getRecords().stream()
                .map(this::convertDetailToVO)
                .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> inboundAsset(AssetInboundRequest request) {
        PurchaseOrderDetail detail = detailMapper.selectById(request.getDetailId());
        if (detail == null) {
            throw new RuntimeException("采购明细不存在");
        }

        int remainingQuantity = detail.getQuantity() - detail.getInboundQuantity();
        if (remainingQuantity <= 0) {
            throw new RuntimeException("该明细已全部入库");
        }

        if (request.getQuantity() > remainingQuantity) {
            throw new RuntimeException("入库数量超过剩余可入库数量");
        }

        List<Long> assetIds = new ArrayList<>();

        // 循环创建多个资产
        for (int i = 0; i < request.getQuantity(); i++) {
            AssetInfo asset = new AssetInfo();
            asset.setAssetNumber(numberGenerator.generateAssetNumber());
            asset.setAssetName(detail.getAssetName());
            asset.setCategoryId(detail.getCategoryId());
            asset.setPurchaseAmount(detail.getUnitPrice());
            asset.setPurchaseDate(LocalDateTime.now().toLocalDate());
            asset.setDepartment(request.getDepartment());
            asset.setCustodian(request.getCustodian());
            asset.setSpecifications(detail.getSpecifications());
            asset.setManufacturer(detail.getManufacturer());
            asset.setAssetStatus(1);
            asset.setRemark(request.getRemark());

            assetInfoMapper.insert(asset);
            assetIds.add(asset.getId());

            // 创建生命周期记录
            AssetLifecycle lifecycle = new AssetLifecycle();
            lifecycle.setAssetId(asset.getId());
            lifecycle.setStage(0);
            lifecycle.setStageDate(LocalDateTime.now().toLocalDate());
            lifecycle.setOperator("system");
            lifecycle.setRemark("从采购单入库：" + detail.getAssetName());

            lifecycleMapper.insert(lifecycle);
        }

        // 更新采购明细入库数量
        detail.setInboundQuantity(detail.getInboundQuantity() + request.getQuantity());
        if (detail.getInboundQuantity().equals(detail.getQuantity())) {
            detail.setDetailStatus(2);
        }
        detailMapper.updateById(detail);

        // 更新采购单状态
        updatePurchaseStatus(detail.getPurchaseId());

        return assetIds;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> batchInbound(BatchInboundRequest request) {
        List<Long> allAssetIds = new ArrayList<>();

        for (AssetInboundRequest inboundRequest : request.getInboundList()) {
            List<Long> assetIds = inboundAsset(inboundRequest);
            allAssetIds.addAll(assetIds);
        }

        return allAssetIds;
    }

    private void updatePurchaseStatus(Long purchaseId) {
        LambdaQueryWrapper<PurchaseOrderDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurchaseOrderDetail::getPurchaseId, purchaseId);
        List<PurchaseOrderDetail> details = detailMapper.selectList(wrapper);

        boolean allInbound = details.stream().allMatch(d -> d.getDetailStatus() == 2);
        boolean anyInbound = details.stream().anyMatch(d -> d.getInboundQuantity() > 0);

        PurchaseOrder purchase = purchaseMapper.selectById(purchaseId);
        if (allInbound) {
            purchase.setPurchaseStatus(3);
        } else if (anyInbound) {
            purchase.setPurchaseStatus(2);
        }
        purchaseMapper.updateById(purchase);
    }

    private PurchaseVO convertToVO(PurchaseOrder purchase) {
        PurchaseVO vo = new PurchaseVO();
        BeanUtils.copyProperties(purchase, vo);
        vo.setPurchaseStatusText(PURCHASE_STATUS_MAP.get(purchase.getPurchaseStatus()));

        // 查询申请人姓名
        if (purchase.getApplicantId() != null) {
            User applicant = userMapper.selectById(purchase.getApplicantId());
            if (applicant != null) {
                vo.setApplicantName(applicant.getUsername());
            }
        }

        return vo;
    }

    private PurchaseVO.PurchaseDetailVO convertDetailToVO(PurchaseOrderDetail detail) {
        PurchaseVO.PurchaseDetailVO vo = new PurchaseVO.PurchaseDetailVO();
        BeanUtils.copyProperties(detail, vo);
        vo.setRemainingQuantity(detail.getQuantity() - detail.getInboundQuantity());
        vo.setDetailStatusText(DETAIL_STATUS_MAP.get(detail.getDetailStatus()));

        if (detail.getCategoryId() != null) {
            AssetCategory category = categoryMapper.selectById(detail.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getCategoryName());
            }
        }

        return vo;
    }
}
