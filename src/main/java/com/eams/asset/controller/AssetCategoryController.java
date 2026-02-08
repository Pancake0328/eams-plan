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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资产分类管理控制器
 *
 * @author Pancake
 * @since 2026-01-03
 */
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "资产分类管理", description = "资产分类管理相关接口")
@Slf4j
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
    @PreAuthorize("hasAuthority('asset:category:add')")
    public Result<Long> createCategory(@Validated @RequestBody CategoryCreateRequest request) {
        log.info("创建资产分类，入参：{}", request);
        Long categoryId = categoryService.createCategory(request);
        log.info("创建资产分类完成，id={}", categoryId);
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
    @PreAuthorize("hasAuthority('asset:category:edit')")
    public Result<Void> updateCategory(
            @Parameter(description = "分类ID") @PathVariable Long id,
            @Validated @RequestBody CategoryUpdateRequest request) {
        log.info("更新资产分类，id={}，入参：{}", id, request);
        categoryService.updateCategory(id, request);
        log.info("更新资产分类完成，id={}", id);
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
    @PreAuthorize("hasAuthority('asset:category:delete')")
    public Result<Void> deleteCategory(@Parameter(description = "分类ID") @PathVariable Long id) {
        log.info("删除资产分类，id={}", id);
        categoryService.deleteCategory(id);
        log.info("删除资产分类完成，id={}", id);
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
    @PreAuthorize("hasAuthority('asset:category:view')")
    public Result<CategoryVO> getCategoryById(@Parameter(description = "分类ID") @PathVariable Long id) {
        log.info("获取资产分类详情，id={}", id);
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
    @PreAuthorize("hasAuthority('asset:category:list')")
    public Result<List<CategoryTreeVO>> getCategoryTree() {
        log.info("获取资产分类树");
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
    @PreAuthorize("hasAuthority('asset:category:list')")
    public Result<List<CategoryVO>> getChildCategories(
            @Parameter(description = "父分类ID") @PathVariable Long parentId) {
        log.info("获取子分类列表，parentId={}", parentId);
        List<CategoryVO> children = categoryService.getChildCategories(parentId);
        return Result.success(children);
    }
}
