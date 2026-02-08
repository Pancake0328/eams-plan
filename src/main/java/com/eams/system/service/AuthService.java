package com.eams.system.service;

import com.eams.system.dto.LoginRequest;
import com.eams.system.vo.LoginResponse;

/**
 * 认证服务接口
 *
 * @author Pancake
 * @since 2026-01-03
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @return 登录响应（包含 Token 和用户信息）
     */
    LoginResponse login(LoginRequest loginRequest);
}
