package com.company.hrms.notification.application.service.send.task;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.notification.application.service.send.context.SendNotificationContext;
import com.company.hrms.notification.infrastructure.channel.LineChannelSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 發送 LINE 通知 Task
 * <p>
 * 職責：透過 LINE Notify API 發送通知
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
public class SendLineTask implements PipelineTask<SendNotificationContext> {

    private final LineChannelSender lineChannelSender;

    @Override
    @Async
    public void execute(SendNotificationContext ctx) {
        log.debug("[SendLineTask] 開始發送 LINE 通知");

        try {
            // 發送 LINE 訊息
            lineChannelSender.send(
                    ctx.getNotification(),
                    ctx.getRequest().getRecipientId()
            );

            // 記錄成功
            ctx.addSuccessResult("LINE");
            log.info("[SendLineTask] LINE 通知發送成功 - 收件人: {}",
                    ctx.getRequest().getRecipientId());

        } catch (Exception e) {
            // 記錄失敗
            ctx.addFailureResult("LINE", e.getMessage());
            log.error("[SendLineTask] LINE 通知發送失敗 - 收件人: {}, 錯誤: {}",
                    ctx.getRequest().getRecipientId(),
                    e.getMessage(),
                    e);
        }
    }
}
