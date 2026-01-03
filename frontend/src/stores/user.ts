import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { LoginResponse } from '@/types'

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
    }

    /**
     * 登出
     */
    const logout = () => {
        token.value = ''
        userInfo.value = null
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
    }

    /**
     * 是否已登录
     */
    const isLoggedIn = () => {
        return !!token.value
    }

    return {
        token,
        userInfo,
        setToken,
        setUserInfo,
        login,
        logout,
        isLoggedIn
    }
})
