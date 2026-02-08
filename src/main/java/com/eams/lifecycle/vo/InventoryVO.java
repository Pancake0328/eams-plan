package com.eams.lifecycle.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 盘点计划VO
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Data
public class InventoryVO {

    /**
     * 主键ID
     */
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
     * 盘点类型
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
     * 专项盘点分类名称
     */
    private String categoryName;

    /**
     * 盘点类型文本
     */
    private String inventoryTypeText;

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
     * 盘点状态
     */
    private Integer inventoryStatus;

    /**
     * 盘点状态文本
     */
    private String inventoryStatusText;

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
     * 盘点完成率
     */
    private Double completionRate;

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
    private LocalDateTime createTime;

    /**
     * 盘点明细
     */
    private List<InventoryDetailVO> details;
}
