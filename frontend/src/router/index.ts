import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { usePermissionStore } from '@/stores/permission'
import Login from '@/views/Login.vue'
import MainLayout from '@/layout/MainLayout.vue'
import PortalLayout from '@/layout/PortalLayout.vue'
import UserManagement from '@/views/UserManagement.vue'
import Welcome from '@/views/Welcome.vue'

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
        redirect: '/welcome',
        meta: {
            requiresAuth: true
        },
        children: [
            {
                path: '/welcome',
                name: 'Welcome',
                component: Welcome,
                meta: { requiresAuth: true, title: '欢迎页' }
            },
            {
                path: '/dashboard',
                name: 'Dashboard',
                component: () => import('@/views/Dashboard.vue'),
                meta: { requiresAuth: true, title: '仪表盘', permission: 'dashboard:view' }
            },
            {
                path: '/user',
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
                    title: '资产分类',
                    requiresAuth: true,
                    permission: 'asset:category:list'
                }
            },
            {
                path: '/assets',
                name: 'AssetManagement',
                component: () => import('@/views/AssetManagement.vue'),
                meta: {
                    title: '全部资产',
                    requiresAuth: true,
                    permission: 'asset:info:list'
                }
            },
            {
                path: '/my-assets',
                name: 'MyAssetManagement',
                component: () => import('@/views/MyAssetManagement.vue'),
                meta: {
                    title: '持有资产',
                    requiresAuth: true,
                    permission: 'asset:info:my:list'
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
                path: '/usage-applications',
                name: 'UsageApplicationManagement',
                component: () => import('@/views/UsageApplicationManagement.vue'),
                meta: {
                    title: '申请审核管理',
                    requiresAuth: true,
                    permission: 'asset:usage:list'
                }
            },
            {
                path: '/usage-application',
                redirect: '/usage-applications',
                meta: {
                    requiresAuth: true
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
                meta: { requiresAuth: true, title: '资产生命周期', permission: 'lifecycle:list' }
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
                meta: { requiresAuth: true, title: '全部报修', permission: 'repair:list' }
            },
            {
                path: '/my-repairs',
                name: 'MyRepairManagement',
                component: () => import('@/views/RepairManagement.vue'),
                meta: { requiresAuth: true, title: '我的报修', permission: 'repair:own:list' }
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
                meta: { requiresAuth: true, title: '菜单管理', permission: 'system:permission:list' }
            },
            {
                path: '/no-access',
                name: 'NoAccess',
                component: () => import('@/views/NoAccess.vue'),
                meta: { requiresAuth: true, title: '无权限访问' }
            },
            
        ]
    },
    {
        path: '/portal',
        component: PortalLayout,
        redirect: '/portal/home',
        meta: {
            requiresAuth: true,
            portalOnly: true
        },
        children: [
            {
                path: 'home',
                name: 'PortalHome',
                component: () => import('@/views/portal/PortalHome.vue'),
                meta: {
                    title: '自助首页',
                    requiresAuth: true,
                    permission: 'asset:portal:view',
                    portalOnly: true
                }
            },
            {
                path: 'assets',
                name: 'PortalAssetCenter',
                component: () => import('@/views/portal/PortalAssetCenter.vue'),
                meta: {
                    title: '公司资产',
                    requiresAuth: true,
                    permission: 'asset:portal:view',
                    portalOnly: true
                }
            },
            {
                path: 'my-assets',
                name: 'PortalMyAssets',
                component: () => import('@/views/portal/PortalMyAssets.vue'),
                meta: {
                    title: '我的资产',
                    requiresAuth: true,
                    permission: 'asset:info:my:list',
                    portalOnly: true
                }
            },
            {
                path: 'my-applications',
                name: 'PortalMyApplications',
                component: () => import('@/views/portal/PortalMyApplications.vue'),
                meta: {
                    title: '我的申请',
                    requiresAuth: true,
                    permission: 'asset:usage:my:list',
                    portalOnly: true
                }
            },
            {
                path: 'my-repairs',
                name: 'PortalMyRepairs',
                component: () => import('@/views/portal/PortalMyRepairs.vue'),
                meta: {
                    title: '我的报修',
                    requiresAuth: true,
                    permission: 'repair:own:list',
                    portalOnly: true
                }
            },
            {
                path: 'no-access',
                name: 'PortalNoAccess',
                component: () => import('@/views/NoAccess.vue'),
                meta: {
                    title: '无权限访问',
                    requiresAuth: true,
                    portalOnly: true
                }
            }
        ]
    },
    {
        path: '/self-service',
        redirect: '/portal/home'
    },
    {
        path: '/:pathMatch(.*)*',
        redirect: '/no-access'
    }
]

/**
 * 创建路由实例
 */
const router = createRouter({
    history: createWebHistory(),
    routes
})

const adminHomeRouteOrder = [
    '/welcome',
    '/dashboard',
    '/user',
    '/departments',
    '/role',
    '/permissions',
    '/assets',
    '/my-assets',
    '/categories',
    '/records',
    '/usage-applications',
    '/purchase',
    '/repair',
    '/my-repairs',
    '/inventory',
    '/lifecycle'
]

const portalHomeRouteOrder = [
    '/portal/home',
    '/portal/assets',
    '/portal/my-assets',
    '/portal/my-applications',
    '/portal/my-repairs'
]

const adminIndicatorPermissions = [
    'dashboard:view',
    'system:user:list',
    'system:role:list',
    'system:permission:list',
    'asset:info:list',
    'asset:record:list',
    'asset:usage:list',
    'repair:list',
    'purchase:list',
    'inventory:list',
    'lifecycle:list'
]

const resolveFirstAccessiblePathByOrder = (
    permissionStore: ReturnType<typeof usePermissionStore>,
    routeOrder: string[],
    fallbackPath: string
) => {
    for (const path of routeOrder) {
        const resolved = router.resolve(path)
        if (!resolved.matched.length) {
            continue
        }
        const permission = resolved.meta.permission as string | undefined
        if (!permission || permissionStore.hasPermission(permission)) {
            return path
        }
    }
    return fallbackPath
}

const isPortalOnlyUser = (permissionStore: ReturnType<typeof usePermissionStore>) => {
    if (!permissionStore.hasPermission('asset:portal:view')) {
        return false
    }
    return !adminIndicatorPermissions.some(permission => permissionStore.hasPermission(permission))
}

const resolveFirstAccessiblePath = (permissionStore: ReturnType<typeof usePermissionStore>) => {
    if (isPortalOnlyUser(permissionStore)) {
        return resolveFirstAccessiblePathByOrder(permissionStore, portalHomeRouteOrder, '/portal/no-access')
    }
    const adminPath = resolveFirstAccessiblePathByOrder(permissionStore, adminHomeRouteOrder, '')
    if (adminPath) {
        return adminPath
    }
    if (permissionStore.hasPermission('asset:portal:view')) {
        return resolveFirstAccessiblePathByOrder(permissionStore, portalHomeRouteOrder, '/portal/no-access')
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
        if (to.path === '/') {
            next(resolveFirstAccessiblePath(permissionStore))
            return
        }
        if (isPortalOnlyUser(permissionStore) && !to.path.startsWith('/portal')) {
            next(resolveFirstAccessiblePath(permissionStore))
            return
        }
        if (to.meta.portalOnly && !permissionStore.hasPermission('asset:portal:view')) {
            next(resolveFirstAccessiblePath(permissionStore))
            return
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
