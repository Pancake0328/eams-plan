package com.eams.asset.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.asset.dto.AssetCreateRequest;
import com.eams.asset.dto.AssetPageQuery;
import com.eams.asset.dto.AssetUpdateRequest;
import com.eams.asset.vo.AssetVO;

/**
 * 资产信息服务接口
 *
 * @author Pancake
 * @since 2026-01-03
 */
public interface AssetInfoService {

    /**
     * 创建资产
     *
     * @param request 创建请求
     * @return 资产ID
     */
    Long createAsset(AssetCreateRequest request);

    /**
     * 更新资产
     *
     * @param id      资产ID
     * @param request 更新请求
     */
    void updateAsset(Long id, AssetUpdateRequest request);

    /**
     * 删除资产
     *
     * @param id 资产ID
     */
    void deleteAsset(Long id);

    /**
     * 获取资产详情
     *
     * @param id 资产ID
     * @return 资产详情
     */
    AssetVO getAssetById(Long id);

    /**
     * 分页查询资产
     *
     * @param query 查询条件
     * @return 资产分页列表
     */
    Page<AssetVO> getAssetPage(AssetPageQuery query);

    /**
     * 更新资产状态
     *
     * @param id     资产ID
     * @param status 资产状态
     */
    void updateAssetStatus(Long id, Integer status);
}
