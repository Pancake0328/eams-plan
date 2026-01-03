package com.eams.system.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 员工信息VO
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
public class EmployeeVO {

    /**
     * 员工ID
     */
    private Long id;

    /**
     * 员工工号
     */
    private String empNo;

    /**
     * 员工姓名
     */
    private String empName;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 部门名称
     */
    private String deptName;

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
     * 性别文本
     */
    private String genderText;

    /**
     * 入职日期
     */
    private LocalDate entryDate;

    /**
     * 状态：1-在职，0-离职
     */
    private Integer status;

    /**
     * 状态文本
     */
    private String statusText;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
