package com.eams.aop;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 用于标记需要记录操作日志的方法
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /**
     * 操作模块
     *
     * @return 操作模块
     */
    String module() default "";

    /**
     * 操作动作
     *
     * @return 操作动作
     */
    String action() default "";

    /**
     * 操作描述
     *
     * @return 操作描述
     */
    String value() default "";

    /**
     * 操作类型
     *
     * @return 操作类型
     */
    String type() default "OTHER";
}
