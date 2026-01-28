package com.company.hrms.notification.application.service.send.task;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.notification.application.service.send.context.SendNotificationContext;
import com.company.hrms.notification.api.response.notification.SendNotificationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 更新通知發送狀態 Task
 * <p>
 * 職責：根據渠道發送結果更新通知的最終狀態
 * </p>
 * <p>
 * Task 類型：Domain Task
 * </p>
 *
 * @author Claude
 * @since 2025-01-26
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateStatusTask implements PipelineTask<SendNotificationContext> {

    @Override
    public void execute(SendNotificationContext ctx) {
        log.debug("[UpdateStatusTask] 開始更新通知狀態");

        Map<String, SendNotificationResponse.ChannelResult> channelResults = ctx.getChannelResults();

        if (channelResults == null || channelResults.isEmpty()) {
            // 沒有渠道發送，可能被延後或過濾
            if (ctx.isShouldDelay()) {
                ctx.setFinalStatus("PENDING");
                log.info("[UpdateStatusTask] 通知標記為延後發送 (PENDING)");
            } else {
                ctx.setFinalStatus("FAILED");
                log.warn("[UpdateStatusTask] 所有渠道都被過濾，通知標記為失敗 (FAILED)");
            }
            return;
        }

        // 檢查是否有任何渠道發送成功
        boolean hasSuccess = channelResults.values().stream()
                .anyMatch(result -> "SUCCESS".equals(result.getStatus()));

        // 檢查是否有任何渠道發送失敗
        boolean hasFailure = channelResults.values().stream()
                .anyMatch(result -> "FAILED".equals(result.getStatus()));

        // 決定最終狀態並更新通知
        String finalStatus;
        if (hasSuccess && !hasFailure) {
            // 全部成功
            finalStatus = "SENT";
            ctx.getNotification().markAsSent();
        } else if (hasSuccess && hasFailure) {
            // 部分成功（視為成功，但記錄失敗原因）
            finalStatus = "SENT";
            ctx.getNotification().markAsSent();
        } else {
            // 全部失敗
            finalStatus = "FAILED";
            String errorMessage = "所有渠道發送失敗";
            ctx.getNotification().markAsFailed(errorMessage);
        }

        ctx.setFinalStatus(finalStatus);

        log.info("[UpdateStatusTask] 通知狀態更新完成 - 最終狀態: {}, 成功渠道: {}, 失敗渠道: {}",
                finalStatus,
                channelResults.values().stream().filter(r -> "SUCCESS".equals(r.getStatus())).count(),
                channelResults.values().stream().filter(r -> "FAILED".equals(r.getStatus())).count());
    }
}
