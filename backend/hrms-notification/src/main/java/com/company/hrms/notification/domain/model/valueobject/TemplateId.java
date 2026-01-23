package com.company.hrms.notification.domain.model.valueobject;

import com.company.hrms.common.domain.model.Identifier;

/**
 * 通知範本 ID 值物件
 * <p>
 * 通知範本的唯一識別碼
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
public class TemplateId extends Identifier<String> {

    private TemplateId(String value) {
        super(value);
    }

    /**
     * 建立範本 ID
     *
     * @param value ID 值
     * @return TemplateId
     */
    public static TemplateId of(String value) {
        return new TemplateId(value);
    }

    /**
     * 產生新的範本 ID
     *
     * @return 新的 TemplateId
     */
    public static TemplateId generate() {
        return new TemplateId("tpl-" + generateUUID());
    }
}
