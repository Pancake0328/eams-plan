package com.eams.asset.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 资产分类树节点 VO
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "资产分类树节点")
public class CategoryTreeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    @Schema(description = "分类ID")
    private Long id;

    /**
     * 父分类ID
     */
    @Schema(description = "父分类ID")
    private Long parentId;

    /**
     * 分类名称
     */
    @Schema(description = "分类名称")
    private String categoryName;

    /**
     * 分类编码
     */
    @Schema(description = "分类编码")
    private String categoryCode;

    /**
     * 排序顺序
     */
    @Schema(description = "排序顺序")
    private Integer sortOrder;

    /**
     * 分类描述
     */
    @Schema(description = "分类描述")
    private String description;

    /**
     * 子分类列表
     */
    @Schema(description = "子分类列表")
    private List<CategoryTreeVO> children;
}
