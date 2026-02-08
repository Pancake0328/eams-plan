package com.eams.system.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 部门树节点VO
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Data
public class DepartmentTreeNode {

    /**
     * 部门ID
     */
    private Long id;

    /**
     * 父部门ID
     */
    private Long parentId;

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

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 子部门列表
     */
    private List<DepartmentTreeNode> children;
}
