import { defineStore } from 'pinia'
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { LoginResponse } from '@/types'
import { usePermissionStore } from '@/stores/permission'

const SESSION_LAST_ACTIVE_KEY = 'session:last-active-time'
const IDLE_TIMEOUT_MS = 60 * 60 * 1000
const IDLE_CHECK_INTERVAL_MS = 30 * 1000
const ACTIVITY_SYNC_GAP_MS = 5 * 1000
const ACTIVITY_EVENTS: Array<keyof WindowEventMap> = ['click', 'keydown', 'mousemove', 'scroll', 'touchstart']

/**
 * 用户状态管理
 */
export const useUserStore = defineStore('user', () => {
    // Token
    const token = ref<string>(localStorage.getItem('token') || '')

    // 用户信息
    const userInfo = ref<LoginResponse['userInfo'] | null>(
        JSON.parse(localStorage.getItem('userInfo') || 'null')
    )
    let inactivityCheckTimer: number | null = null
    let activityListening = false
    let lastSyncedActiveAt = 0
    let timingOut = false

    /**
     * 记录最后活跃时间
     */
    const persistLastActiveAt = (force = false) => {
        if (!token.value) {
            return
        }
        const now = Date.now()
        if (!force && now - lastSyncedActiveAt < ACTIVITY_SYNC_GAP_MS) {
            return
        }
        lastSyncedActiveAt = now
        localStorage.setItem(SESSION_LAST_ACTIVE_KEY, String(now))
    }

    /**
     * 清理活跃时间
     */
    const clearLastActiveAt = () => {
        lastSyncedActiveAt = 0
        localStorage.removeItem(SESSION_LAST_ACTIVE_KEY)
    }

    /**
     * 停止空闲监测
     */
    const stopInactivityMonitor = () => {
        if (inactivityCheckTimer !== null) {
            window.clearInterval(inactivityCheckTimer)
            inactivityCheckTimer = null
        }
        if (activityListening) {
            ACTIVITY_EVENTS.forEach(eventName => {
                window.removeEventListener(eventName, handleUserActivity)
            })
            document.removeEventListener('visibilitychange', handleVisibilityChange)
            activityListening = false
        }
        clearLastActiveAt()
        timingOut = false
    }

    /**
     * 执行超时检查
     */
    const checkInactivityTimeout = () => {
        if (!token.value || timingOut) {
            return
        }
        const lastActiveRaw = localStorage.getItem(SESSION_LAST_ACTIVE_KEY)
        const lastActiveAt = lastActiveRaw ? Number(lastActiveRaw) : 0
        if (!lastActiveAt || Number.isNaN(lastActiveAt)) {
            persistLastActiveAt(true)
            return
        }
        if (Date.now() - lastActiveAt >= IDLE_TIMEOUT_MS) {
            timingOut = true
            logout()
            ElMessage.warning('登录超时，已自动退出，请重新登录')
            window.location.href = '/login'
        }
    }

    /**
     * 用户行为事件
     */
    const handleUserActivity = () => {
        persistLastActiveAt()
    }

    /**
     * 标签页切换后立即校验
     */
    const handleVisibilityChange = () => {
        if (document.visibilityState !== 'visible' || !token.value) {
            return
        }
        checkInactivityTimeout()
        if (token.value) {
            persistLastActiveAt(true)
        }
    }

    /**
     * 启动空闲监测
     */
    const startInactivityMonitor = () => {
        if (typeof window === 'undefined' || !token.value) {
            return
        }
        if (!activityListening) {
            ACTIVITY_EVENTS.forEach(eventName => {
                window.addEventListener(eventName, handleUserActivity, { passive: true })
            })
            document.addEventListener('visibilitychange', handleVisibilityChange)
            activityListening = true
        }
        persistLastActiveAt(true)
        if (inactivityCheckTimer !== null) {
            window.clearInterval(inactivityCheckTimer)
        }
        inactivityCheckTimer = window.setInterval(checkInactivityTimeout, IDLE_CHECK_INTERVAL_MS)
    }

    /**
     * 设置 Token
     */
    const setToken = (newToken: string) => {
        token.value = newToken
        localStorage.setItem('token', newToken)
    }

    /**
     * 设置用户信息
     */
    const setUserInfo = (info: LoginResponse['userInfo']) => {
        userInfo.value = info
        localStorage.setItem('userInfo', JSON.stringify(info))
    }

    /**
     * 登录
     */
    const login = (loginData: LoginResponse) => {
        setToken(loginData.token)
        setUserInfo(loginData.userInfo)
        startInactivityMonitor()
    }

    /**
     * 登出
     */
    const logout = () => {
        const permissionStore = usePermissionStore()
        token.value = ''
        userInfo.value = null
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        permissionStore.clearPermissions()
        stopInactivityMonitor()
    }

    /**
     * 是否已登录
     */
    const isLoggedIn = () => {
        return !!token.value
    }

    // 页面刷新后若仍有 token，自动恢复空闲监测
    if (token.value) {
        startInactivityMonitor()
    }

    return {
        token,
        userInfo,
        setToken,
        setUserInfo,
        login,
        logout,
        isLoggedIn,
        persistLastActiveAt
    }
})
