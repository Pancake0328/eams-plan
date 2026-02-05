package com.eams.system.service.impl;

import com.eams.common.result.ResultCode;
import com.eams.exception.BusinessException;
import com.eams.security.JwtProperties;
import com.eams.security.JwtUtil;
import com.eams.security.UserDetailsImpl;
import com.eams.system.dto.LoginRequest;
import com.eams.system.entity.User;
import com.eams.system.service.AuthService;
import com.eams.system.vo.LoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现类
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        try {
            // 创建认证对象
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword());

            // 进行认证
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // 获取用户详情
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User user = userDetails.getUser();

            // 生成 Token
            String token = jwtUtil.generateToken(user.getUsername());

            // 构建用户信息
            LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .nickname(user.getNickname())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .avatar(user.getAvatar())
                    .departmentId(user.getDepartmentId())
                    .build();

            // 构建响应
            LoginResponse response = LoginResponse.builder()
                    .token(token)
                    .tokenType("Bearer")
                    .expiresIn(jwtProperties.getExpiration())
                    .userInfo(userInfo)
                    .build();

            log.info("用户登录成功: {}", user.getUsername());
            return response;

        } catch (BadCredentialsException e) {
            log.error("用户名或密码错误: {}", loginRequest.getUsername());
            throw new BusinessException(ResultCode.LOGIN_FAILED);
        } catch (DisabledException e) {
            log.error("账号已被禁用: {}", loginRequest.getUsername());
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        } catch (Exception e) {
            log.error("登录失败: {}", e.getMessage(), e);
            throw new BusinessException("登录失败，请稍后重试");
        }
    }
}
