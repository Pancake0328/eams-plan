package com.eams;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * EAMS 系统主启动类
 *
 * @author Pancake
 * @since 2026-01-03
 */
@SpringBootApplication
@MapperScan("com.eams.**.mapper")
public class EamsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EamsApplication.class, args);
        System.out.println("========================================");
        System.out.println("EAMS 企业资产管理系统启动成功！");
        System.out.println("Swagger 文档地址: http://localhost:8080/api/swagger-ui.html");
        System.out.println("========================================");
    }
}
