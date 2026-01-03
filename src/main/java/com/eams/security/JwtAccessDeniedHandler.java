package com.eams.security;

import cn.hutool.json.JSONUtil;
import com.eams.common.result.Result;
import com.eams.common.result.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 访问拒绝处理器
 * 处理权限不足的请求
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * 处理访问拒绝异常
     *
     * @param request               请求
     * @param response              响应
     * @param accessDeniedException 访问拒绝异常
     * @throws IOException IO异常
     */
    @Override
    public void handle(HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {
        log.error("权限不足: {}, 异常: {}", request.getRequestURI(), accessDeniedException.getMessage());

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        Result<?> result = Result.fail(ResultCode.FORBIDDEN);
        response.getWriter().write(JSONUtil.toJsonStr(result));
    }
}
