package com.company.hrms.notification.infrastructure.persistence.mapper;

import com.company.hrms.notification.domain.model.aggregate.NotificationTemplate;
import com.company.hrms.notification.domain.model.valueobject.*;
import com.company.hrms.notification.infrastructure.persistence.entity.NotificationTemplatePO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通知範本領域物件與持久化物件轉換器
 * <p>
 * 負責 NotificationTemplate (Domain) ↔ NotificationTemplatePO (Infrastructure) 的雙向轉換
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
@Component
public class TemplateMapper {

    private final ObjectMapper objectMapper;

    public TemplateMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Domain → PO
     *
     * @param template 領域物件
     * @return 持久化物件
     */
    public NotificationTemplatePO toPO(NotificationTemplate template) {
        if (template == null) {
            return null;
        }

        return NotificationTemplatePO.builder()
                .id(template.getId().getValue())
                .templateCode(template.getTemplateCode())
                .name(template.getName())
                .description(template.getDescription())
                .subject(template.getSubject())
                .body(template.getBody())
                .notificationType(template.getNotificationType().name())
                .defaultPriority(template.getDefaultPriority().name())
                .defaultChannels(serializeChannels(template.getDefaultChannels()))
                .variables(serializeVariables(template.getVariables()))
                .status(template.isActive() ? "ACTIVE" : "INACTIVE")
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .createdBy(template.getCreatedBy())
                .updatedBy(template.getUpdatedBy())
                .version(template.getVersion())
                .isDeleted(template.getIsDeleted())
                .build();
    }

    /**
     * PO → Domain
     *
     * @param po 持久化物件
     * @return 領域物件
     */
    public NotificationTemplate toDomain(NotificationTemplatePO po) {
        if (po == null) {
            return null;
        }

        NotificationTemplate template = new NotificationTemplate(TemplateId.of(po.getId()));

        // 設定基本屬性
        template.setTemplateCode(po.getTemplateCode());
        template.setName(po.getName());
        template.setDescription(po.getDescription());
        template.setSubject(po.getSubject());
        template.setBody(po.getBody());
        template.setNotificationType(NotificationType.valueOf(po.getNotificationType()));
        template.setDefaultPriority(NotificationPriority.valueOf(po.getDefaultPriority()));
        template.setDefaultChannels(deserializeChannels(po.getDefaultChannels()));
        template.setVariables(deserializeVariables(po.getVariables()));
        template.setActive("ACTIVE".equals(po.getStatus()));

        // 設定審計欄位
        template.setCreatedBy(po.getCreatedBy());
        template.setUpdatedBy(po.getUpdatedBy());
        template.setVersion(po.getVersion());
        template.setIsDeleted(po.getIsDeleted());

        return template;
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
                    new TypeReference<List<String>>() {}
            );
            return channelNames.stream()
                    .map(NotificationChannel::valueOf)
                    .toList();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize channels", e);
        }
    }

    /**
     * 序列化變數說明為 JSON
     */
    private String serializeVariables(Map<String, String> variables) {
        if (variables == null || variables.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(variables);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize variables", e);
        }
    }

    /**
     * 反序列化 JSON 為變數說明
     */
    private Map<String, String> deserializeVariables(String variablesJson) {
        if (variablesJson == null || variablesJson.isEmpty()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(
                    variablesJson,
                    new TypeReference<Map<String, String>>() {}
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize variables", e);
        }
    }

    /**
     * 批次轉換 Domain → PO
     */
    public List<NotificationTemplatePO> toPOList(List<NotificationTemplate> templates) {
        if (templates == null || templates.isEmpty()) {
            return new ArrayList<>();
        }
        return templates.stream()
                .map(this::toPO)
                .toList();
    }

    /**
     * 批次轉換 PO → Domain
     */
    public List<NotificationTemplate> toDomainList(List<NotificationTemplatePO> pos) {
        if (pos == null || pos.isEmpty()) {
            return new ArrayList<>();
        }
        return pos.stream()
                .map(this::toDomain)
                .toList();
    }
}
