package com.company.hrms.notification.application.service.send.task;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.notification.application.service.send.context.SendNotificationContext;
import com.company.hrms.notification.domain.model.aggregate.NotificationPreference;
import com.company.hrms.notification.domain.model.valueobject.QuietHours;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

/**
 * 檢查靜音時段 Task
 * <p>
 * 職責：檢查當前時間是否在使用者設定的靜音時段內
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
public class CheckQuietHoursTask implements PipelineTask<SendNotificationContext> {

    @Override
    public void execute(SendNotificationContext ctx) {
        log.debug("[CheckQuietHoursTask] 開始檢查靜音時段");

        // 取得使用者偏好
        NotificationPreference preference = ctx.getPreference();
        QuietHours quietHours = preference.getQuietHours();

        // 檢查靜音時段是否啟用
        if (quietHours == null || !quietHours.isEnabled()) {
            ctx.setInQuietHours(false);
            ctx.setShouldDelay(false);
            log.debug("[CheckQuietHoursTask] 靜音時段未啟用");
            return;
        }

        // 取得當前時間
        LocalTime now = LocalTime.now();
        LocalTime startTime = quietHours.getStartTime();
        LocalTime endTime = quietHours.getEndTime();

        // 檢查是否在靜音時段內
        boolean inQuietHours = isInQuietHours(now, startTime, endTime);

        ctx.setInQuietHours(inQuietHours);

        if (inQuietHours) {
            // 檢查優先級：URGENT 不延後
            boolean shouldDelay = !"URGENT".equals(ctx.getRequest().getPriority());
            ctx.setShouldDelay(shouldDelay);

            if (shouldDelay) {
                log.info("[CheckQuietHoursTask] 當前在靜音時段內 ({} - {})，通知將延後發送",
                        startTime, endTime);
            } else {
                log.info("[CheckQuietHoursTask] 當前在靜音時段內，但通知為 URGENT，立即發送");
            }
        } else {
            ctx.setShouldDelay(false);
            log.debug("[CheckQuietHoursTask] 當前不在靜音時段內");
        }
    }

    /**
     * 檢查時間是否在靜音時段內
     *
     * @param current 當前時間
     * @param start   靜音開始時間
     * @param end     靜音結束時間
     * @return 是否在靜音時段內
     */
    private boolean isInQuietHours(LocalTime current, LocalTime start, LocalTime end) {
        if (start == null || end == null) {
            return false;
        }

        // 處理跨日情況（例如 22:00 - 08:00）
        if (start.isBefore(end)) {
            // 同一天內（例如 12:00 - 14:00）
            return !current.isBefore(start) && !current.isAfter(end);
        } else {
            // 跨日（例如 22:00 - 08:00）
            return !current.isBefore(start) || !current.isAfter(end);
        }
    }
}
