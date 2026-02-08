import request from '@/utils/request'
import type {
    Result,
    Department,
    DepartmentCreateRequest,
    DepartmentUpdateRequest
} from '@/types'

/**
 * 部门管理 API
 */
export const departmentApi = {
    /**
     * 创建部门
     */
    createDepartment(data: DepartmentCreateRequest): Promise<Result<number>> {
        return request.post('/departments', data)
    },

    /**
     * 更新部门
     */
    updateDepartment(id: number, data: DepartmentUpdateRequest): Promise<Result<void>> {
        return request.put(`/departments/${id}`, data)
    },

    /**
     * 删除部门
     */
    deleteDepartment(id: number): Promise<Result<void>> {
        return request.delete(`/departments/${id}`)
    },

    /**
     * 获取部门详情
     */
    getDepartmentById(id: number): Promise<Result<Department>> {
        return request.get(`/departments/${id}`)
    },

    /**
     * 获取部门树
     */
    getDepartmentTree(): Promise<Result<Department[]>> {
        return request.get('/departments/tree')
    },

    /**
     * 获取子部门列表
     */
    getChildDepartments(parentId: number): Promise<Result<Department[]>> {
        return request.get(`/departments/children/${parentId}`)
    }
}
