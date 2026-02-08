package com.eams.lifecycle.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.lifecycle.dto.LifecycleCreateRequest;
import com.eams.lifecycle.vo.LifecycleVO;

import java.util.List;

/**
 * 资产生命周期服务
 *
 * @author Pancake
 * @since 2026-01-03
 */
public interface AssetLifecycleService {

    /**
     * 创建生命周期记录
     *
     * @param request 创建请求
     * @return 记录ID
     */
    Long createLifecycle(LifecycleCreateRequest request);

    /**
     * 获取资产的生命周期历史
     *
     * @param assetId 资产ID
     * @return 生命周期记录列表
     */
    List<LifecycleVO> getAssetLifecycleHistory(Long assetId);

    /**
     * 获取资产当前生命周期阶段
     *
     * @param assetId 资产ID
     * @return 当前生命周期VO
     */
    LifecycleVO getCurrentLifecycle(Long assetId);

    /**
     * 分页查询生命周期记录
     *
     * @param current 当前页
     * @param size    每页大小
     * @param stage   阶段筛选
     * @return 分页结果
     */
    Page<LifecycleVO> getLifecyclePage(Integer current, Integer size, Integer stage);

    /**
     * 变更资产生命周期阶段
     *
     * @param request 变更请求
     * @return 记录ID
     */
    Long changeStage(LifecycleCreateRequest request);
}
