package com.company.hrms.notification.application.service.send.task;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.notification.application.service.send.context.SendNotificationContext;
import com.company.hrms.notification.domain.event.NotificationSentEvent;
import com.company.hrms.notification.domain.event.NotificationFailedEvent;
import com.company.hrms.notification.domain.model.aggregate.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 發布領域事件 Task
 * <p>
 * 職責：發布通知發送成功或失敗的領域事件
 * </p>
 * <p>
 * Task 類型：Infrastructure Task
 * </p>
 *
 * @author Claude
 * @since 2025-01-26
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PublishEventTask implements PipelineTask<SendNotificationContext> {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void execute(SendNotificationContext ctx) {
        log.debug("[PublishEventTask] 開始發布領域事件");

        Notification notification = ctx.getNotification();
        String finalStatus = ctx.getFinalStatus();

        // 根據最終狀態發布對應事件
        if ("SENT".equals(finalStatus)) {
            // 發送成功事件
            NotificationSentEvent event = new NotificationSentEvent(
                    notification.getId().getValue(),
                    notification.getRecipientId(),
                    notification.getChannels()
            );

            eventPublisher.publishEvent(event);
            log.info("[PublishEventTask] NotificationSentEvent 已發布 - 通知ID: {}", notification.getId().getValue());

        } else if ("FAILED".equals(finalStatus)) {
            // 發送失敗事件
            NotificationFailedEvent event = new NotificationFailedEvent(
                    notification.getId().getValue(),
                    notification.getRecipientId(),
                    "所有渠道發送失敗"
            );

            eventPublisher.publishEvent(event);
            log.warn("[PublishEventTask] NotificationFailedEvent 已發布 - 通知ID: {}", notification.getId().getValue());

        } else if ("PENDING".equals(finalStatus)) {
            // 延後發送，不發布事件
            log.debug("[PublishEventTask] 通知延後發送，不發布事件");
        }

        log.debug("[PublishEventTask] 領域事件發布完成");
    }
}
