package com.eams.lifecycle.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 资产生命周期记录实体
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
@TableName("asset_lifecycle")
public class AssetLifecycle {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 资产ID
     */
    private Long assetId;

    /**
     * 生命周期阶段：1-购入 2-使用中 3-维修中 4-闲置 5-报废
     */
    private Integer stage;

    /**
     * 上一阶段
     */
    private Integer previousStage;

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
