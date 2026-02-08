package com.eams.system.dto;

import lombok.Data;

/**
 * 更新部门请求DTO
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Data
public class DepartmentUpdateRequest {

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 部门编码
     */
    private String deptCode;

    /**
     * 部门负责人
     */
    private String leader;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 显示顺序
     */
    private Integer sortOrder;

    /**
     * 状态：1-正常，0-停用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
