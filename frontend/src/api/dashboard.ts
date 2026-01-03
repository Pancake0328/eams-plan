import request from '@/utils/request'
import type {
    Result,
    DashboardStatistics,
    AssetDistribution,
    TimeTrendStatistics
} from '@/types'

/**
 * 仪表盘数据 API
 */
export const dashboardApi = {
    /**
     * 获取仪表盘概览数据
     */
    getStatistics(): Promise<Result<DashboardStatistics>> {
        return request.get('/dashboard/statistics')
    },

    /**
     * 获取部门资产分布
     */
    getDepartmentDistribution(): Promise<Result<AssetDistribution[]>> {
        return request.get('/dashboard/distribution/department')
    },

    /**
     * 获取资产分类分布
     */
    getCategoryDistribution(): Promise<Result<AssetDistribution[]>> {
        return request.get('/dashboard/distribution/category')
    },

    /**
     * 获取资产状态分布
     */
    getStatusDistribution(): Promise<Result<AssetDistribution[]>> {
        return request.get('/dashboard/distribution/status')
    },

    /**
     * 获取时间趋势统计
     */
    getTimeTrend(startDate: string, endDate: string): Promise<Result<TimeTrendStatistics[]>> {
        return request.get('/dashboard/trend', {
            params: { startDate, endDate }
        })
    }
}

/**
 * 报表导出 API
 */
export const exportApi = {
    /**
     * 导出资产列表
     */
    exportAssetList() {
        window.open(`${import.meta.env.VITE_API_BASE_URL}/export/asset-list`, '_blank')
    },

    /**
     * 导出仪表盘统计报表
     */
    exportDashboardStatistics() {
        window.open(`${import.meta.env.VITE_API_BASE_URL}/export/dashboard-statistics`, '_blank')
    },

    /**
     * 导出时间趋势统计
     */
    exportTimeTrend(startDate: string, endDate: string) {
        window.open(
            `${import.meta.env.VITE_API_BASE_URL}/export/time-trend?startDate=${startDate}&endDate=${endDate}`,
            '_blank'
        )
    }
}
