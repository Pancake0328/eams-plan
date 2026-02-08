package com.eams.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器
 * 从请求头中提取 Token 并进行认证
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtProperties jwtProperties;

    /**
     * 过滤器核心逻辑
     *
     * @param request     请求
     * @param response    响应
     * @param filterChain 过滤器链
     * @throws ServletException Servlet异常
     * @throws IOException      IO异常
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            // 从请求头中获取 Token
            String authorizationHeader = request.getHeader(jwtProperties.getHeaderName());
            String token = jwtUtil.extractToken(authorizationHeader);

            // 如果 Token 存在且有效，进行认证
            if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
                // 从 Token 中获取用户名
                String username = jwtUtil.getUsernameFromToken(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 加载用户详情
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // 创建认证对象
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 将认证信息设置到 Security 上下文中
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // 同时设置到自定义的上下文中（方便业务代码获取）
                    if (userDetails instanceof UserDetailsImpl) {
                        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
                        com.eams.security.SecurityContextHolder.setCurrentUserId(userDetailsImpl.getUser().getId());
                        com.eams.security.SecurityContextHolder
                                .setCurrentUsername(userDetailsImpl.getUser().getUsername());
                    }

                    log.debug("已认证用户: {}", username);
                }
            }
        } catch (Exception e) {
            log.error("JWT 认证失败: {}", e.getMessage());
        }

        // 继续过滤器链
        filterChain.doFilter(request, response);

        // 请求结束后清除上下文
        com.eams.security.SecurityContextHolder.clear();
    }
}
