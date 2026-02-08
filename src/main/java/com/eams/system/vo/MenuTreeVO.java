package com.eams.system.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单树VO
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Data
public class MenuTreeVO {
    private Long id;
    private Long parentId;
    private String menuName;
    private String menuType;
    private String permissionCode;
    private String path;
    private String component;
    private String icon;
    private Integer sortOrder;
    private Integer status;
    private Integer visible;
    private List<MenuTreeVO> children = new ArrayList<>();
}
