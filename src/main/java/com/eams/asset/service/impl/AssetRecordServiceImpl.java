package com.eams.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.asset.dto.RecordCreateRequest;
import com.eams.asset.dto.RecordPageQuery;
import com.eams.asset.entity.AssetInfo;
import com.eams.asset.entity.AssetRecord;
import com.eams.asset.mapper.AssetInfoMapper;
import com.eams.asset.mapper.AssetRecordMapper;
import com.eams.asset.service.AssetRecordService;
import com.eams.asset.vo.RecordVO;
import com.eams.common.result.ResultCode;
import com.eams.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 资产流转记录服务实现类
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssetRecordServiceImpl implements AssetRecordService {

    private final AssetRecordMapper recordMapper;
    private final AssetInfoMapper assetInfoMapper;
    private final com.eams.system.mapper.DepartmentMapper departmentMapper;

    /**
     * 记录类型映射
     */
    private static final Map<Integer, String> RECORD_TYPE_MAP = new HashMap<>();

    /**
     * 资产状态映射
     */
    private static final Map<Integer, String> ASSET_STATUS_MAP = new HashMap<>();

    static {
        RECORD_TYPE_MAP.put(1, "入库");
        RECORD_TYPE_MAP.put(2, "分配");
        RECORD_TYPE_MAP.put(3, "调拨");
        RECORD_TYPE_MAP.put(4, "归还");
        RECORD_TYPE_MAP.put(5, "报废");
        RECORD_TYPE_MAP.put(6, "送修");
        RECORD_TYPE_MAP.put(7, "维修完成");

        ASSET_STATUS_MAP.put(1, "闲置");
        ASSET_STATUS_MAP.put(2, "使用中");
        ASSET_STATUS_MAP.put(3, "维修中");
        ASSET_STATUS_MAP.put(4, "报废");
    }

    /**
     * 资产入库
     *
     * @param request 入库请求
     * @return 记录ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long assetIn(RecordCreateRequest request) {
        AssetInfo asset = getAssetEntityById(request.getAssetId());

        // 创建入库记录
        AssetRecord record = createRecord(
                asset,
                1, // 入库
                null, request.getToDepartmentId(),
                null, request.getToCustodian(),
                asset.getAssetStatus(), 1, // 状态变更为闲置
                request.getRemark());
        recordMapper.insert(record);

        // 更新资产信息
        asset.setDepartmentId(request.getToDepartmentId());
        asset.setCustodian(request.getToCustodian());
        asset.setAssetStatus(1); // 闲置
        assetInfoMapper.updateById(asset);

        log.info("资产入库成功，资产编号: {}", asset.getAssetNumber());
        return record.getId();
    }

    /**
     * 资产分配（分配给员工）
     *
     * @param request 分配请求
     * @return 记录ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long allocateAsset(RecordCreateRequest request) {
        AssetInfo asset = getAssetEntityById(request.getAssetId());

        // 状态校验：只有闲置的资产才能分配
        if (asset.getAssetStatus() != 1) {
            throw new BusinessException("只有闲置状态的资产才能分配");
        }

        // 创建分配记录
        AssetRecord record = createRecord(
                asset,
                2, // 分配
                asset.getDepartmentId(), request.getToDepartmentId(),
                asset.getCustodian(), request.getToCustodian(),
                asset.getAssetStatus(), 2, // 状态变更为使用中
                request.getRemark());
        recordMapper.insert(record);

        // 更新资产信息
        asset.setDepartmentId(request.getToDepartmentId());
        asset.setCustodian(request.getToCustodian());
        asset.setAssetStatus(2); // 使用中
        assetInfoMapper.updateById(asset);

        log.info("资产分配成功，资产编号: {}, 责任人: {}", asset.getAssetNumber(), request.getToCustodian());
        return record.getId();
    }

    /**
     * 资产调拨（部门间调拨）
     *
     * @param request 调拨请求
     * @return 记录ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long transferAsset(RecordCreateRequest request) {
        AssetInfo asset = getAssetEntityById(request.getAssetId());

        // 状态校验：只有闲置或使用中的资产才能调拨
        if (asset.getAssetStatus() != 1 && asset.getAssetStatus() != 2) {
            throw new BusinessException("只有闲置或使用中的资产才能调拨");
        }

        // 创建调拨记录
        AssetRecord record = createRecord(
                asset,
                3, // 调拨
                asset.getDepartmentId(), request.getToDepartmentId(),
                asset.getCustodian(), request.getToCustodian(),
                asset.getAssetStatus(), asset.getAssetStatus(), // 状态不变
                request.getRemark());
        recordMapper.insert(record);

        // 更新资产信息
        asset.setDepartmentId(request.getToDepartmentId());
        asset.setCustodian(request.getToCustodian());
        assetInfoMapper.updateById(asset);

        log.info("资产调拨成功，资产编号: {}, 目标部门: {}", asset.getAssetNumber(), request.getToDepartment());
        return record.getId();
    }

    /**
     * 资产归还
     *
     * @param request 归还请求
     * @return 记录ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long returnAsset(RecordCreateRequest request) {
        AssetInfo asset = getAssetEntityById(request.getAssetId());

        // 状态校验：只有使用中的资产才能归还
        if (asset.getAssetStatus() != 2) {
            throw new BusinessException("只有使用中的资产才能归还");
        }

        // 创建归还记录
        AssetRecord record = createRecord(
                asset,
                4, // 归还
                asset.getDepartmentId(), null,
                asset.getCustodian(), null,
                asset.getAssetStatus(), 1, // 状态变更为闲置
                request.getRemark());
        recordMapper.insert(record);

        // 更新资产信息
        asset.setCustodian(null);
        asset.setAssetStatus(1); // 闲置
        assetInfoMapper.updateById(asset);

        log.info("资产归还成功，资产编号: {}", asset.getAssetNumber());
        return record.getId();
    }

    /**
     * 资产报废
     *
     * @param request 报废请求
     * @return 记录ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long scrapAsset(RecordCreateRequest request) {
        AssetInfo asset = getAssetEntityById(request.getAssetId());

        // 状态校验：已报废的资产不能再次报废
        if (asset.getAssetStatus() == 4) {
            throw new BusinessException("资产已报废，不能重复操作");
        }

        // 创建报废记录
        AssetRecord record = createRecord(
                asset,
                5, // 报废
                asset.getDepartmentId(), null,
                asset.getCustodian(), null,
                asset.getAssetStatus(), 4, // 状态变更为报废
                request.getRemark());
        recordMapper.insert(record);

        // 更新资产信息
        asset.setAssetStatus(4); // 报废
        assetInfoMapper.updateById(asset);

        log.info("资产报废成功，资产编号: {}", asset.getAssetNumber());
        return record.getId();
    }

    /**
     * 资产送修
     *
     * @param request 送修请求
     * @return 记录ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long sendForRepair(RecordCreateRequest request) {
        AssetInfo asset = getAssetEntityById(request.getAssetId());

        // 状态校验：报废的资产不能送修
        if (asset.getAssetStatus() == 4) {
            throw new BusinessException("报废的资产不能送修");
        }
        // 已在维修中的资产不能重复送修
        if (asset.getAssetStatus() == 3) {
            throw new BusinessException("资产已在维修中");
        }

        // 创建送修记录
        AssetRecord record = createRecord(
                asset,
                6, // 送修
                asset.getDepartmentId(), asset.getDepartmentId(),
                asset.getCustodian(), asset.getCustodian(),
                asset.getAssetStatus(), 3, // 状态变更为维修中
                request.getRemark());
        recordMapper.insert(record);

        // 更新资产信息
        asset.setAssetStatus(3); // 维修中
        assetInfoMapper.updateById(asset);

        log.info("资产送修成功，资产编号: {}", asset.getAssetNumber());
        return record.getId();
    }

    /**
     * 维修完成
     *
     * @param request 维修完成请求
     * @return 记录ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long repairComplete(RecordCreateRequest request) {
        AssetInfo asset = getAssetEntityById(request.getAssetId());

        // 状态校验：只有维修中的资产才能完成维修
        if (asset.getAssetStatus() != 3) {
            throw new BusinessException("只有维修中的资产才能完成维修");
        }

        // 创建维修完成记录
        AssetRecord record = createRecord(
                asset,
                7, // 维修完成
                asset.getDepartmentId(), asset.getDepartmentId(),
                asset.getCustodian(), asset.getCustodian(),
                asset.getAssetStatus(), 1, // 状态变更为闲置
                request.getRemark());
        recordMapper.insert(record);

        // 更新资产信息
        asset.setAssetStatus(1); // 闲置
        assetInfoMapper.updateById(asset);

        log.info("资产维修完成，资产编号: {}", asset.getAssetNumber());
        return record.getId();
    }

    /**
     * 分页查询流转记录
     *
     * @param query 查询条件
     * @return 流转记录分页列表
     */
    @Override
    public Page<RecordVO> getRecordPage(RecordPageQuery query) {
        // 构建查询条件
        LambdaQueryWrapper<AssetRecord> wrapper = new LambdaQueryWrapper<>();

        // 资产ID精确查询
        if (query.getAssetId() != null) {
            wrapper.eq(AssetRecord::getAssetId, query.getAssetId());
        }

        // 记录类型精确查询
        if (query.getRecordType() != null) {
            wrapper.eq(AssetRecord::getRecordType, query.getRecordType());
        }

        // 操作人模糊查询
        if (StringUtils.hasText(query.getOperator())) {
            wrapper.like(AssetRecord::getOperator, query.getOperator());
        }

        // 按操作时间倒序排列
        wrapper.orderByDesc(AssetRecord::getOperateTime);

        // 分页查询
        Page<AssetRecord> page = new Page<>(query.getCurrent(), query.getSize());
        page = recordMapper.selectPage(page, wrapper);

        // 转换为VO
        Page<RecordVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<RecordVO> voList = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    /**
     * 查询资产流转历史
     *
     * @param assetId 资产ID
     * @return 流转记录列表
     */
    @Override
    public List<RecordVO> getAssetRecordHistory(Long assetId) {
        List<AssetRecord> records = recordMapper.selectList(
                new LambdaQueryWrapper<AssetRecord>()
                        .eq(AssetRecord::getAssetId, assetId)
                        .orderByDesc(AssetRecord::getOperateTime));

        return records.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 创建流转记录
     *
     * @param asset          资产
     * @param recordType     记录类型
     * @param fromDepartment 原部门
     * @param toDepartment   目标部门
     * @param fromCustodian  原责任人
     * @param toCustodian    目标责任人
     * @param oldStatus      原状态
     * @param newStatus      新状态
     * @param remark         备注
     * @return 流转记录
     */
    private AssetRecord createRecord(AssetInfo asset, Integer recordType,
            Long fromDepartmentId, Long toDepartmentId,
            String fromCustodian, String toCustodian,
            Integer oldStatus, Integer newStatus,
            String remark) {
        AssetRecord record = new AssetRecord();
        record.setAssetId(asset.getId());
        record.setRecordType(recordType);
        // 查询部门名称并存储
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

    /**
     * 根据ID获取资产实体（内部方法）
     *
     * @param id 资产ID
     * @return 资产实体
     */
    private AssetInfo getAssetEntityById(Long id) {
        AssetInfo asset = assetInfoMapper.selectById(id);
        if (asset == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXISTED);
        }
        return asset;
    }

    /**
     * 获取当前登录用户名
     *
     * @return 用户名
     */
    private String getCurrentUsername() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception e) {
            return "系统";
        }
    }

    /**
     * 将实体转换为VO
     *
     * @param record 流转记录实体
     * @return 流转记录VO
     */
    private RecordVO convertToVO(AssetRecord record) {
        // 获取资产信息
        AssetInfo asset = assetInfoMapper.selectById(record.getAssetId());

        return RecordVO.builder()
                .id(record.getId())
                .assetId(record.getAssetId())
                .assetNumber(asset != null ? asset.getAssetNumber() : null)
                .assetName(asset != null ? asset.getAssetName() : null)
                .recordType(record.getRecordType())
                .recordTypeText(RECORD_TYPE_MAP.get(record.getRecordType()))
                .fromDepartment(record.getFromDepartment())
                .toDepartment(record.getToDepartment())
                .fromCustodian(record.getFromCustodian())
                .toCustodian(record.getToCustodian())
                .oldStatus(record.getOldStatus())
                .oldStatusText(ASSET_STATUS_MAP.get(record.getOldStatus()))
                .newStatus(record.getNewStatus())
                .newStatusText(ASSET_STATUS_MAP.get(record.getNewStatus()))
                .remark(record.getRemark())
                .operator(record.getOperator())
                .operateTime(record.getOperateTime())
                .createTime(record.getCreateTime())
                .build();
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
