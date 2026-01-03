import request from '@/utils/request'
import type {
    Result,
    AssetCategory,
    CategoryTreeNode,
    CategoryCreateRequest,
    CategoryUpdateRequest
} from '@/types'

/**
 * 资产分类管理 API
 */
export const categoryApi = {
    /**
     * 获取完整分类树
     */
    getCategoryTree(): Promise<Result<CategoryTreeNode[]>> {
        return request.get('/categories/tree')
    },

    /**
     * 获取分类详情
     */
    getCategoryById(id: number): Promise<Result<AssetCategory>> {
        return request.get(`/categories/${id}`)
    },

    /**
     * 获取子分类列表
     */
    getChildCategories(parentId: number): Promise<Result<AssetCategory[]>> {
        return request.get(`/categories/children/${parentId}`)
    },

    /**
     * 创建分类
     */
    createCategory(data: CategoryCreateRequest): Promise<Result<number>> {
        return request.post('/categories', data)
    },

    /**
     * 更新分类
     */
    updateCategory(id: number, data: CategoryUpdateRequest): Promise<Result<void>> {
        return request.put(`/categories/${id}`, data)
    },

    /**
     * 删除分类
     */
    deleteCategory(id: number): Promise<Result<void>> {
        return request.delete(`/categories/${id}`)
    }
}
