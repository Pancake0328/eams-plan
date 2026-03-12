package com.eams.purchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.asset.entity.AssetCategory;
import com.eams.asset.entity.AssetInfo;
import com.eams.asset.mapper.AssetCategoryMapper;
import com.eams.asset.mapper.AssetInfoMapper;
import com.eams.asset.service.AssetNumberGenerator;
import com.eams.common.util.MybatisBatchExecutor;
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
import com.eams.purchase.vo.PurchaseBillStatisticVO;
import com.eams.purchase.vo.PurchaseFundOverviewVO;
import com.eams.purchase.vo.PurchaseFundStatisticVO;
import com.eams.purchase.vo.PurchaseVO;
import com.eams.system.entity.User;
import com.eams.system.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private final com.eams.system.mapper.DepartmentMapper departmentMapper;
    private final UserMapper userMapper;
    private final AssetNumberGenerator numberGenerator;
    private final MybatisBatchExecutor batchExecutor;

    private static final Map<Integer, String> PURCHASE_STATUS_MAP = new HashMap<>();
    private static final Map<Integer, String> DETAIL_STATUS_MAP = new HashMap<>();
    private static final int BATCH_SIZE = 500;

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
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // 根据用户名查询用户ID
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getUsername, currentUsername);
        User applicant = userMapper.selectOne(userWrapper);
        if (applicant == null) {
            throw new BusinessException("当前用户不存在");
        }

        String purchaseNumber = numberGenerator.generatePurchaseNumber();

        PurchaseOrder purchase = new PurchaseOrder();
        purchase.setPurchaseNumber(purchaseNumber);
        purchase.setPurchaseDate(request.getPurchaseDate());
        purchase.setSupplier(request.getSupplier());
        purchase.setApplicantId(applicant.getId());
        purchase.setRemark(request.getRemark());
        purchase.setPurchaseStatus(1);

        BigDecimal totalAmount = request.getDetails().stream()
                .map(d -> d.getUnitPrice().multiply(BigDecimal.valueOf(d.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        purchase.setTotalAmount(totalAmount);

        purchaseMapper.insert(purchase);

        String targetDepartmentName = getDepartmentName(applicant.getDepartmentId());
        Map<Long, AssetCategory> categoryCache = new HashMap<>();
        List<AssetInfo> assetsToCreate = new ArrayList<>();
        for (PurchaseCreateRequest.PurchaseDetailRequest detail : request.getDetails()) {
            if (detail.getCategoryId() == null) {
                throw new BusinessException("资产分类不能为空");
            }
            AssetCategory category = categoryCache.get(detail.getCategoryId());
            if (category == null) {
                category = categoryMapper.selectById(detail.getCategoryId());
                if (category == null) {
                    throw new BusinessException("资产分类不存在");
                }
                categoryCache.put(detail.getCategoryId(), category);
            }

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

            // 创建采购阶段资产信息（先不展示，入库时继续使用）
            List<String> assetNumbers = numberGenerator.generateAssetNumbers(resolveCategoryPrefix(category),
                    detail.getQuantity());
            for (String assetNumber : assetNumbers) {
                AssetInfo asset = new AssetInfo();
                asset.setAssetNumber(assetNumber);
                asset.setAssetName(detail.getAssetName());
                asset.setCategoryId(category.getId());
                asset.setPurchaseDetailId(purchaseDetail.getId());
                asset.setPurchaseAmount(detail.getUnitPrice());
                asset.setPurchaseDate(request.getPurchaseDate());
                asset.setSpecifications(detail.getSpecifications());
                asset.setManufacturer(detail.getManufacturer());
                asset.setRemark(detail.getRemark());
                asset.setCustodian(currentUsername);
                asset.setDepartmentId(applicant.getDepartmentId());
                asset.setAssetStatus(0);
                assetsToCreate.add(asset);
            }
        }

        batchExecutor.execute(assetsToCreate, BATCH_SIZE, assetInfoMapper::insertBatch);

        Map<String, Long> assetIdMap = loadAssetIdsByNumbers(assetsToCreate);
        List<AssetLifecycle> lifecycleList = new ArrayList<>(assetsToCreate.size());
        for (AssetInfo asset : assetsToCreate) {
            Long assetId = assetIdMap.get(asset.getAssetNumber());
            if (assetId == null) {
                throw new BusinessException("采购资产创建失败，请重试");
            }
            AssetLifecycle lifecycle = new AssetLifecycle();
            lifecycle.setAssetId(assetId);
            lifecycle.setStage(1);
            lifecycle.setStageDate(request.getPurchaseDate());
            lifecycle.setReason("采购创建");
            lifecycle.setOperator(currentUsername);
            lifecycle.setRemark("采购单创建生成");
            lifecycle.setFromDepartmentId(null);
            lifecycle.setFromDepartment(null);
            lifecycle.setFromCustodian(null);
            lifecycle.setToDepartmentId(applicant.getDepartmentId());
            lifecycle.setToDepartment(targetDepartmentName);
            lifecycle.setToCustodian(currentUsername);
            lifecycleList.add(lifecycle);
        }
        batchExecutor.execute(lifecycleList, BATCH_SIZE, lifecycleMapper::insertBatch);

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        PurchaseOrder purchase = purchaseMapper.selectById(id);
        if (purchase == null) {
            return;
        }

        purchase.setPurchaseStatus(4);
        purchaseMapper.updateById(purchase);

        List<PurchaseOrderDetail> details = detailMapper.selectList(
                new LambdaQueryWrapper<PurchaseOrderDetail>()
                        .eq(PurchaseOrderDetail::getPurchaseId, id));
        if (details.isEmpty()) {
            return;
        }

        List<Long> detailIds = details.stream().map(PurchaseOrderDetail::getId).collect(Collectors.toList());
        List<AssetInfo> assets = assetInfoMapper.selectList(new LambdaQueryWrapper<AssetInfo>()
                .in(AssetInfo::getPurchaseDetailId, detailIds)
                .eq(AssetInfo::getAssetStatus, 0));
        if (assets.isEmpty()) {
            return;
        }

        List<Long> assetIds = assets.stream().map(AssetInfo::getId).collect(Collectors.toList());
        Map<Long, Integer> latestStageMap = getLatestLifecycleStageMap(assetIds);
        Map<Long, String> departmentNameCache = new HashMap<>();
        List<AssetLifecycle> lifecycleList = new ArrayList<>(assets.size());
        for (AssetInfo asset : assets) {
            Long departmentId = asset.getDepartmentId();
            String departmentName = departmentNameCache.computeIfAbsent(departmentId, this::getDepartmentName);

            AssetLifecycle lifecycle = new AssetLifecycle();
            lifecycle.setAssetId(asset.getId());
            lifecycle.setStage(6);
            lifecycle.setStageDate(LocalDate.now());
            lifecycle.setReason("取消采购");
            lifecycle.setOperator(currentUsername);
            lifecycle.setRemark("采购单取消");
            lifecycle.setPreviousStage(latestStageMap.get(asset.getId()));
            lifecycle.setFromDepartmentId(departmentId);
            lifecycle.setFromDepartment(departmentName);
            lifecycle.setFromCustodian(asset.getCustodian());
            lifecycle.setToDepartmentId(departmentId);
            lifecycle.setToDepartment(departmentName);
            lifecycle.setToCustodian(asset.getCustodian());
            lifecycleList.add(lifecycle);
        }

        batchExecutor.execute(lifecycleList, BATCH_SIZE, lifecycleMapper::insertBatch);
        batchExecutor.execute(assetIds, BATCH_SIZE, ids -> assetInfoMapper.updateAssetStatusByIds(ids, 6));
    }

    @Override
    public Page<PurchaseVO.PurchaseDetailVO> getPendingInboundDetails(int current, int size) {
        // 先查询未取消的采购单ID列表（排除status=4）
        LambdaQueryWrapper<PurchaseOrder> purchaseWrapper = new LambdaQueryWrapper<>();
        purchaseWrapper.ne(PurchaseOrder::getPurchaseStatus, 4); // 排除已取消
        List<PurchaseOrder> validPurchases = purchaseMapper.selectList(purchaseWrapper);

        if (validPurchases.isEmpty()) {
            return new Page<>(current, size, 0);
        }

        List<Long> validPurchaseIds = validPurchases.stream()
                .map(PurchaseOrder::getId)
                .collect(Collectors.toList());

        // 查询这些有效采购单中待入库的明细
        Page<PurchaseOrderDetail> page = new Page<>(current, size);
        LambdaQueryWrapper<PurchaseOrderDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(PurchaseOrderDetail::getPurchaseId, validPurchaseIds); // 只查询有效采购单的明细
        wrapper.eq(PurchaseOrderDetail::getDetailStatus, 1); // 待入库状态
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
        String currentUsername = getCurrentUsername();
        User currentUser = getCurrentUser(currentUsername);
        return inboundAsset(request, currentUser, currentUsername, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> batchInbound(BatchInboundRequest request) {
        String currentUsername = getCurrentUsername();
        User currentUser = getCurrentUser(currentUsername);
        List<Long> allAssetIds = new ArrayList<>();
        Set<Long> purchaseIdsToRefresh = new HashSet<>();

        for (AssetInboundRequest inboundRequest : request.getInboundList()) {
            List<Long> assetIds = inboundAsset(inboundRequest, currentUser, currentUsername, purchaseIdsToRefresh);
            allAssetIds.addAll(assetIds);
        }
        for (Long purchaseId : purchaseIdsToRefresh) {
            updatePurchaseStatus(purchaseId);
        }

        return allAssetIds;
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    private User getCurrentUser(String currentUsername) {
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getUsername, currentUsername);
        User currentUser = userMapper.selectOne(userWrapper);
        if (currentUser == null) {
            throw new BusinessException("当前用户不存在");
        }
        return currentUser;
    }

    private List<Long> inboundAsset(AssetInboundRequest request, User currentUser, String currentUsername,
            Set<Long> purchaseIdsToRefresh) {
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

        LambdaQueryWrapper<AssetInfo> assetWrapper = new LambdaQueryWrapper<>();
        assetWrapper.eq(AssetInfo::getPurchaseDetailId, detail.getId())
                .eq(AssetInfo::getAssetStatus, 0)
                .orderByAsc(AssetInfo::getId)
                .last("LIMIT " + request.getQuantity());
        List<AssetInfo> assets = assetInfoMapper.selectList(assetWrapper);
        if (assets.size() < request.getQuantity()) {
            throw new BusinessException("可入库资产不足");
        }

        List<Long> assetIds = assets.stream().map(AssetInfo::getId).collect(Collectors.toList());
        Map<Long, Integer> latestStageMap = getLatestLifecycleStageMap(assetIds);
        String toDepartmentName = getDepartmentName(currentUser.getDepartmentId());
        Map<Long, String> departmentNameCache = new HashMap<>();
        List<AssetLifecycle> lifecycleList = new ArrayList<>(assets.size());

        for (AssetInfo asset : assets) {
            Long fromDepartmentId = asset.getDepartmentId();
            String fromCustodian = asset.getCustodian();

            asset.setDepartmentId(currentUser.getDepartmentId());
            asset.setCustodian(currentUser.getUsername());
            asset.setAssetStatus(1);
            asset.setRemark(request.getRemark());

            AssetLifecycle lifecycle = new AssetLifecycle();
            lifecycle.setAssetId(asset.getId());
            lifecycle.setStage(4);
            lifecycle.setStageDate(LocalDate.now());
            lifecycle.setReason("采购入库");
            lifecycle.setOperator(currentUsername);
            lifecycle.setRemark("从采购单入库：" + detail.getAssetName());
            lifecycle.setPreviousStage(latestStageMap.get(asset.getId()));
            lifecycle.setFromDepartmentId(fromDepartmentId);
            lifecycle.setFromDepartment(departmentNameCache.computeIfAbsent(fromDepartmentId, this::getDepartmentName));
            lifecycle.setFromCustodian(fromCustodian);
            lifecycle.setToDepartmentId(currentUser.getDepartmentId());
            lifecycle.setToDepartment(toDepartmentName);
            lifecycle.setToCustodian(currentUser.getUsername());
            lifecycleList.add(lifecycle);
        }

        batchExecutor.execute(assetIds, BATCH_SIZE, ids -> assetInfoMapper.inboundBatchUpdate(ids,
                currentUser.getDepartmentId(), currentUser.getUsername(), request.getRemark()));
        batchExecutor.execute(lifecycleList, BATCH_SIZE, lifecycleMapper::insertBatch);

        detail.setInboundQuantity(detail.getInboundQuantity() + request.getQuantity());
        if (detail.getInboundQuantity().equals(detail.getQuantity())) {
            detail.setDetailStatus(2);
        }
        detailMapper.updateById(detail);

        if (purchaseIdsToRefresh != null) {
            purchaseIdsToRefresh.add(detail.getPurchaseId());
        } else {
            updatePurchaseStatus(detail.getPurchaseId());
        }
        return assetIds;
    }

    private Map<String, Long> loadAssetIdsByNumbers(List<AssetInfo> assets) {
        Map<String, Long> assetIdMap = new HashMap<>();
        if (assets == null || assets.isEmpty()) {
            return assetIdMap;
        }

        List<String> assetNumbers = assets.stream().map(AssetInfo::getAssetNumber).collect(Collectors.toList());
        for (int i = 0; i < assetNumbers.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, assetNumbers.size());
            List<AssetInfo> persistedAssets = assetInfoMapper.selectByAssetNumbers(assetNumbers.subList(i, end));
            for (AssetInfo persistedAsset : persistedAssets) {
                assetIdMap.put(persistedAsset.getAssetNumber(), persistedAsset.getId());
            }
        }
        return assetIdMap;
    }

    private Map<Long, Integer> getLatestLifecycleStageMap(List<Long> assetIds) {
        Map<Long, Integer> stageMap = new HashMap<>();
        if (assetIds == null || assetIds.isEmpty()) {
            return stageMap;
        }

        for (int i = 0; i < assetIds.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, assetIds.size());
            List<AssetLifecycle> latestStages = lifecycleMapper.selectLatestStagesByAssetIds(assetIds.subList(i, end));
            for (AssetLifecycle latestStage : latestStages) {
                stageMap.put(latestStage.getAssetId(), latestStage.getStage());
            }
        }
        return stageMap;
    }

    @Override
    public PurchaseBillStatisticVO getMonthlyBillStatistic(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        PurchaseFundOverviewVO overview = normalizeOverview(purchaseMapper.selectSummaryByDateRange(startDate, endDate));
        return buildBillStatistic(overview, yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")), "月度");
    }

    @Override
    public PurchaseBillStatisticVO getAnnualBillStatistic(int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        PurchaseFundOverviewVO overview = normalizeOverview(purchaseMapper.selectSummaryByDateRange(startDate, endDate));
        return buildBillStatistic(overview, String.valueOf(year), "年度");
    }

    @Override
    public PurchaseFundOverviewVO getFundOverview() {
        return normalizeOverview(purchaseMapper.selectTotalSummary());
    }

    @Override
    public List<PurchaseFundStatisticVO> getFundStatisticsBySupplier() {
        return purchaseMapper.selectSupplierStatistics();
    }

    @Override
    public List<PurchaseFundStatisticVO> getFundStatisticsByTime(LocalDate startDate, LocalDate endDate) {
        return purchaseMapper.selectTimeStatistics(startDate, endDate);
    }

    private PurchaseFundOverviewVO normalizeOverview(PurchaseFundOverviewVO overview) {
        if (overview == null) {
            overview = new PurchaseFundOverviewVO();
            overview.setOrderCount(0L);
            overview.setTotalAmount(BigDecimal.ZERO);
        }
        if (overview.getOrderCount() == null) {
            overview.setOrderCount(0L);
        }
        if (overview.getTotalAmount() == null) {
            overview.setTotalAmount(BigDecimal.ZERO);
        }
        if (overview.getOrderCount() > 0) {
            overview.setAverageAmount(overview.getTotalAmount().divide(BigDecimal.valueOf(overview.getOrderCount()), 2, RoundingMode.HALF_UP));
        } else {
            overview.setAverageAmount(BigDecimal.ZERO);
        }
        return overview;
    }

    private PurchaseBillStatisticVO buildBillStatistic(PurchaseFundOverviewVO overview, String period, String type) {
        PurchaseBillStatisticVO stat = new PurchaseBillStatisticVO();
        stat.setBillPeriod(period);
        stat.setBillType(type);
        stat.setOrderCount(overview.getOrderCount());
        stat.setTotalAmount(overview.getTotalAmount());
        stat.setAverageAmount(overview.getAverageAmount());
        return stat;
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
                vo.setApplicantName(StringUtils.hasText(applicant.getNickname())
                        ? applicant.getNickname()
                        : applicant.getUsername());
            }
        }

        return vo;
    }

    private String resolveCategoryPrefix(AssetCategory category) {
        if (category == null) {
            return null;
        }
        return StringUtils.hasText(category.getCategoryCode()) ? category.getCategoryCode() : null;
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

    private String getDepartmentName(Long departmentId) {
        if (departmentId == null) {
            return null;
        }
        com.eams.system.entity.Department department = departmentMapper.selectById(departmentId);
        return department != null ? department.getDeptName() : null;
    }

}
