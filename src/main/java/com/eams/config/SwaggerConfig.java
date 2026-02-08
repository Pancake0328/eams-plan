package com.eams.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger 3 (OpenAPI 3) 配置类
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Configuration
public class SwaggerConfig {

    /**
     * 配置 OpenAPI 文档信息
     *
     * @return OpenAPI
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // 基本信息配置
                .info(new Info()
                        .title("EAMS 企业资产管理系统 API 文档")
                        .description("提供企业资产全生命周期管理的 RESTful API 接口")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Pancake Team")
                                .email("eams@enterprise.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                // 配置 JWT 认证
                .components(new Components()
                        .addSecuritySchemes("Bearer Token",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("请在下方输入 JWT Token（无需添加 'Bearer ' 前缀）")))
                // 全局应用 JWT 认证
                .addSecurityItem(new SecurityRequirement().addList("Bearer Token"));
    }
}
