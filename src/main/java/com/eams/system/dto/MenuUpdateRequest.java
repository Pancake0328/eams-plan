package com.eams.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新菜单权限请求DTO
 *
 * @author Pancake
 * @since 2026-02-08
 */
@Data
public class MenuUpdateRequest {

    /**
     * 父菜单ID，0表示顶级菜单
     */
    @NotNull(message = "父菜单ID不能为空")
    private Long parentId;

    /**
     * 菜单名称
     */
    @NotBlank(message = "菜单名称不能为空")
    private String menuName;

    /**
     * 菜单类型：DIR-目录，MENU-菜单，BUTTON-按钮
     */
    @NotBlank(message = "菜单类型不能为空")
    private String menuType;

    /**
     * 权限标识
     */
    private String permissionCode;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;

    /**
     * 是否可见：1-可见，0-隐藏
     */
    private Integer visible;
}
