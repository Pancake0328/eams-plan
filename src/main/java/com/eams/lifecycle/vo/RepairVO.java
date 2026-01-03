package com.eams.lifecycle.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 报修记录VO
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
public class RepairVO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 报修编号
     */
    private String repairNumber;

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
     * 故障描述
     */
    private String faultDescription;

    /**
     * 报修类型
     */
    private Integer repairType;

    /**
     * 报修类型文本
     */
    private String repairTypeText;

    /**
     * 优先级
     */
    private Integer repairPriority;

    /**
     * 优先级文本
     */
    private String repairPriorityText;

    /**
     * 报修人
     */
    private String reporter;

    /**
     * 报修时间
     */
    private LocalDateTime reportTime;

    /**
     * 报修状态
     */
    private Integer repairStatus;

    /**
     * 报修状态文本
     */
    private String repairStatusText;

    /**
     * 审批人
     */
    private String approver;

    /**
     * 审批时间
     */
    private LocalDateTime approvalTime;

    /**
     * 维修人
     */
    private String repairPerson;

    /**
     * 维修开始时间
     */
    private LocalDateTime repairStartTime;

    /**
     * 维修完成时间
     */
    private LocalDateTime repairEndTime;

    /**
     * 维修费用
     */
    private BigDecimal repairCost;

    /**
     * 维修结果
     */
    private String repairResult;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
