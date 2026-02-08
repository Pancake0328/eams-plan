package com.eams.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性类
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 密钥
     */
    private String secret;

    /**
     * Token 有效期（单位：秒）
     */
    private Long expiration;

    /**
     * Token 前缀
     */
    private String tokenPrefix;

    /**
     * Token 请求头名称
     */
    private String headerName;
}
