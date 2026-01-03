package com.eams.notify.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.common.result.Result;
import com.eams.notify.dto.NotifyCreateRequest;
import com.eams.notify.service.NotifyService;
import com.eams.notify.vo.NotifyVO;
import com.eams.notify.websocket.NotifyWebSocketHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知控制器
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Tag(name = "通知管理")
@RestController
@RequestMapping("/notify")
@RequiredArgsConstructor
public class NotifyController {

    private final NotifyService notifyService;
    private final NotifyWebSocketHandler webSocketHandler;

    @Operation(summary = "创建通知")
    @PostMapping
    public Result<Long> createNotify(@Valid @RequestBody NotifyCreateRequest request) {
        Long id = notifyService.createNotify(request);

        // 如果指定了接收人，通过WebSocket推送
        if (request.getReceiverType() == 1 && request.getReceiver() != null) {
            NotifyVO notify = notifyService.getUserNotifies(request.getReceiver(), false).stream()
                    .filter(n -> n.getId().equals(id))
                    .findFirst()
                    .orElse(null);
            if (notify != null) {
                webSocketHandler.sendNotifyToUser(request.getReceiver(), notify);
            }
        }

        return Result.success(id);
    }

    @Operation(summary = "广播通知")
    @PostMapping("/broadcast")
    public Result<Void> broadcast(@Valid @RequestBody NotifyCreateRequest request) {
        notifyService.broadcast(request);

        // WebSocket广播
        NotifyVO notify = notifyService.getUserNotifies(null, false).get(0);
        webSocketHandler.broadcast(notify);

        return Result.success();
    }

    @Operation(summary = "标记已读")
    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id) {
        notifyService.markAsRead(id);
        return Result.success();
    }

    @Operation(summary = "批量标记已读")
    @PutMapping("/batch-read")
    public Result<Void> batchMarkAsRead(@RequestBody List<Long> ids) {
        notifyService.batchMarkAsRead(ids);
        return Result.success();
    }

    @Operation(summary = "获取未读通知数量")
    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount(@RequestParam String receiver) {
        Long count = notifyService.getUnreadCount(receiver);
        return Result.success(count);
    }

    @Operation(summary = "获取用户通知列表")
    @GetMapping("/user")
    public Result<List<NotifyVO>> getUserNotifies(
            @RequestParam String receiver,
            @RequestParam(required = false) Boolean isRead) {
        List<NotifyVO> notifies = notifyService.getUserNotifies(receiver, isRead);
        return Result.success(notifies);
    }

    @Operation(summary = "分页查询通知")
    @GetMapping
    public Result<Page<NotifyVO>> getNotifyPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String receiver,
            @RequestParam(required = false) Integer notifyType,
            @RequestParam(required = false) Boolean isRead) {
        Page<NotifyVO> page = notifyService.getNotifyPage(current, size, receiver, notifyType, isRead);
        return Result.success(page);
    }

    @Operation(summary = "删除通知")
    @DeleteMapping("/{id}")
    public Result<Void> deleteNotify(@PathVariable Long id) {
        notifyService.deleteNotify(id);
        return Result.success();
    }

    @Operation(summary = "清空已读通知")
    @DeleteMapping("/clear-read")
    public Result<Void> clearReadNotifies(@RequestParam String receiver) {
        notifyService.clearReadNotifies(receiver);
        return Result.success();
    }

    @Operation(summary = "获取在线用户数")
    @GetMapping("/online-count")
    public Result<Integer> getOnlineCount() {
        int count = webSocketHandler.getOnlineCount();
        return Result.success(count);
    }
}
