package com.eams.lifecycle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.asset.entity.AssetInfo;
import com.eams.asset.mapper.AssetInfoMapper;
import com.eams.common.exception.BusinessException;
import com.eams.lifecycle.dto.InventoryCreateRequest;
import com.eams.lifecycle.dto.InventoryExecuteRequest;
import com.eams.lifecycle.entity.AssetInventory;
import com.eams.lifecycle.entity.AssetInventoryDetail;
import com.eams.lifecycle.mapper.AssetInventoryDetailMapper;
import com.eams.lifecycle.mapper.AssetInventoryMapper;
import com.eams.lifecycle.service.AssetInventoryService;
import com.eams.lifecycle.vo.InventoryDetailVO;
import com.eams.lifecycle.vo.InventoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资产盘点服务实现
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Service
@RequiredArgsConstructor
public class AssetInventoryServiceImpl implements AssetInventoryService {

    private final AssetInventoryMapper inventoryMapper;
    private final AssetInventoryDetailMapper detailMapper;
    private final AssetInfoMapper assetMapper;
    private final com.eams.system.mapper.DepartmentMapper departmentMapper;

    private static final Map<Integer, String> TYPE_MAP = new HashMap<>();
    private static final Map<Integer, String> STATUS_MAP = new HashMap<>();
    private static final Map<Integer, String> RESULT_MAP = new HashMap<>();

