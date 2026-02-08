package com.eams.system.controller;

import com.eams.aop.OperationLog;
import com.eams.common.result.Result;
import com.eams.system.dto.LoginRequest;
import com.eams.system.service.AuthService;
import com.eams.system.vo.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 *
 * @author Pancake
 * @since 2026-01-03
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户认证相关接口")
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "使用用户名和密码登录系统")
    @OperationLog(value = "用户登录", type = "LOGIN")
    public Result<LoginResponse> login(@Validated @RequestBody LoginRequest loginRequest) {
        log.info("用户登录，username={}", loginRequest.getUsername());
        LoginResponse response = authService.login(loginRequest);
        log.info("用户登录完成，username={}", loginRequest.getUsername());
        return Result.success(response);
    }
}
