package com.eams.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统菜单权限实体
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Data
@TableName("sys_menu")
public class SysMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 父菜单ID，0表示顶级菜单
     */
    private Long parentId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 菜单类型：DIR-目录，MENU-菜单，BUTTON-按钮
     */
    private String menuType;

    /**
     * 权限标识（如：system:user:add）
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

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标记
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
