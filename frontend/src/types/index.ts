/**
 * 通用响应结果接口
 */
export interface Result<T = any> {
    code: number
    message: string
    data: T
    timestamp: number
}

/**
 * 分页响应接口
 */
export interface PageResult<T> {
    records: T[]
    total: number
    size: number
    current: number
    pages: number
}

/**
 * 用户信息接口
 */
export interface User {
    id: number
    username: string
    nickname: string
    email?: string
    phone?: string
    avatar?: string
    status: number
    createTime: string
    updateTime: string
}

/**
 * 创建用户请求
 */
export interface UserCreateRequest {
    username: string
    password: string
    nickname: string
    email?: string
    phone?: string
    avatar?: string
    status?: number
}

/**
 * 更新用户请求
 */
export interface UserUpdateRequest {
    nickname: string
    email?: string
    phone?: string
    avatar?: string
    status?: number
}

/**
 * 重置密码请求
 */
export interface ResetPasswordRequest {
    newPassword: string
}

/**
 * 用户分页查询参数
 */
export interface UserPageQuery {
    current?: number
    size?: number
    username?: string
    nickname?: string
    phone?: string
    status?: number
}

/**
 * 登录请求
 */
export interface LoginRequest {
    username: string
    password: string
}

/**
 * 登录响应
 */
export interface LoginResponse {
    token: string
    tokenType: string
    expiresIn: number
    userInfo: {
        id: number
        username: string
        nickname: string
        email?: string
        phone?: string
        avatar?: string
    }
}

/**
 * 资产分类
 */
export interface AssetCategory {
    id: number
    parentId: number
    categoryName: string
    categoryCode?: string
    sortOrder?: number
    description?: string
    createTime: string
    updateTime: string
}

/**
 * 资产分类树节点
 */
export interface CategoryTreeNode {
    id: number
    parentId: number
    categoryName: string
    categoryCode?: string
    sortOrder?: number
    description?: string
    children?: CategoryTreeNode[]
}

/**
 * 创建资产分类请求
 */
export interface CategoryCreateRequest {
    parentId: number
    categoryName: string
    categoryCode?: string
    sortOrder?: number
    description?: string
}

/**
 * 更新资产分类请求
 */
export interface CategoryUpdateRequest {
    categoryName: string
    categoryCode?: string
    sortOrder?: number
    description?: string
}
