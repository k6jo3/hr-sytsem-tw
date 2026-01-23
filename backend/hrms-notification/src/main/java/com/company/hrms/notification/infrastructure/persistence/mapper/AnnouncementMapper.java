package com.company.hrms.notification.infrastructure.persistence.mapper;

import com.company.hrms.notification.domain.model.aggregate.Announcement;
import com.company.hrms.notification.domain.model.valueobject.AnnouncementId;
import com.company.hrms.notification.domain.model.valueobject.NotificationPriority;
import com.company.hrms.notification.infrastructure.persistence.entity.AnnouncementPO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 公告領域物件與持久化物件轉換器
 * <p>
 * 負責 Announcement (Domain) ↔ AnnouncementPO (Infrastructure) 的雙向轉換
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
@Component
public class AnnouncementMapper {

    private final ObjectMapper objectMapper;

    public AnnouncementMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Domain → PO
     *
     * @param announcement 領域物件
     * @return 持久化物件
     */
    public AnnouncementPO toPO(Announcement announcement) {
        if (announcement == null) {
            return null;
        }

        return AnnouncementPO.builder()
                .id(announcement.getId().getValue())
                .title(announcement.getTitle())
                .content(announcement.getContent())
                .status(announcement.getStatus().name())
                .priority(announcement.getPriority().name())
                .targetAudience(announcement.getTargetAudience().name())
                .targetDepartmentIds(serializeList(announcement.getTargetDepartmentIds()))
                .targetRoleIds(serializeList(announcement.getTargetRoleIds()))
                .targetEmployeeIds(serializeList(announcement.getTargetEmployeeIds()))
                .publishedBy(announcement.getPublishedBy())
                .publishedAt(announcement.getPublishedAt())
                .effectiveFrom(announcement.getEffectiveFrom())
                .effectiveTo(announcement.getEffectiveTo())
                .isPinned(announcement.isPinned())
                .attachments(serializeList(announcement.getAttachments()))
                .readCount(announcement.getReadCount())
                .createdAt(announcement.getCreatedAt())
                .updatedAt(announcement.getUpdatedAt())
                .createdBy(announcement.getCreatedBy())
                .updatedBy(announcement.getUpdatedBy())
                .version(announcement.getVersion())
                .isDeleted(announcement.getIsDeleted())
                .build();
    }

    /**
     * PO → Domain
     *
     * @param po 持久化物件
     * @return 領域物件
     */
    public Announcement toDomain(AnnouncementPO po) {
        if (po == null) {
            return null;
        }

        Announcement announcement = new Announcement(AnnouncementId.of(po.getId()));

        // 設定基本屬性
        announcement.setTitle(po.getTitle());
        announcement.setContent(po.getContent());
        announcement.setStatus(Announcement.AnnouncementStatus.valueOf(po.getStatus()));
        announcement.setPriority(NotificationPriority.valueOf(po.getPriority()));
        announcement.setTargetAudience(Announcement.TargetAudience.valueOf(po.getTargetAudience()));
        announcement.setTargetDepartmentIds(deserializeList(po.getTargetDepartmentIds()));
        announcement.setTargetRoleIds(deserializeList(po.getTargetRoleIds()));
        announcement.setTargetEmployeeIds(deserializeList(po.getTargetEmployeeIds()));
        announcement.setPublishedBy(po.getPublishedBy());
        announcement.setPublishedAt(po.getPublishedAt());
        announcement.setEffectiveFrom(po.getEffectiveFrom());
        announcement.setEffectiveTo(po.getEffectiveTo());
        announcement.setPinned(po.getIsPinned());
        announcement.setAttachments(deserializeList(po.getAttachments()));
        announcement.setReadCount(po.getReadCount());

        // 設定審計欄位
        announcement.setCreatedAt(po.getCreatedAt());
        announcement.setUpdatedAt(po.getUpdatedAt());
        announcement.setCreatedBy(po.getCreatedBy());
        announcement.setUpdatedBy(po.getUpdatedBy());
        announcement.setVersion(po.getVersion());
        announcement.setIsDeleted(po.getIsDeleted());

        return announcement;
    }

    /**
     * 序列化字串列表為 JSON
     */
    private String serializeList(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize list", e);
        }
    }

    /**
     * 反序列化 JSON 為字串列表
     */
    private List<String> deserializeList(String json) {
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(
                    json,
                    new TypeReference<List<String>>() {}
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize list", e);
        }
    }

    /**
     * 批次轉換 Domain → PO
     */
    public List<AnnouncementPO> toPOList(List<Announcement> announcements) {
        if (announcements == null || announcements.isEmpty()) {
            return new ArrayList<>();
        }
        return announcements.stream()
                .map(this::toPO)
                .toList();
    }

    /**
     * 批次轉換 PO → Domain
     */
    public List<Announcement> toDomainList(List<AnnouncementPO> pos) {
        if (pos == null || pos.isEmpty()) {
            return new ArrayList<>();
        }
        return pos.stream()
                .map(this::toDomain)
                .toList();
    }
}
