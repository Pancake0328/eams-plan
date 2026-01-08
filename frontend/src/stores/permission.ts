import { defineStore } from 'pinia'
import { ref } from 'vue'
import { permissionApi } from '@/api/permission'
import type { Menu } from '@/api/permission'

/**
 * 权限状态管理
 */
export const usePermissionStore = defineStore('permission', () => {
    // 用户权限标识集合
    const permissions = ref<Set<string>>(new Set())

    // 用户菜单树
    const menuTree = ref<Menu[]>([])

    /**
     * 加载用户权限
     */
    const loadUserPermissions = async (userId: number) => {
        try {
            const res = await permissionApi.getUserPermissions(userId)
            permissions.value = new Set(res.data)
        } catch (error) {
            console.error('加载用户权限失败:', error)
            permissions.value = new Set()
        }
    }

    /**
     * 加载用户菜单树
     */
    const loadUserMenuTree = async (userId: number) => {
        try {
            const res = await permissionApi.getUserMenuTree(userId)
            menuTree.value = res.data
        } catch (error) {
            console.error('加载用户菜单失败:', error)
            menuTree.value = []
        }
    }

    /**
     * 检查是否拥有权限
     */
    const hasPermission = (permission: string): boolean => {
        return permissions.value.has(permission)
    }

    /**
     * 检查是否拥有任一权限
     */
    const hasAnyPermission = (...perms: string[]): boolean => {
        return perms.some(p => permissions.value.has(p))
    }

    /**
     * 检查是否拥有所有权限
     */
    const hasAllPermissions = (...perms: string[]): boolean => {
        return perms.every(p => permissions.value.has(p))
    }

    /**
     * 清空权限数据
     */
    const clearPermissions = () => {
        permissions.value.clear()
        menuTree.value = []
    }

    return {
        permissions,
        menuTree,
        loadUserPermissions,
        loadUserMenuTree,
        hasPermission,
        hasAnyPermission,
        hasAllPermissions,
        clearPermissions
    }
})
