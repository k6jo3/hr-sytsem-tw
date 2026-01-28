package com.company.hrms.notification.application.service.send.task;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.notification.application.service.send.context.SendNotificationContext;
import com.company.hrms.notification.domain.model.aggregate.NotificationPreference;
import com.company.hrms.notification.domain.repository.INotificationPreferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 載入通知偏好設定 Task
 * <p>
 * 職責：根據 recipientId 載入收件人的通知偏好設定
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
public class LoadPreferenceTask implements PipelineTask<SendNotificationContext> {

    private final INotificationPreferenceRepository preferenceRepository;

    @Override
    public void execute(SendNotificationContext ctx) {
        String recipientId = ctx.getRequest().getRecipientId();

        log.debug("[LoadPreferenceTask] 載入收件人偏好設定: {}", recipientId);

        // 查詢偏好設定，若不存在則建立預設值
        NotificationPreference preference = preferenceRepository
                .findByEmployeeIdOrCreateDefault(recipientId);

        ctx.setPreference(preference);
        log.debug("[LoadPreferenceTask] 偏好設定載入完成 - Email:{}, Push:{}, InApp:{}",
                preference.isEmailEnabled(),
                preference.isPushEnabled(),
                preference.isInAppEnabled());
    }
}
