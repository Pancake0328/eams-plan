package com.eams.asset.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.asset.dto.RecordCreateRequest;
import com.eams.asset.dto.RecordPageQuery;
import com.eams.asset.vo.RecordVO;

import java.util.List;

/**
 * 资产流转记录服务接口
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
public interface AssetRecordService {

    /**
     * 资产入库
     *
     * @param request 入库请求
     * @return 记录ID
     */
    Long assetIn(RecordCreateRequest request);

    /**
     * 资产分配（分配给员工）
     *
     * @param request 分配请求
     * @return 记录ID
     */
    Long allocateAsset(RecordCreateRequest request);

    /**
     * 资产调拨（部门间调拨）
     *
     * @param request 调拨请求
     * @return 记录ID
     */
    Long transferAsset(RecordCreateRequest request);

    /**
     * 资产归还
     *
     * @param request 归还请求
     * @return 记录ID
     */
    Long returnAsset(RecordCreateRequest request);

    /**
     * 资产报废
     *
     * @param request 报废请求
     * @return 记录ID
     */
    Long scrapAsset(RecordCreateRequest request);

    /**
     * 资产送修
     *
     * @param request 送修请求
     * @return 记录ID
     */
    Long sendForRepair(RecordCreateRequest request);

    /**
     * 维修完成
     *
     * @param request 维修完成请求
     * @return 记录ID
     */
    Long repairComplete(RecordCreateRequest request);

    /**
     * 分页查询流转记录
     *
     * @param query 查询条件
     * @return 流转记录分页列表
     */
    Page<RecordVO> getRecordPage(RecordPageQuery query);

    /**
     * 查询资产流转历史
     *
     * @param assetId 资产ID
     * @return 流转记录列表
     */
    List<RecordVO> getAssetRecordHistory(Long assetId);
}
