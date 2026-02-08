package com.eams.common.result;

import lombok.Getter;

/**
 * 响应状态码枚举
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Getter
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 失败
     */
    FAIL(500, "操作失败"),

    /**
     * 参数校验失败
     */
    VALIDATE_FAILED(400, "参数校验失败"),

    /**
     * 未认证
     */
    UNAUTHORIZED(401, "未认证，请先登录"),

    /**
     * 无权限
     */
    FORBIDDEN(403, "权限不足，拒绝访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "请求的资源不存在"),

    /**
     * 请求方法不支持
     */
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),

    /**
     * 业务异常
     */
    BUSINESS_ERROR(600, "业务处理异常"),

    /**
     * 系统异常
     */
    SYSTEM_ERROR(500, "系统异常，请联系管理员"),

    /**
     * Token 过期
     */
    TOKEN_EXPIRED(401, "Token已过期，请重新登录"),

    /**
     * Token 无效
     */
    TOKEN_INVALID(401, "Token无效"),

    /**
     * 用户名或密码错误
     */
    LOGIN_FAILED(401, "用户名或密码错误"),

    /**
     * 账号已被禁用
     */
    ACCOUNT_DISABLED(403, "账号已被禁用"),

    /**
     * 数据已存在
     */
    DATA_EXISTED(400, "数据已存在"),

    /**
     * 数据不存在
     */
    DATA_NOT_EXISTED(404, "数据不存在");

    /**
     * 响应码
     */
    private final Integer code;

    /**
     * 响应消息
     */
    private final String message;

    /**
     * 构造方法
     *
     * @param code    响应码
     * @param message 响应消息
     */
    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
