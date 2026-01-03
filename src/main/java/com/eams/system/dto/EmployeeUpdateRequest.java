package com.eams.system.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 更新员工请求DTO
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
public class EmployeeUpdateRequest {

    /**
     * 员工姓名
     */
    private String empName;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 职位
     */
    private String position;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 性别：1-男，2-女
     */
    private Integer gender;

    /**
     * 入职日期
     */
    private LocalDate entryDate;

    /**
     * 状态：1-在职，0-离职
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
