package com.eams.security;

/**
 * 安全上下文持有者
 * 用于在当前线程中存储和获取用户信息
 *
 * @author Pancake
 * @since 2026-01-03
 */
public class SecurityContextHolder {

    /**
     * 线程本地变量，存储当前登录用户ID
     */
    private static final ThreadLocal<Long> CURRENT_USER_ID = new ThreadLocal<>();

    /**
     * 线程本地变量，存储当前登录用户名
     */
    private static final ThreadLocal<String> CURRENT_USERNAME = new ThreadLocal<>();

    /**
     * 设置当前用户ID
     *
     * @param userId 用户ID
     */
    public static void setCurrentUserId(Long userId) {
        CURRENT_USER_ID.set(userId);
    }

    /**
     * 获取当前用户ID
     *
     * @return 用户ID
     */
    public static Long getCurrentUserId() {
        return CURRENT_USER_ID.get();
    }

    /**
     * 设置当前用户名
     *
     * @param username 用户名
     */
    public static void setCurrentUsername(String username) {
        CURRENT_USERNAME.set(username);
    }

    /**
     * 获取当前用户名
     *
     * @return 用户名
     */
    public static String getCurrentUsername() {
        return CURRENT_USERNAME.get();
    }

    /**
     * 清除当前用户信息
     */
    public static void clear() {
        CURRENT_USER_ID.remove();
        CURRENT_USERNAME.remove();
    }
}
