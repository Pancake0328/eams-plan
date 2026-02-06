package com.eams.lifecycle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.asset.entity.AssetInfo;
import com.eams.asset.mapper.AssetInfoMapper;
import com.eams.common.exception.BusinessException;
import com.eams.lifecycle.dto.LifecycleCreateRequest;
import com.eams.lifecycle.entity.AssetLifecycle;
import com.eams.lifecycle.mapper.AssetLifecycleMapper;
import com.eams.lifecycle.service.AssetLifecycleService;
import com.eams.lifecycle.vo.LifecycleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资产生命周期服务实现
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Service
@RequiredArgsConstructor
public class AssetLifecycleServiceImpl implements AssetLifecycleService {

    private final AssetLifecycleMapper lifecycleMapper;
    private final AssetInfoMapper assetMapper;
    private final com.eams.system.mapper.DepartmentMapper departmentMapper;

    private static final Map<Integer, String> STAGE_MAP = new HashMap<>();

    static {
        STAGE_MAP.put(1, "购入");
        STAGE_MAP.put(2, "使用中");
        STAGE_MAP.put(3, "维修中");
        STAGE_MAP.put(4, "闲置");
        STAGE_MAP.put(5, "报废");
        STAGE_MAP.put(6, "取消采购");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createLifecycle(LifecycleCreateRequest request) {
        // 验证资产存在
        AssetInfo asset = assetMapper.selectById(request.getAssetId());
        if (asset == null) {
            throw new BusinessException("资产不存在");
        }

        // 获取当前阶段
        AssetLifecycle current = getCurrentLifecycleEntity(request.getAssetId());

        // 创建生命周期记录
        AssetLifecycle lifecycle = new AssetLifecycle();
        BeanUtils.copyProperties(request, lifecycle);
        lifecycle.setPreviousStage(current != null ? current.getStage() : null);
        lifecycle.setFromDepartmentId(asset.getDepartmentId());
        lifecycle.setToDepartmentId(asset.getDepartmentId());
        lifecycle.setFromDepartment(getDepartmentName(asset.getDepartmentId()));
        lifecycle.setToDepartment(getDepartmentName(asset.getDepartmentId()));
        lifecycle.setFromCustodian(asset.getCustodian());
        lifecycle.setToCustodian(asset.getCustodian());

        lifecycleMapper.insert(lifecycle);

        return lifecycle.getId();
    }

    @Override
    public List<LifecycleVO> getAssetLifecycleHistory(Long assetId) {
        LambdaQueryWrapper<AssetLifecycle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetLifecycle::getAssetId, assetId);
        wrapper.orderByAsc(AssetLifecycle::getStageDate)
                .orderByAsc(AssetLifecycle::getCreateTime)
                .orderByAsc(AssetLifecycle::getId);

        List<AssetLifecycle> lifecycles = lifecycleMapper.selectList(wrapper);

        List<LifecycleVO> voList = new ArrayList<>();
        for (AssetLifecycle lifecycle : lifecycles) {
            LifecycleVO vo = convertToVO(lifecycle);
            voList.add(vo);
        }

        return voList;
    }

    @Override
    public LifecycleVO getCurrentLifecycle(Long assetId) {
        AssetLifecycle lifecycle = getCurrentLifecycleEntity(assetId);
        return lifecycle != null ? convertToVO(lifecycle) : null;
    }

    @Override
    public Page<LifecycleVO> getLifecyclePage(Integer current, Integer size, Integer stage) {
        Page<AssetLifecycle> page = new Page<>(current, size);

        LambdaQueryWrapper<AssetLifecycle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(stage != null, AssetLifecycle::getStage, stage);
        wrapper.orderByDesc(AssetLifecycle::getStageDate)
                .orderByDesc(AssetLifecycle::getCreateTime)
                .orderByDesc(AssetLifecycle::getId);

        Page<AssetLifecycle> lifecyclePage = lifecycleMapper.selectPage(page, wrapper);

        Page<LifecycleVO> voPage = new Page<>();
        BeanUtils.copyProperties(lifecyclePage, voPage, "records");

        List<LifecycleVO> voList = new ArrayList<>();
        for (AssetLifecycle lifecycle : lifecyclePage.getRecords()) {
            LifecycleVO vo = convertToVO(lifecycle);
            voList.add(vo);
        }
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long changeStage(LifecycleCreateRequest request) {
        // 验证状态变更合法性
        AssetLifecycle current = getCurrentLifecycleEntity(request.getAssetId());
        if (current != null && !isValidStageChange(current.getStage(), request.getStage())) {
            throw new BusinessException("不允许的状态变更");
        }

        return createLifecycle(request);
    }

    /**
     * 获取资产当前生命周期实体
     */
    private AssetLifecycle getCurrentLifecycleEntity(Long assetId) {
        LambdaQueryWrapper<AssetLifecycle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetLifecycle::getAssetId, assetId);

        wrapper.orderByDesc(AssetLifecycle::getCreateTime)
                .orderByDesc(AssetLifecycle::getId);
        wrapper.last("LIMIT 1");

        return lifecycleMapper.selectOne(wrapper);
    }

    /**
     * 验证状态变更是否合法
     */
    private boolean isValidStageChange(Integer fromStage, Integer toStage) {
        if (fromStage.equals(toStage)) {
            return false; // 不能变更到相同状态
        }

        // 报废状态不可逆
        if (fromStage == 5) {
            return false;
        }

        return true;
    }

    /**
     * 转换为VO
     */
    private LifecycleVO convertToVO(AssetLifecycle lifecycle) {
        LifecycleVO vo = new LifecycleVO();
        BeanUtils.copyProperties(lifecycle, vo);

        // 查询资产信息
        AssetInfo asset = assetMapper.selectById(lifecycle.getAssetId());
        if (asset != null) {
            vo.setAssetNumber(asset.getAssetNumber());
            vo.setAssetName(asset.getAssetName());
        }

        // 设置阶段文本
        vo.setStageText(STAGE_MAP.getOrDefault(lifecycle.getStage(), "未知"));
        vo.setPreviousStageText(STAGE_MAP.getOrDefault(lifecycle.getPreviousStage(), "无"));
        if (!StringUtils.hasText(vo.getFromDepartment()) && vo.getFromDepartmentId() != null) {
            vo.setFromDepartment(getDepartmentName(vo.getFromDepartmentId()));
        }
        if (!StringUtils.hasText(vo.getToDepartment()) && vo.getToDepartmentId() != null) {
            vo.setToDepartment(getDepartmentName(vo.getToDepartmentId()));
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
