package com.eams.aop;

import cn.hutool.json.JSONUtil;
import com.eams.security.SecurityContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 操作日志切面
 * 用于记录标记了 @OperationLog 注解的方法的操作日志
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    /**
     * 定义切点：所有标记了 @OperationLog 注解的方法
     */
    @Pointcut("@annotation(com.eams.aop.OperationLog)")
    public void operationLogPointcut() {
    }

    /**
     * 环绕通知：记录操作日志
     *
     * @param joinPoint 连接点
     * @return 方法执行结果
     * @throws Throwable 异常
     */
    @Around("operationLogPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取注解信息
        OperationLog operationLog = method.getAnnotation(OperationLog.class);
        String operation = operationLog.value();
        String type = operationLog.type();

        // 获取方法参数
        Object[] args = joinPoint.getArgs();
        String params = "";
        try {
            params = JSONUtil.toJsonStr(args);
            // 限制参数长度，避免日志过长
            if (params.length() > 500) {
                params = params.substring(0, 500) + "...";
            }
        } catch (Exception e) {
            params = "无法序列化参数";
        }

        // 获取当前用户信息
        String username = SecurityContextHolder.getCurrentUsername();
        Long userId = SecurityContextHolder.getCurrentUserId();

        // 记录请求信息
        log.info("========== 操作日志开始 ==========");
        log.info("操作描述: {}", operation);
        log.info("操作类型: {}", type);
        log.info("操作用户: {} (ID: {})", username, userId);
        if (request != null) {
            log.info("请求方式: {}", request.getMethod());
            log.info("请求路径: {}", request.getRequestURI());
            log.info("客户端IP: {}", getClientIp(request));
        }
        log.info("方法名称: {}.{}", signature.getDeclaringTypeName(), signature.getName());
        log.info("请求参数: {}", params);

        Object result = null;
        try {
            // 执行目标方法
            result = joinPoint.proceed();

            // 计算耗时
            long elapsedTime = System.currentTimeMillis() - startTime;
            log.info("执行结果: 成功");
            log.info("执行耗时: {} ms", elapsedTime);
            log.info("========== 操作日志结束 ==========");

            return result;
        } catch (Throwable e) {
            // 计算耗时
            long elapsedTime = System.currentTimeMillis() - startTime;
            log.error("执行结果: 失败");
            log.error("异常信息: {}", e.getMessage());
            log.error("执行耗时: {} ms", elapsedTime);
            log.error("========== 操作日志结束 ==========");
            throw e;
        }
    }

    /**
     * 获取客户端真实IP地址
     *
     * @param request HttpServletRequest
     * @return 客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
