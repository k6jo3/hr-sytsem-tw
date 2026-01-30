package com.company.hrms.notification.domain.model.aggregate;

import java.time.LocalDateTime;

import com.company.hrms.common.domain.model.AggregateRoot;
import com.company.hrms.notification.domain.model.valueobject.AnnouncementReadRecordId;

import lombok.Getter;

/**
 * 公告閱讀記錄聚合根
 *
 * @author Claude
 * @since 2026-01-30
 */
@Getter
public class AnnouncementReadRecord extends AggregateRoot<AnnouncementReadRecordId> {

    private String announcementId;
    private String employeeId;
    private LocalDateTime readAt;

    public AnnouncementReadRecord(AnnouncementReadRecordId id) {
        super(id);
    }

    public static AnnouncementReadRecord create(String announcementId, String employeeId) {
        AnnouncementReadRecord record = new AnnouncementReadRecord(AnnouncementReadRecordId.generate());
        record.announcementId = announcementId;
        record.employeeId = employeeId;
        record.readAt = LocalDateTime.now();
        return record;
    }

    // Reconstruct from persistence
    public static AnnouncementReadRecord reconstruct(String id, String announcementId, String employeeId,
            LocalDateTime readAt) {
        AnnouncementReadRecord record = new AnnouncementReadRecord(AnnouncementReadRecordId.of(id));
        record.announcementId = announcementId;
        record.employeeId = employeeId;
        record.readAt = readAt;
        return record;
    }
}
