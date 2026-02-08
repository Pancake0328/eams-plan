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
    purchaseStatusText: string
    applicantId: number
    applicantName: string
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

// ==================== 采购统计 ====================
export interface PurchaseBillStatistic {
    billPeriod: string
    billType: string
    orderCount: number
    totalAmount: number
    averageAmount: number
}

export interface PurchaseFundOverview {
    orderCount: number
    totalAmount: number
    averageAmount: number
}

export interface PurchaseFundStatistic {
    dimension: string
    orderCount: number
    totalAmount: number
}

export interface AssetInboundRequest {
    detailId: number
    quantity: number
    storageLocation?: string
    departmentId?: number
    remark?: string
}

// 批量入库请求
export interface BatchInboundRequest {
    inboundList: AssetInboundRequest[]
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
    inboundAsset(data: AssetInboundRequest): Promise<Result<number[]>> {
        return request.post('/purchase/inbound', data)
    },

    /**
     * 批量入库
     */
    batchInbound(data: BatchInboundRequest): Promise<Result<number[]>> {
        return request.post('/purchase/batch-inbound', data)
    },

    /**
     * 月度账单统计
     */
    getMonthlyBillStatistic(year: number, month: number): Promise<Result<PurchaseBillStatistic>> {
        return request.get('/purchase/statistics/bill/monthly', { params: { year, month } })
    },

    /**
     * 年度账单统计
     */
    getAnnualBillStatistic(year: number): Promise<Result<PurchaseBillStatistic>> {
        return request.get('/purchase/statistics/bill/annual', { params: { year } })
    },

    /**
     * 资金概览
     */
    getFundOverview(): Promise<Result<PurchaseFundOverview>> {
        return request.get('/purchase/statistics/overview')
    },

    /**
     * 按供应商统计
     */
    getFundStatisticsBySupplier(): Promise<Result<PurchaseFundStatistic[]>> {
        return request.get('/purchase/statistics/by-supplier')
    },

    /**
     * 按时间统计
     */
    getFundStatisticsByTime(startDate: string, endDate: string): Promise<Result<PurchaseFundStatistic[]>> {
        return request.get('/purchase/statistics/by-time', { params: { startDate, endDate } })
    }
}
