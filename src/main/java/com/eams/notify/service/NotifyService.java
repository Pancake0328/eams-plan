package com.eams.notify.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.notify.dto.NotifyCreateRequest;
import com.eams.notify.vo.NotifyVO;

import java.util.List;

/**
 * 通知服务
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
public interface NotifyService {

    /**
     * 创建通知
     */
    Long createNotify(NotifyCreateRequest request);

    /**
     * 批量创建通知（广播）
     */
    void broadcast(NotifyCreateRequest request);

    /**
     * 标记已读
     */
    void markAsRead(Long id);

    /**
     * 批量标记已读
     */
    void batchMarkAsRead(List<Long> ids);

    /**
     * 获取未读通知数量
     */
    Long getUnreadCount(String receiver);

    /**
     * 获取用户通知列表
     */
    List<NotifyVO> getUserNotifies(String receiver, Boolean isRead);

    /**
     * 分页查询通知
     */
    Page<NotifyVO> getNotifyPage(int current, int size, String receiver, Integer notifyType, Boolean isRead);

    /**
     * 删除通知
     */
    void deleteNotify(Long id);

    /**
     * 清空已读通知
     */
    void clearReadNotifies(String receiver);
}
