package com.company.hrms.notification.application.service.announcement.context;

import com.company.hrms.notification.api.request.announcement.CreateAnnouncementRequest;
import com.company.hrms.notification.domain.model.aggregate.Announcement;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 發布公告上下文
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnnouncementContext extends com.company.hrms.common.application.pipeline.PipelineContext {

    // === 輸入 ===
    private final CreateAnnouncementRequest request;
    private final String publishedBy;

    // === 中間數據 ===
    private Announcement announcement;
    private int recipientCount;

    public AnnouncementContext(CreateAnnouncementRequest request, String publishedBy) {
        this.request = request;
        this.publishedBy = publishedBy;
    }
}
