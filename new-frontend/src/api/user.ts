import request from '@/utils/request'
import type {
    Result,
    PageResult,
    User,
    UserCreateRequest,
    UserUpdateRequest,
    ResetPasswordRequest,
    UserPageQuery,
    LoginRequest,
    LoginResponse
} from '@/types'

/**
 * 用户管理 API
 */
export const userApi = {
    /**
     * 用户登录
     */
    login(data: LoginRequest): Promise<Result<LoginResponse>> {
        return request.post('/auth/login', data)
    },

    /**
     * 分页查询用户列表
     */
    getUserPage(params: UserPageQuery): Promise<Result<PageResult<User>>> {
        return request.get('/users', { params })
    },

    /**
     * 获取用户详情
     */
    getUserById(id: number): Promise<Result<User>> {
        return request.get(`/users/${id}`)
    },

    /**
     * 创建用户
     */
    createUser(data: UserCreateRequest): Promise<Result<number>> {
        return request.post('/users', data)
    },

    /**
     * 更新用户
     */
    updateUser(id: number, data: UserUpdateRequest): Promise<Result<void>> {
        return request.put(`/users/${id}`, data)
    },

    /**
     * 删除用户
     */
    deleteUser(id: number): Promise<Result<void>> {
        return request.delete(`/users/${id}`)
    },

    /**
     * 更新用户状态
     */
    updateUserStatus(id: number, status: number): Promise<Result<void>> {
        return request.put(`/users/${id}/status`, null, { params: { status } })
    },

    /**
     * 重置密码
     */
    resetPassword(id: number, data: ResetPasswordRequest): Promise<Result<void>> {
        return request.put(`/users/${id}/password`, data)
    }
}
