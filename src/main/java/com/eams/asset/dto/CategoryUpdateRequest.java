package com.eams.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * 更新资产分类请求 DTO
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Data
@Schema(description = "更新资产分类请求")
public class CategoryUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称长度不能超过50个字符")
    @Schema(description = "分类名称", example = "办公设备")
    private String categoryName;

    /**
     * 分类编码
     */
    @Size(max = 50, message = "分类编码长度不能超过50个字符")
    @Schema(description = "分类编码", example = "OFFICE")
    private String categoryCode;

    /**
     * 排序顺序
     */
    @Schema(description = "排序顺序", example = "1")
    private Integer sortOrder;

    /**
     * 分类描述
     */
    @Size(max = 200, message = "分类描述长度不能超过200个字符")
    @Schema(description = "分类描述")
    private String description;
}
