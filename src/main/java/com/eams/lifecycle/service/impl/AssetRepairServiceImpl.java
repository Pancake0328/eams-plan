package com.eams.lifecycle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.asset.entity.AssetInfo;
import com.eams.asset.entity.AssetRecord;
import com.eams.asset.mapper.AssetInfoMapper;
import com.eams.asset.mapper.AssetRecordMapper;
import com.eams.common.exception.BusinessException;
import com.eams.lifecycle.dto.RepairCreateRequest;
import com.eams.lifecycle.entity.AssetLifecycle;
import com.eams.lifecycle.entity.AssetRepair;
import com.eams.lifecycle.mapper.AssetLifecycleMapper;
import com.eams.lifecycle.mapper.AssetRepairMapper;
import com.eams.lifecycle.service.AssetRepairService;
import com.eams.lifecycle.vo.RepairVO;
import com.eams.system.entity.User;
import com.eams.system.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资产报修服务实现
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Service
@RequiredArgsConstructor
public class AssetRepairServiceImpl implements AssetRepairService {

    private final AssetRepairMapper repairMapper;
    private final AssetInfoMapper assetMapper;
    private final AssetRecordMapper recordMapper;
    private final AssetLifecycleMapper lifecycleMapper;
    private final com.eams.system.mapper.DepartmentMapper departmentMapper;
    private final UserMapper userMapper;

    private static final Map<Integer, String> TYPE_MAP = new HashMap<>();
    private static final Map<Integer, String> PRIORITY_MAP = new HashMap<>();
    private static final Map<Integer, String> STATUS_MAP = new HashMap<>();

