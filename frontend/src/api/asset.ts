import request from '@/utils/request'
import type {
    Result,
    PageResult,
    Asset,
    AssetCreateRequest,
    AssetUpdateRequest,
    AssetPageQuery
} from '@/types'

/**
 * 资产信息管理 API
 */
export const assetApi = {
    /**
     * 分页查询资产
     */
    getAssetPage(query: AssetPageQuery): Promise<Result<PageResult<Asset>>> {
        return request.get('/assets', { params: query })
    },

    /**
     * 获取资产详情
     */
    getAssetById(id: number): Promise<Result<Asset>> {
        return request.get(`/assets/${id}`)
    },

    /**
     * 创建资产
     */
    createAsset(data: AssetCreateRequest): Promise<Result<number>> {
        return request.post('/assets', data)
    },

    /**
     * 更新资产
     */
    updateAsset(id: number, data: AssetUpdateRequest): Promise<Result<void>> {
        return request.put(`/assets/${id}`, data)
    },

    /**
     * 删除资产
     */
    deleteAsset(id: number): Promise<Result<void>> {
        return request.delete(`/assets/${id}`)
    },

    /**
     * 更新资产状态
     */
    updateAssetStatus(id: number, status: number): Promise<Result<void>> {
        return request.put(`/assets/${id}/status`, null, { params: { status } })
    }
}
