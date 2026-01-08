import request from '@/utils/request'
import type { Result, Page } from '@/types'

// ==================== 采购相关类型 ====================
export interface Purchase {
    id: number
    purchaseNumber: string
    purchaseDate: string
    supplier?: string
    totalAmount: number
    purchaseStatus: number
    purchaseStatusText?: string
    applicant: string
    approver?: string
    approveTime?: string
    remark?: string
    createTime: string
    details?: PurchaseDetail[]
}

export interface PurchaseDetail {
    id: number
    purchaseId: number
    assetName: string
    categoryId?: number
    categoryName?: string
    specifications?: string
    manufacturer?: string
    unitPrice: number
    quantity: number
    inboundQuantity: number
    remainingQuantity: number
    totalAmount: number
    expectedLife?: number
    remark?: string
    detailStatus: number
    detailStatusText?: string
}

export interface PurchaseCreateRequest {
    purchaseDate: string
    supplier?: string
    applicant: string
    remark?: string
    details: PurchaseDetailRequest[]
}

export interface PurchaseDetailRequest {
    assetName: string
    categoryId?: number
    specifications?: string
    manufacturer?: string
    unitPrice: number
    quantity: number
    expectedLife?: number
    remark?: string
}

export interface AssetInboundRequest {
    detailId: number
    storageLocation?: string
    department?: string
    custodian?: string
    remark?: string
}

/**
 * 采购管理API
 */
export const purchaseApi = {
    /**
     * 创建采购单
     */
    createPurchase(data: PurchaseCreateRequest): Promise<Result<number>> {
        return request.post('/purchase', data)
    },

    /**
     * 获取采购单详情
     */
    getPurchaseById(id: number): Promise<Result<Purchase>> {
        return request.get(`/purchase/${id}`)
    },

    /**
     * 分页查询采购单
     */
    getPurchasePage(params: {
        current: number
        size: number
        purchaseNumber?: string
        status?: number
    }): Promise<Result<Page<Purchase>>> {
        return request.get('/purchase', { params })
    },

    /**
     * 取消采购单
     */
    cancelPurchase(id: number): Promise<Result<void>> {
        return request.put(`/purchase/${id}/cancel`)
    },

    /**
     * 获取待入库明细列表
     */
    getPendingInboundDetails(params: {
        current: number
        size: number
    }): Promise<Result<Page<PurchaseDetail>>> {
        return request.get('/purchase/pending-inbound', { params })
    },

    /**
     * 资产入库
     */
    inboundAsset(data: AssetInboundRequest): Promise<Result<number>> {
        return request.post('/purchase/inbound', data)
    }
}
