import request from '@/utils/request'
import type { Result, Page } from '@/types'

// ==================== 角色相关类型 ====================
export interface Role {
    id: number
    roleCode: string
    roleName: string
    status: number
    statusText?: string
    remark?: string
    createTime: string
    updateTime: string
}

export interface RoleCreateRequest {
    roleCode: string
    roleName: string
    status?: number
    remark?: string
}

export interface AssignPermissionRequest {
    roleId: number
    menuIds: number[]
}

// ==================== 菜单相关类型 ====================
export interface Menu {
    id: number
    parentId: number
    menuName: string
    menuType: 'DIR' | 'MENU' | 'BUTTON'
    permissionCode?: string
    path?: string
    component?: string
    icon?: string
    sortOrder: number
    status: number
    visible: number
    children?: Menu[]
}

/**
 * 角色管理API
 */
export const roleApi = {
    /**
     * 创建角色
     */
    createRole(data: RoleCreateRequest): Promise<Result<number>> {
        return request.post('/role', data)
    },

    /**
     * 更新角色
     */
    updateRole(id: number, data: RoleCreateRequest): Promise<Result<void>> {
        return request.put(`/role/${id}`, data)
    },

    /**
     * 删除角色
     */
    deleteRole(id: number): Promise<Result<void>> {
        return request.delete(`/role/${id}`)
    },

    /**
     * 获取角色详情
     */
    getRoleById(id: number): Promise<Result<Role>> {
        return request.get(`/role/${id}`)
    },

    /**
     * 分页查询角色
     */
    getRolePage(params: {
        current: number
        size: number
        roleName?: string
        status?: number
    }): Promise<Result<Page<Role>>> {
        return request.get('/role', { params })
    },

    /**
     * 启用/禁用角色
     */
    updateRoleStatus(id: number, status: number): Promise<Result<void>> {
        return request.put(`/role/${id}/status`, null, { params: { status } })
    },

    /**
     * 为角色分配权限
     */
    assignPermissions(data: AssignPermissionRequest): Promise<Result<void>> {
        return request.post('/role/assign-permissions', data)
    },

    /**
     * 获取角色已分配的菜单ID列表
     */
    getRoleMenuIds(roleId: number): Promise<Result<number[]>> {
        return request.get(`/role/${roleId}/menu-ids`)
    },

    /**
     * 获取角色权限标识集合
     */
    getRolePermissions(roleId: number): Promise<Result<string[]>> {
        return request.get(`/role/${roleId}/permissions`)
    },

    /**
     * 获取所有角色列表
     */
    getAllRoles(): Promise<Result<Role[]>> {
        return request.get('/role/all')
    }
}

/**
 * 权限管理API
 */
export const permissionApi = {
    /**
     * 获取用户权限标识集合
     */
    getUserPermissions(userId: number): Promise<Result<string[]>> {
        return request.get(`/permission/user/${userId}/permissions`)
    },

    /**
     * 获取用户菜单树
     */
    getUserMenuTree(userId: number): Promise<Result<Menu[]>> {
        return request.get(`/permission/user/${userId}/menu-tree`)
    },

    /**
     * 获取所有菜单树
     */
    getAllMenuTree(): Promise<Result<Menu[]>> {
        return request.get('/permission/menu-tree')
    },

    /**
     * 为用户分配角色
     */
    assignRolesToUser(userId: number, roleIds: number[]): Promise<Result<void>> {
        return request.post(`/permission/user/${userId}/assign-roles`, roleIds)
    },

    /**
     * 获取用户角色ID列表
     */
    getUserRoleIds(userId: number): Promise<Result<number[]>> {
        return request.get(`/permission/user/${userId}/role-ids`)
    }
}
