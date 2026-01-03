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
                path: '/records',
                name: 'RecordManagement',
                component: () => import('@/views/RecordManagement.vue'),
                meta: {
                    title: '流转记录',
                    requiresAuth: true
                }
            }
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
