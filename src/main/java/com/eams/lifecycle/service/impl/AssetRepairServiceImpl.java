package com.eams.lifecycle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.asset.entity.AssetInfo;
import com.eams.asset.mapper.AssetInfoMapper;
import com.eams.common.exception.BusinessException;
import com.eams.lifecycle.dto.RepairCreateRequest;
import com.eams.lifecycle.entity.AssetRepair;
import com.eams.lifecycle.mapper.AssetRepairMapper;
import com.eams.lifecycle.service.AssetRepairService;
import com.eams.lifecycle.vo.RepairVO;
import lombok.RequiredArgsConstructor;
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

/**
 * 资产报修服务实现
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Service
@RequiredArgsConstructor
public class AssetRepairServiceImpl implements AssetRepairService {

    private final AssetRepairMapper repairMapper;
    private final AssetInfoMapper assetMapper;

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

        // 生成报修编号
        String repairNumber = generateRepairNumber();

        // 创建报修记录
        AssetRepair repair = new AssetRepair();
        BeanUtils.copyProperties(request, repair);
        repair.setRepairNumber(repairNumber);
        repair.setAssetNumber(asset.getAssetNumber());
        repair.setAssetName(asset.getAssetName());
        repair.setReportTime(LocalDateTime.now());
        repair.setRepairStatus(1); // 待审批
        repair.setRepairCost(BigDecimal.ZERO);

        repairMapper.insert(repair);

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

        repair.setApprover(approver);
        repair.setApprovalTime(LocalDateTime.now());
        repair.setRepairStatus(approved ? 2 : 5); // 2-已审批 5-已拒绝

        repairMapper.updateById(repair);
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

        repair.setRepairPerson(repairPerson);
        repair.setRepairStartTime(LocalDateTime.now());
        repair.setRepairStatus(3); // 维修中

        repairMapper.updateById(repair);
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

        return vo;
    }
}
