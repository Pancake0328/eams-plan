package com.eams.asset.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 资产流转记录实体
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Data
@TableName("asset_record")
public class AssetRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 资产ID
     */
    private Long assetId;

    /**
     * 记录类型：1-入库，2-分配，3-调拨，4-归还，5-报废，6-送修，7-维修完成
     */
    private Integer recordType;

    /**
     * 原部门ID
     */
    private Long fromDepartmentId;

    /**
     * 原部门
     */
    private String fromDepartment;

    /**
     * 目标部门ID
     */
    private Long toDepartmentId;

    /**
     * 目标部门
     */
    private String toDepartment;

    /**
     * 原责任人
     */
    private String fromCustodian;

    /**
     * 目标责任人
     */
    private String toCustodian;

    /**
     * 原状态
     */
    private Integer oldStatus;

    /**
     * 新状态
     */
    private Integer newStatus;

    /**
     * 备注
     */
    private String remark;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 操作时间
     */
    private LocalDateTime operateTime;

    /**
     * 删除标记：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;

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
}
