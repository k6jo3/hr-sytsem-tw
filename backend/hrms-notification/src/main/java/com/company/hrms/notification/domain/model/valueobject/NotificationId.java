package com.company.hrms.notification.domain.model.valueobject;

import com.company.hrms.common.domain.model.Identifier;

/**
 * 通知 ID 值物件
 * <p>
 * 通知的唯一識別碼
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
public class NotificationId extends Identifier<String> {

    private NotificationId(String value) {
        super(value);
    }

    /**
     * 建立通知 ID
     *
     * @param value ID 值
     * @return NotificationId
     */
    public static NotificationId of(String value) {
        return new NotificationId(value);
    }

    /**
     * 產生新的通知 ID
     *
     * @return 新的 NotificationId
     */
    public static NotificationId generate() {
        return new NotificationId("ntf-" + generateUUID());
    }
}
