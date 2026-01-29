package com.company.hrms.notification.infrastructure.persistence.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.company.hrms.notification.domain.model.aggregate.Notification;
import com.company.hrms.notification.domain.model.valueobject.NotificationChannel;
import com.company.hrms.notification.domain.model.valueobject.NotificationId;
import com.company.hrms.notification.domain.model.valueobject.NotificationPriority;
import com.company.hrms.notification.domain.model.valueobject.NotificationStatus;
import com.company.hrms.notification.domain.model.valueobject.NotificationType;
import com.company.hrms.notification.infrastructure.persistence.entity.NotificationPO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 通知領域物件與持久化物件轉換器
 * <p>
 * 負責 Notification (Domain) ↔ NotificationPO (Infrastructure) 的雙向轉換
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
@Component
public class NotificationMapper {

    private final ObjectMapper objectMapper;

    public NotificationMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Domain → PO
     *
     * @param notification 領域物件
     * @return 持久化物件
     */
    public NotificationPO toPO(Notification notification) {
        if (notification == null) {
            return null;
        }

        return NotificationPO.builder()
                .id(notification.getId().getValue())
                .recipientId(notification.getRecipientId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .notificationType(notification.getNotificationType().name())
                .priority(notification.getPriority().name())
                .status(notification.getStatus().name())
                .channels(serializeChannels(notification.getChannels()))
                .relatedBusinessId(notification.getRelatedBusinessId())
                .relatedBusinessType(notification.getRelatedBusinessType())
                .relatedBusinessUrl(notification.getRelatedBusinessUrl())
                .templateCode(notification.getTemplateCode())
                .templateVariables(serializeTemplateVariables(notification.getTemplateVariables()))
                .sentAt(notification.getSentAt())
                .readAt(notification.getReadAt())
                .failureReason(notification.getFailureReason())
                .retryCount(notification.getRetryCount())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .createdBy(notification.getCreatedBy())
                .updatedBy(notification.getUpdatedBy())
                .version(notification.getVersion())
                .isDeleted(notification.getIsDeleted())
                .build();
    }

    /**
     * PO → Domain
     *
     * @param po 持久化物件
     * @return 領域物件
     */
    public Notification toDomain(NotificationPO po) {
        if (po == null) {
            return null;
        }

        Notification notification = new Notification(NotificationId.of(po.getId()));

        // 設定基本屬性
        notification.setRecipientId(po.getRecipientId());
        notification.setTitle(po.getTitle());
        notification.setContent(po.getContent());
        notification.setNotificationType(NotificationType.valueOf(po.getNotificationType()));
        notification.setPriority(NotificationPriority.valueOf(po.getPriority()));
        notification.setStatus(NotificationStatus.valueOf(po.getStatus()));
        notification.setChannels(deserializeChannels(po.getChannels()));
        notification.setRelatedBusinessId(po.getRelatedBusinessId());
        notification.setRelatedBusinessType(po.getRelatedBusinessType());
        notification.setRelatedBusinessUrl(po.getRelatedBusinessUrl());
        notification.setTemplateCode(po.getTemplateCode());
        notification.setTemplateVariables(deserializeTemplateVariables(po.getTemplateVariables()));
        notification.setSentAt(po.getSentAt());
        notification.setReadAt(po.getReadAt());
        notification.setFailureReason(po.getFailureReason());
        notification.setRetryCount(po.getRetryCount());

        // 設定審計欄位
        notification.setCreatedBy(po.getCreatedBy());
        notification.setUpdatedBy(po.getUpdatedBy());
        notification.setVersion(po.getVersion());
        notification.setIsDeleted(po.getIsDeleted());

        return notification;
    }

    /**
     * 序列化渠道列表為 JSON
     */
    private String serializeChannels(List<NotificationChannel> channels) {
        if (channels == null || channels.isEmpty()) {
            return null;
        }
        try {
            List<String> channelNames = channels.stream()
                    .map(NotificationChannel::name)
                    .toList();
            return objectMapper.writeValueAsString(channelNames);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize channels", e);
        }
    }

    /**
     * 反序列化 JSON 為渠道列表
     */
    private List<NotificationChannel> deserializeChannels(String channelsJson) {
        if (channelsJson == null || channelsJson.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            List<String> channelNames = objectMapper.readValue(
                    channelsJson,
                    new TypeReference<List<String>>() {
                    });
            return channelNames.stream()
                    .map(NotificationChannel::valueOf)
                    .toList();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize channels", e);
        }
    }

    /**
     * 序列化範本變數為 JSON
     */
    private String serializeTemplateVariables(Map<String, Object> variables) {
        if (variables == null || variables.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(variables);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize template variables", e);
        }
    }

    /**
     * 反序列化 JSON 為範本變數
     */
    private Map<String, Object> deserializeTemplateVariables(String variablesJson) {
        if (variablesJson == null || variablesJson.isEmpty()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(
                    variablesJson,
                    new TypeReference<Map<String, Object>>() {
                    });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize template variables", e);
        }
    }

    /**
     * 批次轉換 Domain → PO
     */
    public List<NotificationPO> toPOList(List<Notification> notifications) {
        if (notifications == null || notifications.isEmpty()) {
            return new ArrayList<>();
        }
        return notifications.stream()
                .map(this::toPO)
                .toList();
    }

    /**
     * 批次轉換 PO → Domain
     */
    public List<Notification> toDomainList(List<NotificationPO> pos) {
        if (pos == null || pos.isEmpty()) {
            return new ArrayList<>();
        }
        return pos.stream()
                .map(this::toDomain)
                .toList();
    }
}
