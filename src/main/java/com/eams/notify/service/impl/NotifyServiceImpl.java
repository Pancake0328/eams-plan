package com.eams.notify.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.notify.dto.NotifyCreateRequest;
import com.eams.notify.entity.SysNotify;
import com.eams.notify.mapper.SysNotifyMapper;
import com.eams.notify.service.NotifyService;
import com.eams.notify.vo.NotifyVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 通知服务实现
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Service
@RequiredArgsConstructor
public class NotifyServiceImpl implements NotifyService {

    private final SysNotifyMapper notifyMapper;

    private static final Map<Integer, String> TYPE_MAP = new HashMap<>();
    private static final Map<Integer, String> LEVEL_MAP = new HashMap<>();
    private static final Map<Integer, String> RECEIVER_TYPE_MAP = new HashMap<>();

    static {
        TYPE_MAP.put(1, "系统通知");
        TYPE_MAP.put(2, "到期提醒");
        TYPE_MAP.put(3, "报修提醒");
        TYPE_MAP.put(4, "盘点提醒");

        LEVEL_MAP.put(1, "普通");
        LEVEL_MAP.put(2, "重要");
        LEVEL_MAP.put(3, "紧急");

        RECEIVER_TYPE_MAP.put(1, "用户");
        RECEIVER_TYPE_MAP.put(2, "角色");
        RECEIVER_TYPE_MAP.put(3, "全体");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createNotify(NotifyCreateRequest request) {
        SysNotify notify = new SysNotify();
        BeanUtils.copyProperties(request, notify);

        if (notify.getSendTime() == null) {
            notify.setSendTime(LocalDateTime.now());
        }

        notifyMapper.insert(notify);
        return notify.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void broadcast(NotifyCreateRequest request) {
        // 广播通知设置接收人类型为全体
        request.setReceiverType(3);
        request.setReceiver(null);
        createNotify(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long id) {
        SysNotify notify = notifyMapper.selectById(id);
        if (notify != null && !notify.getIsRead()) {
            notify.setIsRead(true);
            notify.setReadTime(LocalDateTime.now());
            notifyMapper.updateById(notify);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchMarkAsRead(List<Long> ids) {
        ids.forEach(this::markAsRead);
    }

    @Override
    public Long getUnreadCount(String receiver) {
        LambdaQueryWrapper<SysNotify> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(SysNotify::getReceiver, receiver)
                .or().eq(SysNotify::getReceiverType, 3));
        wrapper.eq(SysNotify::getIsRead, false);
        return notifyMapper.selectCount(wrapper);
    }

    @Override
    public List<NotifyVO> getUserNotifies(String receiver, Boolean isRead) {
        LambdaQueryWrapper<SysNotify> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(SysNotify::getReceiver, receiver)
                .or().eq(SysNotify::getReceiverType, 3));

        if (isRead != null) {
            wrapper.eq(SysNotify::getIsRead, isRead);
        }

        wrapper.orderByDesc(SysNotify::getSendTime);
        wrapper.last("LIMIT 100");

        List<SysNotify> notifies = notifyMapper.selectList(wrapper);
        return notifies.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public Page<NotifyVO> getNotifyPage(int current, int size, String receiver, Integer notifyType, Boolean isRead) {
        Page<SysNotify> page = new Page<>(current, size);
        LambdaQueryWrapper<SysNotify> wrapper = new LambdaQueryWrapper<>();

        if (receiver != null) {
            wrapper.and(w -> w.eq(SysNotify::getReceiver, receiver)
                    .or().eq(SysNotify::getReceiverType, 3));
        }

        if (notifyType != null) {
            wrapper.eq(SysNotify::getNotifyType, notifyType);
        }

        if (isRead != null) {
            wrapper.eq(SysNotify::getIsRead, isRead);
        }

        wrapper.orderByDesc(SysNotify::getSendTime);

        Page<SysNotify> notifyPage = notifyMapper.selectPage(page, wrapper);
        Page<NotifyVO> voPage = new Page<>(notifyPage.getCurrent(), notifyPage.getSize(), notifyPage.getTotal());
        voPage.setRecords(notifyPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotify(Long id) {
        notifyMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearReadNotifies(String receiver) {
        LambdaQueryWrapper<SysNotify> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysNotify::getReceiver, receiver);
        wrapper.eq(SysNotify::getIsRead, true);
        notifyMapper.delete(wrapper);
    }

    /**
     * 转换为VO
     */
    private NotifyVO convertToVO(SysNotify notify) {
        NotifyVO vo = new NotifyVO();
        BeanUtils.copyProperties(notify, vo);
        vo.setNotifyTypeText(TYPE_MAP.get(notify.getNotifyType()));
        vo.setNotifyLevelText(LEVEL_MAP.get(notify.getNotifyLevel()));
        vo.setReceiverTypeText(RECEIVER_TYPE_MAP.get(notify.getReceiverType()));
        return vo;
    }
}
