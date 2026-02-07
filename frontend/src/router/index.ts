import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'
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
                meta: { requiresAuth: true, title: '仪表盘' }
            },
            {
                path: '',
                name: 'UserManagement',
                component: UserManagement,
                meta: {
                    title: '用户管理',
                    requiresAuth: true
                }
            },
            {
                path: '/categories',
                name: 'CategoryManagement',
                component: () => import('@/views/CategoryManagement.vue'),
                meta: {
                    title: '资产分类管理',
                    requiresAuth: true
                }
            },
            {
                path: '/assets',
                name: 'AssetManagement',
                component: () => import('@/views/AssetManagement.vue'),
                meta: {
                    title: '资产信息管理',
                    requiresAuth: true
                }
            },
        {
          path: '/purchase',
          name: 'PurchaseManagement',
          component: () => import('@/views/PurchaseManagement.vue'),
          meta: { requiresAuth: true, title: '采购管理' }
        },
            {
                path: '/records',
                name: 'RecordManagement',
                component: () => import('@/views/RecordManagement.vue'),
                meta: {
                    title: '流转记录',
                    requiresAuth: true
                }
            },
            {
                path: '/departments',
                name: 'DepartmentManagement',
                component: () => import('@/views/DepartmentManagement.vue'),
                meta: {
                    title: '部门管理',
                    requiresAuth: true
                }
            },
            {
                path: '/employees',
                name: 'EmployeeManagement',
                component: () => import('@/views/EmployeeManagement.vue'),
                meta: {
                    title: '员工管理',
                    requiresAuth: true
                }
            },
            {
                path: '/asset-assigns',
                name: 'AssetAssignManagement',
                component: () => import('@/views/AssetAssignManagement.vue'),
                meta: {
                    title: '资产分配管理',
                    requiresAuth: true
                }
            },
            {
                path: '/finance',
                name: 'FinanceManagement',
                component: () => import('@/views/FinanceManagement.vue'),
                meta: {
                    title: '账单与资金管理',
                    requiresAuth: true
                }
            },
            {
                path: '/lifecycle',
                name: 'LifecycleManagement',
                component: () => import('@/views/LifecycleManagement.vue'),
                meta: { requiresAuth: true, title: '生命周期管理' }
            },
            {
                path: '/inventory',
                name: 'InventoryManagement',
                component: () => import('@/views/InventoryManagement.vue'),
                meta: { requiresAuth: true, title: '盘点管理' }
            },
            {
                path: '/repair',
                name: 'RepairManagement',
                component: () => import('@/views/RepairManagement.vue'),
                meta: { requiresAuth: true, title: '报修管理' }
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

/**
 * 路由守卫 - 权限验证
 */
router.beforeEach((to, _from, next) => {
    const userStore = useUserStore()

    // 设置页面标题
    document.title = `${to.meta.title || 'EAMS'} - 企业资产管理系统`

    // 检查是否需要登录
    if (to.meta.requiresAuth && !userStore.isLoggedIn()) {
        // 未登录，跳转到登录页
        next('/login')
    } else if (to.path === '/login' && userStore.isLoggedIn()) {
        // 已登录，访问登录页时跳转到首页
        next('/')
    } else {
        next()
    }
})

export default router
