package com.eams.lifecycle.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.lifecycle.dto.InventoryCreateRequest;
import com.eams.lifecycle.dto.InventoryExecuteRequest;
import com.eams.lifecycle.vo.InventoryVO;

/**
 * 资产盘点服务
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
public interface AssetInventoryService {

    /**
     * 创建盘点计划
     *
     * @param request 创建请求
     * @return 盘点计划ID
     */
    Long createInventory(InventoryCreateRequest request);

    /**
     * 开始盘点
     *
     * @param inventoryId 盘点计划ID
     */
    void startInventory(Long inventoryId);

    /**
     * 执行盘点
     *
     * @param request 执行请求
     */
    void executeInventory(InventoryExecuteRequest request);

    /**
     * 完成盘点
     *
     * @param inventoryId 盘点计划ID
     */
    void completeInventory(Long inventoryId);

    /**
     * 取消盘点
     *
     * @param inventoryId 盘点计划ID
     */
    void cancelInventory(Long inventoryId);

    /**
     * 获取盘点详情
     *
     * @param inventoryId 盘点计划ID
     * @return 盘点VO
     */
    InventoryVO getInventoryDetail(Long inventoryId);

    /**
     * 分页查询盘点计划
     *
     * @param current 当前页
     * @param size    每页大小
     * @param status  状态筛选
     * @return 分页结果
     */
    Page<InventoryVO> getInventoryPage(Integer current, Integer size, Integer status);
}
