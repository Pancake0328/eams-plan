import request from '@/utils/request'
import type {
    Result,
    PageResult,
    Purchase,
    PurchaseCreateRequest,
    DepreciationInfo,
    Bill,
    FinanceStatistics
} from '@/types'

/**
 * 财务管理 API
 */
export const financeApi = {
    // ==================== 采购管理 ====================

    /**
     * 创建采购记录
     */
    createPurchase(data: PurchaseCreateRequest): Promise<Result<number>> {
        return request.post('/finance/purchases', data)
    },

    /**
     * 分页查询采购记录
     */
    getPurchasePage(params: any): Promise<Result<PageResult<Purchase>>> {
        return request.get('/finance/purchases', { params })
    },

    /**
     * 审批采购记录
     */
    approvePurchase(id: number, approved: boolean, approver: string): Promise<Result<void>> {
        return request.put(`/finance/purchases/${id}/approve`, null, {
            params: { approved, approver }
        })
    },

    /**
     * 删除采购记录
     */
    deletePurchase(id: number): Promise<Result<void>> {
        return request.delete(`/finance/purchases/${id}`)
    },

    // ==================== 折旧计算 ====================

    /**
     * 计算单个资产折旧
     */
    calculateAssetDepreciation(assetId: number, targetDate?: string): Promise<Result<DepreciationInfo>> {
        return request.get(`/finance/depreciation/asset/${assetId}`, {
            params: { targetDate }
        })
    },

    /**
     * 计算所有资产折旧
     */
    calculateAllDepreciation(targetDate?: string): Promise<Result<DepreciationInfo[]>> {
        return request.get('/finance/depreciation/all', {
            params: { targetDate }
        })
    },

    // ==================== 账单管理 ====================

    /**
     * 生成月度账单
     */
    generateMonthlyBill(year: number, month: number): Promise<Result<number>> {
        return request.post('/finance/bills/monthly', null, {
            params: { year, month }
        })
    },

    /**
     * 生成年度账单
     */
    generateAnnualBill(year: number): Promise<Result<number>> {
        return request.post('/finance/bills/annual', null, {
            params: { year }
        })
    },

    /**
     * 获取账单详情
     */
    getBillDetail(billId: number): Promise<Result<Bill>> {
        return request.get(`/finance/bills/${billId}`)
    },

    /**
     * 确认账单
     */
    confirmBill(billId: number): Promise<Result<void>> {
        return request.put(`/finance/bills/${billId}/confirm`)
    },

    /**
     * 分页查询账单
     */
    getBillPage(params: any): Promise<Result<PageResult<Bill>>> {
        return request.get('/finance/bills', { params })
    },

    /**
     * 删除账单
     */
    deleteBill(billId: number): Promise<Result<void>> {
        return request.delete(`/finance/bills/${billId}`)
    },

    // ==================== 资金统计 ====================

    /**
     * 按部门统计
     */
    statisticsByDepartment(): Promise<Result<FinanceStatistics[]>> {
        return request.get('/finance/statistics/by-department')
    },

    /**
     * 按时间统计
     */
    statisticsByTime(startDate: string, endDate: string): Promise<Result<FinanceStatistics[]>> {
        return request.get('/finance/statistics/by-time', {
            params: { startDate, endDate }
        })
    },

    /**
     * 财务概览
     */
    getFinanceOverview(): Promise<Result<FinanceStatistics>> {
        return request.get('/finance/statistics/overview')
    }
}
