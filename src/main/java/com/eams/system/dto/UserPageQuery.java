package com.eams.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户分页查询条件 DTO
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
@Schema(description = "用户分页查询条件")
public class UserPageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    @Schema(description = "当前页码", example = "1")
    private Long current = 1L;

    /**
     * 每页数量
     */
    @Schema(description = "每页数量", example = "10")
    private Long size = 10L;

    /**
     * 用户名（模糊查询）
     */
    @Schema(description = "用户名（模糊查询）", example = "admin")
    private String username;

    /**
     * 昵称（模糊查询）
     */
    @Schema(description = "昵称（模糊查询）", example = "管理员")
    private String nickname;

    /**
     * 手机号（模糊查询）
     */
    @Schema(description = "手机号（模糊查询）", example = "138")
    private String phone;

    /**
     * 状态：0-禁用，1-正常
     */
    @Schema(description = "状态：0-禁用，1-正常")
    private Integer status;
}
