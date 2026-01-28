package com.company.hrms.notification.application.service.send.task;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.notification.application.service.send.context.SendNotificationContext;
import com.company.hrms.notification.domain.model.aggregate.Notification;
import com.company.hrms.notification.domain.model.valueobject.NotificationChannel;
import com.company.hrms.notification.domain.model.valueobject.NotificationPriority;
import com.company.hrms.notification.domain.model.valueobject.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 建立通知聚合根 Task
 * <p>
 * 職責：使用渲染後的內容建立 Notification 聚合根
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
public class CreateNotificationTask implements PipelineTask<SendNotificationContext> {

    @Override
    public void execute(SendNotificationContext ctx) {
        log.debug("[CreateNotificationTask] 開始建立通知聚合根");

        // 轉換類型：String → Enum
        NotificationType notificationType = NotificationType.valueOf(
                ctx.getRequest().getNotificationType()
        );

        // 轉換類型：List<String> → List<NotificationChannel>
        List<NotificationChannel> channels = ctx.getFilteredChannels().stream()
                .map(NotificationChannel::valueOf)
                .collect(Collectors.toList());

        // 轉換類型：String → NotificationPriority
        NotificationPriority priority = NotificationPriority.valueOf(
                ctx.getRequest().getPriority() != null ? ctx.getRequest().getPriority() : "NORMAL"
        );

        // 使用 Domain Model 的工廠方法建立通知（只傳 6 個核心參數）
        Notification notification = Notification.create(
                ctx.getRequest().getRecipientId(),
                ctx.getRenderedTitle(),
                ctx.getRenderedContent(),
                notificationType,
                channels,
                priority
        );

        // 設定業務關聯（分別設定）
        if (ctx.getRequest().getBusinessType() != null) {
            notification.setBusinessRelation(
                    ctx.getRequest().getBusinessType(),
                    ctx.getRequest().getBusinessId()
            );
        }
        if (ctx.getRequest().getBusinessUrl() != null) {
            notification.setBusinessUrl(ctx.getRequest().getBusinessUrl());
        }

        // 設定範本代碼
        if (ctx.getRequest().getTemplateCode() != null) {
            notification.setTemplateCode(ctx.getRequest().getTemplateCode());
        }

        // 設定建立者
        notification.setCreatedBy(ctx.getCurrentUser().getEmployeeNumber());

        // 若在靜音時段且需延後，標記為 PENDING
        if (ctx.isShouldDelay()) {
            // 通知將保持 PENDING 狀態，由排程 Job 處理
            log.info("[CreateNotificationTask] 通知標記為延後發送 - ID: {}", notification.getId().getValue());
        }

        ctx.setNotification(notification);

        log.debug("[CreateNotificationTask] 通知聚合根建立完成 - ID: {}, 狀態: {}",
                notification.getId().getValue(),
                notification.getStatus());
    }
}
