import type { Directive } from 'vue'
import { usePermissionStore } from '@/stores/permission'

/**
 * 权限指令 v-permission
 * 用法：v-permission="'system:user:add'"
 * 或：v-permission="['system:user:add', 'system:user:edit']"
 */
export const permission: Directive = {
    mounted(el, binding) {
        const permissionStore = usePermissionStore()
        const { value } = binding

        if (value) {
            let hasPermission = false

            if (Array.isArray(value)) {
                // 数组：拥有任一权限即显示
                hasPermission = value.some(permission => permissionStore.hasPermission(permission))
            } else if (typeof value === 'string') {
                // 字符串：需要拥有该权限
                hasPermission = permissionStore.hasPermission(value)
            }

            if (!hasPermission) {
                // 无权限则移除元素
                el.parentNode?.removeChild(el)
            }
        }
    }
}

/**
 * 注册全局指令
 */
export function setupPermissionDirective(app: any) {
    app.directive('permission', permission)
}
