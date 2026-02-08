package com.eams.lifecycle.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 资产盘点明细实体
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Data
@TableName("asset_inventory_detail")
public class AssetInventoryDetail {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
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
     * 盘点结果：1-未盘点 2-正常 3-位置异常 4-状态异常 5-丢失
     */
    private Integer inventoryResult;

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

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除：0-未删除 1-已删除
     */
    @TableLogic
    private Integer deleted;
}
