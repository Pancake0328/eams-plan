package com.eams.asset.service;

import com.eams.asset.dto.CategoryCreateRequest;
import com.eams.asset.dto.CategoryUpdateRequest;
import com.eams.asset.vo.CategoryTreeVO;
import com.eams.asset.vo.CategoryVO;

import java.util.List;

/**
 * 资产分类服务接口
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
public interface AssetCategoryService {

    /**
     * 创建资产分类
     *
     * @param request 创建请求
     * @return 分类ID
     */
    Long createCategory(CategoryCreateRequest request);

    /**
     * 更新资产分类
     *
     * @param id      分类ID
     * @param request 更新请求
     */
    void updateCategory(Long id, CategoryUpdateRequest request);

    /**
     * 删除资产分类
     *
     * @param id 分类ID
     */
    void deleteCategory(Long id);

    /**
     * 获取分类详情
     *
     * @param id 分类ID
     * @return 分类详情
     */
    CategoryVO getCategoryById(Long id);

    /**
     * 获取完整分类树
     *
     * @return 分类树列表
     */
    List<CategoryTreeVO> getCategoryTree();

    /**
     * 获取子分类列表
     *
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<CategoryVO> getChildCategories(Long parentId);
}
