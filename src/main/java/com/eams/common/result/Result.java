package com.eams.common.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果封装类
 *
 * @param <T> 数据类型
 * @author Pancake
 * @since 2026-01-03
 */
@Data
@Schema(description = "统一返回结果")
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    @Schema(description = "响应码")
    private Integer code;

    /**
     * 响应消息
     */
    @Schema(description = "响应消息")
    private String message;

    /**
     * 响应数据
     */
    @Schema(description = "响应数据")
    private T data;

    /**
     * 时间戳
     */
    @Schema(description = "时间戳")
    private Long timestamp;

    /**
     * 私有构造方法
     */
    private Result() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功返回（无数据）
     *
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        return result;
    }

    /**
     * 成功返回（带数据）
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return Result
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    /**
     * 成功返回（带消息和数据）
     *
     * @param message 响应消息
     * @param data    响应数据
     * @param <T>     数据类型
     * @return Result
     */
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /**
     * 失败返回
     *
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> fail() {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.FAIL.getCode());
        result.setMessage(ResultCode.FAIL.getMessage());
        return result;
    }

    /**
     * 失败返回（带消息）
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return Result
     */
    public static <T> Result<T> fail(String message) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.FAIL.getCode());
        result.setMessage(message);
        return result;
    }

    /**
     * 失败返回（带状态码和消息）
     *
     * @param code    响应码
     * @param message 错误消息
     * @param <T>     数据类型
     * @return Result
     */
    public static <T> Result<T> fail(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * 失败返回（根据结果码枚举）
     *
     * @param resultCode 结果码枚举
     * @param <T>        数据类型
     * @return Result
     */
    public static <T> Result<T> fail(ResultCode resultCode) {
        Result<T> result = new Result<>();
        result.setCode(resultCode.getCode());
        result.setMessage(resultCode.getMessage());
        return result;
    }

    /**
     * 自定义返回
     *
     * @param code    响应码
     * @param message 响应消息
     * @param data    响应数据
     * @param <T>     数据类型
     * @return Result
     */
    public static <T> Result<T> build(Integer code, String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return result;
    }
}
