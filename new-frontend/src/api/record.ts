import request from '@/utils/request'
import type {
    Result,
    PageResult,
    AssetRecord,
    RecordCreateRequest,
    RecordPageQuery
} from '@/types'

/**
 * 资产流转记录管理 API
 */
export const recordApi = {
    /**
     * 资产入库
     */
    assetIn(data: RecordCreateRequest): Promise<Result<number>> {
        return request.post('/asset-records/in', data)
    },

    /**
     * 资产分配
     */
    allocateAsset(data: RecordCreateRequest): Promise<Result<number>> {
        return request.post('/asset-records/allocate', data)
    },

    /**
     * 资产调拨
     */
    transferAsset(data: RecordCreateRequest): Promise<Result<number>> {
        return request.post('/asset-records/transfer', data)
    },

    /**
     * 资产归还
     */
    returnAsset(data: RecordCreateRequest): Promise<Result<number>> {
        return request.post('/asset-records/return', data)
    },

    /**
     * 资产报废
     */
    scrapAsset(data: RecordCreateRequest): Promise<Result<number>> {
        return request.post('/asset-records/scrap', data)
    },

    /**
     * 资产送修
     */
    sendForRepair(data: RecordCreateRequest): Promise<Result<number>> {
        return request.post('/asset-records/repair', data)
    },

    /**
     * 维修完成
     */
    repairComplete(data: RecordCreateRequest): Promise<Result<number>> {
        return request.post('/asset-records/repair-complete', data)
    },

    /**
     * 分页查询流转记录
     */
    getRecordPage(query: RecordPageQuery): Promise<Result<PageResult<AssetRecord>>> {
        return request.get('/asset-records', { params: query })
    },

    /**
     * 查询资产流转历史
     */
    getAssetRecordHistory(assetId: number): Promise<Result<AssetRecord[]>> {
        return request.get(`/asset-records/asset/${assetId}`)
    }
}
