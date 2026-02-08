package com.eams.common.exception;

/**
 * 业务异常类
 *
 * @author Pancake
 * @since 2026-01-03
 */
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
