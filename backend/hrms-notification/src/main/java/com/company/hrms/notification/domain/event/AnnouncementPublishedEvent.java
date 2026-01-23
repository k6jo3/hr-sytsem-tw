package com.company.hrms.notification.domain.event;

import com.company.hrms.common.domain.event.DomainEvent;
import com.company.hrms.notification.domain.model.valueobject.NotificationChannel;

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
public class AnnouncementPublishedEvent extends DomainEvent {

    private final String announcementId;
    private final String title;
    private final String targetAudienceType;
    private final List<NotificationChannel> channels;
    private final String publishedBy;

    /**
     * 建構子
     *
     * @param announcementId      公告 ID
     * @param title               公告標題
     * @param targetAudienceType  目標對象類型
     * @param channels            發送渠道
     * @param publishedBy         發布者 ID
     */
    public AnnouncementPublishedEvent(
            String announcementId,
            String title,
            String targetAudienceType,
            List<NotificationChannel> channels,
            String publishedBy) {
        super();
        this.announcementId = announcementId;
        this.title = title;
        this.targetAudienceType = targetAudienceType;
        this.channels = channels;
        this.publishedBy = publishedBy;
    }

    @Override
    public String getAggregateId() {
        return announcementId;
    }

    @Override
    public String getAggregateType() {
        return "Announcement";
    }

    public String getAnnouncementId() {
        return announcementId;
    }

    public String getTitle() {
        return title;
    }

    public String getTargetAudienceType() {
        return targetAudienceType;
    }

    public List<NotificationChannel> getChannels() {
        return channels;
    }

    public String getPublishedBy() {
        return publishedBy;
    }
}
