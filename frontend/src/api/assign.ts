import request from '@/utils/request'
import type {
    Result,
    PageResult,
    AssetAssignRequest,
    AssetAssignRecord,
    AssignRecordPageQuery
} from '@/types'

/**
 * 资产分配管理 API
 */
export const assignApi = {
    /**
     * 分配资产给员工
     */
    assignAsset(data: AssetAssignRequest): Promise<Result<number>> {
        return request.post('/asset-assigns/assign', data)
    },

    /**
     * 回收资产
     */
    returnAsset(assetId: number, remark?: string): Promise<Result<number>> {
        return request.post(`/asset-assigns/return/${assetId}`, null, { params: { remark } })
    },

    /**
     * 部门内调拨资产
     */
    transferAsset(data: AssetAssignRequest): Promise<Result<number>> {
        return request.post('/asset-assigns/transfer', data)
    },

    /**
     * 分页查询分配记录
     */
    getAssignRecordPage(params: AssignRecordPageQuery): Promise<Result<PageResult<AssetAssignRecord>>> {
        return request.get('/asset-assigns', { params })
    },

    /**
     * 查询资产分配历史
     */
    getAssetAssignHistory(assetId: number): Promise<Result<AssetAssignRecord[]>> {
        return request.get(`/asset-assigns/asset/${assetId}`)
    },

    /**
     * 查询员工资产分配历史
     */
    getEmployeeAssignHistory(empId: number): Promise<Result<AssetAssignRecord[]>> {
        return request.get(`/asset-assigns/employee/${empId}`)
    }
}
