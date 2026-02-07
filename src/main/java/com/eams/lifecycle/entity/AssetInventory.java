package com.eams.lifecycle.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 资产盘点计划实体
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
@TableName("asset_inventory")
public class AssetInventory {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 盘点编号
     */
    private String inventoryNumber;

    /**
     * 盘点名称
     */
    private String inventoryName;

    /**
     * 盘点类型：1-全面盘点 2-抽样盘点 3-专项盘点
     */
    private Integer inventoryType;

    /**
     * 抽样数量
     */
    private Integer sampleCount;

    /**
     * 专项盘点分类ID
     */
    private Long categoryId;

    /**
     * 计划开始日期
     */
    private LocalDate planStartDate;

    /**
     * 计划结束日期
     */
    private LocalDate planEndDate;

    /**
     * 实际开始日期
     */
    private LocalDate actualStartDate;

    /**
     * 实际结束日期
     */
    private LocalDate actualEndDate;

    /**
     * 盘点状态：1-计划中 2-进行中 3-已完成 4-已取消
     */
    private Integer inventoryStatus;

    /**
     * 应盘资产数量
     */
    private Integer totalCount;

    /**
     * 实盘资产数量
     */
    private Integer actualCount;

    /**
     * 正常资产数量
     */
    private Integer normalCount;

    /**
     * 异常资产数量
     */
    private Integer abnormalCount;

    /**
     * 创建人
     */
    private String creator;

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
