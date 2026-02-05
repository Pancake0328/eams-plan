package com.eams.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.asset.dto.AssetCreateRequest;
import com.eams.asset.dto.AssetPageQuery;
import com.eams.asset.dto.AssetUpdateRequest;
import com.eams.asset.entity.AssetCategory;
import com.eams.asset.entity.AssetInfo;
import com.eams.asset.mapper.AssetCategoryMapper;
import com.eams.asset.mapper.AssetInfoMapper;
import com.eams.asset.service.AssetInfoService;
import com.eams.asset.service.AssetNumberGenerator;
import com.eams.asset.vo.AssetVO;
import com.eams.common.result.ResultCode;
import com.eams.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.eams.security.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 资产信息服务实现类
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssetInfoServiceImpl implements AssetInfoService {

    private final AssetInfoMapper assetInfoMapper;
    private final AssetCategoryMapper categoryMapper;
    private final AssetNumberGenerator numberGenerator;
    private final com.eams.system.mapper.DepartmentMapper departmentMapper;
    private final com.eams.system.mapper.UserMapper userMapper;

    /**
     * 资产状态映射
     */
    private static final Map<Integer, String> ASSET_STATUS_MAP = new HashMap<>();

    static {
        ASSET_STATUS_MAP.put(1, "闲置");
        ASSET_STATUS_MAP.put(2, "使用中");
        ASSET_STATUS_MAP.put(3, "维修中");
        ASSET_STATUS_MAP.put(4, "报废");
    }

    /**
     * 创建资产
     *
     * @param request 创建请求
     * @return 资产ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAsset(AssetCreateRequest request) {
        // 检查资产分类是否存在
        AssetCategory category = categoryMapper.selectById(request.getCategoryId());
        if (category == null) {
            throw new BusinessException("资产分类不存在");
        }

        // 生成资产编号
        String assetNumber = numberGenerator.generateAssetNumber();

        // 创建资产
        AssetInfo asset = new AssetInfo();
        asset.setAssetNumber(assetNumber);
        asset.setAssetName(request.getAssetName());
        asset.setCategoryId(request.getCategoryId());
        asset.setPurchaseAmount(request.getPurchaseAmount());
        asset.setPurchaseDate(request.getPurchaseDate());
        asset.setDepartmentId(getCurrentUserDepartmentId());
        asset.setCustodian(getCurrentUsername());
        asset.setAssetStatus(request.getAssetStatus() != null ? request.getAssetStatus() : 1); // 默认闲置
        asset.setSpecifications(request.getSpecifications());
        asset.setManufacturer(request.getManufacturer());
        asset.setRemark(request.getRemark());

        assetInfoMapper.insert(asset);
        log.info("创建资产成功，资产编号: {}", assetNumber);

        return asset.getId();
    }

    /**
     * 更新资产
     *
     * @param id      资产ID
     * @param request 更新请求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAsset(Long id, AssetUpdateRequest request) {
        // 检查资产是否存在
        AssetInfo asset = getAssetEntityById(id);

        // 检查资产分类是否存在
        AssetCategory category = categoryMapper.selectById(request.getCategoryId());
        if (category == null) {
            throw new BusinessException("资产分类不存在");
        }

        // 更新资产信息
        asset.setAssetName(request.getAssetName());
        asset.setCategoryId(request.getCategoryId());
        asset.setPurchaseAmount(request.getPurchaseAmount());
        asset.setPurchaseDate(request.getPurchaseDate());
        asset.setDepartmentId(getCurrentUserDepartmentId());
        asset.setCustodian(getCurrentUsername());
        asset.setAssetStatus(request.getAssetStatus());
        asset.setSpecifications(request.getSpecifications());
        asset.setManufacturer(request.getManufacturer());
        asset.setRemark(request.getRemark());

        assetInfoMapper.updateById(asset);
        log.info("更新资产成功，资产ID: {}", id);
    }

    /**
     * 删除资产
     *
     * @param id 资产ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAsset(Long id) {
        // 检查资产是否存在
        getAssetEntityById(id);

        // 逻辑删除
        assetInfoMapper.deleteById(id);
        log.info("删除资产成功，资产ID: {}", id);
    }

    /**
     * 获取资产详情
     *
     * @param id 资产ID
     * @return 资产详情
     */
    @Override
    public AssetVO getAssetById(Long id) {
        AssetInfo asset = getAssetEntityById(id);
        return convertToVO(asset);
    }

    /**
     * 分页查询资产
     *
     * @param query 查询条件
     * @return 资产分页列表
     */
    @Override
    public Page<AssetVO> getAssetPage(AssetPageQuery query) {
        // 构建查询条件
        LambdaQueryWrapper<AssetInfo> wrapper = new LambdaQueryWrapper<>();

        // 资产编号模糊查询
        if (StringUtils.hasText(query.getAssetNumber())) {
            wrapper.like(AssetInfo::getAssetNumber, query.getAssetNumber());
        }

        // 资产名称模糊查询
        if (StringUtils.hasText(query.getAssetName())) {
            wrapper.like(AssetInfo::getAssetName, query.getAssetName());
        }

        // 分类ID精确查询
        if (query.getCategoryId() != null) {
            wrapper.eq(AssetInfo::getCategoryId, query.getCategoryId());
        }

        // 使用部门ID精确查询
        if (query.getDepartmentId() != null) {
            wrapper.eq(AssetInfo::getDepartmentId, query.getDepartmentId());
        }

        // 责任人模糊查询
        if (StringUtils.hasText(query.getCustodian())) {
            wrapper.like(AssetInfo::getCustodian, query.getCustodian());
        }

        // 资产状态精确查询
        if (query.getAssetStatus() != null) {
            wrapper.eq(AssetInfo::getAssetStatus, query.getAssetStatus());
        }

        // 按创建时间倒序排列
        wrapper.orderByDesc(AssetInfo::getCreateTime);

        // 分页查询
        Page<AssetInfo> page = new Page<>(query.getCurrent(), query.getSize());
        page = assetInfoMapper.selectPage(page, wrapper);

        // 转换为VO
        Page<AssetVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<AssetVO> voList = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    /**
     * 更新资产状态
     *
     * @param id     资产ID
     * @param status 资产状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAssetStatus(Long id, Integer status) {
        // 检查资产是否存在
        AssetInfo asset = getAssetEntityById(id);

        // 验证状态值
        if (!ASSET_STATUS_MAP.containsKey(status)) {
            throw new BusinessException("资产状态值无效");
        }

        // 更新状态
        asset.setAssetStatus(status);
        assetInfoMapper.updateById(asset);
        log.info("更新资产状态成功，资产ID: {}, 新状态: {}", id, ASSET_STATUS_MAP.get(status));
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
     * 将实体转换为VO
     *
     * @param asset 资产实体
     * @return 资产VO
     */
    private AssetVO convertToVO(AssetInfo asset) {
        // 获取分类名称
        String categoryName = null;
        if (asset.getCategoryId() != null) {
            AssetCategory category = categoryMapper.selectById(asset.getCategoryId());
            if (category != null) {
                categoryName = category.getCategoryName();
            }
        }

        return AssetVO.builder()
                .id(asset.getId())
                .assetNumber(asset.getAssetNumber())
                .assetName(asset.getAssetName())
                .categoryId(asset.getCategoryId())
                .categoryName(categoryName)
                .purchaseAmount(asset.getPurchaseAmount())
                .purchaseDate(asset.getPurchaseDate())
                .departmentId(asset.getDepartmentId())
                .departmentName(getDepartmentName(asset.getDepartmentId()))
                .custodian(asset.getCustodian())
                .assetStatus(asset.getAssetStatus())
                .assetStatusText(ASSET_STATUS_MAP.get(asset.getAssetStatus()))
                .specifications(asset.getSpecifications())
                .manufacturer(asset.getManufacturer())
                .remark(asset.getRemark())
                .createTime(asset.getCreateTime())
                .updateTime(asset.getUpdateTime())
                .build();
    }

    /**
     * 根据部门ID获取部门名称
     *
     * @param departmentId 部门ID
     * @return 部门名称
     */
    private String getDepartmentName(Long departmentId) {
        if (departmentId == null) {
            return null;
        }
        com.eams.system.entity.Department department = departmentMapper.selectById(departmentId);
        return department != null ? department.getDeptName() : null;
    }

    /**
     * 获取当前登录用户名
     */
    private String getCurrentUsername() {
        String username = SecurityContextHolder.getCurrentUsername();
        if (!StringUtils.hasText(username)) {
            throw new BusinessException("未获取到当前登录用户");
        }
        return username;
    }

    private Long getCurrentUserDepartmentId() {
        Long userId = SecurityContextHolder.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("未获取到当前登录用户");
        }
        com.eams.system.entity.User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("当前用户不存在");
        }
        return user.getDepartmentId();
    }
}
