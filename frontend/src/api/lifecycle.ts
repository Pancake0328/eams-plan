import request from '@/utils/request'
import type {
    Result,
    PageResult,
    Lifecycle,
    LifecycleCreateRequest,
    Inventory,
    InventoryCreateRequest,
    InventoryExecuteRequest,
    Repair,
    RepairCreateRequest
} from '@/types'

/**
 * 生命周期管理 API
 */
export const lifecycleApi = {
    /**
     * 创建生命周期记录
     */
    createLifecycle(data: LifecycleCreateRequest): Promise<Result<number>> {
        return request.post('/lifecycle', data)
    },

    /**
     * 获取资产生命周期历史
     */
    getAssetLifecycleHistory(assetId: number): Promise<Result<Lifecycle[]>> {
        return request.get(`/lifecycle/asset/${assetId}`)
    },

    /**
     * 获取资产当前生命周期
     */
    getCurrentLifecycle(assetId: number): Promise<Result<Lifecycle>> {
        return request.get(`/lifecycle/asset/${assetId}/current`)
    },

    /**
     * 分页查询生命周期记录
     */
    getLifecyclePage(params: any): Promise<Result<PageResult<Lifecycle>>> {
        return request.get('/lifecycle', { params })
    },

    /**
     * 变更资产生命周期阶段
     */
    changeStage(data: LifecycleCreateRequest): Promise<Result<number>> {
        return request.put('/lifecycle/change-stage', data)
    }
}

/**
 * 盘点管理 API
 */
export const inventoryApi = {
    /**
     * 创建盘点计划
     */
    createInventory(data: InventoryCreateRequest): Promise<Result<number>> {
        return request.post('/inventory', data)
    },

    /**
     * 开始盘点
     */
    startInventory(inventoryId: number): Promise<Result<void>> {
        return request.put(`/inventory/${inventoryId}/start`)
    },

    /**
     * 执行盘点
     */
    executeInventory(data: InventoryExecuteRequest): Promise<Result<void>> {
        return request.put('/inventory/execute', data)
    },

    /**
     * 完成盘点
     */
    completeInventory(inventoryId: number): Promise<Result<void>> {
        return request.put(`/inventory/${inventoryId}/complete`)
    },

    /**
     * 取消盘点
     */
    cancelInventory(inventoryId: number): Promise<Result<void>> {
        return request.put(`/inventory/${inventoryId}/cancel`)
    },

    /**
     * 获取盘点详情
     */
    getInventoryDetail(inventoryId: number): Promise<Result<Inventory>> {
        return request.get(`/inventory/${inventoryId}`)
    },

    /**
     * 分页查询盘点计划
     */
    getInventoryPage(params: any): Promise<Result<PageResult<Inventory>>> {
        return request.get('/inventory', { params })
    }
}

/**
 * 报修管理 API
 */
export const repairApi = {
    /**
     * 创建报修记录
     */
    createRepair(data: RepairCreateRequest): Promise<Result<number>> {
        return request.post('/repair', data)
    },

    /**
     * 审批报修
     */
    approveRepair(repairId: number, approved: boolean, approver: string): Promise<Result<void>> {
        return request.put(`/repair/${repairId}/approve`, null, {
            params: { approved, approver }
        })
    },

    /**
     * 开始维修
     */
    startRepair(repairId: number, repairPerson: string): Promise<Result<void>> {
        return request.put(`/repair/${repairId}/start`, null, {
            params: { repairPerson }
        })
    },

    /**
     * 完成维修
     */
    completeRepair(repairId: number, repairCost: number, repairResult: string): Promise<Result<void>> {
        return request.put(`/repair/${repairId}/complete`, null, {
            params: { repairCost, repairResult }
        })
    },

    /**
     * 获取报修详情
     */
    getRepairDetail(repairId: number): Promise<Result<Repair>> {
        return request.get(`/repair/${repairId}`)
    },

    /**
     * 分页查询报修记录
     */
    getRepairPage(params: any): Promise<Result<PageResult<Repair>>> {
        return request.get('/repair', { params })
    }
}
