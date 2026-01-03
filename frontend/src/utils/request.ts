import axios, { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse, AxiosError } from 'axios'
import { ElMessage } from 'element-plus'
import type { Result } from '@/types'

/**
 * 创建 Axios 实例
 */
const request: AxiosInstance = axios.create({
    baseURL: '/api',
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json;charset=UTF-8'
    }
})

/**
 * 请求拦截器
 */
request.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        // 从 localStorage 获取 token
        const token = localStorage.getItem('token')
        if (token && config.headers) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    (error: AxiosError) => {
        console.error('请求错误:', error)
        return Promise.reject(error)
    }
)

/**
 * 响应拦截器
 */
request.interceptors.response.use(
    (response: AxiosResponse<Result>) => {
        const res = response.data

        // 请求成功
        if (res.code === 200) {
            return res
        }

        // 业务错误
        ElMessage.error(res.message || '请求失败')
        return Promise.reject(new Error(res.message || '请求失败'))
    },
    (error: AxiosError<Result>) => {
        console.error('响应错误:', error)

        // 处理 HTTP 错误
        if (error.response) {
            const { status, data } = error.response

            switch (status) {
                case 401:
                    ElMessage.error('未认证，请先登录')
                    // 清除 token
                    localStorage.removeItem('token')
                    localStorage.removeItem('userInfo')
                    // 跳转到登录页
                    window.location.href = '/login'
                    break
                case 403:
                    ElMessage.error('权限不足，拒绝访问')
                    break
                case 404:
                    ElMessage.error('请求的资源不存在')
                    break
                case 500:
                    ElMessage.error(data?.message || '服务器错误')
                    break
                default:
                    ElMessage.error(data?.message || '请求失败')
            }
        } else if (error.request) {
            ElMessage.error('网络错误，请检查网络连接')
        } else {
            ElMessage.error('请求配置错误')
        }

        return Promise.reject(error)
    }
)

export default request
