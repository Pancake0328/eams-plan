package com.eams.notify.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知VO
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
public class NotifyVO {

    private Long id;
    private Integer notifyType;
    private String notifyTypeText;
    private Integer notifyLevel;
    private String notifyLevelText;
    private String title;
    private String content;
    private String receiver;
    private Integer receiverType;
    private String receiverTypeText;
    private Boolean isRead;
    private LocalDateTime readTime;
    private Long relatedId;
    private String relatedType;
    private LocalDateTime sendTime;
    private LocalDateTime expireTime;
    private LocalDateTime createTime;
}
