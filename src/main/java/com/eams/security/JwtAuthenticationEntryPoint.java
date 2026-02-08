package com.eams.security;

import cn.hutool.json.JSONUtil;
import com.eams.common.result.Result;
import com.eams.common.result.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 认证入口点
 * 处理未认证的请求
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * 处理未认证异常
     *
     * @param request       请求
     * @param response      响应
     * @param authException 认证异常
     * @throws IOException IO异常
     */
    @Override
    public void commence(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        log.error("未认证访问: {}, 异常: {}", request.getRequestURI(), authException.getMessage());

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Result<?> result = Result.fail(ResultCode.UNAUTHORIZED);
        response.getWriter().write(JSONUtil.toJsonStr(result));
    }
}
