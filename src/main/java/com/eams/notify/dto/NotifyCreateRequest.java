package com.eams.notify.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知创建请求DTO
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
public class NotifyCreateRequest {

    /**
     * 通知类型：1-系统通知，2-到期提醒，3-报修提醒，4-盘点提醒
     */
    @NotNull(message = "通知类型不能为空")
    private Integer notifyType;

    /**
     * 通知级别：1-普通，2-重要，3-紧急
     */
    private Integer notifyLevel = 1;

    /**
     * 通知标题
     */
    @NotBlank(message = "通知标题不能为空")
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 接收人
     */
    private String receiver;

    /**
     * 接收人类型：1-用户，2-角色，3-全体
     */
    private Integer receiverType = 1;

    /**
     * 关联业务ID
     */
    private Long relatedId;

    /**
     * 关联业务类型
     */
    private String relatedType;

    /**
     * 发送时间
     */
    private LocalDateTime sendTime;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
}
