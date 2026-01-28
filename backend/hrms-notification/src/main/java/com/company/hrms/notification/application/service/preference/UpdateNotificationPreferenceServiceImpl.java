package com.company.hrms.notification.application.service.preference;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.notification.api.request.preference.UpdateNotificationPreferenceRequest;
import com.company.hrms.notification.api.response.preference.NotificationPreferenceResponse;
import com.company.hrms.notification.domain.model.aggregate.NotificationPreference;
import com.company.hrms.notification.domain.model.valueobject.QuietHours;
import com.company.hrms.notification.domain.repository.INotificationPreferenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 更新通知偏好設定 Application Service
 *
 * @author Claude
 * @since 2025-01-23
 */
@Service("updateNotificationPreferenceServiceImpl")
@Transactional
public class UpdateNotificationPreferenceServiceImpl
        implements CommandApiService<UpdateNotificationPreferenceRequest, NotificationPreferenceResponse> {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final INotificationPreferenceRepository preferenceRepository;

    public UpdateNotificationPreferenceServiceImpl(INotificationPreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    @Override
    public NotificationPreferenceResponse execCommand(
            UpdateNotificationPreferenceRequest request,
            JWTModel currentUser,
            String... args) throws Exception {

        // 1. 取得員工編號
        String employeeId = currentUser.getEmployeeNumber();

        // 2. 查詢現有偏好設定，若不存在則建立預設值
        NotificationPreference preference = preferenceRepository
                .findByEmployeeIdOrCreateDefault(employeeId);

        // 3. 更新渠道偏好 (部分更新)
        if (request.getChannels() != null) {
            updateChannelPreferences(preference, request.getChannels());
        }

        // 4. 更新靜音時段 (部分更新)
        if (request.getQuietHours() != null) {
            updateQuietHours(preference, request.getQuietHours());
        }

        // 5. 設定更新者
        preference.setUpdatedBy(currentUser.getUserId());

        // 6. 儲存更新
        NotificationPreference savedPreference = preferenceRepository.save(preference);

        // 7. 組裝回應
        return buildResponse(savedPreference);
    }

    /**
     * 更新渠道偏好設定
     *
     * @param preference 偏好設定聚合根
     * @param channels   渠道偏好請求
     */
    private void updateChannelPreferences(
            NotificationPreference preference,
            UpdateNotificationPreferenceRequest.ChannelPreferences channels) {

        boolean inAppEnabled = channels.getInAppEnabled() != null
                ? channels.getInAppEnabled()
                : preference.isInAppEnabled();

        boolean emailEnabled = channels.getEmailEnabled() != null
                ? channels.getEmailEnabled()
                : preference.isEmailEnabled();

        boolean pushEnabled = channels.getPushEnabled() != null
                ? channels.getPushEnabled()
                : preference.isPushEnabled();

        boolean teamsEnabled = channels.getTeamsEnabled() != null
                ? channels.getTeamsEnabled()
                : preference.isTeamsEnabled();

        boolean lineEnabled = channels.getLineEnabled() != null
                ? channels.getLineEnabled()
                : preference.isLineEnabled();

        preference.updateChannelPreferences(
                emailEnabled,
                pushEnabled,
                inAppEnabled,
                teamsEnabled,
                lineEnabled
        );
    }

    /**
     * 更新靜音時段設定
     *
     * @param preference  偏好設定聚合根
     * @param quietHours  靜音時段請求
     */
    private void updateQuietHours(
            NotificationPreference preference,
            UpdateNotificationPreferenceRequest.QuietHoursSettings quietHours) {

        // 如果明確停用靜音時段
        if (quietHours.getEnabled() != null && !quietHours.getEnabled()) {
            preference.updateQuietHours(QuietHours.disabled());
            return;
        }

        // 如果啟用靜音時段，需要驗證時間格式
        if (quietHours.getEnabled() != null && quietHours.getEnabled()) {
            if (quietHours.getStartTime() == null || quietHours.getEndTime() == null) {
                throw new IllegalArgumentException("啟用靜音時段時，必須提供開始時間與結束時間");
            }

            try {
                LocalTime startTime = LocalTime.parse(quietHours.getStartTime(), TIME_FORMATTER);
                LocalTime endTime = LocalTime.parse(quietHours.getEndTime(), TIME_FORMATTER);

                preference.updateQuietHours(QuietHours.of(startTime, endTime));
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        String.format("靜音時段時間格式錯誤，請使用 HH:mm 格式 (例如: 22:00)。錯誤: %s", e.getMessage())
                );
            }
        }
    }

    /**
     * 建立回應物件
     *
     * @param preference 偏好設定聚合根
     * @return NotificationPreferenceResponse
     */
    private NotificationPreferenceResponse buildResponse(NotificationPreference preference) {
        // 組裝渠道設定
        NotificationPreferenceResponse.ChannelPreferences channels =
                NotificationPreferenceResponse.ChannelPreferences.builder()
                        .inAppEnabled(preference.isInAppEnabled())
                        .emailEnabled(preference.isEmailEnabled())
                        .pushEnabled(preference.isPushEnabled())
                        .teamsEnabled(preference.isTeamsEnabled())
                        .lineEnabled(preference.isLineEnabled())
                        .build();

        // 組裝靜音時段設定
        QuietHours quietHours = preference.getQuietHours();
        NotificationPreferenceResponse.QuietHoursSettings quietHoursSettings =
                NotificationPreferenceResponse.QuietHoursSettings.builder()
                        .enabled(quietHours != null && quietHours.isEnabled())
                        .startTime(quietHours != null && quietHours.getStartTime() != null
                                ? quietHours.getStartTime().format(TIME_FORMATTER)
                                : null)
                        .endTime(quietHours != null && quietHours.getEndTime() != null
                                ? quietHours.getEndTime().format(TIME_FORMATTER)
                                : null)
                        .build();

        return NotificationPreferenceResponse.builder()
                .preferenceId(preference.getId().getValue())
                .employeeId(preference.getEmployeeId())
                .channels(channels)
                .quietHours(quietHoursSettings)
                .createdAt(preference.getCreatedAt())
                .createdBy(preference.getCreatedBy())
                .updatedAt(preference.getUpdatedAt())
                .updatedBy(preference.getUpdatedBy())
                .build();
    }
}
