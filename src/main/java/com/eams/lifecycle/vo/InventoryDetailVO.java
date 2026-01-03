package com.eams.lifecycle.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 盘点明细VO
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
public class InventoryDetailVO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 盘点计划ID
     */
    private Long inventoryId;

    /**
     * 资产ID
     */
    private Long assetId;

    /**
     * 资产编号
     */
    private String assetNumber;

    /**
     * 资产名称
     */
    private String assetName;

    /**
     * 预期位置
     */
    private String expectedLocation;

    /**
     * 实际位置
     */
    private String actualLocation;

    /**
     * 盘点结果
     */
    private Integer inventoryResult;

    /**
     * 盘点结果文本
     */
    private String inventoryResultText;

    /**
     * 盘点人
     */
    private String inventoryPerson;

    /**
     * 盘点时间
     */
    private LocalDateTime inventoryTime;

    /**
     * 备注
     */
    private String remark;
}
