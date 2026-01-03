package com.eams.notify.task;

import com.eams.notify.dto.NotifyCreateRequest;
import com.eams.notify.service.NotifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 通知定时任务
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotifyScheduledTask {

    private final NotifyService notifyService;

    /**
     * 每天上午9点检查到期资产
     * 这里仅为示例，实际应该查询asset_depreciation表
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void checkExpireAssets() {
        log.info("开始检查到期资产");

        // TODO: 实际应该查询折旧到期的资产
        // 这里仅作为示例
        NotifyCreateRequest request = new NotifyCreateRequest();
        request.setNotifyType(2); // 到期提醒
        request.setNotifyLevel(2); // 重要
        request.setTitle("资产折旧到期提醒");
        request.setContent("有资产折旧即将到期，请及时处理");
        request.setReceiverType(3); // 全体

        notifyService.broadcast(request);

        log.info("到期资产检查完成");
    }

    /**
     * 每小时检查一次待处理的报修单
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void checkPendingRepairs() {
        log.info("开始检查待处理报修单");

        // TODO: 查询待审批的紧急报修单

        log.info("待处理报修单检查完成");
    }

    /**
     * 每天下午5点检查进行中的盘点
     */
    @Scheduled(cron = "0 0 17 * * ?")
    public void checkOngoingInventories() {
        log.info("开始检查进行中的盘点");

        // TODO: 查询超期未完成的盘点计划

        log.info("进行中盘点检查完成");
    }
}
