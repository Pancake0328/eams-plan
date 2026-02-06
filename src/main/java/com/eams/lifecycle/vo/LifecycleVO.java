package com.eams.lifecycle.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 生命周期记录VO
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
public class LifecycleVO {

    /**
     * 主键ID
     */
    private Long id;

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
     * 变更前部门ID
     */
    private Long fromDepartmentId;

    /**
     * 变更前部门
     */
    private String fromDepartment;

    /**
     * 变更后部门ID
     */
    private Long toDepartmentId;

    /**
     * 变更后部门
     */
    private String toDepartment;

    /**
     * 变更前责任人
     */
    private String fromCustodian;

    /**
     * 变更后责任人
     */
    private String toCustodian;

    /**
     * 生命周期阶段
     */
    private Integer stage;

    /**
     * 阶段文本
     */
    private String stageText;

    /**
     * 上一阶段
     */
    private Integer previousStage;

    /**
     * 上一阶段文本
     */
    private String previousStageText;

    /**
     * 阶段变更日期
     */
    private LocalDate stageDate;

    /**
     * 变更原因
     */
    private String reason;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
