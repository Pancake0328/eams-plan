package com.eams.lifecycle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 盘点计划创建请求
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
public class InventoryCreateRequest {

    /**
     * 盘点名称
     */
    @NotBlank(message = "盘点名称不能为空")
    private String inventoryName;

    /**
     * 盘点类型：1-全面盘点 2-抽样盘点 3-专项盘点
     */
    @NotNull(message = "盘点类型不能为空")
    private Integer inventoryType;

    /**
     * 抽样数量（抽样盘点使用）
     */
    private Integer sampleCount;

    /**
     * 计划开始日期
     */
    @NotNull(message = "计划开始日期不能为空")
    private LocalDate planStartDate;

    /**
     * 计划结束日期
     */
    @NotNull(message = "计划结束日期不能为空")
    private LocalDate planEndDate;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 盘点分类（专项盘点使用）
     */
    private Long categoryId;

    /**
     * 备注
     */
    private String remark;
}
