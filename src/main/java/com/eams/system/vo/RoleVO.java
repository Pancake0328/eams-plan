package com.eams.system.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色VO
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
public class RoleVO {
    private Long id;
    private String roleCode;
    private String roleName;
    private Integer status;
    private String statusText;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
