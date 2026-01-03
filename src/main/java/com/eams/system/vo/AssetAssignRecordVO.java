package com.eams.system.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 资产分配记录VO
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
public class AssetAssignRecordVO {

    /**
     * 记录ID
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
     * 分配类型：1-分配给员工，2-回收，3-部门内调拨
     */
    private Integer assignType;

    /**
     * 分配类型文本
     */
    private String assignTypeText;

    /**
     * 原员工ID
     */
    private Long fromEmpId;

    /**
     * 原员工姓名
     */
    private String fromEmpName;

    /**
     * 目标员工ID
     */
    private Long toEmpId;

    /**
     * 目标员工姓名
     */
    private String toEmpName;

    /**
     * 原部门ID
     */
    private Long fromDeptId;

    /**
     * 原部门名称
     */
    private String fromDeptName;

    /**
     * 目标部门ID
     */
    private Long toDeptId;

    /**
     * 目标部门名称
     */
    private String toDeptName;

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
    private LocalDateTime createTime;
}
