import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { usePermissionStore } from '@/stores/permission'
import Login from '@/views/Login.vue'
import MainLayout from '@/layout/MainLayout.vue'
import UserManagement from '@/views/UserManagement.vue'

/**
 * 路由配置
 */
const routes: RouteRecordRaw[] = [
    {
        path: '/login',
        name: 'Login',
        component: Login,
        meta: {
            title: '登录',
            requiresAuth: false
        }
    },
    {
        path: '/',
        component: MainLayout,
        meta: {
            requiresAuth: true
        },
        children: [
            {
                path: '/dashboard',
                name: 'Dashboard',
                component: () => import('@/views/Dashboard.vue'),
                meta: { requiresAuth: true, title: '仪表盘', permission: 'dashboard:view' }
            },
            {
                path: '',
                name: 'UserManagement',
                component: UserManagement,
                meta: {
                    title: '用户管理',
                    requiresAuth: true,
                    permission: 'system:user:list'
                }
            },
            {
                path: '/categories',
                name: 'CategoryManagement',
                component: () => import('@/views/CategoryManagement.vue'),
                meta: {
                    title: '资产分类管理',
                    requiresAuth: true,
                    permission: 'asset:category:list'
                }
            },
            {
                path: '/assets',
                name: 'AssetManagement',
                component: () => import('@/views/AssetManagement.vue'),
                meta: {
                    title: '资产信息管理',
                    requiresAuth: true,
                    permission: 'asset:info:list'
                }
            },
        {
          path: '/purchase',
          name: 'PurchaseManagement',
          component: () => import('@/views/PurchaseManagement.vue'),
          meta: { requiresAuth: true, title: '采购管理', permission: 'purchase:list' }
        },
            {
                path: '/records',
                name: 'RecordManagement',
                component: () => import('@/views/RecordManagement.vue'),
                meta: {
                    title: '流转记录',
                    requiresAuth: true,
                    permission: 'asset:record:list'
                }
            },
            {
                path: '/departments',
                name: 'DepartmentManagement',
                component: () => import('@/views/DepartmentManagement.vue'),
                meta: {
                    title: '部门管理',
                    requiresAuth: true,
                    permission: 'system:department:list'
                }
            },
            {
                path: '/lifecycle',
                name: 'LifecycleManagement',
                component: () => import('@/views/LifecycleManagement.vue'),
                meta: { requiresAuth: true, title: '生命周期管理', permission: 'lifecycle:list' }
            },
            {
                path: '/inventory',
                name: 'InventoryManagement',
                component: () => import('@/views/InventoryManagement.vue'),
                meta: { requiresAuth: true, title: '盘点管理', permission: 'inventory:list' }
            },
            {
                path: '/repair',
                name: 'RepairManagement',
                component: () => import('@/views/RepairManagement.vue'),
                meta: { requiresAuth: true, title: '报修管理', permission: 'repair:list' }
            },
            {
                path: '/role',
                name: 'RoleManagement',
                component: () => import('@/views/RoleManagement.vue'),
                meta: { requiresAuth: true, title: '角色管理', permission: 'system:role:list' }
            },
            {
                path: '/permissions',
                name: 'PermissionManagement',
                component: () => import('@/views/PermissionManagement.vue'),
                meta: { requiresAuth: true, title: '权限管理', permission: 'system:permission:list' }
            },
            {
                path: '/no-access',
                name: 'NoAccess',
                component: () => import('@/views/NoAccess.vue'),
                meta: { requiresAuth: true, title: '无权限访问' }
            },
            
        ]
    }
]

/**
 * 创建路由实例
 */
const router = createRouter({
    history: createWebHistory(),
    routes
})

const homeRouteOrder = [
    '/dashboard',
    '/assets',
    '/purchase',
    '/categories',
    '/records',
    '/departments',
    '/lifecycle',
    '/inventory',
    '/repair',
    '/role',
    '/permissions'
]

const resolveFirstAccessiblePath = (permissionStore: ReturnType<typeof usePermissionStore>) => {
    for (const path of homeRouteOrder) {
        const resolved = router.resolve(path)
        if (!resolved.matched.length) {
            continue
        }
        const permission = resolved.meta.permission as string | undefined
        if (!permission || permissionStore.hasPermission(permission)) {
            return path
        }
    }
    return '/no-access'
}

/**
 * 路由守卫 - 权限验证
 */
router.beforeEach(async (to, _from, next) => {
    const userStore = useUserStore()
    const permissionStore = usePermissionStore()

    // 设置页面标题
    document.title = `${to.meta.title || 'EAMS'} - 企业资产管理系统`

    // 检查是否需要登录
    if (to.meta.requiresAuth && !userStore.isLoggedIn()) {
        // 未登录，跳转到登录页
        next('/login')
    } else if (to.path === '/login' && userStore.isLoggedIn()) {
        // 已登录，访问登录页时跳转到首页
        if (userStore.userInfo?.id && !permissionStore.loaded) {
            await permissionStore.initializePermissions(userStore.userInfo.id)
        }
        next(resolveFirstAccessiblePath(permissionStore))
    } else {
        if (userStore.isLoggedIn() && userStore.userInfo?.id && !permissionStore.loaded) {
            await permissionStore.initializePermissions(userStore.userInfo.id)
        }
        if (to.meta.permission && !permissionStore.hasPermission(to.meta.permission as string)) {
            const redirectPath = resolveFirstAccessiblePath(permissionStore)
            if (redirectPath === to.path) {
                next()
            } else {
                next(redirectPath)
            }
            return
        }
        next()
    }
})

export default router
