package com.company.hrms.notification.application.service.send.task;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.notification.application.service.send.context.SendNotificationContext;
import com.company.hrms.notification.domain.model.aggregate.NotificationPreference;
import com.company.hrms.notification.domain.model.valueobject.NotificationChannel;
import com.company.hrms.notification.domain.model.valueobject.NotificationPriority;
import com.company.hrms.notification.domain.service.ChannelFilteringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 過濾通知渠道 Task
 * <p>
 * 職責：根據使用者偏好設定過濾禁用的渠道
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
public class FilterChannelsTask implements PipelineTask<SendNotificationContext> {

    private final ChannelFilteringService channelFilteringService;

    @Override
    public void execute(SendNotificationContext ctx) {
        log.debug("[FilterChannelsTask] 開始過濾通知渠道");

        // 取得請求的渠道（String 列表）
        List<String> requestedChannelsStr = ctx.getRequest().getChannels();
        if (requestedChannelsStr == null || requestedChannelsStr.isEmpty()) {
            // 若未指定渠道，使用預設渠道
            ctx.setFilteredChannels(List.of("IN_APP"));
            log.debug("[FilterChannelsTask] 未指定渠道，使用預設: IN_APP");
            return;
        }

        // 轉換為 NotificationChannel 枚舉
        List<NotificationChannel> requestedChannels = requestedChannelsStr.stream()
                .map(NotificationChannel::valueOf)
                .collect(Collectors.toList());

        // 取得優先級
        NotificationPriority priority = NotificationPriority.valueOf(
                ctx.getRequest().getPriority() != null ? ctx.getRequest().getPriority() : "NORMAL"
        );

        // 使用 Domain Service 過濾渠道
        List<NotificationChannel> filteredChannels = channelFilteringService.filterChannels(
                requestedChannels,
                ctx.getPreference(),
                priority
        );

        // 轉換回 String 列表
        List<String> filteredChannelsStr = filteredChannels.stream()
                .map(Enum::name)
                .collect(Collectors.toList());

        // 設定過濾結果
        ctx.setFilteredChannels(filteredChannelsStr);

        log.debug("[FilterChannelsTask] 渠道過濾完成 - 原始: {}, 過濾後: {}",
                requestedChannelsStr,
                filteredChannelsStr);

        // 若所有渠道都被過濾，記錄警告
        if (filteredChannelsStr.isEmpty()) {
            log.warn("[FilterChannelsTask] 所有渠道都被使用者偏好禁用 - 收件人: {}",
                    ctx.getRequest().getRecipientId());
        }
    }
}
