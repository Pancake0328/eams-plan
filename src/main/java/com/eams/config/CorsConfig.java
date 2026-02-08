package com.eams.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置类
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Configuration
public class CorsConfig {

    /**
     * 配置跨域过滤器
     *
     * @return CorsFilter
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 允许所有域名跨域（生产环境应配置具体域名）
        config.addAllowedOriginPattern("*");

        // 允许所有请求头
        config.addAllowedHeader("*");

        // 允许所有请求方法
        config.addAllowedMethod("*");

        // 允许携带认证信息（如 Cookie）
        config.setAllowCredentials(true);

        // 预检请求的有效期（单位：秒）
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有路径生效
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
