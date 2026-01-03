package com.eams.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.system.dto.AssignRecordPageQuery;
import com.eams.system.dto.AssetAssignRequest;
import com.eams.system.vo.AssetAssignRecordVO;

import java.util.List;

/**
 * 资产分配服务接口
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
public interface AssetAssignService {

    /**
     * 分配资产给员工
     *
     * @param request 分配请求
     * @return 记录ID
     */
    Long assignAssetToEmployee(AssetAssignRequest request);

    /**
     * 回收资产
     *
     * @param assetId 资产ID
     * @param remark  备注
     * @return 记录ID
     */
    Long returnAsset(Long assetId, String remark);

    /**
     * 部门内调拨资产
     *
     * @param request 调拨请求
     * @return 记录ID
     */
    Long transferAsset(AssetAssignRequest request);

    /**
     * 分页查询分配记录
     *
     * @param query 查询条件
     * @return 分页结果
     */
    Page<AssetAssignRecordVO> getAssignRecordPage(AssignRecordPageQuery query);

    /**
     * 查询资产分配历史
     *
     * @param assetId 资产ID
     * @return 分配历史列表
     */
    List<AssetAssignRecordVO> getAssetAssignHistory(Long assetId);

    /**
     * 查询员工资产分配历史
     *
     * @param empId 员工ID
     * @return 分配历史列表
     */
    List<AssetAssignRecordVO> getEmployeeAssignHistory(Long empId);
}
