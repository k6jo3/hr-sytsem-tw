package com.company.hrms.notification.domain.model.valueobject;

import com.company.hrms.common.domain.model.Identifier;

/**
 * 公告閱讀記錄 ID
 *
 * @author Claude
 * @since 2026-01-30
 */
public class AnnouncementReadRecordId extends Identifier<String> {

    public AnnouncementReadRecordId(String value) {
        super(value);
    }

    public static AnnouncementReadRecordId generate() {
        return new AnnouncementReadRecordId(generateUUID());
    }

    public static AnnouncementReadRecordId of(String value) {
        return new AnnouncementReadRecordId(value);
    }
}
