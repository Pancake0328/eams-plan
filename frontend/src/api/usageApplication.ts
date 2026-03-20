import request from '@/utils/request'
import type {
  Result,
  PageResult,
  UsageApplication,
  UsageApplicationCreateRequest,
  UsageApplicationAuditRequest,
  UsageApplicationPageQuery
} from '@/types'

/**
 * 资产使用申请 API
 */
export const usageApplicationApi = {
  /**
   * 创建资产使用申请
   */
  createApplication(data: UsageApplicationCreateRequest): Promise<Result<number>> {
    return request.post('/asset-usage-applications', data)
  },

  /**
   * 分页查询资产使用申请
   */
  getApplicationPage(params: UsageApplicationPageQuery): Promise<Result<PageResult<UsageApplication>>> {
    return request.get('/asset-usage-applications', { params })
  },

  /**
   * 分页查询我的资产使用申请
   */
  getMyApplicationPage(params: UsageApplicationPageQuery): Promise<Result<PageResult<UsageApplication>>> {
    return request.get('/asset-usage-applications/my', { params })
  },

  /**
   * 审核资产使用申请
   */
  auditApplication(id: number, data: UsageApplicationAuditRequest): Promise<Result<void>> {
    return request.put(`/asset-usage-applications/${id}/audit`, data)
  }
}
