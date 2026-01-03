package com.eams.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eams.asset.dto.CategoryCreateRequest;
import com.eams.asset.dto.CategoryUpdateRequest;
import com.eams.asset.entity.AssetCategory;
import com.eams.asset.mapper.AssetCategoryMapper;
import com.eams.asset.service.AssetCategoryService;
import com.eams.asset.vo.CategoryTreeVO;
import com.eams.asset.vo.CategoryVO;
import com.eams.common.result.ResultCode;
import com.eams.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 资产分类服务实现类
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssetCategoryServiceImpl implements AssetCategoryService {

    private final AssetCategoryMapper categoryMapper;

    /**
     * 创建资产分类
     *
     * @param request 创建请求
     * @return 分类ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCategory(CategoryCreateRequest request) {
        // 检查父分类是否存在（如果不是顶级分类）
        if (request.getParentId() != 0) {
            AssetCategory parent = categoryMapper.selectById(request.getParentId());
            if (parent == null) {
                throw new BusinessException("父分类不存在");
            }
        }

        // 检查同级分类名称唯一性
        if (checkCategoryNameExists(request.getCategoryName(), request.getParentId(), null)) {
            throw new BusinessException("同级分类中已存在相同名称");
        }

        // 创建分类
        AssetCategory category = new AssetCategory();
        category.setParentId(request.getParentId());
        category.setCategoryName(request.getCategoryName());
        category.setCategoryCode(request.getCategoryCode());
        category.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        category.setDescription(request.getDescription());

        categoryMapper.insert(category);
        log.info("创建资产分类成功，分类名称: {}", category.getCategoryName());

        return category.getId();
    }

    /**
     * 更新资产分类
     *
     * @param id      分类ID
     * @param request 更新请求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(Long id, CategoryUpdateRequest request) {
        // 检查分类是否存在
        AssetCategory category = getCategoryEntityById(id);

        // 检查同级分类名称唯一性（排除自己）
        if (checkCategoryNameExists(request.getCategoryName(), category.getParentId(), id)) {
            throw new BusinessException("同级分类中已存在相同名称");
        }

        // 更新分类信息
        category.setCategoryName(request.getCategoryName());
        category.setCategoryCode(request.getCategoryCode());
        category.setSortOrder(request.getSortOrder());
        category.setDescription(request.getDescription());

        categoryMapper.updateById(category);
        log.info("更新资产分类成功，分类ID: {}", id);
    }

    /**
     * 删除资产分类
     *
     * @param id 分类ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long id) {
        // 检查分类是否存在
        AssetCategory category = getCategoryEntityById(id);

        // 检查是否存在子分类
        Long childCount = categoryMapper.selectCount(
                new LambdaQueryWrapper<AssetCategory>()
                        .eq(AssetCategory::getParentId, id));
        if (childCount > 0) {
            throw new BusinessException("该分类下存在子分类，无法删除");
        }

        // TODO: 检查是否存在关联资产（需要资产表实现后添加）
        // 暂时注释，等资产模块开发后再取消注释
        // Long assetCount = assetMapper.selectCount(
        // new LambdaQueryWrapper<Asset>()
        // .eq(Asset::getCategoryId, id)
        // );
        // if (assetCount > 0) {
        // throw new BusinessException("该分类下存在资产，无法删除");
        // }

        // 逻辑删除
        categoryMapper.deleteById(id);
        log.info("删除资产分类成功，分类ID: {}", id);
    }

    /**
     * 获取分类详情
     *
     * @param id 分类ID
     * @return 分类详情
     */
    @Override
    public CategoryVO getCategoryById(Long id) {
        AssetCategory category = getCategoryEntityById(id);
        return convertToVO(category);
    }

    /**
     * 获取完整分类树
     *
     * @return 分类树列表
     */
    @Override
    public List<CategoryTreeVO> getCategoryTree() {
        // 查询所有分类
        List<AssetCategory> allCategories = categoryMapper.selectList(
                new LambdaQueryWrapper<AssetCategory>()
                        .orderByAsc(AssetCategory::getSortOrder)
                        .orderByAsc(AssetCategory::getId));

        // 转换为树节点
        List<CategoryTreeVO> allNodes = allCategories.stream()
                .map(this::convertToTreeVO)
                .collect(Collectors.toList());

        // 构建树结构
        return buildTree(allNodes, 0L);
    }

    /**
     * 获取子分类列表
     *
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    @Override
    public List<CategoryVO> getChildCategories(Long parentId) {
        List<AssetCategory> children = categoryMapper.selectList(
                new LambdaQueryWrapper<AssetCategory>()
                        .eq(AssetCategory::getParentId, parentId)
                        .orderByAsc(AssetCategory::getSortOrder)
                        .orderByAsc(AssetCategory::getId));

        return children.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取分类实体（内部方法）
     *
     * @param id 分类ID
     * @return 分类实体
     */
    private AssetCategory getCategoryEntityById(Long id) {
        AssetCategory category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXISTED);
        }
        return category;
    }

    /**
     * 检查分类名称是否已存在
     *
     * @param categoryName 分类名称
     * @param parentId     父分类ID
     * @param excludeId    排除的分类ID（用于更新时排除自己）
     * @return true-已存在，false-不存在
     */
    private boolean checkCategoryNameExists(String categoryName, Long parentId, Long excludeId) {
        LambdaQueryWrapper<AssetCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetCategory::getCategoryName, categoryName)
                .eq(AssetCategory::getParentId, parentId);
        if (excludeId != null) {
            wrapper.ne(AssetCategory::getId, excludeId);
        }
        return categoryMapper.selectCount(wrapper) > 0;
    }

    /**
     * 构建树结构
     *
     * @param allNodes 所有节点
     * @param parentId 父节点ID
     * @return 树结构
     */
    private List<CategoryTreeVO> buildTree(List<CategoryTreeVO> allNodes, Long parentId) {
        List<CategoryTreeVO> result = new ArrayList<>();

        for (CategoryTreeVO node : allNodes) {
            if (node.getParentId().equals(parentId)) {
                // 递归查找子节点
                List<CategoryTreeVO> children = buildTree(allNodes, node.getId());
                node.setChildren(children.isEmpty() ? null : children);
                result.add(node);
            }
        }

        return result;
    }

    /**
     * 将实体转换为VO
     *
     * @param category 分类实体
     * @return 分类VO
     */
    private CategoryVO convertToVO(AssetCategory category) {
        return CategoryVO.builder()
                .id(category.getId())
                .parentId(category.getParentId())
                .categoryName(category.getCategoryName())
                .categoryCode(category.getCategoryCode())
                .sortOrder(category.getSortOrder())
                .description(category.getDescription())
                .createTime(category.getCreateTime())
                .updateTime(category.getUpdateTime())
                .build();
    }

    /**
     * 将实体转换为树节点VO
     *
     * @param category 分类实体
     * @return 树节点VO
     */
    private CategoryTreeVO convertToTreeVO(AssetCategory category) {
        return CategoryTreeVO.builder()
                .id(category.getId())
                .parentId(category.getParentId())
                .categoryName(category.getCategoryName())
                .categoryCode(category.getCategoryCode())
                .sortOrder(category.getSortOrder())
                .description(category.getDescription())
                .children(null)
                .build();
    }
}