    static {
        TYPE_MAP.put(1, "日常维修");
        TYPE_MAP.put(2, "故障维修");
        TYPE_MAP.put(3, "预防性维修");

        PRIORITY_MAP.put(1, "紧急");
        PRIORITY_MAP.put(2, "普通");
        PRIORITY_MAP.put(3, "低");

        STATUS_MAP.put(1, "待审批");
        STATUS_MAP.put(2, "已审批");
        STATUS_MAP.put(3, "维修中");
        STATUS_MAP.put(4, "已完成");
        STATUS_MAP.put(5, "已拒绝");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRepair(RepairCreateRequest request) {
        // 验证资产存在
        AssetInfo asset = assetMapper.selectById(request.getAssetId());
        if (asset == null) {
            throw new BusinessException("资产不存在");
        }
        Integer originalStatus = asset.getAssetStatus();
        if (originalStatus == 4) {
            throw new BusinessException("报废的资产不能报修");
        }
        if (originalStatus == 3) {
            throw new BusinessException("资产已处于维修中");
        }
        if (originalStatus != 1 && originalStatus != 2) {
            throw new BusinessException("只有闲置或使用中的资产才能报修");
        }

        // 生成报修编号
        String repairNumber = generateRepairNumber();
        String operator = getCurrentUsername();

        // 创建报修记录
        AssetRepair repair = new AssetRepair();
        BeanUtils.copyProperties(request, repair);
        repair.setRepairNumber(repairNumber);
        repair.setAssetNumber(asset.getAssetNumber());
        repair.setAssetName(asset.getAssetName());
        repair.setReportTime(LocalDateTime.now());
        repair.setRepairStatus(1); // 待审批
        repair.setRepairCost(BigDecimal.ZERO);
        repair.setReporter(operator);
        repair.setOriginalStatus(originalStatus);

        repairMapper.insert(repair);

        Long fromDepartmentId = asset.getDepartmentId();
        String fromCustodian = asset.getCustodian();

        asset.setAssetStatus(3); // 维修中
        assetMapper.updateById(asset);

        AssetRecord record = createRecord(
                asset,
                6,
                fromDepartmentId, fromDepartmentId,
                fromCustodian, fromCustodian,
                originalStatus, 3,
                request.getRemark());
        recordMapper.insert(record);

        recordLifecycle(asset.getId(), 3, "资产送修", request.getRemark(),
                fromDepartmentId, fromDepartmentId,
                fromCustodian, fromCustodian);

        return repair.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveRepair(Long repairId, Boolean approved, String approver) {
        AssetRepair repair = repairMapper.selectById(repairId);
        if (repair == null) {
            throw new BusinessException("报修记录不存在");
        }

        if (repair.getRepairStatus() != 1) {
            throw new BusinessException("只有待审批的报修才能审批");
        }

        String operator = getCurrentUsername();
        repair.setApprover(operator);
        repair.setApprovalTime(LocalDateTime.now());
        repair.setRepairStatus(approved ? 2 : 5); // 2-已审批 5-已拒绝

        repairMapper.updateById(repair);

        AssetInfo asset = assetMapper.selectById(repair.getAssetId());
        if (asset != null) {
            if (!approved) {
                Integer restoreStatus = resolveRestoreStatus(repair.getOriginalStatus());
                Integer oldStatus = asset.getAssetStatus();
                Long fromDepartmentId = asset.getDepartmentId();
                String fromCustodian = asset.getCustodian();

                asset.setAssetStatus(restoreStatus);
                assetMapper.updateById(asset);

                AssetRecord record = createRecord(
                        asset,
                        8,
                        fromDepartmentId, fromDepartmentId,
                        fromCustodian, fromCustodian,
                        oldStatus, restoreStatus,
                        "报修拒绝");
                recordMapper.insert(record);

                recordLifecycle(asset.getId(), mapStatusToStage(restoreStatus), "报修拒绝", null,
                        fromDepartmentId, fromDepartmentId,
                        fromCustodian, fromCustodian);
            } else {
                recordLifecycle(asset.getId(), 3, "报修审批通过", null,
                        asset.getDepartmentId(), asset.getDepartmentId(),
                        asset.getCustodian(), asset.getCustodian());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startRepair(Long repairId, String repairPerson) {
        AssetRepair repair = repairMapper.selectById(repairId);
        if (repair == null) {
            throw new BusinessException("报修记录不存在");
        }

        if (repair.getRepairStatus() != 2) {
            throw new BusinessException("只有已审批的报修才能开始维修");
        }

        String operator = getCurrentUsername();
        repair.setRepairPerson(operator);
        repair.setRepairStartTime(LocalDateTime.now());
        repair.setRepairStatus(3); // 维修中

        repairMapper.updateById(repair);

        AssetInfo asset = assetMapper.selectById(repair.getAssetId());
        if (asset != null) {
            asset.setAssetStatus(3);
            assetMapper.updateById(asset);
            recordLifecycle(asset.getId(), 3, "开始维修", null,
                    asset.getDepartmentId(), asset.getDepartmentId(),
                    asset.getCustodian(), asset.getCustodian());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeRepair(Long repairId, BigDecimal repairCost, String repairResult) {
        AssetRepair repair = repairMapper.selectById(repairId);
        if (repair == null) {
            throw new BusinessException("报修记录不存在");
        }

        if (repair.getRepairStatus() != 3) {
            throw new BusinessException("只有维修中的报修才能完成");
        }

        repair.setRepairEndTime(LocalDateTime.now());
        repair.setRepairCost(repairCost);
        repair.setRepairResult(repairResult);
        repair.setRepairStatus(4); // 已完成

        repairMapper.updateById(repair);

        AssetInfo asset = assetMapper.selectById(repair.getAssetId());
        if (asset != null) {
            Integer oldStatus = asset.getAssetStatus();
            Long fromDepartmentId = asset.getDepartmentId();
            String fromCustodian = asset.getCustodian();

            Integer restoreStatus = resolveRestoreStatus(repair.getOriginalStatus());
            asset.setAssetStatus(restoreStatus);
            assetMapper.updateById(asset);

            AssetRecord record = createRecord(
                    asset,
                    7,
                    fromDepartmentId, fromDepartmentId,
                    fromCustodian, fromCustodian,
                    oldStatus, restoreStatus,
                    repairResult);
            recordMapper.insert(record);

            recordLifecycle(asset.getId(), mapStatusToStage(restoreStatus), "维修完成", repairResult,
                    fromDepartmentId, fromDepartmentId,
                    fromCustodian, fromCustodian);
        }
    }

    @Override
    public RepairVO getRepairDetail(Long repairId) {
        AssetRepair repair = repairMapper.selectById(repairId);
        if (repair == null) {
            throw new BusinessException("报修记录不存在");
        }

        return convertToVO(repair);
    }

    @Override
    public Page<RepairVO> getRepairPage(Integer current, Integer size, Integer status, Long assetId) {
        Page<AssetRepair> page = new Page<>(current, size);

        LambdaQueryWrapper<AssetRepair> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(status != null, AssetRepair::getRepairStatus, status);
        wrapper.eq(assetId != null, AssetRepair::getAssetId, assetId);
        wrapper.orderByDesc(AssetRepair::getReportTime);

        Page<AssetRepair> repairPage = repairMapper.selectPage(page, wrapper);

        Page<RepairVO> voPage = new Page<>();
        BeanUtils.copyProperties(repairPage, voPage, "records");

        List<RepairVO> voList = new ArrayList<>();
        for (AssetRepair repair : repairPage.getRecords()) {
            RepairVO vo = convertToVO(repair);
            voList.add(vo);
        }
        voPage.setRecords(voList);

        return voPage;
    }

    /**
     * 生成报修编号
     */
    private String generateRepairNumber() {
        return "REP-" + LocalDate.now().toString().replace("-", "") + "-" +
                String.format("%03d", System.currentTimeMillis() % 1000);
    }

    /**
     * 转换为VO
     */
    private RepairVO convertToVO(AssetRepair repair) {
        RepairVO vo = new RepairVO();
        BeanUtils.copyProperties(repair, vo);

        vo.setRepairTypeText(TYPE_MAP.getOrDefault(repair.getRepairType(), "未知"));
        vo.setRepairPriorityText(PRIORITY_MAP.getOrDefault(repair.getRepairPriority(), "未知"));
        vo.setRepairStatusText(STATUS_MAP.getOrDefault(repair.getRepairStatus(), "未知"));
        vo.setReporterName(getUserDisplayName(repair.getReporter()));
        vo.setApproverName(getUserDisplayName(repair.getApprover()));
        vo.setRepairPersonName(getUserDisplayName(repair.getRepairPerson()));

        return vo;
    }

    private AssetRecord createRecord(AssetInfo asset, Integer recordType,
                                     Long fromDepartmentId, Long toDepartmentId,
                                     String fromCustodian, String toCustodian,
                                     Integer oldStatus, Integer newStatus,
                                     String remark) {
        AssetRecord record = new AssetRecord();
        record.setAssetId(asset.getId());
        record.setRecordType(recordType);
        record.setFromDepartmentId(fromDepartmentId);
        record.setToDepartmentId(toDepartmentId);
        record.setFromDepartment(getDepartmentName(fromDepartmentId));
        record.setToDepartment(getDepartmentName(toDepartmentId));
        record.setFromCustodian(fromCustodian);
        record.setToCustodian(toCustodian);
        record.setOldStatus(oldStatus);
        record.setNewStatus(newStatus);
        record.setRemark(remark);
        record.setOperator(getCurrentUsername());
        record.setOperateTime(LocalDateTime.now());
        return record;
    }

    private void recordLifecycle(Long assetId, Integer stage, String reason, String remark,
                                 Long fromDepartmentId, Long toDepartmentId,
                                 String fromCustodian, String toCustodian) {
        AssetLifecycle lifecycle = new AssetLifecycle();
        lifecycle.setAssetId(assetId);
        lifecycle.setStage(stage);
        lifecycle.setStageDate(LocalDate.now());
        lifecycle.setReason(reason);
        lifecycle.setOperator(getCurrentUsername());
        lifecycle.setRemark(remark);
        lifecycle.setPreviousStage(getLatestLifecycleStage(assetId));
        lifecycle.setFromDepartmentId(fromDepartmentId);
        lifecycle.setFromDepartment(getDepartmentName(fromDepartmentId));
        lifecycle.setToDepartmentId(toDepartmentId);
        lifecycle.setToDepartment(getDepartmentName(toDepartmentId));
        lifecycle.setFromCustodian(fromCustodian);
        lifecycle.setToCustodian(toCustodian);
        lifecycleMapper.insert(lifecycle);
    }

    private String getUserDisplayName(String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username));
        if (user == null) {
            return username;
        }
        return StringUtils.hasText(user.getNickname()) ? user.getNickname() : user.getUsername();
    }

    private Integer resolveRestoreStatus(Integer originalStatus) {
        if (originalStatus == null) {
            return 1;
        }
        return originalStatus;
    }

    private Integer mapStatusToStage(Integer status) {
        if (status == null) {
            return null;
        }
        switch (status) {
            case 0:
                return 1;
            case 1:
                return 4;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 5;
            case 6:
                return 6;
            default:
                return null;
        }
    }

    private Integer getLatestLifecycleStage(Long assetId) {
        AssetLifecycle current = lifecycleMapper.selectOne(new LambdaQueryWrapper<AssetLifecycle>()
                .eq(AssetLifecycle::getAssetId, assetId)
                .orderByDesc(AssetLifecycle::getCreateTime)
                .orderByDesc(AssetLifecycle::getId)
                .last("LIMIT 1"));
        return current != null ? current.getStage() : null;
    }

    private String getDepartmentName(Long departmentId) {
        if (departmentId == null) {
            return null;
        }
        com.eams.system.entity.Department department = departmentMapper.selectById(departmentId);
        return department != null ? department.getDeptName() : null;
    }

    private String getCurrentUsername() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception e) {
            return "系统";
        }
    }
}
