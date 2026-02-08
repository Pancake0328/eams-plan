package com.eams.lifecycle.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.lifecycle.dto.RepairCreateRequest;
import com.eams.lifecycle.vo.RepairVO;

import java.math.BigDecimal;

/**
 * 资产报修服务
 *
 * @author Pancake
 * @since 2026-01-03
 */
public interface AssetRepairService {

    /**
     * 创建报修记录
     *
     * @param request 创建请求
     * @return 报修ID
     */
    Long createRepair(RepairCreateRequest request);

    /**
     * 审批报修
     *
     * @param repairId 报修ID
     * @param approved 是否通过
     * @param approver 审批人
     */
    void approveRepair(Long repairId, Boolean approved, String approver);

    /**
     * 开始维修
     *
     * @param repairId     报修ID
     * @param repairPerson 维修人
     */
    void startRepair(Long repairId, String repairPerson);

    /**
     * 完成维修
     *
     * @param repairId     报修ID
     * @param repairCost   维修费用
     * @param repairResult 维修结果
     */
    void completeRepair(Long repairId, BigDecimal repairCost, String repairResult);

    /**
     * 获取报修详情
     *
     * @param repairId 报修ID
     * @return 报修VO
     */
    RepairVO getRepairDetail(Long repairId);

    /**
     * 分页查询报修记录
     *
     * @param current 当前页
     * @param size    每页大小
     * @param status  状态筛选
     * @param assetId 资产ID筛选
     * @return 分页结果
     */
    Page<RepairVO> getRepairPage(Integer current, Integer size, Integer status, Long assetId);
}
