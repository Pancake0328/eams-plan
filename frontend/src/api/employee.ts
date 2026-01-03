import request from '@/utils/request'
import type {
    Result,
    PageResult,
    Employee,
    EmployeeCreateRequest,
    EmployeeUpdateRequest,
    EmployeePageQuery
} from '@/types'

/**
 * 员工管理 API
 */
export const employeeApi = {
    /**
     * 创建员工
     */
    createEmployee(data: EmployeeCreateRequest): Promise<Result<number>> {
        return request.post('/employees', data)
    },

    /**
     * 更新员工
     */
    updateEmployee(id: number, data: EmployeeUpdateRequest): Promise<Result<void>> {
        return request.put(`/employees/${id}`, data)
    },

    /**
     * 删除员工
     */
    deleteEmployee(id: number): Promise<Result<void>> {
        return request.delete(`/employees/${id}`)
    },

    /**
     * 获取员工详情
     */
    getEmployeeById(id: number): Promise<Result<Employee>> {
        return request.get(`/employees/${id}`)
    },

    /**
     * 分页查询员工
     */
    getEmployeePage(params: EmployeePageQuery): Promise<Result<PageResult<Employee>>> {
        return request.get('/employees', { params })
    },

    /**
     * 更新员工状态
     */
    updateEmployeeStatus(id: number, status: number): Promise<Result<void>> {
        return request.put(`/employees/${id}/status`, null, { params: { status } })
    }
}
