package com.company.hrms.notification.domain.model.valueobject;

import com.company.hrms.common.domain.model.Identifier;

/**
 * 公告 ID 值物件
 * <p>
 * 公告的唯一識別碼
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
public class AnnouncementId extends Identifier<String> {

    private AnnouncementId(String value) {
        super(value);
    }

    /**
     * 建立公告 ID
     *
     * @param value ID 值
     * @return AnnouncementId
     */
    public static AnnouncementId of(String value) {
        return new AnnouncementId(value);
    }

    /**
     * 產生新的公告 ID
     *
     * @return 新的 AnnouncementId
     */
    public static AnnouncementId generate() {
        return new AnnouncementId("ann-" + generateUUID());
    }
}
