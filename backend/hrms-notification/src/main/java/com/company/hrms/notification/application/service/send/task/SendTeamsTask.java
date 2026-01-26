package com.company.hrms.notification.application.service.send.task;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.notification.application.service.send.context.SendNotificationContext;
import com.company.hrms.notification.infrastructure.channel.TeamsChannelSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 發送 Microsoft Teams 通知 Task
 * <p>
 * 職責：透過 Teams Incoming Webhook 發送通知
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
public class SendTeamsTask implements PipelineTask<SendNotificationContext> {

    private final TeamsChannelSender teamsChannelSender;

    @Override
    @Async
    public void execute(SendNotificationContext ctx) {
        log.debug("[SendTeamsTask] 開始發送 Teams 通知");

        try {
            // 發送 Teams 訊息
            teamsChannelSender.send(
                    ctx.getNotification(),
                    ctx.getRequest().getRecipientId()
            );

            // 記錄成功
            ctx.addSuccessResult("TEAMS");
            log.info("[SendTeamsTask] Teams 通知發送成功 - 收件人: {}",
                    ctx.getRequest().getRecipientId());

        } catch (Exception e) {
            // 記錄失敗
            ctx.addFailureResult("TEAMS", e.getMessage());
            log.error("[SendTeamsTask] Teams 通知發送失敗 - 收件人: {}, 錯誤: {}",
                    ctx.getRequest().getRecipientId(),
                    e.getMessage(),
                    e);
        }
    }
}
