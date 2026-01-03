package com.eams.lifecycle.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 资产报修记录实体
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
@TableName("asset_repair")
public class AssetRepair {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
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
     * 报修类型：1-日常维修 2-故障维修 3-预防性维修
     */
    private Integer repairType;

    /**
     * 优先级：1-紧急 2-普通 3-低
     */
    private Integer repairPriority;

    /**
     * 报修人
     */
    private String reporter;

    /**
     * 报修时间
     */
    private LocalDateTime reportTime;

    /**
     * 报修状态：1-待审批 2-已审批 3-维修中 4-已完成 5-已拒绝
     */
    private Integer repairStatus;

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
