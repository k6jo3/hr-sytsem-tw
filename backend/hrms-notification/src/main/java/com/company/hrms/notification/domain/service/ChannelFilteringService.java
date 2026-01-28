package com.company.hrms.notification.domain.service;

import java.util.ArrayList;
import java.util.List;

import com.company.hrms.notification.domain.model.aggregate.NotificationPreference;
import com.company.hrms.notification.domain.model.valueobject.NotificationChannel;
import com.company.hrms.notification.domain.model.valueobject.NotificationPriority;

/**
 * 渠道過濾領域服務
 * <p>
 * 負責根據使用者偏好設定和通知優先級過濾發送渠道
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
@org.springframework.stereotype.Service
public class ChannelFilteringService {

    /**
     * 過濾通知渠道
     * <p>
     * 根據使用者偏好設定和通知優先級決定實際發送的渠道
     * </p>
     *
     * @param requestedChannels 請求的渠道列表
     * @param preference        使用者偏好設定
     * @param priority          通知優先級
     * @return 過濾後的渠道列表
     */
    public List<NotificationChannel> filterChannels(
            List<NotificationChannel> requestedChannels,
            NotificationPreference preference,
            NotificationPriority priority) {

        if (requestedChannels == null || requestedChannels.isEmpty()) {
            return List.of(NotificationChannel.IN_APP);
        }

        // 如果是緊急通知，忽略使用者偏好，使用所有請求的渠道
        if (priority != null && priority.isUrgent()) {
            return new ArrayList<>(requestedChannels);
        }

        // 根據使用者偏好過濾
        if (preference != null) {
            return preference.filterChannels(requestedChannels);
        }

        // 如果沒有偏好設定，使用所有請求的渠道
        return new ArrayList<>(requestedChannels);
    }

    /**
     * 檢查是否應該延後發送
     * <p>
     * 根據靜音時段和優先級判斷是否應該延後發送通知
     * </p>
     *
     * @param preference 使用者偏好設定
     * @param priority   通知優先級
     * @return true 表示應該延後發送
     */
    public boolean shouldDelaySending(NotificationPreference preference, NotificationPriority priority) {
        // 緊急通知不延後
        if (priority != null && priority.isUrgent()) {
            return false;
        }

        // 檢查是否在靜音時段且優先級需要遵守靜音時段
        if (preference != null && priority != null && priority.shouldRespectQuietHours()) {
            return preference.isInQuietHours();
        }

        return false;
    }

    /**
     * 取得建議的重試渠道
     * <p>
     * 當某些渠道發送失敗時，建議可以重試的渠道
     * </p>
     *
     * @param failedChannels 失敗的渠道列表
     * @param priority       通知優先級
     * @return 建議重試的渠道列表
     */
    public List<NotificationChannel> getRetryChannels(
            List<NotificationChannel> failedChannels,
            NotificationPriority priority) {

        if (failedChannels == null || failedChannels.isEmpty()) {
            return List.of();
        }

        List<NotificationChannel> retryChannels = new ArrayList<>();

        for (NotificationChannel channel : failedChannels) {
            // 非同步渠道可以重試
            if (channel.isAsync()) {
                retryChannels.add(channel);
            }
        }

        // 如果所有渠道都失敗了，至少保留系統內通知
        if (retryChannels.isEmpty() && !failedChannels.contains(NotificationChannel.IN_APP)) {
            retryChannels.add(NotificationChannel.IN_APP);
        }

        return retryChannels;
    }
}
