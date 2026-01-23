package com.company.hrms.notification.domain.model.valueobject;

import com.company.hrms.common.domain.model.Identifier;

/**
 * 通知偏好設定 ID 值物件
 * <p>
 * 通知偏好設定的唯一識別碼
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
public class PreferenceId extends Identifier<String> {

    private PreferenceId(String value) {
        super(value);
    }

    /**
     * 建立偏好設定 ID
     *
     * @param value ID 值
     * @return PreferenceId
     */
    public static PreferenceId of(String value) {
        return new PreferenceId(value);
    }

    /**
     * 產生新的偏好設定 ID
     *
     * @return 新的 PreferenceId
     */
    public static PreferenceId generate() {
        return new PreferenceId("pref-" + generateUUID());
    }
}
