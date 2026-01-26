package com.company.hrms.notification.infrastructure.persistence.mapper;

import com.company.hrms.notification.domain.model.aggregate.NotificationPreference;
import com.company.hrms.notification.domain.model.valueobject.PreferenceId;
import com.company.hrms.notification.domain.model.valueobject.QuietHours;
import com.company.hrms.notification.infrastructure.persistence.entity.NotificationPreferencePO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 通知偏好設定領域物件與持久化物件轉換器
 * <p>
 * 負責 NotificationPreference (Domain) ↔ NotificationPreferencePO (Infrastructure) 的雙向轉換
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
@Component
public class PreferenceMapper {

    private final ObjectMapper objectMapper;

    public PreferenceMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Domain → PO
     *
     * @param preference 領域物件
     * @return 持久化物件
     */
    public NotificationPreferencePO toPO(NotificationPreference preference) {
        if (preference == null) {
            return null;
        }

        QuietHours quietHours = preference.getQuietHours();

        return NotificationPreferencePO.builder()
                .id(preference.getId().getValue())
                .employeeId(preference.getEmployeeId())
                .inAppEnabled(preference.isInAppEnabled())
                .emailEnabled(preference.isEmailEnabled())
                .pushEnabled(preference.isPushEnabled())
                .teamsEnabled(preference.isTeamsEnabled())
                .lineEnabled(preference.isLineEnabled())
                .quietHoursEnabled(quietHours != null && quietHours.isEnabled())
                .quietHoursStart(quietHours != null ? quietHours.getStartTime() : null)
                .quietHoursEnd(quietHours != null ? quietHours.getEndTime() : null)
                .emailAddress(preference.getEmailAddress())
                .pushTokens(serializePushTokens(preference.getPushTokens()))
                .lineUserId(preference.getLineUserId())
                .teamsWebhookUrl(preference.getTeamsWebhookUrl())
                .createdAt(preference.getCreatedAt())
                .updatedAt(preference.getUpdatedAt())
                .createdBy(preference.getCreatedBy())
                .updatedBy(preference.getUpdatedBy())
                .version(preference.getVersion())
                .isDeleted(preference.getIsDeleted())
                .build();
    }

    /**
     * PO → Domain
     *
     * @param po 持久化物件
     * @return 領域物件
     */
    public NotificationPreference toDomain(NotificationPreferencePO po) {
        if (po == null) {
            return null;
        }

        NotificationPreference preference = new NotificationPreference(
                PreferenceId.of(po.getId())
        );

        // 設定基本屬性
        preference.setEmployeeId(po.getEmployeeId());
        preference.setInAppEnabled(po.getInAppEnabled());
        preference.setEmailEnabled(po.getEmailEnabled());
        preference.setPushEnabled(po.getPushEnabled());
        preference.setTeamsEnabled(po.getTeamsEnabled());
        preference.setLineEnabled(po.getLineEnabled());

        // 設定靜音時段
        if (po.getQuietHoursEnabled() != null && po.getQuietHoursEnabled()) {
            QuietHours quietHours = QuietHours.of(
                    po.getQuietHoursStart(),
                    po.getQuietHoursEnd()
            );
            preference.setQuietHours(quietHours);
        } else {
            preference.setQuietHours(QuietHours.disabled());
        }

        preference.setEmailAddress(po.getEmailAddress());
        preference.setPushTokens(deserializePushTokens(po.getPushTokens()));
        preference.setLineUserId(po.getLineUserId());
        preference.setTeamsWebhookUrl(po.getTeamsWebhookUrl());

        // 設定審計欄位
        preference.setCreatedBy(po.getCreatedBy());
        preference.setUpdatedBy(po.getUpdatedBy());
        preference.setVersion(po.getVersion());
        preference.setIsDeleted(po.getIsDeleted());

        return preference;
    }

    /**
     * 序列化推播 Token 列表為 JSON
     */
    private String serializePushTokens(List<String> pushTokens) {
        if (pushTokens == null || pushTokens.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(pushTokens);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize push tokens", e);
        }
    }

    /**
     * 反序列化 JSON 為推播 Token 列表
     */
    private List<String> deserializePushTokens(String pushTokensJson) {
        if (pushTokensJson == null || pushTokensJson.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(
                    pushTokensJson,
                    new TypeReference<List<String>>() {}
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize push tokens", e);
        }
    }

    /**
     * 批次轉換 Domain → PO
     */
    public List<NotificationPreferencePO> toPOList(List<NotificationPreference> preferences) {
        if (preferences == null || preferences.isEmpty()) {
            return new ArrayList<>();
        }
        return preferences.stream()
                .map(this::toPO)
                .toList();
    }

    /**
     * 批次轉換 PO → Domain
     */
    public List<NotificationPreference> toDomainList(List<NotificationPreferencePO> pos) {
        if (pos == null || pos.isEmpty()) {
            return new ArrayList<>();
        }
        return pos.stream()
                .map(this::toDomain)
                .toList();
    }
}
