package com.company.hrms.notification.domain.event;

import com.company.hrms.common.domain.event.DomainEvent;
import com.company.hrms.notification.domain.model.valueobject.NotificationChannel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 公告發布事件
 * <p>
 * 當公告被發布時發布此事件
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementPublishedEvent extends DomainEvent {

    private String announcementId;
    private String title;
    private String targetAudienceType;
    private List<NotificationChannel> channels;
    private String publishedBy;

    @Override
    public String getAggregateId() {
        return announcementId;
    }

    @Override
    public String getAggregateType() {
        return "Announcement";
    }
}
