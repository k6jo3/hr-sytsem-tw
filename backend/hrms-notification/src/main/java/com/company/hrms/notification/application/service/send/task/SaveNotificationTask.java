package com.company.hrms.notification.application.service.send.task;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.notification.application.service.send.context.SendNotificationContext;
import com.company.hrms.notification.domain.model.aggregate.Notification;
import com.company.hrms.notification.domain.repository.INotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 儲存通知記錄 Task
 * <p>
 * 職責：將通知聚合根持久化到資料庫
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
public class SaveNotificationTask implements PipelineTask<SendNotificationContext> {

    private final INotificationRepository notificationRepository;

    @Override
    public void execute(SendNotificationContext ctx) {
        log.debug("[SaveNotificationTask] 開始儲存通知記錄");

        Notification notification = ctx.getNotification();

        // 持久化通知
        notificationRepository.save(notification);

        log.info("[SaveNotificationTask] 通知記錄儲存成功 - ID: {}, 狀態: {}",
                notification.getId().getValue(),
                notification.getStatus());
    }
}
