package com.eams.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 资产分配记录实体
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
@TableName("asset_assign_record")
public class AssetAssignRecord {

    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 资产ID
     */
    private Long assetId;

    /**
     * 分配类型：1-分配给员工，2-回收，3-部门内调拨
     */
    private Integer assignType;

    /**
     * 原员工ID
     */
    private Long fromEmpId;

    /**
     * 目标员工ID
     */
    private Long toEmpId;

    /**
     * 原部门ID
     */
    private Long fromDeptId;

    /**
     * 目标部门ID
     */
    private Long toDeptId;

    /**
     * 分配日期
     */
    private LocalDateTime assignDate;

    /**
     * 归还日期
     */
    private LocalDateTime returnDate;

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
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