    static {
        TYPE_MAP.put(1, "全面盘点");
        TYPE_MAP.put(2, "抽样盘点");
        TYPE_MAP.put(3, "专项盘点");

        STATUS_MAP.put(1, "计划中");
        STATUS_MAP.put(2, "进行中");
        STATUS_MAP.put(3, "已完成");
        STATUS_MAP.put(4, "已取消");

        RESULT_MAP.put(1, "未盘点");
        RESULT_MAP.put(2, "正常");
        RESULT_MAP.put(3, "位置异常");
        RESULT_MAP.put(4, "状态异常");
        RESULT_MAP.put(5, "丢失");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createInventory(InventoryCreateRequest request) {
        // 生成盘点编号
        String inventoryNumber = generateInventoryNumber();

        // 创建盘点计划
        AssetInventory inventory = new AssetInventory();
        BeanUtils.copyProperties(request, inventory);
        inventory.setInventoryNumber(inventoryNumber);
        inventory.setInventoryStatus(1); // 计划中

        inventoryMapper.insert(inventory);

        // 创建盘点明细（根据盘点类型查询资产）
        List<AssetInfo> assets = getAssetsByInventoryType(request.getInventoryType());
        inventory.setTotalCount(assets.size());
        inventoryMapper.updateById(inventory);

        for (AssetInfo asset : assets) {
            AssetInventoryDetail detail = new AssetInventoryDetail();
            detail.setInventoryId(inventory.getId());
            detail.setAssetId(asset.getId());
            detail.setAssetNumber(asset.getAssetNumber());
            detail.setAssetName(asset.getAssetName());
            detail.setExpectedLocation(getDepartmentName(asset.getDepartmentId())); // 查询部门名称作为预期位置
            detail.setInventoryResult(1); // 未盘点

            detailMapper.insert(detail);
        }

        return inventory.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startInventory(Long inventoryId) {
        AssetInventory inventory = inventoryMapper.selectById(inventoryId);
        if (inventory == null) {
            throw new BusinessException("盘点计划不存在");
        }

        if (inventory.getInventoryStatus() != 1) {
            throw new BusinessException("只有计划中的盘点才能开始");
        }

        inventory.setInventoryStatus(2); // 进行中
        inventory.setActualStartDate(LocalDate.now());
        inventoryMapper.updateById(inventory);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeInventory(InventoryExecuteRequest request) {
        AssetInventoryDetail detail = detailMapper.selectById(request.getDetailId());
        if (detail == null) {
            throw new BusinessException("盘点明细不存在");
        }

        // 更新盘点明细
        detail.setActualLocation(request.getActualLocation());
        detail.setInventoryResult(request.getInventoryResult());
        detail.setInventoryPerson(request.getInventoryPerson());
        detail.setInventoryTime(LocalDateTime.now());
        detail.setRemark(request.getRemark());

        detailMapper.updateById(detail);

        // 更新盘点计划统计
        updateInventoryStatistics(detail.getInventoryId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeInventory(Long inventoryId) {
        AssetInventory inventory = inventoryMapper.selectById(inventoryId);
        if (inventory == null) {
            throw new BusinessException("盘点计划不存在");
        }

        if (inventory.getInventoryStatus() != 2) {
            throw new BusinessException("只有进行中的盘点才能完成");
        }

        inventory.setInventoryStatus(3); // 已完成
        inventory.setActualEndDate(LocalDate.now());
        inventoryMapper.updateById(inventory);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelInventory(Long inventoryId) {
        AssetInventory inventory = inventoryMapper.selectById(inventoryId);
        if (inventory == null) {
            throw new BusinessException("盘点计划不存在");
        }

        if (inventory.getInventoryStatus() == 3) {
            throw new BusinessException("已完成的盘点不能取消");
        }

        inventory.setInventoryStatus(4); // 已取消
        inventoryMapper.updateById(inventory);
    }

    @Override
    public InventoryVO getInventoryDetail(Long inventoryId) {
        AssetInventory inventory = inventoryMapper.selectById(inventoryId);
        if (inventory == null) {
            throw new BusinessException("盘点计划不存在");
        }

        InventoryVO vo = convertToVO(inventory);

        // 查询盘点明细
        LambdaQueryWrapper<AssetInventoryDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetInventoryDetail::getInventoryId, inventoryId);
        List<AssetInventoryDetail> details = detailMapper.selectList(wrapper);

        List<InventoryDetailVO> detailVOs = new ArrayList<>();
        for (AssetInventoryDetail detail : details) {
            InventoryDetailVO detailVO = new InventoryDetailVO();
            BeanUtils.copyProperties(detail, detailVO);
            detailVO.setInventoryResultText(RESULT_MAP.getOrDefault(detail.getInventoryResult(), "未知"));
            detailVOs.add(detailVO);
        }
        vo.setDetails(detailVOs);

        return vo;
    }

    @Override
    public Page<InventoryVO> getInventoryPage(Integer current, Integer size, Integer status) {
        Page<AssetInventory> page = new Page<>(current, size);

        LambdaQueryWrapper<AssetInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(status != null, AssetInventory::getInventoryStatus, status);
        wrapper.orderByDesc(AssetInventory::getPlanStartDate);

        Page<AssetInventory> inventoryPage = inventoryMapper.selectPage(page, wrapper);

        Page<InventoryVO> voPage = new Page<>();
        BeanUtils.copyProperties(inventoryPage, voPage, "records");

        List<InventoryVO> voList = new ArrayList<>();
        for (AssetInventory inventory : inventoryPage.getRecords()) {
            InventoryVO vo = convertToVO(inventory);
            voList.add(vo);
        }
        voPage.setRecords(voList);

        return voPage;
    }

    /**
     * 生成盘点编号
     */
    private String generateInventoryNumber() {
        return "INV-" + LocalDate.now().toString().replace("-", "") + "-" +
                String.format("%03d", System.currentTimeMillis() % 1000);
    }

    /**
     * 根据盘点类型获取资产列表
     */
    private List<AssetInfo> getAssetsByInventoryType(Integer inventoryType) {
        LambdaQueryWrapper<AssetInfo> wrapper = new LambdaQueryWrapper<>();

        if (inventoryType == 2) {
            // 抽样盘点：随机抽取部分资产
            wrapper.last("ORDER BY RAND() LIMIT 50");
        }
        // 全面盘点和专项盘点：查询所有资产

        return assetMapper.selectList(wrapper);
    }

    /**
     * 更新盘点统计
     */
    private void updateInventoryStatistics(Long inventoryId) {
        LambdaQueryWrapper<AssetInventoryDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetInventoryDetail::getInventoryId, inventoryId);
        List<AssetInventoryDetail> details = detailMapper.selectList(wrapper);

        int actualCount = 0;
        int normalCount = 0;
        int abnormalCount = 0;

        for (AssetInventoryDetail detail : details) {
            if (detail.getInventoryResult() != 1) { // 已盘点
                actualCount++;
                if (detail.getInventoryResult() == 2) { // 正常
                    normalCount++;
                } else {
                    abnormalCount++;
                }
            }
        }

        AssetInventory inventory = inventoryMapper.selectById(inventoryId);
        inventory.setActualCount(actualCount);
        inventory.setNormalCount(normalCount);
        inventory.setAbnormalCount(abnormalCount);
        inventoryMapper.updateById(inventory);
    }

    /**
     * 转换为VO
     */
    private InventoryVO convertToVO(AssetInventory inventory) {
        InventoryVO vo = new InventoryVO();
        BeanUtils.copyProperties(inventory, vo);

        vo.setInventoryTypeText(TYPE_MAP.getOrDefault(inventory.getInventoryType(), "未知"));
        vo.setInventoryStatusText(STATUS_MAP.getOrDefault(inventory.getInventoryStatus(), "未知"));

        // 计算完成率
        if (inventory.getTotalCount() != null && inventory.getTotalCount() > 0) {
            double rate = (double) inventory.getActualCount() / inventory.getTotalCount() * 100;
            vo.setCompletionRate(Math.round(rate * 100.0) / 100.0);
        }

        return vo;
    }

    /**
     * 根据部门ID获取部门名称
     */
    private String getDepartmentName(Long departmentId) {
        if (departmentId == null) {
            return null;
        }
        com.eams.system.entity.Department department = departmentMapper.selectById(departmentId);
        return department != null ? department.getDeptName() : null;
    }
}
