package com.company.hrms.notification.application.service.preference;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.notification.api.response.preference.NotificationPreferenceResponse;
import com.company.hrms.notification.domain.model.aggregate.NotificationPreference;
import com.company.hrms.notification.domain.model.valueobject.QuietHours;
import com.company.hrms.notification.domain.repository.INotificationPreferenceRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * 查詢通知偏好設定 Application Service
 *
 * @author Claude
 * @since 2025-01-23
 */
@Service("getNotificationPreferenceServiceImpl")
public class GetNotificationPreferenceServiceImpl
        implements QueryApiService<Void, NotificationPreferenceResponse> {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final INotificationPreferenceRepository preferenceRepository;

    public GetNotificationPreferenceServiceImpl(INotificationPreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    @Override
    public NotificationPreferenceResponse getResponse(
            Void request,
            JWTModel currentUser,
            String... args) throws Exception {

        // 1. 取得員工 ID
        String employeeId = currentUser.getEmployeeId();

        // 2. 查詢偏好設定，若不存在則建立預設值
        NotificationPreference preference = preferenceRepository
                .findByEmployeeIdOrCreateDefault(employeeId);

        // 3. 組裝回應
        return buildResponse(preference);
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
