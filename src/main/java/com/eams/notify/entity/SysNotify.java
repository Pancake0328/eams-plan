package com.eams.notify.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统通知实体
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
@TableName("sys_notify")
public class SysNotify implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 通知ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 通知类型：1-系统通知，2-到期提醒，3-报修提醒，4-盘点提醒
     */
    private Integer notifyType;

    /**
     * 通知级别：1-普通，2-重要，3-紧急
     */
    private Integer notifyLevel;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 接收人（用户名或角色）
     */
    private String receiver;

    /**
     * 接收人类型：1-用户，2-角色，3-全体
     */
    private Integer receiverType;

    /**
     * 是否已读：0-未读，1-已读
     */
    private Boolean isRead;

    /**
     * 已读时间
     */
    private LocalDateTime readTime;

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

    /**
     * 删除标记
     */
    @TableLogic
    private Boolean deleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
