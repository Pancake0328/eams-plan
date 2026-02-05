package com.eams.asset.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 流转记录视图对象 VO
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "流转记录信息")
public class RecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @Schema(description = "记录ID")
    private Long id;

    /**
     * 资产ID
     */
    @Schema(description = "资产ID")
    private Long assetId;

    /**
     * 资产编号
     */
    @Schema(description = "资产编号")
    private String assetNumber;

    /**
     * 资产名称
     */
    @Schema(description = "资产名称")
    private String assetName;

    /**
     * 记录类型
     */
    @Schema(description = "记录类型")
    private Integer recordType;

    /**
     * 记录类型文本
     */
    @Schema(description = "记录类型文本")
    private String recordTypeText;

    /**
     * 原部门
     */
    @Schema(description = "原部门")
    private String fromDepartment;

    /**
     * 原部门ID
     */
    @Schema(description = "原部门ID")
    private Long fromDepartmentId;

    /**
     * 目标部门
     */
    @Schema(description = "目标部门")
    private String toDepartment;

    /**
     * 目标部门ID
     */
    @Schema(description = "目标部门ID")
    private Long toDepartmentId;

    /**
     * 原责任人
     */
    @Schema(description = "原责任人")
    private String fromCustodian;

    /**
     * 目标责任人
     */
    @Schema(description = "目标责任人")
    private String toCustodian;

    /**
     * 原状态
     */
    @Schema(description = "原状态")
    private Integer oldStatus;

    /**
     * 原状态文本
     */
    @Schema(description = "原状态文本")
    private String oldStatusText;

    /**
     * 新状态
     */
    @Schema(description = "新状态")
    private Integer newStatus;

    /**
     * 新状态文本
     */
    @Schema(description = "新状态文本")
    private String newStatusText;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 操作人
     */
    @Schema(description = "操作人")
    private String operator;

    /**
     * 操作时间
     */
    @Schema(description = "操作时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operateTime;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
