package com.company.hrms.notification.application.service.send.task;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.notification.application.service.send.context.SendNotificationContext;
import com.company.hrms.notification.infrastructure.channel.InAppChannelSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 發送站內通知 Task
 * <p>
 * 職責：透過 WebSocket (STOMP) 發送站內即時通知
 * </p>
 * <p>
 * Task 類型：Integration Task
 * </p>
 *
 * @author Claude
 * @since 2025-01-26
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SendInAppTask implements PipelineTask<SendNotificationContext> {

    private final InAppChannelSender inAppChannelSender;

    @Override
    public void execute(SendNotificationContext ctx) {
        log.debug("[SendInAppTask] 開始發送站內通知");

        try {
            // 發送站內通知
            inAppChannelSender.send(
                    ctx.getNotification(),
                    ctx.getRequest().getRecipientId()
            );

            // 記錄成功
            ctx.addSuccessResult("IN_APP");
            log.info("[SendInAppTask] 站內通知發送成功 - 收件人: {}",
                    ctx.getRequest().getRecipientId());

        } catch (Exception e) {
            // 記錄失敗
            ctx.addFailureResult("IN_APP", e.getMessage());
            log.error("[SendInAppTask] 站內通知發送失敗 - 收件人: {}, 錯誤: {}",
                    ctx.getRequest().getRecipientId(),
                    e.getMessage(),
                    e);
        }
    }
}
