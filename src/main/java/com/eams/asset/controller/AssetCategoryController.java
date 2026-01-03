package com.eams.asset.controller;

import com.eams.aop.OperationLog;
import com.eams.asset.dto.CategoryCreateRequest;
import com.eams.asset.dto.CategoryUpdateRequest;
import com.eams.asset.service.AssetCategoryService;
import com.eams.asset.vo.CategoryTreeVO;
import com.eams.asset.vo.CategoryVO;
import com.eams.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资产分类管理控制器
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "资产分类管理", description = "资产分类管理相关接口")
public class AssetCategoryController {

    private final AssetCategoryService categoryService;

    /**
     * 创建资产分类
     *
     * @param request 创建请求
     * @return 分类ID
     */
    @PostMapping
    @Operation(summary = "创建资产分类", description = "创建新的资产分类")
    @OperationLog(value = "创建资产分类", type = "CREATE")
    public Result<Long> createCategory(@Validated @RequestBody CategoryCreateRequest request) {
        Long categoryId = categoryService.createCategory(request);
        return Result.success("创建资产分类成功", categoryId);
    }

    /**
     * 更新资产分类
     *
     * @param id      分类ID
     * @param request 更新请求
     * @return 操作结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新资产分类", description = "更新资产分类信息")
    @OperationLog(value = "更新资产分类", type = "UPDATE")
    public Result<Void> updateCategory(
            @Parameter(description = "分类ID") @PathVariable Long id,
            @Validated @RequestBody CategoryUpdateRequest request) {
        categoryService.updateCategory(id, request);
        return Result.success();
    }

    /**
     * 删除资产分类
     *
     * @param id 分类ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除资产分类", description = "逻辑删除资产分类")
    @OperationLog(value = "删除资产分类", type = "DELETE")
    public Result<Void> deleteCategory(@Parameter(description = "分类ID") @PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }

    /**
     * 获取分类详情
     *
     * @param id 分类ID
     * @return 分类详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取分类详情", description = "根据ID获取资产分类详细信息")
    public Result<CategoryVO> getCategoryById(@Parameter(description = "分类ID") @PathVariable Long id) {
        CategoryVO categoryVO = categoryService.getCategoryById(id);
        return Result.success(categoryVO);
    }

    /**
     * 获取完整分类树
     *
     * @return 分类树列表
     */
    @GetMapping("/tree")
    @Operation(summary = "获取完整分类树", description = "获取树形结构的完整资产分类列表")
    public Result<List<CategoryTreeVO>> getCategoryTree() {
        List<CategoryTreeVO> tree = categoryService.getCategoryTree();
        return Result.success(tree);
    }

    /**
     * 获取子分类列表
     *
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    @GetMapping("/children/{parentId}")
    @Operation(summary = "获取子分类列表", description = "根据父分类ID获取子分类列表")
    public Result<List<CategoryVO>> getChildCategories(
            @Parameter(description = "父分类ID") @PathVariable Long parentId) {
        List<CategoryVO> children = categoryService.getChildCategories(parentId);
        return Result.success(children);
    }
}
