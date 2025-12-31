package com.company.hrms.attendance.domain.model.valueobject;

import com.company.hrms.common.domain.model.Identifier;

/**
 * 打卡記錄 ID
 */
public class RecordId extends Identifier<String> {
    public RecordId(String value) {
        super(value);
    }

    public static RecordId next() {
        return new RecordId(generateUUID());
    }
}
