package com.eams.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户视图对象 VO
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户信息")
public class UserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long id;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;

    /**
     * 昵称
     */
    @Schema(description = "昵称")
    private String nickname;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String phone;

    /**
     * 头像URL
     */
    @Schema(description = "头像URL")
    private String avatar;

    /**
     * 所属部门ID
     */
    @Schema(description = "所属部门ID")
    private Long departmentId;

    /**
     * 所属部门名称
     */
    @Schema(description = "所属部门名称")
    private String departmentName;

    /**
     * 状态：0-禁用，1-正常
     */
    @Schema(description = "状态：0-禁用，1-正常")
    private Integer status;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
