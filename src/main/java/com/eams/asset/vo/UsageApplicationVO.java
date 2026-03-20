package com.eams.asset.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 资产使用申请视图对象
 */
@Data
@Schema(description = "资产使用申请信息")
public class UsageApplicationVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "申请ID")
    private Long id;

    @Schema(description = "申请单号")
    private String applicationNumber;

    @Schema(description = "资产ID")
    private Long assetId;

    @Schema(description = "资产编号")
    private String assetNumber;

    @Schema(description = "资产名称")
    private String assetName;

    @Schema(description = "申请人")
    private String applicant;

    @Schema(description = "申请人姓名")
    private String applicantName;

    @Schema(description = "申请人部门ID")
    private Long applicantDepartmentId;

    @Schema(description = "申请人部门")
    private String applicantDepartment;

    @Schema(description = "申请原因")
    private String applyReason;

    @Schema(description = "申请状态：1-待审核，2-已通过，3-已拒绝")
    private Integer applyStatus;

    @Schema(description = "申请状态文本")
    private String applyStatusText;

    @Schema(description = "审核人")
    private String auditor;

    @Schema(description = "审核人姓名")
    private String auditorName;

    @Schema(description = "审核时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditTime;

    @Schema(description = "审核备注")
    private String auditRemark;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
