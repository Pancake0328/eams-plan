package com.eams.system.dto;

import lombok.Data;

/**
 * 员工分页查询DTO
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
public class EmployeePageQuery {

    /**
     * 当前页
     */
    private Integer current = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;

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
     * 状态：1-在职，0-离职
     */
    private Integer status;
}
