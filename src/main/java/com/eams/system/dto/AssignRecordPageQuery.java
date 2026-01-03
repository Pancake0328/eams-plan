package com.eams.system.dto;

import lombok.Data;

/**
 * 资产分配记录查询DTO
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
public class AssignRecordPageQuery {

    /**
     * 当前页
     */
    private Integer current = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;

    /**
     * 资产ID
     */
    private Long assetId;

    /**
     * 员工ID
     */
    private Long empId;

    /**
     * 分配类型：1-分配给员工，2-回收，3-部门内调拨
     */
    private Integer assignType;
}
